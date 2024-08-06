package dev.dhyces.compostbag.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record CompostBagTooltip(int maxCount, int count, int maxLevel, int level) implements TooltipComponent {
}
