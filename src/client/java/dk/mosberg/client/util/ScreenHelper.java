// ScreenHelper.java - Already correct, no changes needed
package dk.mosberg.client.util;

public class ScreenHelper {

    public static void initialize() {
        // Initialize screen helper systems if needed
    }

    /**
     * Converts a percentage (0.0 to 1.0) to a pixel position on the screen width.
     *
     * @param percent The percentage of the screen width (0.0 to 1.0)
     * @param screenWidth The total width of the screen in pixels
     * @return The pixel position corresponding to the percentage
     */
    public static int percentToPixelX(float percent, int screenWidth) {
        percent = Math.max(0.0f, Math.min(percent, 1.0f));
        return (int) (percent * screenWidth);
    }

    /**
     * Converts a percentage (0.0 to 1.0) to a pixel position on the screen height.
     *
     * @param percent The percentage of the screen height (0.0 to 1.0)
     * @param screenHeight The total height of the screen in pixels
     * @return The pixel position corresponding to the percentage
     */
    public static int percentToPixelY(float percent, int screenHeight) {
        percent = Math.max(0.0f, Math.min(percent, 1.0f));
        return (int) (percent * screenHeight);
    }
}
