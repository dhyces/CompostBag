package dhyces.compostbag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.glfw.GLFW;

import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CompostBag.MODID)
public class Events {
	
	@SubscribeEvent
	static void getToCancelTooltips(final RenderTooltipEvent event) {
		
	}

	@SubscribeEvent
	static void cancelOtherTooltips(final RenderTooltipEvent.GatherComponents event) {
		if (event.getTooltipElements().get(0).right().map(c -> c instanceof ClientCompostBagTooltip).orElse(false)) {
			return;
		}
		var mc = Minecraft.getInstance();
		if (mc.screen.getFocused() instanceof RecipeBookComponent && mc.screen instanceof AbstractContainerScreen<?> screen) {
			if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem && Screen.hasControlDown()) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	static void renderTooltipWhileHovering(final ContainerScreenEvent.DrawForeground e) {
		var clientPlayer = Minecraft.getInstance().player;
		var carried = clientPlayer.inventoryMenu.getCarried();
		if (!Screen.hasControlDown() || carried.isEmpty() || !carried.is(CompostBag.RegistryEvents.COMPOST_BAG))
			return;
		
		var screen = e.getContainerScreen();
		var pose = e.getPoseStack();
		screen.renderTooltip(pose, screen.getTooltipFromItem(carried), carried.getTooltipImage(), e.getMouseX()-screen.getGuiLeft(), e.getMouseY()-screen.getGuiTop(), carried);
	}
	
	static final Ticker TICKER = new Ticker(50, 40);
	
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
					var x = Minecraft.getInstance().mouseHandler.xpos() * Minecraft.getInstance().getWindow().getGuiScaledWidth() / Minecraft.getInstance().getWindow().getScreenWidth();
					var y = Minecraft.getInstance().mouseHandler.ypos() * Minecraft.getInstance().getWindow().getGuiScaledHeight() / Minecraft.getInstance().getWindow().getScreenHeight();
					screen.mouseReleased(x, y, 1);
				}
			}
		}
	}
	
	private static SlotAccess createSlotAccess(AbstractContainerScreen<?> container) {
		return new SlotAccess() {
			public ItemStack get() {
				return container.getMenu().getCarried();
			}
			
			public boolean set(ItemStack p_150452_) {
				container.getMenu().setCarried(p_150452_);
				return true;
			}
		};
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
