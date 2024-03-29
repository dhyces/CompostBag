package dev.dhyces.compostbag;

import com.mojang.blaze3d.platform.InputConstants;
import dev.dhyces.compostbag.item.CompostBagItem;
import dev.dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import dev.dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class ClientEvents {

	static void init(IEventBus modBus, IEventBus forgeBus) {
		modBus.addListener(ClientEvents::clientSetup);
		modBus.addListener(ClientEvents::onAddToTabs);
		modBus.addListener(ClientEvents::registerTooltipComponents);

		forgeBus.addListener(ClientEvents::cancelOtherTooltips);
		forgeBus.addListener(ClientEvents::renderTooltipWhileHovering);
		forgeBus.addListener(ClientEvents::multiDrop);
		forgeBus.addListener(ClientEvents::cancelRightClickTick);
	}

	private static void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemProperties.register(Common.COMPOST_BAG_ITEM.get(), Common.modLoc("filled"), CompostBagItem::getFullnessDisplay);
		});
	}

	private static void onAddToTabs(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Common.COMPOST_BAG_ITEM.get());
		}
	}

	private static void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
		event.register(CompostBagTooltip.class, ClientCompostBagTooltip::new);
	}

	private static void cancelOtherTooltips(final RenderTooltipEvent.Pre event) {
		if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> screen) {
			if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem && event.getComponents().stream().noneMatch(c -> c instanceof ClientCompostBagTooltip)) {
				event.setCanceled(true);
			}
		}
	}

	private static void renderTooltipWhileHovering(final ContainerScreenEvent.Render.Foreground e) {
		AbstractContainerScreen<?> screen = e.getContainerScreen();

		ItemStack bag = ItemStack.EMPTY;

		Slot hoveredSlot = screen.getSlotUnderMouse();
		ItemStack carried = screen.getMenu().getCarried();
		if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof CompostBagItem && !carried.isEmpty()) {
			bag = hoveredSlot.getItem();
		}
		else if (carried.getItem() instanceof CompostBagItem)
			bag = carried;

		if (bag.isEmpty())
			return;

		Minecraft mc = Minecraft.getInstance();
		GuiGraphics guiGraphics = e.getGuiGraphics();
		int x = e.getMouseX()-screen.getGuiLeft();
		int y = e.getMouseY()-screen.getGuiTop();
		guiGraphics.renderTooltip(mc.font, bag, x, y);
	}


	private static void multiDrop(final TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		AbstractClientPlayer clientPlayer = mc.player;
		Screen s = mc.screen;
		if (event.phase.equals(TickEvent.Phase.END) || clientPlayer == null || !(s instanceof AbstractContainerScreen<?> screen))
			return;
		int mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
		if (mouseDown == GLFW.GLFW_PRESS) {
			Slot slot = screen.getSlotUnderMouse();
			// A note for the last check, this ensures that this doesn't continue in the case that the menu is a CreativeModeInventoryScreen#ItemPickerMenu.
			// See the implementation of canTakeItemForPickAll in the aforementioned menu class
			if (slot != null && slot.hasItem() && screen.getMenu().canTakeItemForPickAll(slot.getItem(), slot)) {
				ItemStack item = slot.getItem();
				ItemStack carried = screen.getMenu().getCarried();
				if (carried.isEmpty() || !ComposterBlock.COMPOSTABLES.containsKey(carried.getItem()))
					return;
				if (CommonClient.getTickerInstance().tick() && item.getItem() instanceof CompostBagItem) {
					if (screen instanceof CreativeModeInventoryScreen)
						screen.getMenu().clicked(slot.index == 0 ? slot.getContainerSlot() : slot.index, InputConstants.MOUSE_BUTTON_RIGHT, ClickType.PICKUP, clientPlayer);
					else
						mc.gameMode.handleInventoryMouseClick(screen.getMenu().containerId, slot.index, InputConstants.MOUSE_BUTTON_RIGHT, ClickType.PICKUP, clientPlayer);
				}
			}
		}
	}

	private static void cancelRightClickTick(final ScreenEvent.MouseButtonReleased.Pre event) {
		var screen = Minecraft.getInstance().screen;
		if (event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && screen instanceof AbstractContainerScreen<?> containerScreen) {
			if (CommonClient.getTickerInstance().inProgress()) {
				containerScreen.isQuickCrafting = false;
				event.setCanceled(true);
			}
			CommonClient.getTickerInstance().restart();
		}
	}
}
