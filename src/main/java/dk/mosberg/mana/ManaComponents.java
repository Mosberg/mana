package dk.mosberg.mana;

import java.util.WeakHashMap;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Simple manager for attaching ManaComponent to players. Replace with Fabric/Cardinal Components if
 * available.
 */
public class ManaComponents {
    private static final WeakHashMap<ServerPlayerEntity, ManaComponent> MANA = new WeakHashMap<>();

    public static ManaComponent get(ServerPlayerEntity player) {
        return MANA.computeIfAbsent(player, ManaComponent::new);
    }

    public static void remove(ServerPlayerEntity player) {
        MANA.remove(player);
    }
}
