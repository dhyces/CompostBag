package dev.dhyces.compostbag.tooltip;

import dev.dhyces.compostbag.CompostBag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

public record ClientCompostBagTooltip(int maxCount, int count, int maxLevel, int level) implements ClientTooltipComponent {
	public static final ResourceLocation TEXTURE_LOCATION = CompostBag.id("textures/gui/compost_bag_tooltip.png");
	private static final ItemStack BONEMEAL = Items.BONE_MEAL.getDefaultInstance();

	public ClientCompostBagTooltip(CompostBagTooltip tooltip) {
		this(tooltip.maxCount(), tooltip.count(), tooltip.maxLevel(), tooltip.level());
	}

	@Override
	public int getHeight() {
		return 24;
	}

	@Override
	public int getWidth(Font font) {
		Component title = Component.translatable("item.compostbag.compost_bag");
		int titleWidth = font.width(title);
		int countTextWidth = font.width(countText());
		int binWidth = 20;
		return Math.max(countTextWidth + binWidth, titleWidth);
	}

	@Override
	public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, BufferSource pBufferSource) {
		String text = countText();
		pFont.drawInBatch(text, pX + getWidth(pFont) - pFont.width(text), pY + 13, count == 0 ? 0x999999 : 0xFFFFFF, false, pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 0xFF, false);
	}

	private String countText() {
		return count + "/" + maxCount;
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		if (maxLevel > 0) {
			this.blitBin(guiGraphics, x, y);
			this.blitFill(guiGraphics, x, y);
		}

		int center = getWidth(font)-10 - (font.width(countText()) / 2);

		guiGraphics.renderItem(BONEMEAL, x+center, y-3);
	}

	private void blitFill(GuiGraphics guiGraphics, int x, int y) {
	    int renderLevel = (int)Math.ceil(((double) level/maxLevel)*16d);
		guiGraphics.blit(TEXTURE_LOCATION, x, y+16-renderLevel, 0, 32-renderLevel, 16, renderLevel, 32, 32);
	}

	private void blitBin(GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.blit(TEXTURE_LOCATION, x, y, 0, 0, 16, 16, 32, 32);
	}
}
