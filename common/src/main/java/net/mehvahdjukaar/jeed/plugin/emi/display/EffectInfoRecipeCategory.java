package net.mehvahdjukaar.jeed.plugin.emi.display;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.mehvahdjukaar.jeed.common.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EffectInfoRecipeCategory extends EmiRecipeCategory {

    public EffectInfoRecipeCategory(ResourceLocation id) {
        super(id, new TabIcon());
    }



    @Override
    public Component getName() {
        return Constants.LOCALIZED_NAME;
    }

}
