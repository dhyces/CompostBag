package dhyces.compostbag.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class CompostBagTooltip implements TooltipComponent {
	
	private ItemStack stack;
	private int count;
	private int level;
	
	public CompostBagTooltip(ItemStack bonemeal, int level, int count) {
		this.stack = bonemeal;
		this.level = level;
		this.count = count;
	}
	
	public ItemStack getBonemeal() {
		return this.stack;
	}
	
	public int getLevel() {
		return this.level;
	}

	public int getCount() {
		return this.count;
	}
}
