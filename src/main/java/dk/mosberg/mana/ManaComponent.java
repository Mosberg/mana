package dk.mosberg.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * Attaches mana pool data to players.
 */
public class ManaComponent {
    private final ManaPool manaPool;
    private final PlayerEntity player;

    public ManaComponent(PlayerEntity player) {
        this.player = player;
        this.manaPool = new ManaPool();
    }

    public ManaPool getManaPool() {
        return manaPool;
    }

    public void tick() {
        manaPool.tick(player);
    }

    public void readFromNbt(NbtCompound tag) {
        manaPool.readNbt(tag);
    }

    public void writeToNbt(NbtCompound tag) {
        manaPool.writeNbt(tag);
    }
}
