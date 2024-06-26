package net.mehvahdjukaar.jeed.plugin.emi;

import dev.emi.emi.api.*;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.EmiStackInteraction;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.common.IPlugin;
import net.mehvahdjukaar.jeed.common.ScreenExtensionsHandler;
import net.mehvahdjukaar.jeed.plugin.emi.display.EffectInfoRecipe;
import net.mehvahdjukaar.jeed.plugin.emi.display.EffectInfoRecipeCategory;
import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectIngredientSerializer;
import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectInstanceStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin, IPlugin {

    public static final ResourceLocation EFFECTS_INFO_CATEGORY = Jeed.res("effects");
    public static final EffectInfoRecipeCategory CATEGORY = new EffectInfoRecipeCategory(EFFECTS_INFO_CATEGORY);

    public EMIPlugin() {
        Jeed.PLUGIN = this;
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(CATEGORY);
        Jeed.getEffectList().stream().map(MobEffectInstance::new)
                .map(EffectInstanceStack::new).forEach(registry::addEmiStack);
        for (MobEffect e : Jeed.getEffectList()) {
            registry.addRecipe(EffectInfoRecipe.create(e));
        }
        registry.addWorkstation(CATEGORY, EmiStack.of(Blocks.BEACON));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.SPLASH_POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.LINGERING_POTION));
        registry.addWorkstation(CATEGORY, EmiStack.of(Items.SUSPICIOUS_STEW));

        for (var e : ScreenExtensionsHandler.EXTENSIONS.entrySet()) {
            Class<? extends AbstractContainerScreen<?>> screenClass = (Class<? extends AbstractContainerScreen<?>>) e.getKey();
            IEffectScreenExtension<AbstractContainerScreen<?>> effect = (IEffectScreenExtension<AbstractContainerScreen<?>>) e.getValue();
            EmiStackProvider extension = new ScreenExtension<>(effect);

            registry.addStackProvider(screenClass, extension);
        }
    }

    @Override
    public void initialize(EmiInitRegistry registry) {
        registry.addRegistryAdapter(EmiRegistryAdapter.simple(MobEffect.class,
                BuiltInRegistries.MOB_EFFECT, (e, t, d) -> new EffectInstanceStack(e, d)));
        registry.addIngredientSerializer(EffectInstanceStack.class, new EffectIngredientSerializer());
    }

    @Override
    public void onClickedEffect(MobEffectInstance effect, double x, double y, int button) {
        EmiApi.getRecipeManager().getRecipesByInput(new EffectInstanceStack(effect))
                .stream().findFirst().ifPresent(EmiApi::displayRecipe);
    }

    public record ScreenExtension<T extends AbstractContainerScreen<?>>
            (IEffectScreenExtension<T> ext) implements EmiStackProvider<T> {

        @Override
        public EmiStackInteraction getStackAt(T screen, int x, int y) {
            var clicked = ext.getEffectAtPosition(screen, x, y, IEffectScreenExtension.CallReason.MOUSE_CLICKED);
            if (clicked != null) {
                return new EmiStackInteraction(new EffectInstanceStack(clicked));
            }
            return EmiStackInteraction.EMPTY;
        }
    }
}
