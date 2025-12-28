package dk.mosberg.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.Mana;
import dk.mosberg.client.overlay.ManaHudOverlay;
import dk.mosberg.client.renderer.OverlayRenderer;
import dk.mosberg.client.util.ColorHelper;
import dk.mosberg.client.util.DrawHelper;
import dk.mosberg.client.util.HealthBarHelper;
import dk.mosberg.client.util.RenderHelper;
import dk.mosberg.client.util.ScreenHelper;
import dk.mosberg.client.util.StatusIconHelper;
import dk.mosberg.client.util.TextHelper;
import net.fabricmc.api.ClientModInitializer;

/**
 * Client-side initialization for the Mana System. Handles rendering and HUD overlay registration.
 */
public class ManaClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Mana.MOD_ID);

	@Override
	public void onInitializeClient() {
		// Initialize client-side rendering systems
		OverlayRenderer.initialize();

		// Register mana HUD overlay
		ManaHudOverlay.register();

		// Register mana ModMenu screen
		// ManaModMenu.register();
		// Note: The above line is commented out to prevent potential issues with ModMenu
		// integration.

		// Initialize utility classes
		ColorHelper.initialize();
		DrawHelper.initialize();
		HealthBarHelper.initialize();
		RenderHelper.initialize();
		ScreenHelper.initialize();
		StatusIconHelper.initialize();
		TextHelper.initialize();

		LOGGER.info("Mana System client initialized!");
	}
}
