package dk.mosberg.client.renderer;

/**
 * Color manipulation and conversion utilities.
 */
public class ColorHelper {

    /**
     * Creates an ARGB color from components.
     *
     * @param alpha The alpha component (0-255)
     * @param red The red component (0-255)
     * @param green The green component (0-255)
     * @param blue The blue component (0-255)
     * @return The ARGB color integer
     */
    public static int argb(int alpha, int red, int green, int blue) {
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
        return argb(255, red, green, blue);
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
     * Interpolates between two colors.
     *
     * @param color1 The first color
     * @param color2 The second color
     * @param delta The interpolation factor (0-1)
     * @return The interpolated color
     */
    public static int lerp(int color1, int color2, float delta) {
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
     * @param saturation The saturation (0-1)
     * @param value The value (0-1)
     * @return The RGB color
     */
    public static int hsvToRgb(float hue, float saturation, float value) {
        int rgb = java.awt.Color.HSBtoRGB(hue / 360f, saturation, value);
        return rgb | 0xFF000000; // Set alpha to fully opaque
    }

    /**
     * Creates a rainbow color based on time.
     *
     * @param ticks The current tick count
     * @param speed The rainbow speed
     * @return The rainbow color
     */
    public static int rainbow(long ticks, float speed) {
        float hue = (ticks * speed) % 360;
        return hsvToRgb(hue, 1.0f, 1.0f);
    }
}
