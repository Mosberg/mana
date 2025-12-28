package dk.mosberg.client.config;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dk.mosberg.Mana;
import dk.mosberg.config.ManaConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * ModMenu integration for Mana System configuration. Provides a graphical interface for adjusting
 * all configuration options using Cloth Config API.
 *
 * <p>
 * Requires ModMenu and Cloth Config to be installed.
 */
public class ManaModMenu implements ModMenuApi {

        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
                return ManaModMenu::createConfigScreen;
        }

        /**
         * Creates the configuration screen with all settings organized into categories.
         *
         * @param parent The parent screen to return to
         * @return The configuration screen
         */
        @NotNull
        private static Screen createConfigScreen(Screen parent) {
                ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
                                .setTitle(Text.translatable("mana.config.title"))
                                .setSavingRunnable(() -> {
                                        try {
                                                ManaConfig.save();
                                                Mana.LOGGER.info(
                                                                "Configuration saved successfully");
                                        } catch (IOException e) {
                                                Mana.LOGGER.error("Failed to save configuration",
                                                                e);
                                        }
                                });

                // Create entry builder
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();

                // === HUD Overlay Category ===
                ConfigCategory overlayCategory = builder.getOrCreateCategory(
                                Text.translatable("mana.config.category.overlay"));

                // Overlay Enabled
                overlayCategory.addEntry(entryBuilder
                                .startBooleanToggle(
                                                Text.translatable("mana.config.overlay.enabled"),
                                                ManaConfig.isOverlayEnabled())
                                .setDefaultValue(true)
                                .setTooltip(Text.translatable(
                                                "mana.config.overlay.enabled.tooltip"))
                                .setSaveConsumer(ManaConfig::setOverlayEnabled).build());

                // Overlay Scale
                overlayCategory.addEntry(entryBuilder
                                .startDoubleField(Text.translatable("mana.config.overlay.scale"),
                                                ManaConfig.getOverlayScale())
                                .setDefaultValue(1.0).setMin(0.5).setMax(2.0)
                                .setTooltip(Text.translatable("mana.config.overlay.scale.tooltip"))
                                .setSaveConsumer(ManaConfig::setOverlayScale).build());

                // Horizontal Offset
                overlayCategory.addEntry(entryBuilder
                                .startIntField(Text.translatable("mana.config.overlay.xOffset"),
                                                ManaConfig.getOverlayXOffset())
                                .setDefaultValue(0)
                                .setTooltip(Text.translatable(
                                                "mana.config.overlay.xOffset.tooltip"))
                                .setSaveConsumer(ManaConfig::setOverlayXOffset).build());

                // Vertical Offset
                overlayCategory.addEntry(entryBuilder
                                .startIntField(Text.translatable("mana.config.overlay.yOffset"),
                                                ManaConfig.getOverlayYOffset())
                                .setDefaultValue(0)
                                .setTooltip(Text.translatable(
                                                "mana.config.overlay.yOffset.tooltip"))
                                .setSaveConsumer(ManaConfig::setOverlayYOffset).build());

                // Transparency
                overlayCategory.addEntry(entryBuilder
                                .startDoubleField(
                                                Text.translatable(
                                                                "mana.config.overlay.transparency"),
                                                ManaConfig.getOverlayTransparency())
                                .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                                .setTooltip(Text.translatable(
                                                "mana.config.overlay.transparency.tooltip"))
                                .setSaveConsumer(ManaConfig::setOverlayTransparency).build());

                // Mana Bar Enabled
                overlayCategory.addEntry(entryBuilder
                                .startBooleanToggle(
                                                Text.translatable("mana.config.manaBar.enabled"),
                                                ManaConfig.isManaBarEnabled())
                                .setDefaultValue(true)
                                .setTooltip(Text.translatable(
                                                "mana.config.manaBar.enabled.tooltip"))
                                .setSaveConsumer(ManaConfig::setManaBarEnabled).build());

                // === Gameplay Balance Category ===
                ConfigCategory gameplayCategory = builder.getOrCreateCategory(
                                Text.translatable("mana.config.category.gameplay"));

                // Spell Cost Multiplier
                gameplayCategory.addEntry(entryBuilder
                                .startDoubleField(
                                                Text.translatable(
                                                                "mana.config.spell.costMultiplier"),
                                                ManaConfig.getSpellManaCostMultiplier())
                                .setDefaultValue(1.0).setMin(0.0).setMax(10.0)
                                .setTooltip(Text.translatable(
                                                "mana.config.spell.costMultiplier.tooltip"))
                                .setSaveConsumer(ManaConfig::setSpellManaCostMultiplier).build());

                // Ritual Difficulty Multiplier
                gameplayCategory.addEntry(entryBuilder
                                .startDoubleField(Text.translatable(
                                                "mana.config.ritual.difficultyMultiplier"),
                                                ManaConfig.getRitualDifficultyMultiplier())
                                .setDefaultValue(1.0).setMin(0.0).setMax(10.0)
                                .setTooltip(Text.translatable(
                                                "mana.config.ritual.difficultyMultiplier.tooltip"))
                                .setSaveConsumer(ManaConfig::setRitualDifficultyMultiplier)
                                .build());

                // === Advanced Settings Category ===
                ConfigCategory advancedCategory = builder.getOrCreateCategory(
                                Text.translatable("mana.config.category.advanced"));

                // Reset to Defaults Button
                advancedCategory.addEntry(entryBuilder
                                .startTextDescription(
                                                Text.translatable("mana.config.reset.description"))
                                .build());

                return builder.build();
        }
}
