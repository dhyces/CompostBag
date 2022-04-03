package dhyces.compostbag.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dhyces.compostbag.item.CompostBagItem;
import dhyces.compostbag.tooltip.CompostBagTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(Screen.class)
public class GatherTooltipsMixin {

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void gatherTooltips(PoseStack poseStack, List<ClientTooltipComponent> list, int i, int j, CallbackInfo ci) {
        if (list.stream().anyMatch(c -> c instanceof CompostBagTooltip)) {
            return;
        }
        var mc = Minecraft.getInstance();
        if (mc.screen.children().stream().anyMatch(c -> c instanceof RecipeBookComponent) && mc.screen instanceof InventoryScreen screen) {
            if (screen.getMenu().getCarried().getItem() instanceof CompostBagItem) {
                ci.cancel();
            }
        }
    }
}
