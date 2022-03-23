package dhyces.compostbag.item;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import dhyces.compostbag.CompostBag;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.items.ItemHandlerHelper;

public class CompostBagItem extends Item {
	
	public static final String TAG_LEVEL = "Level";
	public static final String TAG_COUNT = "Count";
	public static final int MAX_BONEMEAL_COUNT = 128;
	public final DispenseItemBehavior DISPENSE_BEHAVIOR = new OptionalDispenseItemBehavior() {
        protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
            this.setSuccess(true);
            var level = blockSource.getLevel();
            var blockpos = blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING));
            var bonemeal = getTagItem(stack);
            if (bonemeal.isEmpty() || !BoneMealItem.growCrop(bonemeal, level, blockpos) && !BoneMealItem.growWaterPlant(bonemeal, level, blockpos, (Direction)null)) {
               this.setSuccess(false);
            } else if (!level.isClientSide) {
               level.levelEvent(1505, blockpos, 0);
               setBonemealCount(stack, bonemeal.getCount());
            }

            return stack;
         }
      };

	public CompostBagItem() {
		super(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
		setRegistryName(CompostBag.MODID, "compost_bag");
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack bonemeal = getTagItem(context.getItemInHand());
		Level l = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (!bonemeal.isEmpty() && BoneMealItem.applyBonemeal(bonemeal, l, pos, context.getPlayer())) {
			setBonemealCount(context.getItemInHand(), bonemeal.getCount());
			playBonemealSound(l, pos);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
	
	public static float getFullnessDisplay(ItemStack bag) {
		return getBonemealCount(bag) / (float)MAX_BONEMEAL_COUNT;
	}
	
	public boolean isBarVisible(ItemStack bag) {
		return getBonemealCount(bag) > 0;
	}

	public int getBarWidth(ItemStack bag) {
		return 1 + (int) ((float)getBonemealCount(bag) / (float)MAX_BONEMEAL_COUNT * MAX_BAR_WIDTH - 1);
	}
	
	@Override
	public int getBarColor(ItemStack p_150901_) {
		return Mth.color(0.4F, 0.4F, 1.0F);
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction clickAction, Player player) {
		// player right clicks on another item with the bag
		if (clickAction == ClickAction.SECONDARY) {
			if (slot.hasItem()) {
				float compostable = getCompostable(slot.getItem());
				if (compostable > 0) {
					
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack otherItem, Slot slot,
			ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction == ClickAction.SECONDARY) {
			if (!otherItem.isEmpty()) {
				float compostable = getCompostable(otherItem);
				var lvl = getLevel(bag);
				var count = getBonemealCount(bag);
				if (compostable > 0F && lvl < ComposterBlock.READY) {
					if (count == MAX_BONEMEAL_COUNT && lvl == ComposterBlock.MAX_LEVEL)
						return false;
					if ((lvl != 0 || !(compostable < 0.0F)) && !(player.getLevel().getRandom().nextDouble() < (double)compostable)) {
						playFillSound(player);
						otherItem.shrink(1);
						return true;
					}
					if (lvl < ComposterBlock.MAX_LEVEL) {
						growLevel(bag, 1);
						playFillSuccessSound(player);
					} else if (count < MAX_BONEMEAL_COUNT) {
						setLevel(bag, ComposterBlock.MIN_LEVEL);
						growBonemealCount(bag, 1);
						playReadySound(player);
					}
					otherItem.shrink(1);
					return true;
				} else if (otherItem.is(Items.BONE_MEAL) && count < MAX_BONEMEAL_COUNT) {
					int growCount = Math.min(otherItem.getCount(), MAX_BONEMEAL_COUNT - count);
					growBonemealCount(bag, growCount);
					otherItem.shrink(growCount);
					playInsertSound(player);
					return true;
				}
			} else {
				remove(bag, MAX_STACK_SIZE).ifPresent(c -> {
					playRemoveSound(player);
					slotAccess.set(c);
				});
				return true;
			}
		}
		return super.overrideOtherStackedOnMe(bag, otherItem, slot, clickAction, player, slotAccess);
	}
	
	private int compost(ItemStack bag, ItemStack compostable) {
		return 0;
	}
	
	private float getCompostable(ItemStack stack) {
		return ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack item) {
		// TODO: I want to make it only show the special tooltip if it's not a creative item picker slot
//		if (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen sc &&
//				sc.getSlotUnderMouse() != null && sc.getMenu().getSlot(sc.getSlotUnderMouse().getContainerSlot()) == sc.getSlotUnderMouse())
//			return Optional.empty();
		return Optional.of(new CompostBagTooltip(getTagItem(item), getLevel(item), getBonemealCount(item)));
	}
	
	private ItemStack insert(ItemStack bag, ItemStack stack) {
		return bag;
	}
	
	private Optional<ItemStack> remove(ItemStack bag, int amount) {
		var count = Math.min(getBonemealCount(bag), MAX_STACK_SIZE);
		if (count == 0)
			return Optional.empty();
		ItemStack item = ItemHandlerHelper.copyStackWithSize(Items.BONE_MEAL.getDefaultInstance(), count);
		growBonemealCount(bag, -count);
		return Optional.of(item);
	}
	
	private ItemStack getTagItem(ItemStack bag) {
		ItemStack stack = Items.BONE_MEAL.getDefaultInstance().copy();
		stack.setCount(getBonemealCount(bag));
		if (stack.getCount() == 0) stack = ItemStack.EMPTY;
		return stack;
	}
	
	private void growBonemealCount(ItemStack bag, int amount) {
		setBonemealCount(bag, getBonemealCount(bag)+amount);
	}
	
	private void setBonemealCount(ItemStack bag, int count) {
		if (count == 0) {
			bag.removeTagKey(TAG_COUNT);
			return;
		}
		bag.getOrCreateTag().putInt(TAG_COUNT, count);
	}
	
	private static int getBonemealCount(ItemStack bag) {
		return bag.getOrCreateTag().getInt(TAG_COUNT);
	}
	
	private void growLevel(ItemStack bag, int amount) {
		setLevel(bag, getLevel(bag)+amount);
	}
	
	private void setLevel(ItemStack bag, int lvl) {
		if (lvl == 0) {
			bag.removeTagKey(TAG_LEVEL);
			return;
		}
		bag.getOrCreateTag().putInt(TAG_LEVEL, lvl);
	}
	
	private int getLevel(ItemStack bag) {
		return bag.getOrCreateTag().getInt(TAG_LEVEL);
	}
	
	private void playBonemealSound(Level level, BlockPos pos) {
		level.playSound((Player)null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
	} 
	
	private void playFillSound(Player player) {
		player.playSound(SoundEvents.COMPOSTER_FILL, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
	}
	
	private void playFillSuccessSound(Player player) {
		player.playSound(SoundEvents.COMPOSTER_FILL_SUCCESS, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
	}
	
	private void playReadySound(Player player) {
		player.playSound(SoundEvents.COMPOSTER_READY, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
	}
	
	private void playRemoveSound(Player player) {
		player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
	}

	private void playInsertSound(Player player) {
		player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
		player.playSound(SoundEvents.BONE_MEAL_USE, 0.4F, 0.8F + player.getLevel().getRandom().nextFloat() * 0.4F);
	}
}
