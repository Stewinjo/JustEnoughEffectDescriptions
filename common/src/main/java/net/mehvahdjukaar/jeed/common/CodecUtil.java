package net.mehvahdjukaar.jeed.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CodecUtil {

    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = RecordCodecBuilder.create((i) -> i.group(
                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item")
                            .forGetter(ItemStack::getItemHolder),
                    DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                            .forGetter(ItemStack::getComponentsPatch))
            .apply(i, (holder, dataComponentPatch) -> new ItemStack(holder, 1, dataComponentPatch))
    );

    public static final Codec<Ingredient> INGREDIENT_WITH_TAG = Codec.withAlternative(
            Ingredient.CODEC_NONEMPTY,
            SINGLE_ITEM_CODEC,
            Ingredient::of
    );

}
