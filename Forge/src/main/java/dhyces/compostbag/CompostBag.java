package dhyces.compostbag;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class CompostBag {
    
    public CompostBag() {
        Common.init();

        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
        
    }
    
    private void onItemTooltip(ItemTooltipEvent event) {
        
        Common.onItemTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}