package net.mehvahdjukaar.jeed.plugin.emi.ingredient;

import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.runtime.EmiLog;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EffectIngredientSerializer implements EmiIngredientSerializer<EffectInstanceStack> {
    @Override
    public String getType() {
        return "mob_effect";
    }

    @Override
    public EmiIngredient deserialize(JsonElement element) {
        try {
            return CODEC.decode(JsonOps.INSTANCE, element).result().map(a -> new EffectInstanceStack(a.getFirst()))
                    .orElseThrow(DecoderException::new);
        } catch (DecoderException e) {
            EmiLog.error("Error parsing mob effect");
            EmiLog.error(e);
            return EmiStack.EMPTY;
        }
    }

    @Override
    public JsonElement serialize(EffectInstanceStack stack) {
        try {
            return CODEC.encodeStart(JsonOps.INSTANCE, stack.getEffect()).result().orElseThrow(EncoderException::new);
        } catch (EncoderException e) {
            EmiLog.error("Error encoding mob effect");
            EmiLog.error(e);
            return new JsonObject();
        }
    }


    public static final MapCodec<MobEffectInstance> MAP_CODEC = new RecursiveMapCodec<>("MobEffectInstance.Details",
            (codec) -> RecordCodecBuilder.mapCodec((instance) -> instance.group(
                    BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("id").forGetter(MobEffectInstance::getEffect),
                    Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance::getDuration),
                    Codec.INT.optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
                    Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
                    Codec.BOOL.optionalFieldOf("visible", true).forGetter(MobEffectInstance::isVisible),
                    Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(MobEffectInstance::showIcon),
                    codec.optionalFieldOf("hidden_effect").forGetter(e -> Optional.ofNullable(e.hiddenEffect)),
                    MobEffectInstance.FactorData.CODEC.optionalFieldOf("factor_data").forGetter(MobEffectInstance::getFactorData)
            ).apply(instance, EffectIngredientSerializer::create)));
    public static final Codec<MobEffectInstance> CODEC = MAP_CODEC.codec();


    private static MobEffectInstance create(MobEffect mobEffect, Integer integer, Integer integer1, Boolean aBoolean, Boolean aBoolean1, Boolean aBoolean2, Optional<MobEffectInstance> mobEffectInstance, Optional<MobEffectInstance.FactorData> factorData) {
        return new MobEffectInstance(mobEffect, integer, integer1, aBoolean, aBoolean1, aBoolean2, mobEffectInstance.orElse(null), factorData);
    }


    private static class RecursiveMapCodec<A> extends MapCodec<A> {
        private final String name;
        private final Supplier<MapCodec<A>> wrapped;

        private RecursiveMapCodec(final String name, final Function<Codec<A>, MapCodec<A>> wrapped) {
            this.name = name;
            this.wrapped = Suppliers.memoize(() -> wrapped.apply(codec()));
        }

        @Override
        public <T> RecordBuilder<T> encode(final A input, final DynamicOps<T> ops, final RecordBuilder<T> prefix) {
            return wrapped.get().encode(input, ops, prefix);
        }

        @Override
        public <T> DataResult<A> decode(final DynamicOps<T> ops, final MapLike<T> input) {
            return wrapped.get().decode(ops, input);
        }

        @Override
        public <T> Stream<T> keys(final DynamicOps<T> ops) {
            return wrapped.get().keys(ops);
        }

        @Override
        public String toString() {
            return "RecursiveMapCodec[" + name + ']';
        }
    }

}
