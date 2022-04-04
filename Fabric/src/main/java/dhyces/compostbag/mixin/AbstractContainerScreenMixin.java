package dhyces.compostbag.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.compostbag.item.CompostBagItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dhyces.compostbag.CompostBag.TICKER;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow protected int topPos;

    @Shadow protected int leftPos;

    @Shadow protected abstract void slotClicked(Slot $$0, int $$1, int $$2, ClickType $$3);

    @Shadow protected boolean isQuickCrafting;

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void multiDrop(CallbackInfo ci) {
        var mc = Minecraft.getInstance();
        var clientPlayer = mc.player;
        var screen = ((AbstractContainerScreen)(Object)this);
        if (clientPlayer == null)
            return;
        var mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
        if (mouseDown == GLFW.GLFW_PRESS) {
            var slot = getHoveredSlot();
            if (slot != null && slot.hasItem()) {
                var item = slot.getItem();
                var carried = screen.getMenu().getCarried();
                if (carried == null || carried.isEmpty() || !ComposterBlock.COMPOSTABLES.containsKey(carried.getItem()))
                    return;
                if (TICKER.tick() && item.getItem() instanceof CompostBagItem) {
                    slotClicked(slot, slot.index, mouseDown, ClickType.PICKUP);
                }
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void cancelTickerClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && mouseDown == GLFW.GLFW_RELEASE) {
            if (TICKER.inProgress()) {
                isQuickCrafting = false;
                cir.setReturnValue(true);
            }
            TICKER.restart();
        }
    }
}
