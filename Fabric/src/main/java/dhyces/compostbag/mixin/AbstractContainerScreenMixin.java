package dhyces.compostbag.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.compostbag.CommonClient;
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

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {

    @Shadow
    protected int topPos;

    @Shadow
    protected int leftPos;
    @Shadow
    protected boolean isQuickCrafting;

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotIndex, int mouseButton, ClickType clickType);

    @Accessor(value = "hoveredSlot")
    public abstract Slot getHoveredSlot();

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderLabels(Lcom/mojang/blaze3d/vertex/PoseStack;II)V", shift = At.Shift.AFTER))
    public void compostbag$renderTooltipWhileHovering(PoseStack poseStack, int mouseX, int mouseY, float f, CallbackInfo ci) {
        var screen = ((AbstractContainerScreen) (Object) this);

        var bag = ItemStack.EMPTY;

        var hoveredSlot = getHoveredSlot();
        var carried = screen.getMenu().getCarried();
        if (hoveredSlot != null && hoveredSlot.getItem().getItem() instanceof CompostBagItem && !carried.isEmpty()) {
            bag = hoveredSlot.getItem();
        } else if (carried.getItem() instanceof CompostBagItem)
            bag = carried;

        if (bag.isEmpty())
            return;

        var x = mouseX - leftPos;
        var y = mouseY - topPos;
        screen.renderTooltip(poseStack, screen.getTooltipFromItem(bag), bag.getTooltipImage(), x, y);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void compostbag$multiDrop(CallbackInfo ci) {
        var mc = Minecraft.getInstance();
        var clientPlayer = mc.player;
        var screen = ((AbstractContainerScreen) (Object) this);
        if (clientPlayer == null)
            return;
        var mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
        if (mouseDown == GLFW.GLFW_PRESS) {
            var slot = getHoveredSlot();
            // A note for the last check, this ensures that this doesn't continue in the case that the menu is a CreativeModeInventoryScreen#ItemPickerMenu.
            // See the implementation of canTakeItemForPickAll in the aforementioned menu class
            if (slot != null && slot.hasItem() && screen.getMenu().canTakeItemForPickAll(slot.getItem(), slot)) {
                var item = slot.getItem();
                var carried = screen.getMenu().getCarried();
                if (carried == null || carried.isEmpty() || !ComposterBlock.COMPOSTABLES.containsKey(carried.getItem()))
                    return;
                if (CommonClient.getTickerInstance().tick() && item.getItem() instanceof CompostBagItem) {
                    slotClicked(slot, slot.index, mouseDown, ClickType.PICKUP);
                }
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    public void compostbag$cancelTickerClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        var mouseDown = GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), InputConstants.MOUSE_BUTTON_RIGHT);
        if (button == InputConstants.MOUSE_BUTTON_RIGHT && mouseDown == GLFW.GLFW_RELEASE) {
            if (CommonClient.getTickerInstance().inProgress()) {
                isQuickCrafting = false;
                cir.setReturnValue(true);
            }
            CommonClient.getTickerInstance().restart();
        }
    }
}
