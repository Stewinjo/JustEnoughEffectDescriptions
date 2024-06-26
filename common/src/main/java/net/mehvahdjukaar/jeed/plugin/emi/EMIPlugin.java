package net.mehvahdjukaar.jeed.plugin.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.plugin.emi.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.emi.display.EffectInfoRecipeCategory;
import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectInstanceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    public static final ResourceLocation EFFECTS_INFO_CATEGORY = Jeed.res("effects");
    public static final EffectInfoRecipeCategory CATEGORY = new EffectInfoRecipeCategory(EFFECTS_INFO_CATEGORY);


    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        Jeed.getEffectList().stream().map(MobEffectInstance::new)
                .map(EffectInstanceStack::new).forEach(registry::addEmiStack);
        for (MobEffect e : Jeed.getEffectList()) {
            registry.addRecipe(EffectInfoRecipe.create(e));
        }
    }
}
