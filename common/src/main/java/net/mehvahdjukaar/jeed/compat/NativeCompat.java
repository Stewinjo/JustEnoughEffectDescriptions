package net.mehvahdjukaar.jeed.compat;


import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.api.IEffectScreenExtension;
import net.mehvahdjukaar.jeed.api.JeedAPI;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;

public class NativeCompat {

    private static MobEffectInstance inventoryHoveredEffect;
    private static boolean small = false;

    public static void init() {
        JeedAPI.registerScreenExtension(EffectRenderingInventoryScreen.class, INVENTORY_EXTENSION);
        JeedAPI.registerScreenExtension(CreativeModeInventoryScreen.class, INVENTORY_EXTENSION);
        JeedAPI.registerScreenExtension(InventoryScreen.class, INVENTORY_EXTENSION);
        JeedAPI.registerScreenExtension(BeaconScreen.class, BEACON_EXTENSION);
    }

    private static final IEffectScreenExtension<BeaconScreen> BEACON_EXTENSION = (screen, mouseX, mouseY, reason) -> {
        if (reason != IEffectScreenExtension.CallReason.MOUSE_CLICKED) {
            for (var b : screen.beaconButtons) {
                if (b instanceof BeaconScreen.BeaconPowerButton pb) {
                    if (pb.isHovered()) {
                        int tier = b.getClass() != BeaconScreen.BeaconPowerButton.class ? 1 : 0;
                        return new MobEffectInstance(pb.effect, 0, tier);
                    }
                }
            }
        }
        return null;
    };


    @SuppressWarnings("all")
    public static final IEffectScreenExtension INVENTORY_EXTENSION = new IEffectScreenExtension< EffectRenderingInventoryScreen>() {

        @Nullable
        @Override
        public MobEffectInstance getEffectAtPosition(EffectRenderingInventoryScreen screen, double mouseX, double mouseY, CallReason reason) {
            if (!reason.isForRender() || (screen.hoveredSlot == null && screen.getMenu().getCarried().isEmpty())) {
                if (small && reason == IEffectScreenExtension.CallReason.TOOLTIP && !Jeed.suppressVanillaTooltips()) {
                    return null;
                }
                return inventoryHoveredEffect;
            }
            return null;
        }

        @Override
        public boolean showDurationOnTooltip() {
            return small;
        }
    };


    public static void setInventoryEffect(@Nullable MobEffectInstance hoveredEffect, boolean isSmall) {
        inventoryHoveredEffect = hoveredEffect;
        small = isSmall;
    }

}