package dhyces.compostbag;

import org.lwjgl.glfw.GLFW;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CompostBag.MODID, value = Dist.CLIENT)
public class Events {

	@SubscribeEvent
	static void cancelOtherTooltips(final RenderTooltipEvent.GatherComponents event) {
		if (event.getTooltipElements().stream().anyMatch(c -> c.right().map(b -> b instanceof CompostBagTooltip).orElse(false))) {
			return;
		}
		var mc = Minecraft.getInstance();
		if (mc.screen.children().stream().anyMatch(c -> c instanceof RecipeBookComponent) && mc.screen instanceof InventoryScreen screen) {
			if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	static void renderTooltipWhileHovering(final ContainerScreenEvent.DrawForeground e) {
		var screen = e.getContainerScreen();

		var bag = ItemStack.EMPTY;

		var hoveredSlot = screen.getSlotUnderMouse();
		var carried = screen.getMenu().getCarried();
		if (hoveredSlot != null && isCompostBag(hoveredSlot.getItem()) && !carried.isEmpty()) {
			bag = hoveredSlot.getItem();
		}
		else if (isCompostBag(carried))
			bag = carried;

		if (bag.isEmpty())
			return;

		var pose = e.getPoseStack();
		var x = e.getMouseX()-screen.getGuiLeft();
		var y = e.getMouseY()-screen.getGuiTop();
		screen.renderTooltip(pose, screen.getTooltipFromItem(bag), bag.getTooltipImage(), x, y, bag);
	}

	private static boolean isCompostBag(ItemStack stack) {
		return stack.getItem() instanceof CompostBagItem;
	}

	static final Ticker TICKER = new Ticker(40, 30);

	@SubscribeEvent
	static void multiDrop(final ContainerScreenEvent event) {
		var clientPlayer = Minecraft.getInstance().player;
		if (clientPlayer == null)
			return;
		var mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_RIGHT);
		if (mouseDown == GLFW.GLFW_RELEASE) {
			TICKER.restart();
		}
		var screen = event.getContainerScreen();
		if (mouseDown == GLFW.GLFW_PRESS) {
			var slot = screen.getSlotUnderMouse();
			if (slot != null && slot.hasItem()) {
				var item = slot.getItem();
				var carried = screen.getMenu().getCarried();
				if (carried == null || carried.isEmpty() || !ComposterBlock.COMPOSTABLES.containsKey(carried.getItem()))
					return;
				if (TICKER.tick() && item.getItem() instanceof CompostBagItem bag) {
					var window = Minecraft.getInstance().getWindow();
					var x = Minecraft.getInstance().mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
					var y = Minecraft.getInstance().mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
					screen.mouseReleased(x, y, 1);
				}
			}
		}
	}

	static class Ticker {
		int ticks;
		final int first;
		final int rest;

		/** On the first interaction, it should be a longer delay before more items are input.
		 * The rest should be fairly short, but enough delay to see items disappear.*/
		public Ticker(int firstInteract, int rest) {
			this.first = firstInteract;
			this.rest = rest;
		}

		/** Returns whether or not this tick should interact*/
		public boolean tick() {
			ticks++;
			if (ticks <= first) {
				return ticks % first == 0;
			}
			if (ticks % rest == 0) {
				ticks = first;
				return true;
			}
			return false;
		}

		public void restart() {
			ticks = 0;
		}
	}
}
