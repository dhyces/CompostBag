package dhyces.compostbag.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import dhyces.compostbag.CompostBag;
import dhyces.compostbag.Registry;
import dhyces.compostbag.item.CompostBagItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientCompostBagTooltip implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CompostBag.MODID, "textures/gui/compost_bag_tooltip.png");

	private ItemStack bonemeal = Items.BONE_MEAL.getDefaultInstance();
	private int level;
	private int count;

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
		var title = new TranslatableComponent("item.compostbag.compost_bag");
		var titleWidth = font.width(title);
		var countTextWidth = neededTextWidth(font);
		var binWidth = 20;
		var extended = countTextWidth + binWidth < titleWidth ? titleWidth : titleWidth + (countTextWidth + binWidth - titleWidth);
		return extended;
	}

	@Override
	public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, BufferSource pBufferSource) {
		var text = allText();
		pFont.drawInBatch(text, pX + getWidth(pFont) - neededTextWidth(pFont), pY+13, count == 0 ? 0x999999 : 0xFFFFFF, false, pMatrix4f, pBufferSource, false, 0, 0xFF);
	}
	
	private String allText() {
		return countText() + "/" + maxText();
	}

	private String countText() {
		return String.valueOf(count);
	}
	
	private int neededTextWidth(Font font) {
		var t = maxText();
		return font.width(t + "/" + t);
	}
	
	private String maxText() {
		return String.valueOf(CompostBagItem.MAX_BONEMEAL_COUNT.get());
	}

	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int blitOffset) {
		this.blitBin(pose, x, y, blitOffset);
		this.blitFill(pose, x, y, blitOffset);

		int slash = getWidth(font) - (font.width(allText()) / 2);

		renderer.renderGuiItem(bonemeal, x+slash-10, y-3);
	}

	private void blitFill(PoseStack pose, int x, int y, int offset) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
	    var renderLevel = level*2;
	    GuiComponent.blit(pose, x, y, offset, 0, 16, 16, 16, 128, 128);
	    GuiComponent.blit(pose, x, y, offset, 0, 0, 16, 16-renderLevel, 128, 128);
	}

	private void blitBin(PoseStack pose, int x, int y, int offset) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	    RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
	    GuiComponent.blit(pose, x, y, offset, 0, 0, 16, 16, 128, 128);
	}
}
