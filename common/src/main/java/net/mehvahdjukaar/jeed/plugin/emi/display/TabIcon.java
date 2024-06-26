package net.mehvahdjukaar.jeed.plugin.emi.display;

import dev.emi.emi.api.render.EmiTexture;
import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class TabIcon extends EmiTexture {

    private static final ResourceLocation resource = Jeed.res("textures/gui/effects.png");

    public TabIcon() {
        super(resource, 0, 0, 15, 16, 15, 16, 15, 16);
    }

}
