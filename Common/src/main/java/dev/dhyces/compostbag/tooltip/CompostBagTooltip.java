package dev.dhyces.compostbag.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class CompostBagTooltip implements TooltipComponent {

	private final int count;
	private final int level;

	public CompostBagTooltip(int level, int count) {
		this.level = level;
		this.count = count;
	}

	public int getLevel() {
		return this.level;
	}

	public int getCount() {
		return this.count;
	}
}
