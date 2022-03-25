package dhyces.compostbag.item;

import java.util.Optional;

import dhyces.compostbag.Config;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.items.ItemHandlerHelper;

public class CompostBagItem extends Item {

	public static final String TAG_LEVEL = "Level";
	public static final String TAG_COUNT = "Count";
	public static final IntValue MAX_BONEMEAL_COUNT = Config.COMMON.MAX_BONEMEAL;
	public static final int MAX_LEVEL = ComposterBlock.MAX_LEVEL;
	public final DispenseItemBehavior DISPENSE_BEHAVIOR = new OptionalDispenseItemBehavior() {
        @Override
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
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack bonemeal = getTagItem(context.getItemInHand());
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		if (!bonemeal.isEmpty() && BoneMealItem.applyBonemeal(bonemeal, level, blockpos, context.getPlayer())) {
			setBonemealCount(context.getItemInHand(), bonemeal.getCount());
			playBonemealSound(level, blockpos);
			level.levelEvent(1505, blockpos, 0);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}

	public static float getFullnessDisplay(ItemStack bag) {
		return getBonemealCount(bag) / (float)MAX_BONEMEAL_COUNT.get();
	}

	@Override
	public boolean isBarVisible(ItemStack bag) {
		return getBonemealCount(bag) > 0;
	}

	@Override
	public int getBarWidth(ItemStack bag) {
		return 1 + (int) ((float)getBonemealCount(bag) / (float)MAX_BONEMEAL_COUNT.get() * MAX_BAR_WIDTH - 1);
	}

	@Override
	public int getBarColor(ItemStack p_150901_) {
		return Mth.color(0.4F, 0.4F, 1.0F);
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction clickAction, Player player) {
		if (clickAction == ClickAction.SECONDARY) {
			if (slot.hasItem()) {
				var slotItem = slot.getItem();
				if (isCompostable(slotItem)) {
					var shrinkBy = 0;
					while (shrinkBy != slotItem.getCount() && !isBagFull(bag)) {
						var result = compost(bag, slotItem, player);
						if (result.getResult().consumesAction()) {
							if (!result.getObject().isEmpty()) {
								setLevel(bag, ComposterBlock.MIN_LEVEL);
								growBonemealCount(bag, 1);
							} else {
								growLevel(bag, 1);
							}
							shrinkBy++;
						}
					}
					if (shrinkBy > 0)
						playReadySound(player);
					playFillSound(player);
					slotItem.shrink(shrinkBy);
					return true;
				}
				if (slotItem.is(Items.BONE_MEAL) && getBonemealCount(bag) < MAX_BONEMEAL_COUNT.get()) {
					insertBonemeal(bag, slotItem);
					playInsertSound(player);
					return true;
				}
			} else {
				remove(bag, MAX_STACK_SIZE).ifPresent(c -> {
					playRemoveSound(player);
					slot.set(c);
				});
			}
		}
		return super.overrideStackedOnOther(bag, slot, clickAction, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack otherItem, Slot slot,
			ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (clickAction == ClickAction.SECONDARY) {
			if (!otherItem.isEmpty()) {
				var count = getBonemealCount(bag);
				if (isCompostable(otherItem) && !isBagFull(bag)) {
					var result = compost(bag, otherItem, player);
					if (result.getResult().consumesAction()) {
						if (!result.getObject().isEmpty()) {
							setLevel(bag, ComposterBlock.MIN_LEVEL);
							growBonemealCount(bag, 1);
							playReadySound(player);
						} else {
							growLevel(bag, 1);
							playFillSuccessSound(player);
						}
						playFillSound(player);
						otherItem.shrink(1);
					}
				}
				if (otherItem.is(Items.BONE_MEAL) && count < MAX_BONEMEAL_COUNT.get()) {
					insertBonemeal(bag, otherItem);
					playInsertSound(player);
				}
			} else {
				remove(bag, MAX_STACK_SIZE).ifPresent(c -> {
					playRemoveSound(player);
					slotAccess.set(c);
				});
			}
			return true;
		}
		return super.overrideOtherStackedOnMe(bag, otherItem, slot, clickAction, player, slotAccess);
	}

	private InteractionResultHolder<ItemStack> compost(ItemStack bag, ItemStack item, Player player) {
		var compostable = getCompostable(item);
		if (compostable == 0)
			return InteractionResultHolder.fail(ItemStack.EMPTY);
		var lvl = getLevel(bag);
		if ((lvl != 0 || !(compostable < 0.0F)) && !(player.getRandom().nextDouble() < compostable))
			return InteractionResultHolder.consume(ItemStack.EMPTY);
		if (lvl < MAX_LEVEL)
			return InteractionResultHolder.success(ItemStack.EMPTY);
		return InteractionResultHolder.success(new ItemStack(Items.BONE_MEAL));
	}

	private boolean isBagFull(ItemStack stack) {
		return getBonemealCount(stack) >= MAX_BONEMEAL_COUNT.get() && getLevel(stack) >= MAX_LEVEL;
	}

	private boolean isCompostable(ItemStack stack) {
		return ComposterBlock.COMPOSTABLES.containsKey(stack.getItem());
	}

	private float getCompostable(ItemStack stack) {
		return ComposterBlock.COMPOSTABLES.getOrDefault(stack.getItem(), 0.0F);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack item) {
		return Optional.of(new CompostBagTooltip(getTagItem(item), getLevel(item), getBonemealCount(item)));
	}

	private ItemStack insertBonemeal(ItemStack bag, ItemStack bonemeal) {
		if (bonemeal.isEmpty() || !(bonemeal.getItem() instanceof BoneMealItem))
			return bonemeal;
		var max = Math.min(bonemeal.getCount(), MAX_BONEMEAL_COUNT.get() - getBonemealCount(bag));
		growBonemealCount(bag, max);
		bonemeal.shrink(max);
		return bonemeal;
	}

	private Optional<ItemStack> remove(ItemStack bag, int amount) {
		var count = Math.min(getBonemealCount(bag), amount);
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
