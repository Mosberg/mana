// ManaPool.java - FULLY FIXED for 1.21+ NBT API
package dk.mosberg.mana;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * Manages a player's three mana pools with automatic regeneration. Uses game ticks for
 * deterministic, multiplayer-safe timing.
 */
public class ManaPool {

    // Constants
    private static final double REGEN_RATE_PRIMARY = 1.0;
    private static final double REGEN_RATE_SECONDARY = 0.75;
    private static final double REGEN_RATE_TERTIARY = 0.5;
    private static final double MAX_MANA_PRIMARY_POOL = 250.0;
    private static final double MAX_MANA_SECONDARY_POOL = 500.0;
    private static final double MAX_MANA_TERTIARY_POOL = 1000.0;
    private static final int TICKS_PER_SECOND = 20;

    // Pool state
    private double primaryMana;
    private double secondaryMana;
    private double tertiaryMana;
    private double primaryMax;
    private double secondaryMax;
    private double tertiaryMax;
    private boolean regenerating = true;

    /**
     * Creates a new ManaPool with default max values.
     */
    public ManaPool() {
        this(MAX_MANA_PRIMARY_POOL, MAX_MANA_SECONDARY_POOL, MAX_MANA_TERTIARY_POOL);
    }

    /**
     * Creates a new ManaPool with custom max values.
     *
     * @param primaryMax Maximum primary mana
     * @param secondaryMax Maximum secondary mana
     * @param tertiaryMax Maximum tertiary mana
     */
    public ManaPool(double primaryMax, double secondaryMax, double tertiaryMax) {
        this.primaryMax = Math.max(0, primaryMax);
        this.secondaryMax = Math.max(0, secondaryMax);
        this.tertiaryMax = Math.max(0, tertiaryMax);
        this.primaryMana = this.primaryMax;
        this.secondaryMana = this.secondaryMax;
        this.tertiaryMana = this.tertiaryMax;
    }

    /**
     * Update mana regeneration based on game ticks. Called every tick from the server.
     *
     * @param player The player entity
     */
    public void tick(PlayerEntity player) {
        if (!regenerating) {
            return;
        }

        // Calculate regen per tick (divide by ticks per second)
        double primaryRegenPerTick = REGEN_RATE_PRIMARY / TICKS_PER_SECOND;
        double secondaryRegenPerTick = REGEN_RATE_SECONDARY / TICKS_PER_SECOND;
        double tertiaryRegenPerTick = REGEN_RATE_TERTIARY / TICKS_PER_SECOND;

        // Regenerate each pool independently
        if (primaryMana < primaryMax) {
            primaryMana = Math.min(primaryMax, primaryMana + primaryRegenPerTick);
        }

        if (secondaryMana < secondaryMax) {
            secondaryMana = Math.min(secondaryMax, secondaryMana + secondaryRegenPerTick);
        }

        if (tertiaryMana < tertiaryMax) {
            tertiaryMana = Math.min(tertiaryMax, tertiaryMana + tertiaryRegenPerTick);
        }
    }

    /**
     * Consume mana from pools in priority order (primary -> secondary -> tertiary).
     *
     * @param amount Amount of mana to consume
     * @return true if mana was consumed, false if insufficient
     */
    public boolean consumeMana(double amount) {
        if (amount < 0) {
            return false;
        }

        if (getTotalMana() < amount) {
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
     * Restore mana to pools in priority order (primary -> secondary -> tertiary).
     *
     * @param amount Amount of mana to restore
     */
    public void restoreMana(double amount) {
        if (amount <= 0) {
            return;
        }

        double remaining = amount;

        // Fill primary first
        double primarySpace = primaryMax - primaryMana;
        if (primarySpace > 0 && remaining > 0) {
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
     * Instantly restore a specific pool to maximum.
     *
     * @param type The pool type to restore
     */
    public void restorePool(ManaPoolType type) {
        switch (type) {
            case PRIMARY -> primaryMana = primaryMax;
            case SECONDARY -> secondaryMana = secondaryMax;
            case TERTIARY -> tertiaryMana = tertiaryMax;
        }
    }

    /**
     * Instantly restore all pools to maximum.
     */
    public void restoreAll() {
        primaryMana = primaryMax;
        secondaryMana = secondaryMax;
        tertiaryMana = tertiaryMax;
    }

    /**
     * Increase maximum mana capacity for a specific pool.
     *
     * @param type The pool type
     * @param amount Amount to increase
     */
    public void increaseMaxMana(ManaPoolType type, double amount) {
        if (amount <= 0) {
            return;
        }

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
     * Shares mana with another ManaPool.
     *
     * @param other The other ManaPool to share with
     * @param amount Amount to share
     * @return true if mana was shared, false otherwise
     */
    public boolean shareMana(ManaPool other, double amount) {
        if (other == null || amount <= 0 || getTotalMana() < amount) {
            return false;
        }

        if (!consumeMana(amount)) {
            return false;
        }

        other.restoreMana(amount);
        return true;
    }

    /**
     * Set regeneration state.
     *
     * @param regenerating Whether mana should regenerate
     */
    public void setRegenerating(boolean regenerating) {
        this.regenerating = regenerating;
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
        return primaryMax > 0 ? primaryMana / primaryMax : 0.0;
    }

    public double getSecondaryPercent() {
        return secondaryMax > 0 ? secondaryMana / secondaryMax : 0.0;
    }

    public double getTertiaryPercent() {
        return tertiaryMax > 0 ? tertiaryMana / tertiaryMax : 0.0;
    }

    public boolean isRegenerating() {
        return regenerating;
    }

    /**
     * Save to NBT.
     *
     * @param nbt The NBT compound to write to
     * @return The modified NBT compound
     */
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putDouble("PrimaryMana", primaryMana);
        nbt.putDouble("SecondaryMana", secondaryMana);
        nbt.putDouble("TertiaryMana", tertiaryMana);
        nbt.putDouble("PrimaryMax", primaryMax);
        nbt.putDouble("SecondaryMax", secondaryMax);
        nbt.putDouble("TertiaryMax", tertiaryMax);
        nbt.putBoolean("Regenerating", regenerating);
        return nbt;
    }

    /**
     * Load from NBT - FIXED for Minecraft 1.21+ NBT API that returns Optional<T>.
     *
     * Uses the fallback overload methods: getDouble(key, fallback), getBoolean(key, fallback).
     *
     * @param nbt The NBT compound to read from
     */
    public void readNbt(NbtCompound nbt) {
        // Use the fallback overloads that accept default values
        primaryMana = nbt.getDouble("PrimaryMana", primaryMax);
        secondaryMana = nbt.getDouble("SecondaryMana", secondaryMax);
        tertiaryMana = nbt.getDouble("TertiaryMana", tertiaryMax);
        primaryMax = nbt.getDouble("PrimaryMax", MAX_MANA_PRIMARY_POOL);
        secondaryMax = nbt.getDouble("SecondaryMax", MAX_MANA_SECONDARY_POOL);
        tertiaryMax = nbt.getDouble("TertiaryMax", MAX_MANA_TERTIARY_POOL);
        regenerating = nbt.getBoolean("Regenerating", true);
    }

    /**
     * Pool type enumeration.
     */
    public enum ManaPoolType {
        PRIMARY, SECONDARY, TERTIARY
    }
}
