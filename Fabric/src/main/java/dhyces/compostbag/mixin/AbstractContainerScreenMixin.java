package dhyces.compostbag.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.util.Ticker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow protected int topPos;

    @Shadow protected int leftPos;

    @Accessor(value = "hoveredSlot")
    public abstract Slot getHoveredSlot();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", shift = At.Shift.AFTER))
    private void renderTooltipWhileHovering(PoseStack poseStack, int mouseX, int mouseY, float f, CallbackInfo ci) {
        var screen = ((AbstractContainerScreen)(Object)this);

        var bag = ItemStack.EMPTY;

        var hoveredSlot = getHoveredSlot();
        var carried = screen.getMenu().getCarried();
        if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof CompostBagItem && !carried.isEmpty()) {
            bag = hoveredSlot.getItem();
        }
        else if (carried.getItem() instanceof CompostBagItem)
            bag = carried;

        if (bag.isEmpty())
            return;

        var x = mouseX-leftPos;
        var y = mouseY-topPos;
        screen.renderTooltip(poseStack, screen.getTooltipFromItem(bag), bag.getTooltipImage(), x, y);
    }

    private static final Ticker TICKER = new Ticker(20, 9);

    @Inject(method = "tick", at = @At("TAIL"))
    private void multiDrop(CallbackInfo ci) {
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
            var slot = getHoveredSlot();
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
