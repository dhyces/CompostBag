package dev.dhyces.compostbag.item;

import dev.dhyces.compostbag.CompostBag;
import dev.dhyces.compostbag.platform.Services;
import dev.dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Optional;

public class CompostBagItem extends Item {

	public static final DispenseItemBehavior DISPENSE_BEHAVIOR = new OptionalDispenseItemBehavior() {
        @Override
		protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
            this.setSuccess(true);
            var level = blockSource.level();
            var blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
            var bonemeal = getTagItem(stack);
            if (bonemeal.isEmpty() || !BoneMealItem.growCrop(bonemeal, level, blockPos) && !BoneMealItem.growWaterPlant(bonemeal, level, blockPos, null)) {
               this.setSuccess(false);
            } else if (!level.isClientSide) {
               level.levelEvent(1505, blockPos, 0);
               setBonemealCount(stack, bonemeal.getCount());
            }

            return stack;
         }
      };

	public CompostBagItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack bonemeal = getTagItem(context.getItemInHand());
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		if (!bonemeal.isEmpty() && Services.PLATFORM.bonemeal(bonemeal, level, blockpos, context.getPlayer())) {
			setBonemealCount(context.getItemInHand(), bonemeal.getCount());
			playBonemealSound(level, blockpos);
			level.levelEvent(1505, blockpos, 0);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return getBonemealCount(stack) > 0;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return 1 + (int) ((float)getBonemealCount(stack) / (float)getMaxBonemeal(stack) * MAX_BAR_WIDTH - 1);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return getBonemealCount(stack) < getMaxBonemeal(stack) ? 0x6666FF : 0xFF3737;
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player) {
		if (!slot.allowModification(player))
			return false;
		if (clickAction == ClickAction.SECONDARY) {
			if (!player.level().isClientSide || player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
				if (slot.hasItem()) {
					var slotItem = slot.getItem();
					if (isCompostable(slotItem)) {
						compostAll(stack, slotItem, player);
					}
					if (slotItem.is(Items.BONE_MEAL) && getBonemealCount(stack) < getMaxBonemeal(stack)) {
						insertBonemeal(stack, slotItem);
						playInsertSound(player);
					}
				} else {
					remove(stack, Items.BONE_MEAL.getDefaultMaxStackSize()).ifPresent(c -> {
						playRemoveSound(player);
						slot.set(c);
					});
				}
			}
			return true;
		}
		return super.overrideStackedOnOther(stack, slot, clickAction, player);
	}

	private void compostAll(ItemStack bag, ItemStack slotItem, Player player) {
		int shrinkBy = 0;
		while (shrinkBy != slotItem.getCount() && !isBagFull(bag)) {
			InteractionResultHolder<ItemStack> result = simulateCompost(bag, slotItem, player);
			if (result.getResult().consumesAction()) {
				if (!result.getObject().isEmpty()) {
					setLevel(bag, ComposterBlock.MIN_LEVEL);
					growBonemealCount(bag, 1);
				} else if (result.getResult().equals(InteractionResult.SUCCESS)) {
					growLevel(bag, 1);
				}
				shrinkBy++;
			}
		}
		if (shrinkBy > 0) {
			playReadySound(player);
		}
		playFillSound(player);
		slotItem.shrink(shrinkBy);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherItem, Slot slot,
			ClickAction clickAction, Player player, SlotAccess slotAccess) {
		if (!slot.allowModification(player))
			return false;
		if (clickAction == ClickAction.SECONDARY) {
			if (!player.level().isClientSide || player.containerMenu instanceof CreativeModeInventoryScreen.ItemPickerMenu) {
				if (!otherItem.isEmpty()) {
					var count = getBonemealCount(stack);
					if (isCompostable(otherItem) && !isBagFull(stack)) {
						var result = simulateCompost(stack, otherItem, player);
						if (result.getResult().consumesAction()) {
							if (!result.getObject().isEmpty()) {
								setLevel(stack, ComposterBlock.MIN_LEVEL);
								growBonemealCount(stack, 1);
								playReadySound(player);
							} else if (result.getResult().equals(InteractionResult.SUCCESS)) {
								growLevel(stack, 1);
								playFillSuccessSound(player);
							}
							playFillSound(player);
							otherItem.shrink(1);
						}
					}
					if (otherItem.is(Items.BONE_MEAL) && count < getMaxBonemeal(stack)) {
						insertBonemeal(stack, otherItem);
						playInsertSound(player);
					}
				} else {
					remove(stack, Items.BONE_MEAL.getDefaultMaxStackSize()).ifPresent(c -> {
						playRemoveSound(player);
						slotAccess.set(c);
					});
				}
			}
			return true;
		}
		return super.overrideOtherStackedOnMe(stack, otherItem, slot, clickAction, player, slotAccess);
	}

	private InteractionResultHolder<ItemStack> simulateCompost(ItemStack stack, ItemStack item, Player player) {
		float compostable = getCompostable(item);
		if (compostable == 0) {
			return InteractionResultHolder.fail(ItemStack.EMPTY);
		}

		int lvl = getLevel(stack);
		if ((lvl != 0 || !(compostable < 0.0F)) && !(player.level().getRandom().nextDouble() < compostable)) {
			return InteractionResultHolder.consume(ItemStack.EMPTY);
		}

		if (lvl < getMaxLevel(stack)) {
			return InteractionResultHolder.success(ItemStack.EMPTY);
		}

		return InteractionResultHolder.success(new ItemStack(Items.BONE_MEAL));
	}

	private boolean isCompostable(ItemStack stack) {
		return Services.PLATFORM.getCompostChance(stack) > 0;
	}

	private float getCompostable(ItemStack stack) {
		return Services.PLATFORM.getCompostChance(stack);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return Optional.of(new CompostBagTooltip(getMaxBonemeal(stack), getBonemealCount(stack), getMaxLevel(stack), getLevel(stack)));
	}

	private ItemStack insertBonemeal(ItemStack stack, ItemStack bonemeal) {
		if (bonemeal.isEmpty() || !(bonemeal.getItem() instanceof BoneMealItem))
			return bonemeal;
		var max = Math.min(bonemeal.getCount(), getMaxBonemeal(stack) - getBonemealCount(stack));
		growBonemealCount(stack, max);
		bonemeal.shrink(max);
		return bonemeal;
	}

	private boolean isBagFull(ItemStack stack) {
		return getBonemealCount(stack) >= getMaxBonemeal(stack) && getLevel(stack) >= getMaxLevel(stack);
	}

	private Optional<ItemStack> remove(ItemStack bag, int amount) {
		var count = Math.min(getBonemealCount(bag), amount);
		if (count == 0)
			return Optional.empty();
		ItemStack item = new ItemStack(Items.BONE_MEAL, count);
		growBonemealCount(bag, -count);
		return Optional.of(item);
	}

	private static ItemStack getTagItem(ItemStack bag) {
		int count = getBonemealCount(bag);
		if (count == 0) {
			return ItemStack.EMPTY;
		}
		return new ItemStack(Items.BONE_MEAL, count);
	}

	public static int getMaxBonemeal(ItemStack stack) {
		return stack.getOrDefault(CompostBag.MAX_BONEMEAL_COUNT.value(), 0);
	}

	private void growBonemealCount(ItemStack bag, int amount) {
		setBonemealCount(bag, getBonemealCount(bag)+amount);
	}

	private static void setBonemealCount(ItemStack bag, int count) {
		if (count == 0) {
			bag.remove(CompostBag.BONEMEAL_COUNT.value());
			return;
		}
		bag.set(CompostBag.BONEMEAL_COUNT.value(), count);
	}

	public static int getBonemealCount(ItemStack stack) {
		return stack.getOrDefault(CompostBag.BONEMEAL_COUNT.value(), 0);
	}

	public static int getMaxLevel(ItemStack stack) {
		return stack.getOrDefault(CompostBag.MAX_COMPOST_LEVEL.value(), 0);
	}

	private void growLevel(ItemStack stack, int amount) {
		setLevel(stack, getLevel(stack)+amount);
	}

	private void setLevel(ItemStack stack, int lvl) {
		if (lvl == 0) {
			stack.remove(CompostBag.COMPOST_LEVEL.value());
			return;
		}
		stack.set(CompostBag.COMPOST_LEVEL.value(), lvl);
	}

	private int getLevel(ItemStack stack) {
		return stack.getOrDefault(CompostBag.COMPOST_LEVEL.value(), 0);
	}

	private void playBonemealSound(Level level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 0.8F, 0.8F + level.getRandom().nextFloat() * 0.4F);
	}

	private void playFillSound(Player player) {
		playerSound(player, SoundEvents.COMPOSTER_FILL, 0.8F);
	}

	private void playFillSuccessSound(Player player) {
		playerSound(player, SoundEvents.COMPOSTER_FILL_SUCCESS, 0.8F);
	}

	private void playReadySound(Player player) {
		playerSound(player, SoundEvents.COMPOSTER_READY, 0.8F);
	}

	private void playRemoveSound(Player player) {
		playerSound(player, SoundEvents.BUNDLE_REMOVE_ONE, 0.8F);
	}

	private void playInsertSound(Player player) {
		playerSound(player, SoundEvents.BUNDLE_INSERT, 0.8F);
		playerSound(player, SoundEvents.BONE_MEAL_USE, 0.4F);
	}

	private void playerSound(Player player, SoundEvent event, float volume) {
		var randPitch = 0.8F + player.level().random.nextFloat() * 0.4F;
		if (player instanceof ServerPlayer serverPlayer)
			serverPlayer.playNotifySound(event, SoundSource.PLAYERS, volume, randPitch);
		else
			player.playSound(event, volume, randPitch);
	}
}
