package net.mehvahdjukaar.jeed.plugin.jei.ingredient;//


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class EffectInstanceHelper implements IIngredientHelper<MobEffectInstance> {

    @Override
    public IIngredientType<MobEffectInstance> getIngredientType() {
        return JEIPlugin.EFFECT_INGREDIENT_TYPE;
    }

    @Override
    public String getDisplayName(MobEffectInstance ingredient) {
        Component displayName = ingredient.getEffect().value().getDisplayName();
        return displayName.getString();
    }

    @Override
    public String getUniqueId(MobEffectInstance ingredient, UidContext uidContext) {
        return "effect:" + getResourceLocation(ingredient);
    }

    @Override
    public ResourceLocation getResourceLocation(MobEffectInstance ingredient) {
        return ingredient.getEffect().unwrapKey().get().location();
    }

    @Override
    public Iterable<Integer> getColors(MobEffectInstance ingredient) {
        return Collections.singletonList(ingredient.getEffect().value().getColor());
    }

    @Override
    public ItemStack getCheatItemStack(MobEffectInstance ingredient) {
        ItemStack item = new ItemStack(Items.POTION);
        PotionContents potionContents = new PotionContents(Optional.empty(),
                Optional.of(ingredient.getEffect().value().getColor()),
                Collections.singletonList(normalize(ingredient)));
        item.set(DataComponents.POTION_CONTENTS, potionContents);
        return item;
    }

    public MobEffectInstance normalize(MobEffectInstance value) {
        return new MobEffectInstance(value.getEffect(), 30 * 20, 0, value.isAmbient(),
                value.isVisible(), value.showIcon(), value.hiddenEffect);
    }

    @Override
    public Stream<ResourceLocation> getTagStream(MobEffectInstance ingredient) {
        return ingredient.getEffect().tags().map(TagKey::location);
    }

    @Override
    public MobEffectInstance copyIngredient(MobEffectInstance ingredient) {
        return new MobEffectInstance(ingredient.getEffect(), ingredient.getDuration(), ingredient.getAmplifier(),
                ingredient.isAmbient(), ingredient.isVisible(), ingredient.showIcon(), ingredient.hiddenEffect);
    }

    @Override
    public MobEffectInstance normalizeIngredient(MobEffectInstance ingredient) {
        return new MobEffectInstance(ingredient.getEffect(), 30 * 20);
    }

    @Override
    public String getErrorInfo(@Nullable MobEffectInstance ingredient) {
        if (ingredient == null) {
            return "null";
        } else {
            ToStringHelper toStringHelper = MoreObjects.toStringHelper(EffectInstance.class);
            MobEffect effect = ingredient.getEffect().value();
            if (effect != null) {
                Component displayName = effect.getDisplayName();
                toStringHelper.add("Effect", displayName.getString());
            } else {
                toStringHelper.add("Effect", "null");
            }

            toStringHelper.add("Duration", ingredient.getDuration());
            toStringHelper.add("Amplifier", ingredient.getAmplifier());

            return toStringHelper.toString();
        }
    }
}
