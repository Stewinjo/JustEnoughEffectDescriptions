package net.mehvahdjukaar.jeed.plugin.rei.display;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.mehvahdjukaar.jeed.common.EffectInfo;
import net.mehvahdjukaar.jeed.plugin.rei.REIPlugin;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EffectInfoDisplay extends EffectInfo implements Display {

    private final List<EntryIngredient> inputEntries;
    private final List<List<EntryStack<?>>> slots;
    private final List<EntryIngredient> outputEntries;

    protected EffectInfoDisplay(MobEffectInstance effectInstance, Component description) {
        super(effectInstance, List.of(description));
        MobEffect effect = effectInstance.getEffect().value();
        List<ItemStack> providers = computeEffectProviders(effect);
        var ingredientsList = groupIngredients(providers);
        var allInputs = new ArrayList<>(ingredientsList.stream().map(EntryIngredients::ofIngredient).toList());
        this.outputEntries = List.of(EntryIngredient.of(EntryStack.of(REIPlugin.EFFECT_ENTRY_TYPE, effectInstance).normalize()));
        allInputs.addAll(outputEntries);

        allInputs.addAll(computeEffectToEffectProviders(effect).stream()
                .map(e -> EntryStack.of(REIPlugin.EFFECT_ENTRY_TYPE, effectInstance).normalize()).map(EntryIngredient::of).toList());

        this.inputEntries = allInputs.stream().toList();
        this.slots = divideIntoSlots(providers, EntryIngredients::ofItemStacks);
    }

    public List<List<EntryStack<?>>> getSlots() {
        return slots;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputEntries;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputEntries;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.EFFECTS_INFO_CATEGORY;
    }

    public static EffectInfoDisplay create(Holder<MobEffect> effect) {
        Component text = getDescription(effect);

        return new EffectInfoDisplay(new MobEffectInstance(effect), text);
    }
}
