# Blockheads Client Tweaks

Fabric client mod for The Blockheads server.

## What it does

Client-side quality-of-life tweaks for the Blockheads server.

- Narrows the creative inventory search box so it does not overlap the sort buttons added by [Inventory Profiles Next](https://modrinth.com/mod/inventory-profiles-next).
- Syncs server-backed `/waypoint shared` entries into Xaero's Minimap/World Map as temporary waypoints in the `Blockheads Shared` set.
- Prevents native Xaero edit/delete actions on shared waypoints so server permissions remain authoritative.

## Requirements

- Minecraft 26.1.x
- [Fabric Loader](https://fabricmc.net/use/installer/) >= 0.15.0
- Xaero's Minimap for shared waypoint display
- This is a client-side only mod.

## Building

```sh
./gradlew build
```

Output jar is at `build/libs/blockheads-client-tweaks-<version>.jar`.

Requires JDK 25 ([Temurin](https://adoptium.net/temurin/releases/?version=25) recommended).

## Releases

Pre-built jars are attached to each [GitHub Release](../../releases).
