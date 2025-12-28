package dk.mosberg.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * Manages a player's mana pools.
 */

public class ManaPool {
    /**
     * Expands all mana pools by a given amount.
     *
     * @param amount Amount to expand each pool
     */
    public void expandAllPools(double amount) {
        increaseMaxMana(ManaPoolType.PRIMARY, amount);
        increaseMaxMana(ManaPoolType.SECONDARY, amount);
        increaseMaxMana(ManaPoolType.TERTIARY, amount);
    }

    /**
     * Shares mana with another ManaPool (stub).
     *
     * @param other The other ManaPool to share with
     * @param amount Amount to share
     * @return true if mana was shared, false otherwise
     */
    public boolean shareMana(ManaPool other, double amount) {
        if (other == null || amount <= 0 || getTotalMana() < amount)
            return false;
        if (!consumeMana(amount))
            return false;
        other.restoreMana(amount);
        return true;
    }

    private static final double MAX_MANA_PER_POOL = 100.0;
    private static final double REGEN_RATE_PRIMARY = 1.0; // per second
    private static final double REGEN_RATE_SECONDARY = 0.75;
    private static final double REGEN_RATE_TERTIARY = 0.5;

    private double primaryMana;
    private double secondaryMana;
    private double tertiaryMana;

    private double primaryMax;
    private double secondaryMax;
    private double tertiaryMax;

    private long lastUpdateTime;
    private boolean regenerating = true;

    public ManaPool() {
        this(MAX_MANA_PER_POOL, MAX_MANA_PER_POOL, MAX_MANA_PER_POOL);
    }

    public ManaPool(double primaryMax, double secondaryMax, double tertiaryMax) {
        this.primaryMax = primaryMax;
        this.secondaryMax = secondaryMax;
        this.tertiaryMax = tertiaryMax;
        this.primaryMana = primaryMax;
        this.secondaryMana = secondaryMax;
        this.tertiaryMana = tertiaryMax;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    /**
     * Update mana regeneration
     */
    public void tick(PlayerEntity player) {
        // Regenerate mana pools if enabled
        if (!regenerating) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        double deltaSeconds = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;

        // Regenerate each pool independently
        if (primaryMana < primaryMax) {
            primaryMana = Math.min(primaryMax, primaryMana + (REGEN_RATE_PRIMARY * deltaSeconds));
        }

        if (secondaryMana < secondaryMax) {
            secondaryMana =
                    Math.min(secondaryMax, secondaryMana + (REGEN_RATE_SECONDARY * deltaSeconds));
        }

        if (tertiaryMana < tertiaryMax) {
            tertiaryMana =
                    Math.min(tertiaryMax, tertiaryMana + (REGEN_RATE_TERTIARY * deltaSeconds));
        }
    }

    /**
     * Consume mana from pools in priority order
     */
    public boolean consumeMana(double amount) {
        double totalMana = getTotalMana();

        if (totalMana < amount) {
            return false;
        }

        double remaining = amount;

        // Try primary first
        if (primaryMana >= remaining) {
            primaryMana -= remaining;
            return true;
        } else {
            remaining -= primaryMana;
            primaryMana = 0;
        }

        // Then secondary
        if (secondaryMana >= remaining) {
            secondaryMana -= remaining;
            return true;
        } else {
            remaining -= secondaryMana;
            secondaryMana = 0;
        }

        // Finally tertiary
        if (tertiaryMana >= remaining) {
            tertiaryMana -= remaining;
            return true;
        }

        return false;
    }

    /**
     * Restore mana to pools
     */
    public void restoreMana(double amount) {
        double remaining = amount;

        // Fill primary first
        double primarySpace = primaryMax - primaryMana;
        if (primarySpace > 0) {
            double toAdd = Math.min(remaining, primarySpace);
            primaryMana += toAdd;
            remaining -= toAdd;
        }

        // Then secondary
        if (remaining > 0) {
            double secondarySpace = secondaryMax - secondaryMana;
            if (secondarySpace > 0) {
                double toAdd = Math.min(remaining, secondarySpace);
                secondaryMana += toAdd;
                remaining -= toAdd;
            }
        }

        // Finally tertiary
        if (remaining > 0) {
            double tertiarySpace = tertiaryMax - tertiaryMana;
            if (tertiarySpace > 0) {
                double toAdd = Math.min(remaining, tertiarySpace);
                tertiaryMana += toAdd;
            }
        }
    }

    /**
     * Instantly restore specific pool to max
     */
    public void restorePool(ManaPoolType type) {
        switch (type) {
            case PRIMARY -> primaryMana = primaryMax;
            case SECONDARY -> secondaryMana = secondaryMax;
            case TERTIARY -> tertiaryMana = tertiaryMax;
        }
    }

    /**
     * Instantly restore all pools
     */
    public void restoreAll() {
        primaryMana = primaryMax;
        secondaryMana = secondaryMax;
        tertiaryMana = tertiaryMax;
    }

    /**
     * Increase maximum mana capacity
     */
    public void increaseMaxMana(ManaPoolType type, double amount) {
        switch (type) {
            case PRIMARY -> {
                primaryMax += amount;
                primaryMana = Math.min(primaryMana + amount, primaryMax);
            }
            case SECONDARY -> {
                secondaryMax += amount;
                secondaryMana = Math.min(secondaryMana + amount, secondaryMax);
            }
            case TERTIARY -> {
                tertiaryMax += amount;
                tertiaryMana = Math.min(tertiaryMana + amount, tertiaryMax);
            }
        }
    }

    /**
     * Stop mana regeneration
     */
    public void stopRegeneration(int ticks) {
        regenerating = false;
        // Schedule re-enable (would need a ticker system)
    }

    // Getters
    public double getPrimaryMana() {
        return primaryMana;
    }

    public double getSecondaryMana() {
        return secondaryMana;
    }

    public double getTertiaryMana() {
        return tertiaryMana;
    }

    public double getPrimaryMax() {
        return primaryMax;
    }

    public double getSecondaryMax() {
        return secondaryMax;
    }

    public double getTertiaryMax() {
        return tertiaryMax;
    }

    public double getTotalMana() {
        return primaryMana + secondaryMana + tertiaryMana;
    }

    public double getTotalMaxMana() {
        return primaryMax + secondaryMax + tertiaryMax;
    }

    public double getPrimaryPercent() {
        return primaryMana / primaryMax;
    }

    public double getSecondaryPercent() {
        return secondaryMana / secondaryMax;
    }

    public double getTertiaryPercent() {
        return tertiaryMana / tertiaryMax;
    }

    /**
     * Save to NBT
     */
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putDouble("PrimaryMana", primaryMana);
        nbt.putDouble("SecondaryMana", secondaryMana);
        nbt.putDouble("TertiaryMana", tertiaryMana);
        nbt.putDouble("PrimaryMax", primaryMax);
        nbt.putDouble("SecondaryMax", secondaryMax);
        nbt.putDouble("TertiaryMax", tertiaryMax);
        nbt.putBoolean("Regenerating", regenerating);
        nbt.putLong("LastUpdate", lastUpdateTime);
        return nbt;
    }

    /**
     * Load from NBT
     */
    public void readNbt(NbtCompound nbt) {
        primaryMana = nbt.getDouble("PrimaryMana").orElse(0.0);
        secondaryMana = nbt.getDouble("SecondaryMana").orElse(0.0);
        tertiaryMana = nbt.getDouble("TertiaryMana").orElse(0.0);
        primaryMax = nbt.getDouble("PrimaryMax").orElse(MAX_MANA_PER_POOL);
        secondaryMax = nbt.getDouble("SecondaryMax").orElse(MAX_MANA_PER_POOL);
        tertiaryMax = nbt.getDouble("TertiaryMax").orElse(MAX_MANA_PER_POOL);
        regenerating = nbt.getBoolean("Regenerating").orElse(true);
        lastUpdateTime = nbt.getLong("LastUpdate").orElse(System.currentTimeMillis());
    }

    public enum ManaPoolType {
        PRIMARY, SECONDARY, TERTIARY
    }
}
