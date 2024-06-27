package net.mehvahdjukaar.jeed.plugin.emi.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiIngredientSerializer;
import dev.emi.emi.runtime.EmiLog;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.world.effect.MobEffectInstance;

public class EffectIngredientSerializer implements EmiIngredientSerializer<EffectInstanceStack> {
    @Override
    public String getType() {
        return "mob_effect";
    }

    @Override
    public EmiIngredient deserialize(JsonElement element) {
        try {
            return MobEffectInstance.CODEC.decode(JsonOps.INSTANCE, element).result().map(a -> new EffectInstanceStack(a.getFirst()))
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
            return MobEffectInstance.CODEC.encodeStart(JsonOps.INSTANCE, stack.getEffect()).result().orElseThrow(EncoderException::new);
        } catch (EncoderException e) {
            EmiLog.error("Error encoding mob effect");
            EmiLog.error(e);
            return new JsonObject();
        }
    }

}
