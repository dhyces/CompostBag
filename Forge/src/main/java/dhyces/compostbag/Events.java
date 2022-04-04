package dhyces.compostbag;

import com.mojang.blaze3d.platform.InputConstants;
import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import dhyces.compostbag.util.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
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
		if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof CompostBagItem && !carried.isEmpty()) {
			bag = hoveredSlot.getItem();
		}
		else if (carried.getItem() instanceof CompostBagItem)
			bag = carried;

		if (bag.isEmpty())
			return;

		var pose = e.getPoseStack();
		var x = e.getMouseX()-screen.getGuiLeft();
		var y = e.getMouseY()-screen.getGuiTop();
		screen.renderTooltip(pose, screen.getTooltipFromItem(bag), bag.getTooltipImage(), x, y, bag);
	}

	static final Ticker TICKER = new Ticker(20, 9);

	@SubscribeEvent
	static void multiDrop(final ClientTickEvent event) {
		var clientPlayer = Minecraft.getInstance().player;
		var s = Minecraft.getInstance().screen;
		if (clientPlayer == null || s == null || !(s instanceof AbstractContainerScreen<?>))
			return;
		var mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
		if (mouseDown == GLFW.GLFW_RELEASE) {
			TICKER.restart();
		}
		var screen = (AbstractContainerScreen<?>) s;
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
}
