package dhyces.compostbag.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import dhyces.compostbag.CompostBag;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientCompostBagTooltip implements ClientTooltipComponent {

	public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(CompostBag.MODID, "textures/gui/compost_bag_tooltip.png");

	private ItemStack bonemeal;
	private int level;
	private int count;

	public ClientCompostBagTooltip(CompostBagTooltip tooltip) {
		this.bonemeal = tooltip.getBonemeal();
		this.level = tooltip.getLevel();
		this.count = tooltip.getCount();
	}

	@Override
	public int getHeight() {
		return 18;
	}

	@Override
	public int getWidth(Font font) {
		return 18;
	}

	@Override
	public void renderText(Font pFont, int pX, int pY, Matrix4f pMatrix4f, BufferSource pBufferSource) {
		if (count == 0)
			return;
		pFont.drawInBatch(countText(), pX+24, pY, 0xFFFFFF, false, pMatrix4f, pBufferSource, false, 12412, 0xFF);
	}

	private String countText() {
		return String.valueOf(count) + "x";
	}

	@Override
	public void renderImage(Font font, int x, int y, PoseStack pose, ItemRenderer renderer, int blitOffset) {
		this.blitBin(pose, x, y, blitOffset);
		if (count == 0 && level == 0)
			return;
		this.blitFill(pose, x, y, blitOffset);

		var width = font.width(countText());

		renderer.renderGuiItem(bonemeal, x+23+width , y-4);
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
