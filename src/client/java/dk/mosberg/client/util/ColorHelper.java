// ColorHelper.java - Already correct, no changes needed
package dk.mosberg.client.util;

/**
 * Color manipulation and conversion utilities. All methods include input validation for robustness.
 */
public class ColorHelper {

    public static void initialize() {
        // Initialize any rendering resources if needed
    }

    // Color component bounds
    private static final int MIN_COMPONENT = 0;
    private static final int MAX_COMPONENT = 255;
    private static final float MIN_PERCENT = 0.0f;
    private static final float MAX_PERCENT = 1.0f;

    /**
     * Creates an ARGB color from components. Components are clamped to valid range (0-255).
     *
     * @param alpha The alpha component (0-255)
     * @param red The red component (0-255)
     * @param green The green component (0-255)
     * @param blue The blue component (0-255)
     * @return The ARGB color integer
     */
    public static int argb(int alpha, int red, int green, int blue) {
        alpha = clamp(alpha, MIN_COMPONENT, MAX_COMPONENT);
        red = clamp(red, MIN_COMPONENT, MAX_COMPONENT);
        green = clamp(green, MIN_COMPONENT, MAX_COMPONENT);
        blue = clamp(blue, MIN_COMPONENT, MAX_COMPONENT);
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Creates an RGB color (fully opaque).
     *
     * @param red The red component (0-255)
     * @param green The green component (0-255)
     * @param blue The blue component (0-255)
     * @return The ARGB color integer
     */
    public static int rgb(int red, int green, int blue) {
        return argb(MAX_COMPONENT, red, green, blue);
    }

    /**
     * Extracts the alpha component from an ARGB color.
     *
     * @param color The ARGB color
     * @return The alpha component (0-255)
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }

    /**
     * Extracts the red component from an ARGB color.
     *
     * @param color The ARGB color
     * @return The red component (0-255)
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    /**
     * Extracts the green component from an ARGB color.
     *
     * @param color The ARGB color
     * @return The green component (0-255)
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    /**
     * Extracts the blue component from an ARGB color.
     *
     * @param color The ARGB color
     * @return The blue component (0-255)
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }

    /**
     * Interpolates between two colors. Delta is clamped to valid range (0.0-1.0).
     *
     * @param color1 The first color
     * @param color2 The second color
     * @param delta The interpolation factor (0.0-1.0)
     * @return The interpolated color
     */
    public static int lerp(int color1, int color2, float delta) {
        delta = clamp(delta, MIN_PERCENT, MAX_PERCENT);
        int a1 = getAlpha(color1);
        int r1 = getRed(color1);
        int g1 = getGreen(color1);
        int b1 = getBlue(color1);

        int a2 = getAlpha(color2);
        int r2 = getRed(color2);
        int g2 = getGreen(color2);
        int b2 = getBlue(color2);

        int a = (int) (a1 + (a2 - a1) * delta);
        int r = (int) (r1 + (r2 - r1) * delta);
        int g = (int) (g1 + (g2 - g1) * delta);
        int b = (int) (b1 + (b2 - b1) * delta);

        return argb(a, r, g, b);
    }

    /**
     * Converts HSV to RGB color.
     *
     * @param hue The hue (0-360)
     * @param saturation The saturation (0.0-1.0)
     * @param value The value/brightness (0.0-1.0)
     * @return The RGB color
     */
    public static int hsvToRgb(float hue, float saturation, float value) {
        hue = clamp(hue, 0.0f, 360.0f);
        saturation = clamp(saturation, MIN_PERCENT, MAX_PERCENT);
        value = clamp(value, MIN_PERCENT, MAX_PERCENT);

        int rgb = java.awt.Color.HSBtoRGB(hue / 360f, saturation, value);
        return rgb | 0xFF000000; // Set alpha to fully opaque
    }

    /**
     * Creates a rainbow color based on time.
     *
     * @param ticks The current tick count
     * @param speed The rainbow speed (lower = slower)
     * @return The rainbow color
     */
    public static int rainbow(long ticks, float speed) {
        speed = Math.max(0.01f, speed); // Prevent division issues
        float hue = (ticks * speed) % 360;
        return hsvToRgb(hue, 1.0f, 1.0f);
    }

    /**
     * Darkens a color by a percentage.
     *
     * @param color The original color
     * @param percent The darkening percentage (0.0-1.0, where 1.0 = fully black)
     * @return The darkened color
     */
    public static int darken(int color, float percent) {
        percent = clamp(percent, MIN_PERCENT, MAX_PERCENT);
        int a = getAlpha(color);
        int r = (int) (getRed(color) * (1.0f - percent));
        int g = (int) (getGreen(color) * (1.0f - percent));
        int b = (int) (getBlue(color) * (1.0f - percent));
        return argb(a, r, g, b);
    }

    /**
     * Lightens a color by a percentage.
     *
     * @param color The original color
     * @param percent The lightening percentage (0.0-1.0, where 1.0 = fully white)
     * @return The lightened color
     */
    public static int lighten(int color, float percent) {
        percent = clamp(percent, MIN_PERCENT, MAX_PERCENT);
        int a = getAlpha(color);
        int r = (int) (getRed(color) + (MAX_COMPONENT - getRed(color)) * percent);
        int g = (int) (getGreen(color) + (MAX_COMPONENT - getGreen(color)) * percent);
        int b = (int) (getBlue(color) + (MAX_COMPONENT - getBlue(color)) * percent);
        return argb(a, r, g, b);
    }

    /**
     * Sets the alpha channel of a color.
     *
     * @param color The original color
     * @param alpha The new alpha value (0-255)
     * @return The color with modified alpha
     */
    public static int withAlpha(int color, int alpha) {
        alpha = clamp(alpha, MIN_COMPONENT, MAX_COMPONENT);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    /**
     * Clamps an integer value between min and max.
     *
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamps a float value between min and max.
     *
     * @param value The value to clamp
     * @param min The minimum value
     * @param max The maximum value
     * @return The clamped value
     */
    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int rgbaToInt(int r, int g, int b, int a) {
        return argb(a, r, g, b);
    }

    public static int rgbToInt(int r, int g, int b) {
        return rgb(r, g, b);
    }
}
