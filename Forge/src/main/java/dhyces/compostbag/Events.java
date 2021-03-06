package dhyces.compostbag;

import com.mojang.blaze3d.platform.InputConstants;
import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class Events {

    @SubscribeEvent
    static void cancelOtherTooltips(final RenderTooltipEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof AbstractContainerScreen screen) {
            if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem && !event.getComponents().stream().anyMatch(c -> c instanceof ClientCompostBagTooltip)) {
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
        } else if (carried.getItem() instanceof CompostBagItem)
            bag = carried;

        if (bag.isEmpty())
            return;

        var pose = e.getPoseStack();
        var x = e.getMouseX() - screen.getGuiLeft();
        var y = e.getMouseY() - screen.getGuiTop();
        screen.renderTooltip(pose, screen.getTooltipFromItem(bag), bag.getTooltipImage(), x, y, bag);
    }


    @SubscribeEvent
    static void multiDrop(final ClientTickEvent event) {
        var mc = Minecraft.getInstance();
        var clientPlayer = mc.player;
        var s = mc.screen;
        if (event.phase.equals(TickEvent.Phase.END) || clientPlayer == null || s == null || !(s instanceof AbstractContainerScreen<?>))
            return;
        var mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
        var screen = (AbstractContainerScreen<?>) s;
        if (mouseDown == GLFW.GLFW_PRESS) {
            var slot = screen.getSlotUnderMouse();
            // A note for the last check, this ensures that this doesn't continue in the case that the menu is a CreativeModeInventoryScreen#ItemPickerMenu.
            // See the implementation of canTakeItemForPickAll in the aforementioned menu class
            if (slot != null && slot.hasItem() && screen.getMenu().canTakeItemForPickAll(slot.getItem(), slot)) {
                var item = slot.getItem();
                var carried = screen.getMenu().getCarried();
                if (carried == null || carried.isEmpty() || !ComposterBlock.COMPOSTABLES.containsKey(carried.getItem()))
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

    @SubscribeEvent
    static void cancelRightClickTick(final ScreenEvent.MouseReleasedEvent.Pre event) {
        var screen = Minecraft.getInstance().screen;
        if (event.getButton() == InputConstants.MOUSE_BUTTON_RIGHT && screen instanceof AbstractContainerScreen containerScreen) {
            if (CommonClient.getTickerInstance().inProgress()) {
                containerScreen.isQuickCrafting = false;
                event.setCanceled(true);
            }
            CommonClient.getTickerInstance().restart();
        }
    }
}
