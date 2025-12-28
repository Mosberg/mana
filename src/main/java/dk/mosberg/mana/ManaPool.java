package dk.mosberg.mana;

import org.jetbrains.annotations.NotNull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * Manages a player's three mana pools with automatic regeneration. Uses game ticks for
 * deterministic, multiplayer-safe timing.
 *
 * <p>
 * Pool Value System: Each pool has both current mana and a "pool value" which represents the
 * base/intrinsic capacity. Pool values can be increased through leveling, items, or other
 * progression mechanics, allowing for permanent mana pool expansion.
 */
public class ManaPool {

    // Default constants
    private static final double REGEN_RATE_PRIMARY = 1.0;
    private static final double REGEN_RATE_SECONDARY = 0.75;
    private static final double REGEN_RATE_TERTIARY = 0.5;
    private static final double DEFAULT_PRIMARY_POOL_VALUE = 250.0;
    private static final double DEFAULT_SECONDARY_POOL_VALUE = 500.0;
    private static final double DEFAULT_TERTIARY_POOL_VALUE = 1000.0;
    private static final int TICKS_PER_SECOND = 20;

    // Current mana in each pool
    private double primaryMana;
    private double secondaryMana;
    private double tertiaryMana;

    // Pool values (base capacity from progression/leveling)
    private double primaryPoolValue;
    private double secondaryPoolValue;
    private double tertiaryPoolValue;

    // Temporary max modifiers (buffs, debuffs, equipment bonuses)
    private double primaryMaxModifier;
    private double secondaryMaxModifier;
    private double tertiaryMaxModifier;

    // Regeneration state
    private boolean regenerating = true;

    /**
     * Creates a new ManaPool with default pool values.
     */
    public ManaPool() {
        this(DEFAULT_PRIMARY_POOL_VALUE, DEFAULT_SECONDARY_POOL_VALUE, DEFAULT_TERTIARY_POOL_VALUE);
    }

    /**
     * Creates a new ManaPool with custom pool values.
     *
     * @param primaryPoolValue Base primary pool capacity
     * @param secondaryPoolValue Base secondary pool capacity
     * @param tertiaryPoolValue Base tertiary pool capacity
     */
    public ManaPool(double primaryPoolValue, double secondaryPoolValue, double tertiaryPoolValue) {
        this.primaryPoolValue = Math.max(0, primaryPoolValue);
        this.secondaryPoolValue = Math.max(0, secondaryPoolValue);
        this.tertiaryPoolValue = Math.max(0, tertiaryPoolValue);

        // Initialize modifiers to zero
        this.primaryMaxModifier = 0;
        this.secondaryMaxModifier = 0;
        this.tertiaryMaxModifier = 0;

        // Start with full mana
        this.primaryMana = getPrimaryMax();
        this.secondaryMana = getSecondaryMax();
        this.tertiaryMana = getTertiaryMax();
    }

    /**
     * Update mana regeneration based on game ticks. Called every tick from the server.
     *
     * @param player The player entity
     */
    public void tick(@NotNull PlayerEntity player) {
        if (!regenerating) {
            return;
        }

        // Calculate regen per tick (divide by ticks per second)
        double primaryRegenPerTick = REGEN_RATE_PRIMARY / TICKS_PER_SECOND;
        double secondaryRegenPerTick = REGEN_RATE_SECONDARY / TICKS_PER_SECOND;
        double tertiaryRegenPerTick = REGEN_RATE_TERTIARY / TICKS_PER_SECOND;

        // Regenerate each pool independently
        if (primaryMana < getPrimaryMax()) {
            primaryMana = Math.min(getPrimaryMax(), primaryMana + primaryRegenPerTick);
        }

        if (secondaryMana < getSecondaryMax()) {
            secondaryMana = Math.min(getSecondaryMax(), secondaryMana + secondaryRegenPerTick);
        }

        if (tertiaryMana < getTertiaryMax()) {
            tertiaryMana = Math.min(getTertiaryMax(), tertiaryMana + tertiaryRegenPerTick);
        }
    }

    /**
     * Consume mana from pools in priority order (primary → secondary → tertiary).
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
     * Restore mana to pools in priority order (primary → secondary → tertiary).
     *
     * @param amount Amount of mana to restore
     */
    public void restoreMana(double amount) {
        if (amount <= 0) {
            return;
        }

        double remaining = amount;

        // Fill primary first
        double primarySpace = getPrimaryMax() - primaryMana;
        if (primarySpace > 0 && remaining > 0) {
            double toAdd = Math.min(remaining, primarySpace);
            primaryMana += toAdd;
            remaining -= toAdd;
        }

        // Then secondary
        if (remaining > 0) {
            double secondarySpace = getSecondaryMax() - secondaryMana;
            if (secondarySpace > 0) {
                double toAdd = Math.min(remaining, secondarySpace);
                secondaryMana += toAdd;
                remaining -= toAdd;
            }
        }

        // Finally tertiary
        if (remaining > 0) {
            double tertiarySpace = getTertiaryMax() - tertiaryMana;
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
    public void restorePool(@NotNull ManaPoolType type) {
        switch (type) {
            case PRIMARY -> primaryMana = getPrimaryMax();
            case SECONDARY -> secondaryMana = getSecondaryMax();
            case TERTIARY -> tertiaryMana = getTertiaryMax();
        }
    }

    /**
     * Instantly restore all pools to maximum.
     */
    public void restoreAll() {
        primaryMana = getPrimaryMax();
        secondaryMana = getSecondaryMax();
        tertiaryMana = getTertiaryMax();
    }

    /**
     * Permanently increase a pool's base capacity (pool value). This represents permanent
     * progression like leveling up.
     *
     * @param type The pool type
     * @param amount Amount to increase the pool value by
     */
    public void increasePoolValue(@NotNull ManaPoolType type, double amount) {
        if (amount <= 0) {
            return;
        }

        switch (type) {
            case PRIMARY -> {
                primaryPoolValue += amount;
                // Also increase current mana proportionally
                primaryMana = Math.min(primaryMana + amount, getPrimaryMax());
            }
            case SECONDARY -> {
                secondaryPoolValue += amount;
                secondaryMana = Math.min(secondaryMana + amount, getSecondaryMax());
            }
            case TERTIARY -> {
                tertiaryPoolValue += amount;
                tertiaryMana = Math.min(tertiaryMana + amount, getTertiaryMax());
            }
        }
    }

    /**
     * Set the base pool value directly (for commands/admin tools).
     *
     * @param type The pool type
     * @param value The new pool value
     */
    public void setPoolValue(@NotNull ManaPoolType type, double value) {
        if (value < 0) {
            return;
        }

        switch (type) {
            case PRIMARY -> {
                primaryPoolValue = value;
                primaryMana = Math.min(primaryMana, getPrimaryMax());
            }
            case SECONDARY -> {
                secondaryPoolValue = value;
                secondaryMana = Math.min(secondaryMana, getSecondaryMax());
            }
            case TERTIARY -> {
                tertiaryPoolValue = value;
                tertiaryMana = Math.min(tertiaryMana, getTertiaryMax());
            }
        }
    }

    /**
     * Apply temporary max modifier (from equipment, buffs, etc.). These don't persist and should be
     * reapplied on login.
     *
     * @param type The pool type
     * @param modifier The modifier amount (can be positive or negative)
     */
    public void applyMaxModifier(@NotNull ManaPoolType type, double modifier) {
        switch (type) {
            case PRIMARY -> {
                primaryMaxModifier += modifier;
                // Clamp current mana if max decreased
                primaryMana = Math.min(primaryMana, getPrimaryMax());
            }
            case SECONDARY -> {
                secondaryMaxModifier += modifier;
                secondaryMana = Math.min(secondaryMana, getSecondaryMax());
            }
            case TERTIARY -> {
                tertiaryMaxModifier += modifier;
                tertiaryMana = Math.min(tertiaryMana, getTertiaryMax());
            }
        }
    }

    /**
     * Clear all temporary max modifiers.
     */
    public void clearMaxModifiers() {
        primaryMaxModifier = 0;
        secondaryMaxModifier = 0;
        tertiaryMaxModifier = 0;

        // Clamp current mana values
        primaryMana = Math.min(primaryMana, getPrimaryMax());
        secondaryMana = Math.min(secondaryMana, getSecondaryMax());
        tertiaryMana = Math.min(tertiaryMana, getTertiaryMax());
    }

    /**
     * Expand all pool values by a given amount.
     *
     * @param amount Amount to expand each pool value
     */
    public void expandAllPools(double amount) {
        increasePoolValue(ManaPoolType.PRIMARY, amount);
        increasePoolValue(ManaPoolType.SECONDARY, amount);
        increasePoolValue(ManaPoolType.TERTIARY, amount);
    }

    /**
     * Share mana with another ManaPool.
     *
     * @param other The other ManaPool to share with
     * @param amount Amount to share
     * @return true if mana was shared, false otherwise
     */
    public boolean shareMana(@NotNull ManaPool other, double amount) {
        if (amount <= 0 || getTotalMana() < amount) {
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

    // ==================== GETTERS: Current Mana ====================

    public double getPrimaryMana() {
        return primaryMana;
    }

    public double getSecondaryMana() {
        return secondaryMana;
    }

    public double getTertiaryMana() {
        return tertiaryMana;
    }

    public double getTotalMana() {
        return primaryMana + secondaryMana + tertiaryMana;
    }

    // ==================== GETTERS: Pool Values ====================

    public double getPrimaryPoolValue() {
        return primaryPoolValue;
    }

    public double getSecondaryPoolValue() {
        return secondaryPoolValue;
    }

    public double getTertiaryPoolValue() {
        return tertiaryPoolValue;
    }

    // ==================== GETTERS: Effective Max (Pool Value + Modifiers)
    // ====================

    public double getPrimaryMax() {
        return Math.max(0, primaryPoolValue + primaryMaxModifier);
    }

    public double getSecondaryMax() {
        return Math.max(0, secondaryPoolValue + secondaryMaxModifier);
    }

    public double getTertiaryMax() {
        return Math.max(0, tertiaryPoolValue + tertiaryMaxModifier);
    }

    public double getTotalMaxMana() {
        return getPrimaryMax() + getSecondaryMax() + getTertiaryMax();
    }

    // ==================== GETTERS: Percentages ====================

    public double getPrimaryPercent() {
        double max = getPrimaryMax();
        return max > 0 ? primaryMana / max : 0.0;
    }

    public double getSecondaryPercent() {
        double max = getSecondaryMax();
        return max > 0 ? secondaryMana / max : 0.0;
    }

    public double getTertiaryPercent() {
        double max = getTertiaryMax();
        return max > 0 ? tertiaryMana / max : 0.0;
    }

    public double getTotalPercent() {
        double totalMax = getTotalMaxMana();
        return totalMax > 0 ? getTotalMana() / totalMax : 0.0;
    }

    // ==================== GETTERS: Modifiers ====================

    public double getPrimaryMaxModifier() {
        return primaryMaxModifier;
    }

    public double getSecondaryMaxModifier() {
        return secondaryMaxModifier;
    }

    public double getTertiaryMaxModifier() {
        return tertiaryMaxModifier;
    }

    // ==================== GETTERS: State ====================

    public boolean isRegenerating() {
        return regenerating;
    }

    /**
     * Check if a specific pool is full.
     */
    public boolean isPoolFull(@NotNull ManaPoolType type) {
        return switch (type) {
            case PRIMARY -> primaryMana >= getPrimaryMax();
            case SECONDARY -> secondaryMana >= getSecondaryMax();
            case TERTIARY -> tertiaryMana >= getTertiaryMax();
        };
    }

    /**
     * Check if all pools are full.
     */
    public boolean isAllPoolsFull() {
        return primaryMana >= getPrimaryMax() && secondaryMana >= getSecondaryMax()
                && tertiaryMana >= getTertiaryMax();
    }

    /**
     * Check if all pools are empty.
     */
    public boolean isAllPoolsEmpty() {
        return primaryMana <= 0 && secondaryMana <= 0 && tertiaryMana <= 0;
    }

    // ==================== SETTERS (For Commands) ====================

    /**
     * Directly set current mana (for commands/debugging).
     */
    public void setPrimaryMana(double value) {
        this.primaryMana = Math.max(0, Math.min(value, getPrimaryMax()));
    }

    public void setSecondaryMana(double value) {
        this.secondaryMana = Math.max(0, Math.min(value, getSecondaryMax()));
    }

    public void setTertiaryMana(double value) {
        this.tertiaryMana = Math.max(0, Math.min(value, getTertiaryMax()));
    }

    // ==================== NBT SERIALIZATION ====================

    /**
     * Save to NBT. Saves current mana, pool values, and modifiers.
     *
     * @param nbt The NBT compound to write to
     * @return The modified NBT compound
     */
    @NotNull
    public NbtCompound writeNbt(@NotNull NbtCompound nbt) {
        // Current mana
        nbt.putDouble("PrimaryMana", primaryMana);
        nbt.putDouble("SecondaryMana", secondaryMana);
        nbt.putDouble("TertiaryMana", tertiaryMana);

        // Pool values (permanent progression)
        nbt.putDouble("PrimaryPoolValue", primaryPoolValue);
        nbt.putDouble("SecondaryPoolValue", secondaryPoolValue);
        nbt.putDouble("TertiaryPoolValue", tertiaryPoolValue);

        // Modifiers (don't save - these are temporary and should be reapplied)
        // nbt.putDouble("PrimaryMaxModifier", primaryMaxModifier);
        // nbt.putDouble("SecondaryMaxModifier", secondaryMaxModifier);
        // nbt.putDouble("TertiaryMaxModifier", tertiaryMaxModifier);

        // State
        nbt.putBoolean("Regenerating", regenerating);

        return nbt;
    }

    /**
     * Load from NBT. Reads current mana and pool values.
     *
     * @param nbt The NBT compound to read from
     */
    public void readNbt(@NotNull NbtCompound nbt) {
        // Read pool values first
        primaryPoolValue = nbt.getDouble("PrimaryPoolValue", DEFAULT_PRIMARY_POOL_VALUE);
        secondaryPoolValue = nbt.getDouble("SecondaryPoolValue", DEFAULT_SECONDARY_POOL_VALUE);
        tertiaryPoolValue = nbt.getDouble("TertiaryPoolValue", DEFAULT_TERTIARY_POOL_VALUE);

        // Reset modifiers (temporary, will be reapplied by equipment/buffs)
        primaryMaxModifier = 0;
        secondaryMaxModifier = 0;
        tertiaryMaxModifier = 0;

        // Read current mana (default to max if not present)
        primaryMana = nbt.getDouble("PrimaryMana", getPrimaryMax());
        secondaryMana = nbt.getDouble("SecondaryMana", getSecondaryMax());
        tertiaryMana = nbt.getDouble("TertiaryMana", getTertiaryMax());

        // Clamp mana values to current max (in case pool values changed)
        primaryMana = Math.min(primaryMana, getPrimaryMax());
        secondaryMana = Math.min(secondaryMana, getSecondaryMax());
        tertiaryMana = Math.min(tertiaryMana, getTertiaryMax());

        // State
        regenerating = nbt.getBoolean("Regenerating", true);
    }

    /**
     * Pool type enumeration.
     */
    public enum ManaPoolType {
        PRIMARY, SECONDARY, TERTIARY
    }

    public void increaseMaxMana(ManaPoolType type, double difference) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'increaseMaxMana'");
    }
}
