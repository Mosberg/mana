# 🔮 Mana System - Minecraft Fabric Mod

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.10-brightgreen.svg)](https://fabricmc.net/)
[![Fabric API](https://img.shields.io/badge/Fabric%20API-0.138.4-blue.svg)](https://fabricmc.net/use/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)

A production-ready mana system for Minecraft 1.21.10 Fabric with three independent mana pools, automatic regeneration, and a beautiful HUD overlay. Designed for modpack creators and magic mod developers.

**[Repository](https://github.com/Mosberg/mana)** • **[Issues](https://github.com/mosberg/mana/issues)** • **[Wiki](https://mosberg.github.io/mana)**

---

## ✨ Features

### Core System

- **🎯 Three Independent Mana Pools**: Primary (250), Secondary (500), and Tertiary (1000) with cascading consumption
- **⚡ Automatic Regeneration**: Configurable tick-based regeneration (1.0/0.75/0.5 per second)
- **💾 Persistent Storage**: UUID-based player data with NBT serialization
- **🔄 Multiplayer-Safe**: Deterministic tick-based timing for perfect server synchronization
- **🎮 Client-Server Architecture**: Proper separation for dedicated server support

### Visual Interface

- **📊 Customizable HUD Overlay**: Three-tier mana bars with smooth animations
- **❤️ Health Bar Integration**: Shows player health alongside mana
- **🎨 Status Effect Display**: Visual indicators for active effects
- **🎛️ Fully Configurable**: Scale, position, transparency, and visibility options

### Developer Friendly

- **📚 Comprehensive API**: Easy integration for spell systems and magic mods
- **🔧 ModPack Ready**: Extensive JSON configuration with multipliers for balancing
- **🛡️ Thread-Safe**: ConcurrentHashMap-based component storage
- **📝 Well-Documented**: JavaDoc on all public methods with usage examples

---

## 📦 Installation

### For Players

1. **Install Prerequisites**

   - Download and install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.10
   - Download [Fabric API 0.138.4+](https://www.curseforge.com/minecraft/mc-mods/fabric-api)

2. **Install Mana System**

   - Place both the Fabric API and Mana System JAR files in your `.minecraft/mods` folder
   - Launch Minecraft with the Fabric profile

3. **Optional Enhancements**
   - [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) - View installed mods in-game
   - [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) - GUI configuration (future support)

### For Modpack Creators

Add to your modpack manifest:

```
{
  "projectID": "mana-system",
  "fileID": "latest",
  "required": true
}
```

---

## ⚙️ Configuration

Configuration file location: `config/mana.json`

### Default Configuration

```
{
  "overlay.enabled": true,
  "overlay.scale": 1.0,
  "overlay.xOffset": 0,
  "overlay.yOffset": 0,
  "overlay.transparency": 1.0,
  "magic.spell.manaCost.multiplier": 1.0,
  "magic.ritual.difficulty.multiplier": 1.0,
  "render.hud.manaBar.enabled": true
}
```

### Configuration Reference

#### HUD Overlay Settings

| Option                       | Type    | Range    | Default | Description                                     |
| ---------------------------- | ------- | -------- | ------- | ----------------------------------------------- |
| `overlay.enabled`            | boolean | -        | `true`  | Master toggle for the entire HUD overlay        |
| `overlay.scale`              | double  | 0.5-2.0  | `1.0`   | Visual scale of the overlay (1.0 = 100%)        |
| `overlay.xOffset`            | integer | -∞ to +∞ | `0`     | Horizontal pixel offset (+ = right, - = left)   |
| `overlay.yOffset`            | integer | -∞ to +∞ | `0`     | Vertical pixel offset (+ = down, - = up)        |
| `overlay.transparency`       | double  | 0.0-1.0  | `1.0`   | Overlay opacity (0.0 = invisible, 1.0 = opaque) |
| `render.hud.manaBar.enabled` | boolean | -        | `true`  | Toggle individual mana bar rendering            |

#### Gameplay Balance Settings

| Option                               | Type   | Range | Default | Description                                      |
| ------------------------------------ | ------ | ----- | ------- | ------------------------------------------------ |
| `magic.spell.manaCost.multiplier`    | double | 0.0+  | `1.0`   | Global spell cost multiplier (2.0 = double cost) |
| `magic.ritual.difficulty.multiplier` | double | 0.0+  | `1.0`   | Ritual difficulty scaling (1.5 = 50% harder)     |

### Example Configurations

**Performance Mode** (Minimal HUD):

```
{
  "overlay.scale": 0.75,
  "overlay.transparency": 0.6
}
```

**Hardcore Balance** (Expensive Spells):

```
{
  "magic.spell.manaCost.multiplier": 2.0,
  "magic.ritual.difficulty.multiplier": 1.5
}
```

**Custom Positioning** (Top-Right Corner):

```
{
  "overlay.xOffset": 200,
  "overlay.yOffset": -50
}
```

---

## 🏗️ Project Structure

```
mana/
├── src/
│   ├── client/java/dk/mosberg/client/
│   │   ├── overlay/
│   │   │   ├── ManaHudOverlay.java       # Main HUD renderer
│   │   │   ├── HealthBarHelper.java      # Health bar integration
│   │   │   └── StatusIconHelper.java     # Status effect icons
│   │   ├── renderer/
│   │   │   ├── OverlayRenderer.java      # Core rendering logic
│   │   │   ├── RenderHelper.java         # Drawing utilities
│   │   │   ├── ColorHelper.java          # Color interpolation
│   │   │   ├── TextHelper.java           # Text rendering
│   │   │   ├── DrawHelper.java           # Geometric shapes
│   │   │   └── ScreenHelper.java         # Screen calculations
│   │   └── ManaClient.java               # Client entry point
│   └── main/java/dk/mosberg/
│       ├── config/
│       │   └── ManaConfig.java           # Configuration manager
│       ├── mana/
│       │   ├── ManaPool.java             # Three-pool system
│       │   ├── ManaComponent.java        # Player attachment
│       │   ├── ManaComponents.java       # Component registry
│       │   └── ManaComponentProvider.java # Component access
│       ├── util/
│       │   ├── ConfigHelper.java         # Environment variables
│       │   └── ManaPoolHelper.java       # Mana operations
│       └── Mana.java                     # Main mod class
├── resources/
│   ├── assets/mana/
│   │   ├── lang/
│   │   │   └── en_us.json               # English translations
│   │   └── icon.png                      # Mod icon
│   └── fabric.mod.json                   # Mod metadata
└── README.md
```

---

## 👨‍💻 API Usage

### Getting Started

Add Mana System as a dependency in your `build.gradle`:

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation "com.github.Mosberg:mana:1.0.0"
}
```

### Basic Operations

#### Accessing Player Mana

```
import dk.mosberg.Mana;
import dk.mosberg.mana.ManaComponent;
import dk.mosberg.mana.ManaPool;
import net.minecraft.server.network.ServerPlayerEntity;

// Get player's mana component
ManaComponent component = Mana.getManaComponent(player);
if (component != null) {
    ManaPool pool = component.getManaPool();

    // Get mana values
    double totalMana = pool.getTotalMana();
    double primaryMana = pool.getPrimaryMana();
    double maxMana = pool.getTotalMaxMana();

    // Get percentages
    double primaryPercent = pool.getPrimaryPercent(); // 0.0 to 1.0
}
```

#### Consuming Mana (Spell Casting)

```
import dk.mosberg.util.ManaPoolHelper;

// Method 1: Direct consumption
ManaPool pool = component.getManaPool();
if (pool.consumeMana(50.0)) {
    player.sendMessage(Text.literal("Spell cast!"));
} else {
    player.sendMessage(Text.literal("Not enough mana!"));
}

// Method 2: Using helper (recommended)
if (ManaPoolHelper.tryConsumeMana(player, 50.0)) {
    // Cast spell logic here
    castFireball(player);
}
```

#### Restoring Mana (Potions/Items)

```
// Restore 25 mana across all pools
pool.restoreMana(25.0);

// Restore a specific pool to maximum
pool.restorePool(ManaPool.ManaPoolType.PRIMARY);

// Restore all pools to maximum
pool.restoreAll();

// Using helper
ManaPoolHelper.restoreMana(player, 25.0);
```

#### Checking Mana Availability

```
// Check if player has enough mana for a spell
if (ManaPoolHelper.hasEnoughMana(player, 100.0)) {
    // Player can cast expensive spell
}

// Get formatted mana display
String display = ManaPoolHelper.formatMana(player, ManaPool.ManaPoolType.PRIMARY);
// Output: "120 / 250"
```

#### Modifying Maximum Mana (Leveling/Upgrades)

```
// Increase a specific pool's maximum
pool.increaseMaxMana(ManaPool.ManaPoolType.PRIMARY, 20.0);

// Expand all pools at once (level up reward)
pool.expandAllPools(10.0);

// Note: Current mana increases proportionally with max
```

#### Advanced: Controlling Regeneration

```
// Stop mana regeneration (combat debuff)
pool.setRegenerating(false);

// Resume mana regeneration
pool.setRegenerating(true);

// Using helper
ManaPoolHelper.setRegenerating(player, false);
```

#### Advanced: Mana Sharing/Transfer

```
// Transfer mana between players
ManaPool sourcePool = sourceManaComponent.getManaPool();
ManaPool targetPool = targetManaComponent.getManaPool();

if (sourcePool.shareMana(targetPool, 50.0)) {
    // Successfully transferred 50 mana
    source.sendMessage(Text.literal("Transferred 50 mana!"));
    target.sendMessage(Text.literal("Received 50 mana!"));
}
```

### Advanced Integration

#### Custom Spell System Example

```
public class SpellSystem {

    public static boolean castSpell(ServerPlayerEntity player, Spell spell) {
        // Apply cost multiplier from config
        double cost = spell.getManaCost() * ManaConfig.getSpellManaCostMultiplier();

        // Check and consume mana
        if (ManaPoolHelper.tryConsumeMana(player, cost)) {
            // Cast the spell
            spell.execute(player);

            // Optional: Stop regen temporarily during combat
            ManaPoolHelper.setRegenerating(player, false);

            // Resume regen after cooldown
            player.getServer().execute(() -> {
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        ManaPoolHelper.setRegenerating(player, true);
                    }
                }, 5000); // 5 seconds
            });

            return true;
        }

        player.sendMessage(Text.literal("Not enough mana!").formatted(Formatting.RED));
        return false;
    }
}
```

#### Custom Mana Item Example

```
public class ManaPotion extends Item {

    private final double manaRestore;

    public ManaPotion(Settings settings, double manaRestore) {
        super(settings);
        this.manaRestore = manaRestore;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            ManaPoolHelper.restoreMana(serverPlayer, manaRestore);

            serverPlayer.sendMessage(
                Text.literal("Restored " + manaRestore + " mana!")
                    .formatted(Formatting.AQUA)
            );

            // Consume item
            ItemStack stack = user.getStackInHand(hand);
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            return TypedActionResult.success(stack);
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
```

---

## 🔧 Development

### Prerequisites

- **Java Development Kit (JDK)**: 21 or higher ([Adoptium](https://adoptium.net/))
- **Gradle**: 8.0+ (included via wrapper)
- **IDE**: IntelliJ IDEA (recommended), VS Code, or Eclipse

### Building from Source

```
# Clone the repository
git clone https://github.com/Mosberg/mana.git
cd mana

# Build the mod
./gradlew build

# Output: build/libs/mana-1.0.0.jar
```

### Running in Development

```
# Launch development client
./gradlew runClient

# Launch development server
./gradlew runServer

# Generate sources for IDE
./gradlew genSources
```

### Testing

```
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

### Gradle Properties

Create `gradle.properties` in the project root:

```
# JVM Performance Optimization
org.gradle.jvmargs=-Xmx4G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

# Mod Metadata
maven_group=dk.mosberg
archives_base_name=mana
mod_id=mana
mod_version=1.0.0
mod_name=Mana System
mod_author=Mosberg
mod_description=A comprehensive mana system for Minecraft

# Dependencies
minecraft_version=1.21.10
loader_version=0.18.4
yarn_mappings=1.21.10+build.3
loom_version=1.14.10
fabric_version=0.138.4+1.21.10
java_version=21
```

---

## 🏛️ Architecture

### Design Principles

✅ **Thread-Safe Concurrency**

- ConcurrentHashMap for player component storage
- Atomic operations for mana modifications
- No race conditions in multiplayer environments

✅ **Deterministic Timing**

- Tick-based regeneration (not `System.currentTimeMillis()`)
- Multiplayer-safe and replay-compatible
- Consistent behavior across servers

✅ **Efficient Rendering**

- Batched draw calls
- Color caching with interpolation
- Minimal OpenGL state changes

✅ **Data Persistence**

- NBT serialization with proper fallbacks
- UUID-based player identification
- Automatic migration for future versions

### Mana Pool System

The mod implements a **cascading three-pool system**:

1. **Primary Pool** (250 max, 1.0/s regen) - Fast regeneration, used first
2. **Secondary Pool** (500 max, 0.75/s regen) - Moderate regeneration, backup
3. **Tertiary Pool** (1000 max, 0.5/s regen) - Slow regeneration, emergency reserve

**Consumption Order**: Primary → Secondary → Tertiary
**Restoration Order**: Primary → Secondary → Tertiary

This design encourages strategic mana management and rewards players who avoid over-casting.

---

## 📋 Changelog

### Version 1.0.0 (Current - December 2025)

**Initial Release** 🎉

#### Core Features

- ✨ Three independent mana pools with automatic regeneration
- 🎮 Server-side mana management with client synchronization
- 💾 Persistent NBT storage with UUID-based player tracking
- ⚡ Tick-based deterministic timing system

#### User Interface

- 📊 Customizable HUD overlay with three-tier mana bars
- ❤️ Health bar integration
- 🎨 Status effect display
- 🎛️ Position, scale, and transparency controls

#### Configuration

- ⚙️ JSON-based configuration system
- 🔧 Runtime config validation
- 🎯 ModPack-friendly balance multipliers
- 🌍 Environment variable support

#### Developer API

- 📚 Comprehensive public API
- 🛡️ Thread-safe helper utilities
- 📝 Complete JavaDoc documentation
- 🔌 Easy integration for magic mods

#### Code Quality

- ✅ Fixed Minecraft 1.21.10 NBT API compatibility
- ✅ Proper singleton pattern
- ✅ Input validation on all public methods
- ✅ No magic numbers (constants extracted)
- ✅ Null-safe with @Nullable annotations

---

## 🤝 Contributing

Contributions are warmly welcomed! Follow these guidelines:

### Getting Started

1. **Fork** the repository to your GitHub account
2. **Clone** your fork locally

   ```
   git clone https://github.com/YOUR-USERNAME/mana.git
   cd mana
   ```

3. **Create a feature branch**

   ```
   git checkout -b feature/amazing-feature
   ```

4. **Make your changes** with clear commit messages

   ```
   git commit -m "Add amazing feature: description"
   ```

5. **Push** to your fork

   ```
   git push origin feature/amazing-feature
   ```

6. **Open a Pull Request** with a detailed description

### Code Standards

- Follow existing code style (Java conventions)
- Add JavaDoc to all public methods
- Write unit tests for new features
- Ensure `./gradlew build` passes without warnings
- Update README.md for user-facing changes

### Reporting Issues

Use the [GitHub Issues](https://github.com/mosberg/mana/issues) tracker:

- **Bug Report**: Include Minecraft version, mod version, and logs
- **Feature Request**: Describe use case and expected behavior
- **Question**: Use [Discussions](https://github.com/mosberg/mana/discussions) instead

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### What This Means

✅ **Commercial Use**: Use in modpacks, even monetized ones
✅ **Modification**: Create derivative works
✅ **Distribution**: Share and redistribute freely
✅ **Private Use**: Use internally without sharing
⚠️ **Attribution**: Credit the original author (Mosberg)
⚠️ **Liability**: Provided "as-is" without warranty

---

## 🙏 Acknowledgments

- **[Fabric Team](https://fabricmc.net/)** - Excellent modding framework and documentation
- **Minecraft Modding Community** - Inspiration, support, and shared knowledge
- **[Yarn Mappings](https://github.com/FabricMC/yarn)** - Deobfuscation and readable code
- **Contributors** - Everyone who reports issues and suggests improvements

---

## 📞 Support

### Need Help?

- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/mosberg/mana/issues)
- 💬 **Questions**: [GitHub Discussions](https://github.com/mosberg/mana/discussions)
- 📖 **Documentation**: [Project Wiki](https://mosberg.github.io/mana)
- 📧 **Contact**: [mosberg@example.com](mailto:mosberg@example.com)

### Community

- 💬 **Discord**: [Join our server](https://discord.gg/yourserver)
- 🐦 **Twitter**: [@Mosberg](https://twitter.com/mosberg)
- 📺 **YouTube**: [Tutorial Videos](https://youtube.com/mosberg)

---

<div align="center">

**Made with ❤️ by [Mosberg](https://github.com/Mosberg)**

_For the Minecraft modding community_

[⬆ Back to Top](#-mana-system---minecraft-fabric-mod)

</div>
