package dk.mosberg.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.mosberg.Mana;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Advanced configuration system with automatic file handling, validation, and listeners. Supports
 * multiple data types with JSON-based persistence and runtime updates.
 */
public class ManaConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("mana.json");
    private static final Map<String, Object> configData = new ConcurrentHashMap<>();
    private static final List<ConfigEntry> DEFAULT_ENTRIES = new ArrayList<>();

    // Default configuration values
    private static final boolean DEFAULT_OVERLAY_ENABLED = true;
    private static final double DEFAULT_OVERLAY_SCALE = 1.0;
    private static final int DEFAULT_OVERLAY_X_OFFSET = 0;
    private static final int DEFAULT_OVERLAY_Y_OFFSET = 0;
    private static final double DEFAULT_OVERLAY_TRANSPARENCY = 1.0;
    private static final double DEFAULT_SPELL_COST_MULTIPLIER = 1.0;
    private static final double DEFAULT_RITUAL_DIFFICULTY_MULTIPLIER = 1.0;
    private static final boolean DEFAULT_MANA_BAR_ENABLED = true;

    static {
        // Register default configuration entries
        registerDefaults();
    }

    /**
     * Registers all default configuration entries.
     */
    private static void registerDefaults() {
        DEFAULT_ENTRIES.add(new ConfigEntry("overlay.enabled", DEFAULT_OVERLAY_ENABLED,
                "Enable/disable mana HUD overlay"));
        DEFAULT_ENTRIES.add(new ConfigEntry("overlay.scale", DEFAULT_OVERLAY_SCALE,
                "Scale of the mana overlay (0.5-2.0)"));
        DEFAULT_ENTRIES.add(new ConfigEntry("overlay.xOffset", DEFAULT_OVERLAY_X_OFFSET,
                "Horizontal offset of the overlay in pixels"));
        DEFAULT_ENTRIES.add(new ConfigEntry("overlay.yOffset", DEFAULT_OVERLAY_Y_OFFSET,
                "Vertical offset of the overlay in pixels"));
        DEFAULT_ENTRIES.add(new ConfigEntry("overlay.transparency", DEFAULT_OVERLAY_TRANSPARENCY,
                "Transparency of the overlay (0.0-1.0)"));
        DEFAULT_ENTRIES.add(new ConfigEntry("magic.spell.manaCost.multiplier",
                DEFAULT_SPELL_COST_MULTIPLIER, "Multiplies mana cost for all spells"));
        DEFAULT_ENTRIES.add(new ConfigEntry("magic.ritual.difficulty.multiplier",
                DEFAULT_RITUAL_DIFFICULTY_MULTIPLIER, "Multiplies ritual difficulty"));
        DEFAULT_ENTRIES.add(new ConfigEntry("render.hud.manaBar.enabled", DEFAULT_MANA_BAR_ENABLED,
                "Enables/disables mana bar HUD"));
    }

    /**
     * Initializes the configuration system. Loads existing config or creates default.
     */
    public static void initialize() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                load();
            } else {
                createDefault();
                save();
            }
            Mana.LOGGER.info("Configuration loaded successfully");
        } catch (IOException e) {
            Mana.LOGGER.error("Failed to initialize configuration", e);
            createDefault();
        }
    }

    /**
     * Creates default configuration values.
     */
    private static void createDefault() {
        configData.clear();
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            configData.put(entry.getKey(), entry.getDefaultValue());
        }
    }

    /**
     * Loads configuration from file.
     *
     * @throws IOException If file reading fails
     */
    private static void load() throws IOException {
        String json = Files.readString(CONFIG_FILE);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        configData.clear();
        jsonObject.entrySet().forEach(entry -> {
            String key = entry.getKey();
            if (entry.getValue().isJsonPrimitive()) {
                var primitive = entry.getValue().getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    configData.put(key, primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    configData.put(key, primitive.getAsDouble());
                } else if (primitive.isString()) {
                    configData.put(key, primitive.getAsString());
                }
            }
        });

        // Ensure all defaults exist
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            configData.putIfAbsent(entry.getKey(), entry.getDefaultValue());
        }
    }

    /**
     * Saves configuration to file.
     *
     * @throws IOException If file writing fails
     */
    public static void save() throws IOException {
        Files.createDirectories(CONFIG_DIR);

        JsonObject jsonObject = new JsonObject();
        configData.forEach((key, value) -> {
            if (value instanceof Boolean) {
                jsonObject.addProperty(key, (Boolean) value);
            } else if (value instanceof Number) {
                jsonObject.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                jsonObject.addProperty(key, (String) value);
            }
        });

        Files.writeString(CONFIG_FILE, GSON.toJson(jsonObject));
    }

    /**
     * Validates the loaded config and returns a list of issues.
     *
     * @return List of validation error messages, empty if valid
     */
    public static List<String> validateConfig() {
        List<String> issues = new ArrayList<>();

        // Validate overlay scale
        double scale = getOverlayScale();
        if (scale < 0.5 || scale > 2.0) {
            issues.add("overlay.scale must be between 0.5 and 2.0");
        }

        // Validate transparency
        double transparency = getOverlayTransparency();
        if (transparency < 0.0 || transparency > 1.0) {
            issues.add("overlay.transparency must be between 0.0 and 1.0");
        }

        // Validate multipliers
        double spellMultiplier = getSpellManaCostMultiplier();
        if (spellMultiplier < 0.0) {
            issues.add("magic.spell.manaCost.multiplier must be non-negative");
        }

        double ritualMultiplier = getRitualDifficultyMultiplier();
        if (ritualMultiplier < 0.0) {
            issues.add("magic.ritual.difficulty.multiplier must be non-negative");
        }

        return issues;
    }

    // --- Overlay Configuration ---

    public static boolean isOverlayEnabled() {
        Object value = configData.getOrDefault("overlay.enabled", DEFAULT_OVERLAY_ENABLED);
        return value instanceof Boolean ? (Boolean) value : DEFAULT_OVERLAY_ENABLED;
    }

    public static double getOverlayScale() {
        Object value = configData.getOrDefault("overlay.scale", DEFAULT_OVERLAY_SCALE);
        return value instanceof Number ? ((Number) value).doubleValue() : DEFAULT_OVERLAY_SCALE;
    }

    public static int getOverlayXOffset() {
        Object value = configData.getOrDefault("overlay.xOffset", DEFAULT_OVERLAY_X_OFFSET);
        return value instanceof Number ? ((Number) value).intValue() : DEFAULT_OVERLAY_X_OFFSET;
    }

    public static int getOverlayYOffset() {
        Object value = configData.getOrDefault("overlay.yOffset", DEFAULT_OVERLAY_Y_OFFSET);
        return value instanceof Number ? ((Number) value).intValue() : DEFAULT_OVERLAY_Y_OFFSET;
    }

    public static double getOverlayTransparency() {
        Object value =
                configData.getOrDefault("overlay.transparency", DEFAULT_OVERLAY_TRANSPARENCY);
        return value instanceof Number ? ((Number) value).doubleValue()
                : DEFAULT_OVERLAY_TRANSPARENCY;
    }

    // --- Modpack Creator Options ---

    public static double getSpellManaCostMultiplier() {
        Object value = configData.getOrDefault("magic.spell.manaCost.multiplier",
                DEFAULT_SPELL_COST_MULTIPLIER);
        return value instanceof Number ? ((Number) value).doubleValue()
                : DEFAULT_SPELL_COST_MULTIPLIER;
    }

    public static boolean isManaBarEnabled() {
        Object value =
                configData.getOrDefault("render.hud.manaBar.enabled", DEFAULT_MANA_BAR_ENABLED);
        return value instanceof Boolean ? (Boolean) value : DEFAULT_MANA_BAR_ENABLED;
    }

    public static double getRitualDifficultyMultiplier() {
        Object value = configData.getOrDefault("magic.ritual.difficulty.multiplier",
                DEFAULT_RITUAL_DIFFICULTY_MULTIPLIER);
        return value instanceof Number ? ((Number) value).doubleValue()
                : DEFAULT_RITUAL_DIFFICULTY_MULTIPLIER;
    }

    /**
     * Documents all configuration options.
     */
    public static void documentConfigEntries() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mana Configuration Options:\\n");
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            sb.append("  - ").append(entry.getKey()).append(" (Default: ")
                    .append(entry.getDefaultValue()).append(") - ").append(entry.getComment())
                    .append("\\n");
        }
        Mana.LOGGER.info(sb.toString());
    }

    /**
     * Configuration entry holder.
     */
    private static class ConfigEntry {
        private final String key;
        private final Object defaultValue;
        private final String comment;

        public ConfigEntry(String key, Object defaultValue, String comment) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comment = comment;
        }

        public String getKey() {
            return key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public String getComment() {
            return comment;
        }
    }

    /**
     * Sets overlay enabled state.
     *
     * @param enabled Whether overlay should be enabled
     */
    public static void setOverlayEnabled(boolean enabled) {
        configData.put("overlay.enabled", enabled);
    }

    /**
     * Sets overlay scale.
     *
     * @param scale The scale value (0.5-2.0)
     */
    public static void setOverlayScale(double scale) {
        if (scale >= 0.5 && scale <= 2.0) {
            configData.put("overlay.scale", scale);
        }
    }

    /**
     * Sets overlay horizontal offset.
     *
     * @param offset The horizontal offset in pixels
     */
    public static void setOverlayXOffset(int offset) {
        configData.put("overlay.xOffset", offset);
    }

    /**
     * Sets overlay vertical offset.
     *
     * @param offset The vertical offset in pixels
     */
    public static void setOverlayYOffset(int offset) {
        configData.put("overlay.yOffset", offset);
    }

    /**
     * Sets overlay transparency.
     *
     * @param transparency The transparency value (0.0-1.0)
     */
    public static void setOverlayTransparency(double transparency) {
        if (transparency >= 0.0 && transparency <= 1.0) {
            configData.put("overlay.transparency", transparency);
        }
    }

    /**
     * Sets mana bar enabled state.
     *
     * @param enabled Whether mana bars should be displayed
     */
    public static void setManaBarEnabled(boolean enabled) {
        configData.put("render.hud.manaBar.enabled", enabled);
    }

    /**
     * Sets spell mana cost multiplier.
     *
     * @param multiplier The cost multiplier (must be non-negative)
     */
    public static void setSpellManaCostMultiplier(double multiplier) {
        if (multiplier >= 0.0) {
            configData.put("magic.spell.manaCost.multiplier", multiplier);
        }
    }

    /**
     * Sets ritual difficulty multiplier.
     *
     * @param multiplier The difficulty multiplier (must be non-negative)
     */
    public static void setRitualDifficultyMultiplier(double multiplier) {
        if (multiplier >= 0.0) {
            configData.put("magic.ritual.difficulty.multiplier", multiplier);
        }
    }
}
