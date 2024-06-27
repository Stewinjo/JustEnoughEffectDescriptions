package net.mehvahdjukaar.jeed.plugin.jei.display;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.drawable.IDrawable;
import net.mehvahdjukaar.jeed.common.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class EffectBox implements IDrawable {

    private final int width, height;

    public EffectBox() {
        this.width = 24;
        this.height = 24;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        graphics.blitSprite(Constants.EFFECT_BACKGROUND_SMALL_SPRITE, xOffset, yOffset, width, height);
    }

}
