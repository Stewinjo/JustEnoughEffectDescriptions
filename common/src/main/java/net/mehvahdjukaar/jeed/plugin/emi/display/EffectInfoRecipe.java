package net.mehvahdjukaar.jeed.plugin.emi.display;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.mehvahdjukaar.jeed.Jeed;
import net.mehvahdjukaar.jeed.common.EffectWindowEntry;
import net.mehvahdjukaar.jeed.common.HSLColor;
import net.mehvahdjukaar.jeed.plugin.emi.EMIPlugin;
import net.mehvahdjukaar.jeed.plugin.emi.ingredient.EffectInstanceStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.mehvahdjukaar.jeed.common.Constants.*;

public class EffectInfoRecipe extends EffectWindowEntry implements EmiRecipe {

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiIngredient> slotsContent;
    private final EmiStack outputs;

    protected EffectInfoRecipe(MobEffectInstance effectInstance, Component description, ResourceLocation id) {
        super(effectInstance, List.of(description));
        this.id = id;
        this.outputs = new EffectInstanceStack(effectInstance);
        var ingredientsList = computeEffectProviders(effectInstance.getEffect())
                .stream().map(EmiIngredient::of).toList();
        List<EmiIngredient> allInputs = new ArrayList<>();
        allInputs.add(outputs);
        allInputs.addAll(ingredientsList);
        this.inputs = allInputs;
        var ingredientsPerSlot = divideIntoSlots(ingredientsList);
        this.slotsContent = ingredientsPerSlot.stream().map(EmiIngredient::of).toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIPlugin.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(outputs);
    }

    @Override
    public int getDisplayWidth() {
        return RECIPE_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return RECIPE_HEIGHT;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {

        MobEffect mobEffect = effect.getEffect();

        MutableComponent name = (MutableComponent) mobEffect.getDisplayName();
        int color = HSLColor.getProcessedColor(mobEffect.getColor());
        name.setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromRgb(color)));

        Font font = Minecraft.getInstance().font;
        int centerX = RECIPE_WIDTH / 2;
        int centerY = RECIPE_HEIGHT / 2;
        int x = (int) (centerX - font.width(name) / 2f);
        widgets.addText(name, x, 0, -1, true);
        //    widgets.addSlot(EffectInstanceStack.of(effect), 0, 0).large(true).recipeContext(this);

        if (Jeed.hasEffectBox()) {
            widgets.addTexture(ContainerScreen.INVENTORY_LOCATION, centerX - 12, Y_OFFSET,
                    24, 24, 141, 166);
        }

        widgets.add(new SlotWidget(outputs, centerX - 9, Y_OFFSET + 3)
                .drawBack(false));

        // widgets.add(new TextWidget())
        //  widgets.addText(EmiPort.ordered(EmiPort.translatable("emi.cooking.experience", recipe.m_43750_())), 26, 28, -1, true);
        // widgets.addSlot(input, 0, 4);
        // widgets.addSlot(output, 56, 0).large(true).recipeContext(this);

        int listH = EffectWindowEntry.getListHeight(slotsContent);

        // widgets.add(new ScrollableTextWidget(new Rectangle(bounds.x + SIZE_DIFF,
        //       rect2.getMaxY() + 1, bounds.width - 2 * SIZE_DIFF,
        //     50 + EffectWindow.MAX_BOX_HEIGHT - listH), display.getComponents()));

        if (listH != 0) {

            int rowsCount = slotsContent.size() <= SLOTS_PER_ROW ? 1 : ROWS;
            int size = SLOTS_PER_ROW * (slotsContent.size() <= SLOTS_PER_ROW ? 1 : ROWS);

            for (int slotId = 0; slotId < size; slotId++) {
                EmiIngredient ingredient;
                if (slotId < slotsContent.size()) {
                    ingredient = slotsContent.get(slotId);
                } else ingredient = EmiIngredient.of(Ingredient.EMPTY);

                int sx = -1 + (int) ((float)centerX + (float) ROWS + (SLOT_W * ((slotId % SLOTS_PER_ROW) - SLOTS_PER_ROW / 2f)));
                int sy = 1 + RECIPE_HEIGHT - SLOT_W * (rowsCount - (slotId / SLOTS_PER_ROW));

                SlotWidget slot = new SlotWidget(ingredient, sx, sy);
                widgets.add(slot);
            }
        }

    }

    public static EffectInfoRecipe create(MobEffect effect) {
        Component text = getDescription(effect);

        return new EffectInfoRecipe(new MobEffectInstance(effect), text, BuiltInRegistries.MOB_EFFECT.getKey(effect));
    }
}
