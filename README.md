# Blockheads Client Tweaks

Fabric client mod for The Blockheads server.

## What it does

Narrows the creative inventory search box from its default width to 70px, preventing it from overlapping the sort buttons added by [Inventory Profiles Next](https://modrinth.com/mod/inventory-profiles-next).

| Without mod | With mod |
|---|---|
| Search box overlaps IPN sort buttons | Search box sits clear of IPN sort buttons |

## Requirements

- Minecraft 26.1.x
- [Fabric Loader](https://fabricmc.net/use/installer/) ≥ 0.15.0
- This is a **client-side only** mod — not required on the server

## Building

```sh
./gradlew build
```

Output jar is at `build/libs/blockheads-client-tweaks-<version>.jar`.

Requires JDK 25 ([Temurin](https://adoptium.net/temurin/releases/?version=25) recommended).

## Releases

Pre-built jars are attached to each [GitHub Release](../../releases).
