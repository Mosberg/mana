package dk.mosberg.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
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
    // --- Modpack Creator Options ---
    /**
     * Config option: Multiplier for all spell mana costs. Key: magic.spell.manaCost.multiplier
     */
    public static double getSpellManaCostMultiplier() {
        Object value = configData.getOrDefault("magic.spell.manaCost.multiplier", 1.0);
        return value instanceof Number ? ((Number) value).doubleValue() : 1.0;
    }

    /**
     * Config option: Enable/disable mana bar HUD overlay. Key: render.hud.manaBar.enabled
     */
    public static boolean isManaBarEnabled() {
        Object value = configData.getOrDefault("render.hud.manaBar.enabled", true);
        return value instanceof Boolean ? (Boolean) value : true;
    }

    /**
     * Config option: Ritual difficulty multiplier. Key: magic.ritual.difficulty.multiplier
     */
    public static double getRitualDifficultyMultiplier() {
        Object value = configData.getOrDefault("magic.ritual.difficulty.multiplier", 1.0);
        return value instanceof Number ? ((Number) value).doubleValue() : 1.0;
    }

    /**
     * Documents modpack creator config options.
     *
     * <pre>
     * magic.spell.manaCost.multiplier - Multiplies mana cost for all spells
     * render.hud.manaBar.enabled - Enables/disables mana bar HUD
     * magic.ritual.difficulty.multiplier - Multiplies ritual difficulty
     * </pre>
     */
    public static void documentModpackOptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("Modpack Config Options:\n");
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            sb.append("- ").append(entry.getKey()).append(" (Default: ")
                    .append(entry.getDefaultValue()).append(") - ").append(entry.getComment())
                    .append("\n");
        }
        sb.append(
                "- magic.spell.manaCost.multiplier (Default: 1.0) - Multiplies mana cost for all spells\n");
        sb.append("- render.hud.manaBar.enabled (Default: true) - Enables/disables mana bar HUD\n");
        sb.append(
                "- magic.ritual.difficulty.multiplier (Default: 1.0) - Multiplies ritual difficulty\n");
        Mana.LOGGER.info(sb.toString());
    }

    /**
     * Validates the loaded config and returns a list of issues.
     *
     * @return List of validation error messages, empty if valid
     */
    public static List<String> validateConfig() {
        List<String> issues = new ArrayList<>();
        // Validate default entries
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            Object value = configData.get(entry.getKey());
            if (value == null) {
                issues.add("Missing config key: " + entry.getKey());
            } else if (!entry.getType().isInstance(value)) {
                // Allow number conversion between Integer/Double
                if (entry.getType() == Double.class && value instanceof Number) {
                    continue;
                } else if (entry.getType() == Integer.class && value instanceof Number) {
                    continue;
                } else if (entry.getType() == Boolean.class && value instanceof Boolean) {
                    continue;
                } else if (entry.getType() == String.class && value instanceof String) {
                    continue;
                } else {
                    issues.add("Type mismatch for key: " + entry.getKey() + " (expected "
                            + entry.getType().getSimpleName() + ", got "
                            + value.getClass().getSimpleName() + ")");
                }
            }
        }
        // Validate modpack options
        if (!(configData.getOrDefault("magic.spell.manaCost.multiplier", 1.0) instanceof Number)) {
            issues.add("magic.spell.manaCost.multiplier must be a number");
        }
        if (!(configData.getOrDefault("render.hud.manaBar.enabled", true) instanceof Boolean)) {
            issues.add("render.hud.manaBar.enabled must be a boolean");
        }
        if (!(configData.getOrDefault("magic.ritual.difficulty.multiplier",
                1.0) instanceof Number)) {
            issues.add("magic.ritual.difficulty.multiplier must be a number");
        }
        return issues;
    }

    /**
     * Documents all config entries and their usage.
     * <p>
     * Example:
     *
     * <pre>
     * magic.spell.manaCost.multiplier - Multiplies mana cost for all spells
     * render.hud.manaBar.enabled - Enables/disables mana bar HUD
     * </pre>
     * </p>
     */
    public static void documentConfigEntries() {
        StringBuilder sb = new StringBuilder();
        sb.append("All Config Entries:\n");
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            sb.append("- ").append(entry.getKey()).append(" (Default: ")
                    .append(entry.getDefaultValue()).append(") - ").append(entry.getComment())
                    .append("\n");
        }
        sb.append(
                "- magic.spell.manaCost.multiplier (Default: 1.0) - Multiplies mana cost for all spells\n");
        sb.append("- render.hud.manaBar.enabled (Default: true) - Enables/disables mana bar HUD\n");
        sb.append(
                "- magic.ritual.difficulty.multiplier (Default: 1.0) - Multiplies ritual difficulty\n");
        Mana.LOGGER.info(sb.toString());
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("Mana.json");

    private static final Map<String, Object> configData = new ConcurrentHashMap<>();
    private static final Map<String, Object> defaultValues = new HashMap<>();
    private static final Map<String, List<Consumer<Object>>> changeListeners = new HashMap<>();
    private static final Set<String> modifiedKeys = new HashSet<>();

    private static boolean initialized = false;
    private static boolean autoSave = true;

    /**
     * Configuration entry definition with metadata
     */
    public static class ConfigEntry {
        private final String key;
        private final Object defaultValue;
        private final String comment;
        private final Class<?> type;

        public ConfigEntry(String key, Object defaultValue, String comment) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comment = comment;
            this.type = defaultValue.getClass();
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

        public Class<?> getType() {
            return type;
        }
    }

    // Default configuration entries
    private static final List<ConfigEntry> DEFAULT_ENTRIES = Arrays.asList(
            new ConfigEntry("debug_mode", false, "Enable debug logging"),
            new ConfigEntry("auto_save_interval", 300,
                    "Auto-save interval in seconds (0 to disable)"),
            new ConfigEntry("particle_multiplier", 1.0, "Particle spawn multiplier (0.0 - 2.0)"),
            new ConfigEntry("enable_damage_numbers", true, "Show damage numbers on hit"),
            new ConfigEntry("sound_volume_multiplier", 1.0, "Sound volume multiplier (0.0 - 1.0)"),
            new ConfigEntry("max_render_distance", 64.0, "Maximum render distance for effects"),
            new ConfigEntry("enable_client_optimizations", true,
                    "Enable client-side performance optimizations"),
            new ConfigEntry("language", "en_us", "Language code"),
            new ConfigEntry("config_version", 1, "Configuration file version"),
            // Overlay config options
            new ConfigEntry("render.hud.overlay.enabled", true,
                    "Enable all custom overlays (mana, health, status)"),
            new ConfigEntry("render.hud.overlay.transparency", 1.0,
                    "Overlay transparency (0.0-1.0)"),
            new ConfigEntry("render.hud.overlay.scale", 1.0, "Overlay scale (0.5-2.0)"),
            new ConfigEntry("render.hud.overlay.xOffset", 0, "Overlay X offset (pixels)"),
            new ConfigEntry("render.hud.overlay.yOffset", 0, "Overlay Y offset (pixels)"));

    // Overlay config getters
    public static boolean isOverlayEnabled() {
        return getBoolean("render.hud.overlay.enabled", true);
    }

    public static double getOverlayTransparency() {
        return getDouble("render.hud.overlay.transparency", 1.0);
    }

    public static double getOverlayScale() {
        return getDouble("render.hud.overlay.scale", 1.0);
    }

    public static int getOverlayXOffset() {
        return getInt("render.hud.overlay.xOffset", 0);
    }

    public static int getOverlayYOffset() {
        return getInt("render.hud.overlay.yOffset", 0);
    }

    /**
     * Initializes the config system and loads existing configuration.
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        // Register default values
        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            defaultValues.put(entry.getKey(), entry.getDefaultValue());
        }

        loadConfig();
        initialized = true;
        Mana.LOGGER.info("Configuration initialized from: {}", CONFIG_FILE);
    }

    /**
     * Loads configuration from disk, merging with defaults.
     */
    private static void loadConfig() {
        // Start with defaults
        configData.putAll(defaultValues);

        if (Files.exists(CONFIG_FILE)) {
            try {
                String json = Files.readString(CONFIG_FILE);
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                jsonObject.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    if (entry.getValue().isJsonPrimitive()) {
                        Object value = parseJsonValue(entry.getValue().getAsJsonPrimitive());

                        // Validate against default type if exists
                        if (defaultValues.containsKey(key)) {
                            Object defaultValue = defaultValues.get(key);

                            // Allow number conversion between Integer and Double
                            if (value instanceof Number && defaultValue instanceof Number) {
                                configData.put(key, value);
                            } else if (value.getClass().equals(defaultValue.getClass())) {
                                configData.put(key, value);
                            } else {
                                Mana.LOGGER.debug(
                                        "Type mismatch for key '{}' (expected: {}, got: {}), using value anyway",
                                        key, defaultValue.getClass().getSimpleName(),
                                        value.getClass().getSimpleName());
                                configData.put(key, value);
                            }
                        } else {
                            // Allow custom keys not in defaults
                            configData.put(key, value);
                        }
                    }
                });

                Mana.LOGGER.info("Loaded {} config entries from file", configData.size());
            } catch (IOException e) {
                Mana.LOGGER.error("Failed to load config file", e);
            } catch (Exception e) {
                Mana.LOGGER.error("Failed to parse config file", e);
            }
        } else {
            Mana.LOGGER.info("No config file found, creating with defaults");
            saveConfig();
        }
    }

    /**
     * Parses a JSON primitive to a Java object. All numbers are returned as doubles to avoid type
     * mismatches.
     */
    private static Object parseJsonValue(com.google.gson.JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isNumber()) {
            // Always return as double for numeric values
            return primitive.getAsDouble();
        } else {
            return primitive.getAsString();
        }
    }

    /**
     * Saves configuration to disk.
     */
    public static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);

            JsonObject jsonObject = new JsonObject();

            // Sort keys for consistent output
            List<String> sortedKeys = new ArrayList<>(configData.keySet());
            Collections.sort(sortedKeys);

            for (String key : sortedKeys) {
                Object value = configData.get(key);
                if (value instanceof Boolean) {
                    jsonObject.addProperty(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    jsonObject.addProperty(key, (Integer) value);
                } else if (value instanceof Double) {
                    jsonObject.addProperty(key, (Double) value);
                } else if (value instanceof Number) {
                    jsonObject.addProperty(key, (Number) value);
                } else {
                    jsonObject.addProperty(key, value.toString());
                }
            }

            Files.writeString(CONFIG_FILE, GSON.toJson(jsonObject));
            modifiedKeys.clear();
            Mana.LOGGER.debug("Config saved successfully to {}", CONFIG_FILE);
        } catch (IOException e) {
            Mana.LOGGER.error("Failed to save config file", e);
        }
    }

    /**
     * Reloads configuration from disk, preserving modified values if requested.
     */
    public static void reload(boolean preserveModified) {
        if (preserveModified) {
            Map<String, Object> modified = new HashMap<>();
            modifiedKeys.forEach(key -> modified.put(key, configData.get(key)));
            loadConfig();
            modified.forEach(configData::put);
        } else {
            modifiedKeys.clear();
            loadConfig();
        }
        Mana.LOGGER.info("Configuration reloaded");
    }

    /**
     * Resets configuration to defaults.
     */
    public static void resetToDefaults() {
        configData.clear();
        configData.putAll(defaultValues);
        modifiedKeys.clear();
        if (autoSave) {
            saveConfig();
        }
        Mana.LOGGER.info("Configuration reset to defaults");
    }

    // Getters with type safety

    public static String getString(String key, String defaultValue) {
        Object value = configData.getOrDefault(key, defaultValue);
        return value instanceof String ? (String) value : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        Object value = configData.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public static double getDouble(String key, double defaultValue) {
        Object value = configData.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    public static float getFloat(String key, float defaultValue) {
        Object value = configData.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        Object value = configData.get(key);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    // Setters with change listeners

    public static void set(String key, Object value) {
        Object oldValue = configData.get(key);
        configData.put(key, value);
        modifiedKeys.add(key);

        if (autoSave) {
            saveConfig();
        }

        // Trigger change listeners
        if (!Objects.equals(oldValue, value)) {
            notifyListeners(key, value);
        }
    }

    public static void setString(String key, String value) {
        set(key, value);
    }

    public static void setInt(String key, int value) {
        set(key, value);
    }

    public static void setDouble(String key, double value) {
        set(key, value);
    }

    public static void setFloat(String key, float value) {
        set(key, (double) value);
    }

    public static void setBoolean(String key, boolean value) {
        set(key, value);
    }

    // Utility methods

    public static boolean has(String key) {
        return configData.containsKey(key);
    }

    public static void remove(String key) {
        configData.remove(key);
        modifiedKeys.add(key);
        if (autoSave) {
            saveConfig();
        }
    }

    public static Object get(String key) {
        return configData.get(key);
    }

    public static Object getOrDefault(String key, Object defaultValue) {
        return configData.getOrDefault(key, defaultValue);
    }

    public static Set<String> getKeys() {
        return new HashSet<>(configData.keySet());
    }

    public static Map<String, Object> getAll() {
        return new HashMap<>(configData);
    }

    public static List<ConfigEntry> getDefaultEntries() {
        return new ArrayList<>(DEFAULT_ENTRIES);
    }

    public static Object getDefault(String key) {
        return defaultValues.get(key);
    }

    public static boolean isModified(String key) {
        return modifiedKeys.contains(key);
    }

    public static boolean hasUnsavedChanges() {
        return !modifiedKeys.isEmpty();
    }

    // Change listener system

    public static void addChangeListener(String key, Consumer<Object> listener) {
        changeListeners.computeIfAbsent(key, k -> new ArrayList<>()).add(listener);
    }

    public static void removeChangeListener(String key, Consumer<Object> listener) {
        List<Consumer<Object>> listeners = changeListeners.get(key);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    private static void notifyListeners(String key, Object newValue) {
        List<Consumer<Object>> listeners = changeListeners.get(key);
        if (listeners != null) {
            listeners.forEach(listener -> {
                try {
                    listener.accept(newValue);
                } catch (Exception e) {
                    Mana.LOGGER.error("Error in config change listener for key '{}'", key, e);
                }
            });
        }
    }

    // Auto-save control

    public static void setAutoSave(boolean enabled) {
        autoSave = enabled;
    }

    public static boolean isAutoSave() {
        return autoSave;
    }

    // Validation

    public static boolean validate() {
        boolean valid = true;

        for (ConfigEntry entry : DEFAULT_ENTRIES) {
            if (!configData.containsKey(entry.getKey())) {
                Mana.LOGGER.warn("Missing config key: {}", entry.getKey());
                configData.put(entry.getKey(), entry.getDefaultValue());
                valid = false;
            }
        }

        return valid;
    }
}
