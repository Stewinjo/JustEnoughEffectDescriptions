package net.mehvahdjukaar.jeed.plugin.jei.ingredient;//


import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.mehvahdjukaar.jeed.plugin.jei.JEIPlugin;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.stream.Stream;

public class EffectInstanceHelper implements IIngredientHelper<MobEffectInstance> {

    @Override
    public IIngredientType<MobEffectInstance> getIngredientType() {
        return JEIPlugin.EFFECT_INGREDIENT_TYPE;
    }

    @Override
    public String getDisplayName(MobEffectInstance ingredient) {
        Component displayName = ingredient.getEffect().getDisplayName();
        return displayName.getString();
    }

    @Override
    public String getUniqueId(MobEffectInstance ingredient, UidContext uidContext) {
        ResourceLocation registryName = BuiltInRegistries.MOB_EFFECT.getKey(ingredient.getEffect());
        return "effect:" + registryName;
    }

    @Override
    public ResourceLocation getResourceLocation(MobEffectInstance ingredient) {
        ResourceLocation registryName = BuiltInRegistries.MOB_EFFECT.getKey(ingredient.getEffect());
        if (registryName == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("effect.getRegistryName() returned null for: " + ingredientInfo);
        } else {
            return registryName;
        }
    }

    @Override
    public Iterable<Integer> getColors(MobEffectInstance ingredient) {
        return Collections.singletonList(ingredient.getEffect().getColor());
    }

    @Override
    public ItemStack getCheatItemStack(MobEffectInstance ingredient) {
        var item = PotionUtils.setCustomEffects(new ItemStack(Items.POTION),
                Collections.singletonList(normalizeIngredient(ingredient)));
        item.getOrCreateTag().putInt("CustomPotionColor", ingredient.getEffect().getColor());
        return item;
    }

    @Override
    public Stream<ResourceLocation> getTagStream(MobEffectInstance ingredient) {
        return BuiltInRegistries.MOB_EFFECT
                .getResourceKey(ingredient.getEffect())
                .flatMap(BuiltInRegistries.MOB_EFFECT::getHolder)
                .map(Holder::tags)
                .orElse(Stream.of())
                .map(TagKey::location);
    }

    @Override
    public MobEffectInstance copyIngredient(MobEffectInstance ingredient) {
        return new MobEffectInstance(ingredient.getEffect(), ingredient.getDuration(), ingredient.getAmplifier(),
                ingredient.isAmbient(), ingredient.isVisible(), ingredient.showIcon(), ingredient.hiddenEffect, ingredient.getFactorData());
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
            MobEffect effect = ingredient.getEffect();
            if (effect != null) {
                Component displayName = ingredient.getEffect().getDisplayName();
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
