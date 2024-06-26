package net.mehvahdjukaar.jeed.plugin.emi.ingredient;

import dev.emi.emi.api.stack.EmiStack;
import net.mehvahdjukaar.jeed.common.EffectRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EffectInstanceStack extends EmiStack {
    private static final EffectRenderer RENDERER = new EffectRenderer(false) {
    };

    private final MobEffectInstance effect;

    public EffectInstanceStack(MobEffectInstance effect) {
        this.effect = effect;
    }

    public EffectInstanceStack(MobEffect effect, long duration) {
        this(new MobEffectInstance(effect, (int) duration));
    }


    public MobEffectInstance getEffect() {
        return effect;
    }

    @Override
    public EmiStack copy() {
        return new EffectInstanceStack(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(),
                effect.isAmbient(), effect.isVisible(), effect.showIcon(), effect.hiddenEffect, effect.getFactorData()));
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        RENDERER.render(draw, effect, x - 1, y - 1, 16, 16);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return effect.getEffect();
    }

    @Override
    public ResourceLocation getId() {
        return BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect());
    }

    @Override
    public List<Component> getTooltipText() {
        return EffectRenderer.getTooltipsWithDescription(effect, TooltipFlag.NORMAL, false, false);
    }

    @Override
    public Component getName() {
        return effect.getEffect().getDisplayName();
    }


}
