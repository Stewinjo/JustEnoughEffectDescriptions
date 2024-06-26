package net.mehvahdjukaar.jeed.common;

import net.mehvahdjukaar.jeed.Jeed;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public final class Constants {
    public static int RECIPE_WIDTH = Jeed.EMI ? 134 : 160;
    public static int RECIPE_HEIGHT = Jeed.EMI ? 130 : 125;
    public static int LINE_SPACING = 2;
    public static int SLOT_W = 19;
    public static int ROWS = 2;
    public static int MAX_BOX_HEIGHT = (SLOT_W * ROWS) + 2;

    public static int SLOTS_PER_ROW = Jeed.EMI ? 7 : 8;

    public static int Y_OFFSET = 12;

    public static Component LOCALIZED_NAME = Component.translatable("jeed.category.effect_info");

    public static Comparator<ResourceLocation> ID_COMPARATOR = (first, second) -> {
        String secondNamespace = second.getNamespace();
        String firstNamespace = first.getNamespace();
        String mc = "minecraft";
        if (mc.equals(firstNamespace) && !mc.equals(secondNamespace)) {
            return -1;
        } else if (!mc.equals(firstNamespace) && mc.equals(secondNamespace)) {
            return 1;
        } else {
            int pathComparison = firstNamespace.compareTo(secondNamespace);
            if (pathComparison != 0) {
                return pathComparison;
            } else {
                return second.getPath().compareTo(second.getPath());
            }
        }
    };
}
