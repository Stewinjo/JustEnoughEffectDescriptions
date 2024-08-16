package net.mehvahdjukaar.jeed.recipes;


import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

//items that can accept any potion
public record PotionProviderRecipe(List<Holder<Potion>> potions,
                                   NonNullList<Ingredient> providers) implements Recipe<SingleRecipeInput> {

    @Override
    public String getGroup() {
        return "potion_provider";
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return providers;
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Jeed.getPotionProviderSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return Jeed.getPotionProviderType();
    }

    public List<? extends Holder<Potion>> getPotions() {
        return potions.isEmpty() ? BuiltInRegistries.POTION.holders().toList() : potions;
    }

    public static class Serializer implements RecipeSerializer<PotionProviderRecipe> {

        private static final MapCodec<PotionProviderRecipe> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                BuiltInRegistries.POTION.holderByNameCodec().listOf().optionalFieldOf("potions", List.of()).forGetter((r) -> r.potions),
                CodecUtil.INGREDIENT_WITH_TAG.listOf().fieldOf("providers").flatXmap((list) -> {
                    Ingredient[] ingredients = list.stream().filter((ingredient) -> !ingredient.isEmpty()).toArray(Ingredient[]::new);
                    if (ingredients.length == 0) {
                        return DataResult.error(() -> "No providers for potion providers recipe");
                    } else {
                        return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                    }
                }, DataResult::success).forGetter((r) -> r.providers)
        ).apply(instance, PotionProviderRecipe::new));


        public static final StreamCodec<RegistryFriendlyByteBuf, PotionProviderRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.holderRegistry(Registries.POTION).apply(ByteBufCodecs.list()), PotionProviderRecipe::potions,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection(NonNullList::createWithCapacity)), PotionProviderRecipe::providers,
                PotionProviderRecipe::new);

        @Override
        public MapCodec<PotionProviderRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PotionProviderRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
