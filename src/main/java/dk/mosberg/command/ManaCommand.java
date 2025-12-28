package dk.mosberg.command;

import java.io.IOException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dk.mosberg.config.ManaConfig;
import dk.mosberg.mana.ManaComponent;
import dk.mosberg.mana.ManaComponents;
import dk.mosberg.mana.ManaPool;
import dk.mosberg.mana.ManaPool.ManaPoolType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Comprehensive command system for the Mana mod. Provides admin and player commands for managing
 * mana pools, configuration, and debugging.
 *
 * <p>
 * Command structure:
 * <ul>
 * <li>/mana get [player] [pool] - Get mana information
 * <li>/mana set &lt;player&gt; &lt;pool&gt; &lt;amount&gt; - Set mana value
 * <li>/mana add &lt;player&gt; &lt;pool&gt; &lt;amount&gt; - Add mana
 * <li>/mana remove &lt;player&gt; &lt;pool&gt; &lt;amount&gt; - Remove mana
 * <li>/mana restore &lt;player&gt; [pool] - Restore mana to maximum
 * <li>/mana setmax &lt;player&gt; &lt;pool&gt; &lt;amount&gt; - Set maximum mana
 * <li>/mana regen &lt;player&gt; &lt;enable|disable&gt; - Control regeneration
 * <li>/mana config &lt;get|set|reload|save&gt; - Configuration management
 * <li>/mana debug - Debug information
 * </ul>
 */
public class ManaCommand {

        // Pool type suggestion provider
        private static final SuggestionProvider<ServerCommandSource> POOL_SUGGESTIONS =
                        (context, builder) -> {
                                return builder.suggest("primary").suggest("secondary")
                                                .suggest("tertiary").suggest("all").buildFuture();
                        };

        /**
         * Registers all mana commands.
         *
         * @param dispatcher The command dispatcher
         * @param registryAccess The registry access
         * @param environment The command environment
         */
        public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher,
                        @NotNull CommandRegistryAccess registryAccess,
                        @NotNull CommandManager.RegistrationEnvironment environment) {
                dispatcher.register(CommandManager.literal("mana")
                                .requires(source -> source.hasPermissionLevel(2))

                                // /mana get [player] [pool]
                                .then(CommandManager.literal("get")
                                                .executes(ManaCommand::getSelfMana)
                                                .then(CommandManager.argument("player",
                                                                EntityArgumentType.player())
                                                                .executes(ManaCommand::getPlayerMana)
                                                                .then(CommandManager.argument(
                                                                                "pool",
                                                                                StringArgumentType
                                                                                                .word())
                                                                                .suggests(POOL_SUGGESTIONS)
                                                                                .executes(ManaCommand::getPlayerPoolMana))))

                                // /mana set <player> <pool> <amount>
                                .then(CommandManager.literal("set").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .then(CommandManager
                                                                .argument("pool", StringArgumentType
                                                                                .word())
                                                                .suggests(POOL_SUGGESTIONS)
                                                                .then(CommandManager.argument(
                                                                                "amount",
                                                                                DoubleArgumentType
                                                                                                .doubleArg(0))
                                                                                .executes(ManaCommand::setMana)))))

                                // /mana add <player> <pool> <amount>
                                .then(CommandManager.literal("add").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .then(CommandManager
                                                                .argument("pool", StringArgumentType
                                                                                .word())
                                                                .suggests(POOL_SUGGESTIONS)
                                                                .then(CommandManager.argument(
                                                                                "amount",
                                                                                DoubleArgumentType
                                                                                                .doubleArg(0))
                                                                                .executes(ManaCommand::addMana)))))

                                // /mana remove <player> <pool> <amount>
                                .then(CommandManager.literal("remove").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .then(CommandManager
                                                                .argument("pool", StringArgumentType
                                                                                .word())
                                                                .suggests(POOL_SUGGESTIONS)
                                                                .then(CommandManager.argument(
                                                                                "amount",
                                                                                DoubleArgumentType
                                                                                                .doubleArg(0))
                                                                                .executes(ManaCommand::removeMana)))))

                                // /mana restore <player> [pool]
                                .then(CommandManager.literal("restore").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .executes(ManaCommand::restoreAllMana)
                                                .then(CommandManager
                                                                .argument("pool", StringArgumentType
                                                                                .word())
                                                                .suggests(POOL_SUGGESTIONS)
                                                                .executes(ManaCommand::restorePoolMana))))

                                // /mana setmax <player> <pool> <amount>
                                .then(CommandManager.literal("setmax").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .then(CommandManager
                                                                .argument("pool", StringArgumentType
                                                                                .word())
                                                                .suggests(POOL_SUGGESTIONS)
                                                                .then(CommandManager.argument(
                                                                                "amount",
                                                                                DoubleArgumentType
                                                                                                .doubleArg(0))
                                                                                .executes(ManaCommand::setMaxMana)))))

                                // /mana regen <player> <enable|disable>
                                .then(CommandManager.literal("regen").then(CommandManager
                                                .argument("player", EntityArgumentType.player())
                                                .then(CommandManager.literal("enable").executes(
                                                                ctx -> setRegeneration(ctx, true)))
                                                .then(CommandManager.literal("disable")
                                                                .executes(ctx -> setRegeneration(
                                                                                ctx, false)))))

                                // /mana config <get|set|reload|save>
                                .then(CommandManager.literal("config")
                                                .then(CommandManager.literal("reload").executes(
                                                                ManaCommand::reloadConfig))
                                                .then(CommandManager.literal("save")
                                                                .executes(ManaCommand::saveConfig))
                                                .then(CommandManager.literal("list")
                                                                .executes(ManaCommand::listConfig)))

                                // /mana debug
                                .then(CommandManager.literal("debug")
                                                .executes(ManaCommand::debugInfo)));
        }

        // ==================== QUERY COMMANDS ====================

        /**
         * Gets the executor's mana information.
         */
        private static int getSelfMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                return displayPlayerMana(ctx, player);
        }

        /**
         * Gets another player's mana information.
         */
        private static int getPlayerMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                return displayPlayerMana(ctx, player);
        }

        /**
         * Gets a specific pool's mana for a player.
         */
        private static int getPlayerPoolMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();

                if ("all".equals(poolName)) {
                        return displayPlayerMana(ctx, player);
                }

                ManaPoolType type = parsePoolType(poolName);
                if (type == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.invalid_pool"));
                        return 0;
                }

                double current = getPoolValue(pool, type);
                double max = getPoolMax(pool, type);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("%s's %s mana: %.1f / %.1f (%.1f%%)",
                                                player.getName().getString(), poolName, current,
                                                max, (current / max) * 100))
                                .formatted(Formatting.AQUA), false);

                return (int) current;
        }

        /**
         * Displays comprehensive mana information for a player.
         */
        private static int displayPlayerMana(@NotNull CommandContext<ServerCommandSource> ctx,
                        @NotNull ServerPlayerEntity player) {
                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();

                ctx.getSource().sendFeedback(() -> Text
                                .literal("=== " + player.getName().getString() + "'s Mana ===")
                                .formatted(Formatting.GOLD), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Primary: %.1f / %.1f (%.1f%%)",
                                                pool.getPrimaryMana(), pool.getPrimaryMax(),
                                                pool.getPrimaryPercent() * 100))
                                .formatted(Formatting.AQUA), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Secondary: %.1f / %.1f (%.1f%%)",
                                                pool.getSecondaryMana(), pool.getSecondaryMax(),
                                                pool.getSecondaryPercent() * 100))
                                .formatted(Formatting.BLUE), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Tertiary: %.1f / %.1f (%.1f%%)",
                                                pool.getTertiaryMana(), pool.getTertiaryMax(),
                                                pool.getTertiaryPercent() * 100))
                                .formatted(Formatting.DARK_PURPLE), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Total: %.1f / %.1f", pool.getTotalMana(),
                                                pool.getTotalMaxMana()))
                                .formatted(Formatting.GREEN), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal("Regenerating: " + (pool.isRegenerating() ? "Yes" : "No"))
                                .formatted(pool.isRegenerating() ? Formatting.GREEN
                                                : Formatting.RED),
                                false);

                return (int) pool.getTotalMana();
        }

        // ==================== MODIFICATION COMMANDS ====================

        /**
         * Sets a player's mana for a specific pool.
         */
        private static int setMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");
                double amount = DoubleArgumentType.getDouble(ctx, "amount");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();
                ManaPoolType type = parsePoolType(poolName);

                if (type == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.invalid_pool"));
                        return 0;
                }

                setPoolValue(pool, type, amount);

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.set.success",
                                                player.getName().getString(), poolName,
                                                String.format("%.1f", amount))
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Adds mana to a player's pool.
         */
        private static int addMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");
                double amount = DoubleArgumentType.getDouble(ctx, "amount");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();

                if ("all".equals(poolName)) {
                        pool.restoreMana(amount);
                } else {
                        ManaPoolType type = parsePoolType(poolName);
                        if (type == null) {
                                ctx.getSource().sendError(Text
                                                .translatable("mana.command.error.invalid_pool"));
                                return 0;
                        }

                        double current = getPoolValue(pool, type);
                        setPoolValue(pool, type,
                                        Math.min(current + amount, getPoolMax(pool, type)));
                }

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.add.success",
                                                String.format("%.1f", amount),
                                                player.getName().getString(), poolName)
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Removes mana from a player's pool.
         */
        private static int removeMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");
                double amount = DoubleArgumentType.getDouble(ctx, "amount");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();

                if ("all".equals(poolName)) {
                        pool.consumeMana(amount);
                } else {
                        ManaPoolType type = parsePoolType(poolName);
                        if (type == null) {
                                ctx.getSource().sendError(Text
                                                .translatable("mana.command.error.invalid_pool"));
                                return 0;
                        }

                        double current = getPoolValue(pool, type);
                        setPoolValue(pool, type, Math.max(0, current - amount));
                }

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.remove.success",
                                                String.format("%.1f", amount),
                                                player.getName().getString(), poolName)
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Restores all mana pools to maximum.
         */
        private static int restoreAllMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                component.getManaPool().restoreAll();

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.restore.all.success",
                                                player.getName().getString())
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Restores a specific pool to maximum.
         */
        private static int restorePoolMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();

                if ("all".equals(poolName)) {
                        pool.restoreAll();
                } else {
                        ManaPoolType type = parsePoolType(poolName);
                        if (type == null) {
                                ctx.getSource().sendError(Text
                                                .translatable("mana.command.error.invalid_pool"));
                                return 0;
                        }

                        pool.restorePool(type);
                }

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.restore.success",
                                                player.getName().getString(), poolName)
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Sets maximum mana for a pool.
         */
        private static int setMaxMana(@NotNull CommandContext<ServerCommandSource> ctx)
                        throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                String poolName = StringArgumentType.getString(ctx, "pool");
                double amount = DoubleArgumentType.getDouble(ctx, "amount");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                ManaPool pool = component.getManaPool();
                ManaPoolType type = parsePoolType(poolName);

                if (type == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.invalid_pool"));
                        return 0;
                }

                double current = getPoolValue(pool, type);
                double oldMax = getPoolMax(pool, type);
                double difference = amount - oldMax;

                pool.increaseMaxMana(type, difference);

                ctx.getSource().sendFeedback(() -> Text
                                .translatable("mana.command.setMax.success",
                                                player.getName().getString(), poolName,
                                                String.format("%.1f", amount))
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        /**
         * Enables or disables mana regeneration.
         */
        private static int setRegeneration(@NotNull CommandContext<ServerCommandSource> ctx,
                        boolean enable) throws CommandSyntaxException {
                ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                ManaComponent component = ManaComponent.get(player);
                if (component == null) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.command.error.no_component"));
                        return 0;
                }

                component.getManaPool().setRegenerating(enable);

                ctx.getSource().sendFeedback(() -> Text
                                .translatable(enable ? "mana.command.regen.enable"
                                                : "mana.command.regen.disable",
                                                player.getName().getString())
                                .formatted(Formatting.GREEN), true);

                return 1;
        }

        // ==================== CONFIG COMMANDS ====================

        /**
         * Reloads configuration from file.
         */
        private static int reloadConfig(@NotNull CommandContext<ServerCommandSource> ctx) {
                try {
                        ManaConfig.initialize();
                        ctx.getSource().sendFeedback(() -> Text.translatable("mana.config.loaded")
                                        .formatted(Formatting.GREEN), true);
                        return 1;
                } catch (Exception e) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.config.error", e.getMessage()));
                        return 0;
                }
        }

        /**
         * Saves current configuration to file.
         */
        private static int saveConfig(@NotNull CommandContext<ServerCommandSource> ctx) {
                try {
                        ManaConfig.save();
                        ctx.getSource().sendFeedback(() -> Text.translatable("mana.config.saved")
                                        .formatted(Formatting.GREEN), true);
                        return 1;
                } catch (IOException e) {
                        ctx.getSource().sendError(
                                        Text.translatable("mana.config.error", e.getMessage()));
                        return 0;
                }
        }

        /**
         * Lists current configuration values.
         */
        private static int listConfig(@NotNull CommandContext<ServerCommandSource> ctx) {
                ctx.getSource().sendFeedback(() -> Text.literal("=== Mana Configuration ===")
                                .formatted(Formatting.GOLD), false);

                ctx.getSource().sendFeedback(() -> Text.literal(String.format("Overlay Enabled: %s",
                                ManaConfig.isOverlayEnabled())), false);

                ctx.getSource().sendFeedback(() -> Text.literal(
                                String.format("Overlay Scale: %.2f", ManaConfig.getOverlayScale())),
                                false);

                ctx.getSource().sendFeedback(
                                () -> Text.literal(String.format("Overlay Position: (%d, %d)",
                                                ManaConfig.getOverlayXOffset(),
                                                ManaConfig.getOverlayYOffset())),
                                false);

                ctx.getSource().sendFeedback(
                                () -> Text.literal(String.format("Overlay Transparency: %.2f",
                                                ManaConfig.getOverlayTransparency())),
                                false);

                ctx.getSource().sendFeedback(
                                () -> Text.literal(String.format("Spell Cost Multiplier: %.2f",
                                                ManaConfig.getSpellManaCostMultiplier())),
                                false);

                ctx.getSource().sendFeedback(
                                () -> Text.literal(String.format(
                                                "Ritual Difficulty Multiplier: %.2f",
                                                ManaConfig.getRitualDifficultyMultiplier())),
                                false);

                return 1;
        }

        // ==================== DEBUG COMMANDS ====================

        /**
         * Displays debug information about the mana system.
         */
        private static int debugInfo(@NotNull CommandContext<ServerCommandSource> ctx) {
                ctx.getSource().sendFeedback(() -> Text.literal("=== Mana System Debug Info ===")
                                .formatted(Formatting.GOLD), false);

                int componentCount = ManaComponents.size();
                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Active ManaComponents: %d", componentCount))
                                .formatted(Formatting.AQUA), false);

                Collection<ServerPlayerEntity> players =
                                ctx.getSource().getServer().getPlayerManager().getPlayerList();
                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Online Players: %d", players.size()))
                                .formatted(Formatting.AQUA), false);

                ctx.getSource().sendFeedback(() -> Text
                                .literal(String.format("Config File: %s",
                                                ManaConfig.isOverlayEnabled() ? "Loaded" : "Error"))
                                .formatted(Formatting.GREEN), false);

                return 1;
        }

        // ==================== HELPER METHODS ====================

        /**
         * Parses a pool type from a string.
         */
        private static ManaPoolType parsePoolType(String name) {
                return switch (name.toLowerCase()) {
                        case "primary" -> ManaPoolType.PRIMARY;
                        case "secondary" -> ManaPoolType.SECONDARY;
                        case "tertiary" -> ManaPoolType.TERTIARY;
                        default -> null;
                };
        }

        /**
         * Gets the current value of a pool.
         */
        private static double getPoolValue(@NotNull ManaPool pool, @NotNull ManaPoolType type) {
                return switch (type) {
                        case PRIMARY -> pool.getPrimaryMana();
                        case SECONDARY -> pool.getSecondaryMana();
                        case TERTIARY -> pool.getTertiaryMana();
                };
        }

        /**
         * Gets the maximum value of a pool.
         */
        private static double getPoolMax(@NotNull ManaPool pool, @NotNull ManaPoolType type) {
                return switch (type) {
                        case PRIMARY -> pool.getPrimaryMax();
                        case SECONDARY -> pool.getSecondaryMax();
                        case TERTIARY -> pool.getTertiaryMax();
                };
        }

        /**
         * Sets the value of a pool (uses reflection-like approach via restore/consume).
         */
        private static void setPoolValue(@NotNull ManaPool pool, @NotNull ManaPoolType type,
                        double value) {
                double current = getPoolValue(pool, type);
                double max = getPoolMax(pool, type);
                value = Math.max(0, Math.min(value, max));

                if (value > current) {
                        // Need to add mana
                        pool.restorePool(type);
                        if (value < max) {
                                double toRemove = max - value;
                                consumeFromPool(pool, type, toRemove);
                        }
                } else if (value < current) {
                        // Need to remove mana
                        double toRemove = current - value;
                        consumeFromPool(pool, type, toRemove);
                }
        }

        /**
         * Consumes a specific amount from a specific pool.
         */
        private static void consumeFromPool(@NotNull ManaPool pool, @NotNull ManaPoolType type,
                        double amount) {
                // Temporarily modify other pools to force consumption from target pool
                double primaryBackup = pool.getPrimaryMana();
                double secondaryBackup = pool.getSecondaryMana();
                double tertiaryBackup = pool.getTertiaryMana();

                // Zero out pools we don't want to consume from
                switch (type) {
                        case PRIMARY -> {
                                pool.consumeMana(pool.getSecondaryMana() + pool.getTertiaryMana());
                                pool.consumeMana(amount);
                                pool.restoreMana(secondaryBackup + tertiaryBackup);
                        }
                        case SECONDARY -> {
                                pool.consumeMana(pool.getPrimaryMana());
                                pool.consumeMana(pool.getTertiaryMana());
                                pool.consumeMana(amount);
                                pool.restoreMana(primaryBackup + tertiaryBackup);
                        }
                        case TERTIARY -> {
                                pool.consumeMana(pool.getPrimaryMana() + pool.getSecondaryMana());
                                pool.consumeMana(amount);
                                pool.restoreMana(primaryBackup + secondaryBackup);
                        }
                }
        }
}
