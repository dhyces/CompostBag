package dhyces.compostbag.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.ClientCompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    public void compostbag_gatherTooltips(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
        if (!list.stream().anyMatch(c -> c instanceof ClientCompostBagTooltip) && Minecraft.getInstance().screen instanceof AbstractContainerScreen screen) {
            if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem) {
                ci.cancel();
            }
        }
    }
}
