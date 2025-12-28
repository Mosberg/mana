package dk.mosberg.util;

import org.jetbrains.annotations.NotNull;
import dk.mosberg.Mana;

/**
 * Utility class for reading environment variables with type-safe defaults and proper error logging.
 * Provides fallback values when environment variables are missing or malformed.
 */
public final class ConfigHelper {

    private ConfigHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Retrieves a string environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found
     * @return The environment variable value or default
     */
    @NotNull
    public static String getEnvOrDefault(@NotNull String envVar, @NotNull String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    /**
     * Retrieves an integer environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found or invalid
     * @return The environment variable value or default
     */
    public static int getEnvOrDefault(@NotNull String envVar, int defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                Mana.LOGGER.warn(
                        "Invalid integer value for environment variable '{}': {}. Using default: {}",
                        envVar, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a boolean environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found
     * @return The environment variable value or default
     */
    public static boolean getEnvOrDefault(@NotNull String envVar, boolean defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    /**
     * Retrieves a double environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found or invalid
     * @return The environment variable value or default
     */
    public static double getEnvOrDefault(@NotNull String envVar, double defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                Mana.LOGGER.warn(
                        "Invalid double value for environment variable '{}': {}. Using default: {}",
                        envVar, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a long environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found or invalid
     * @return The environment variable value or default
     */
    public static long getEnvOrDefault(@NotNull String envVar, long defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                Mana.LOGGER.warn(
                        "Invalid long value for environment variable '{}': {}. Using default: {}",
                        envVar, value, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * Retrieves a float environment variable with a default fallback.
     *
     * @param envVar The environment variable name
     * @param defaultValue The default value if not found or invalid
     * @return The environment variable value or default
     */
    public static float getEnvOrDefault(@NotNull String envVar, float defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isEmpty()) {
            try {
                return Float.parseFloat(value.trim());
            } catch (NumberFormatException e) {
                Mana.LOGGER.warn(
                        "Invalid float value for environment variable '{}': {}. Using default: {}",
                        envVar, value, defaultValue);
            }
        }
        return defaultValue;
    }
}
