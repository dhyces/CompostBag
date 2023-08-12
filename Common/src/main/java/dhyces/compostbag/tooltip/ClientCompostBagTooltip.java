package dhyces.compostbag.tooltip;

import dhyces.compostbag.Constants;
import dhyces.compostbag.item.CompostBagItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4f;

public class ClientCompostBagTooltip implements ClientTooltipComponent {
	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Constants.MOD_ID, "textures/gui/compost_bag_tooltip.png");

	private final ItemStack bonemeal = Items.BONE_MEAL.getDefaultInstance();
	private final int level;
	private final int count;

	public ClientCompostBagTooltip(CompostBagTooltip tooltip) {
		this.level = tooltip.getLevel();
		this.count = tooltip.getCount();
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
		return count + "/" + CompostBagItem.MAX_BONEMEAL_COUNT.get();
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
		this.blitBin(guiGraphics, x, y);
		this.blitFill(guiGraphics, x, y);

		int center = getWidth(font)-10 - (font.width(countText()) / 2);

		guiGraphics.renderItem(bonemeal, x+center, y-3);
	}

	private void blitFill(GuiGraphics guiGraphics, int x, int y) {
	    int renderLevel = level*2;
		guiGraphics.blit(TEXTURE_LOCATION, x, y+16-renderLevel, 0, 32-renderLevel, 16, renderLevel, 32, 32);
	}

	private void blitBin(GuiGraphics guiGraphics, int x, int y) {
		guiGraphics.blit(TEXTURE_LOCATION, x, y, 0, 0, 16, 16, 32, 32);
	}
}
