// ManaComponent.java - FIXED
package dk.mosberg.mana;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Attaches mana pool data to players. Handles ticking and NBT serialization.
 */
public class ManaComponent {

    private final ManaPool manaPool;
    private final PlayerEntity player;

    public ManaComponent(@NotNull PlayerEntity player) {
        this.player = player;
        this.manaPool = new ManaPool();
    }

    /**
     * Gets the mana pool for this player.
     *
     * @return The mana pool
     */
    @NotNull
    public ManaPool getManaPool() {
        return manaPool;
    }

    /**
     * Gets the player entity.
     *
     * @return The player
     */
    @NotNull
    public PlayerEntity getPlayer() {
        return player;
    }

    /**
     * Ticks the mana pool for regeneration.
     */
    public void tick() {
        manaPool.tick(player);
    }

    /**
     * Reads mana data from NBT.
     *
     * @param tag The NBT compound
     */
    public void readFromNbt(@NotNull NbtCompound tag) {
        // FIXED: Use getCompoundOrEmpty() instead of getCompound() which returns Optional
        if (tag.contains("ManaPool")) {
            NbtCompound poolTag = tag.getCompoundOrEmpty("ManaPool");
            manaPool.readNbt(poolTag);
        }
    }

    /**
     * Writes mana data to NBT.
     *
     * @param tag The NBT compound
     * @return The modified NBT compound
     */
    @NotNull
    public NbtCompound writeToNbt(@NotNull NbtCompound tag) {
        NbtCompound poolTag = new NbtCompound();
        manaPool.writeNbt(poolTag);
        tag.put("ManaPool", poolTag);
        return tag;
    }

    /**
     * Gets the ManaComponent attached to a server player.
     *
     * @param player The server player
     * @return The player's ManaComponent, or null if not available
     */
    @Nullable
    public static ManaComponent get(@NotNull ServerPlayerEntity player) {
        return ManaComponents.get(player);
    }
}
