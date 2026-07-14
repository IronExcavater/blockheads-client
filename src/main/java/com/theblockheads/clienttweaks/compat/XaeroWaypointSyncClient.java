package com.theblockheads.clienttweaks.compat;

import com.theblockheads.clienttweaks.ClientTweaksConstants;
import com.theblockheads.clienttweaks.network.WaypointSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.path.XaeroPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class XaeroWaypointSyncClient {
    private static final String DEFAULT_SET = "Blockheads";

    private XaeroWaypointSyncClient() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(WaypointSyncPayload.TYPE, WaypointSyncPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(WaypointSyncPayload.TYPE, (payload, context) ->
            context.client().execute(() -> apply(payload)));
        ClientTweaksConstants.LOGGER.info("Blockheads Xaero waypoint sync registered");
    }

    private static void apply(WaypointSyncPayload payload) {
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        if (session == null || session.getWorldState().getAutoRootContainerPath() == null) {
            ClientTweaksConstants.LOGGER.warn("Skipped Blockheads waypoint sync for {} because Xaero is not ready", payload.name());
            return;
        }

        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, Identifier.parse(payload.dimension()));
        MinimapWorld world = worldFor(session, dimension);
        if (world == null) {
            ClientTweaksConstants.LOGGER.warn("Skipped Blockheads waypoint sync for {} because no Xaero world was available", payload.name());
            return;
        }

        WaypointSet existingSet = findSetContaining(world, payload.name());
        removeByName(world, payload.name());

        if (!payload.delete()) {
            WaypointSet targetSet = existingSet != null ? existingSet : set(world, cleanSetName(payload.setName()));
            Waypoint waypoint = new Waypoint(
                payload.x(),
                payload.y(),
                payload.z(),
                safeWaypointText(payload.name(), 32, "Waypoint"),
                safeWaypointText(payload.initials(), 3, "WP"),
                WaypointColor.fromIndex(Math.floorMod(payload.color(), WaypointColor.values().length)),
                WaypointPurpose.NORMAL,
                false,
                true
            );
            targetSet.add(waypoint);
            session.getWaypointSession().setSetChangedTime(System.currentTimeMillis());
        }

        try {
            session.getWorldManagerIO().saveWorld(world);
        } catch (IOException e) {
            ClientTweaksConstants.LOGGER.warn("Could not save Xaero waypoint sync for {}", payload.name(), e);
        }
    }

    private static MinimapWorld worldFor(MinimapSession session, ResourceKey<Level> dimension) {
        MinimapWorld current = session.getWorldManager().getCurrentWorld();
        if (current != null && dimension.equals(current.getDimId())) {
            return current;
        }

        MinimapWorld autoWorld = session.getWorldManager().getAutoWorld();
        String dimensionDirectory = session.getDimensionHelper().getDimensionDirectoryName(dimension);
        XaeroPath containerPath = session.getWorldState().getAutoRootContainerPath().resolve(dimensionDirectory);
        MinimapWorldContainer container = session.getWorldManager().getWorldContainer(containerPath);
        if (autoWorld != null && container == autoWorld.getContainer()) {
            return autoWorld;
        }

        MinimapWorld world = autoWorld == null ? null : container.getFirstWorldConnectedTo(autoWorld);
        if (world == null) {
            world = container.getFirstWorld();
        }
        if (world == null) {
            world = container.addWorld(session.getWorldStateUpdater().getPotentialWorldNode(dimension, false));
        }
        return world;
    }

    private static WaypointSet set(MinimapWorld world, String setName) {
        WaypointSet set = world.getWaypointSet(setName);
        if (set == null) {
            world.addWaypointSet(setName);
            set = world.getWaypointSet(setName);
        }
        if (set == null) {
            set = world.getCurrentWaypointSet();
        }
        return set;
    }

    private static WaypointSet findSetContaining(MinimapWorld world, String name) {
        for (WaypointSet set : world.getIterableWaypointSets()) {
            for (Waypoint waypoint : set.getWaypoints()) {
                if (sameName(waypoint, name)) {
                    return set;
                }
            }
        }
        return null;
    }

    private static void removeByName(MinimapWorld world, String name) {
        for (WaypointSet set : world.getIterableWaypointSets()) {
            List<Waypoint> toRemove = new ArrayList<>();
            for (Waypoint waypoint : set.getWaypoints()) {
                if (sameName(waypoint, name)) {
                    toRemove.add(waypoint);
                }
            }
            toRemove.forEach(set::remove);
        }
    }

    private static boolean sameName(Waypoint waypoint, String name) {
        return waypoint.getName().trim().equalsIgnoreCase(name.trim());
    }

    private static String cleanSetName(String value) {
        String cleaned = value == null ? "" : value.replace(":", "").trim();
        return cleaned.isEmpty() ? DEFAULT_SET : safeWaypointText(cleaned, 32, DEFAULT_SET);
    }

    private static String safeWaypointText(String value, int maxLength, String fallback) {
        String cleaned = value == null ? "" : value.replace(":", "").replace('\n', ' ').replace('\r', ' ').trim();
        if (cleaned.isEmpty()) {
            cleaned = fallback;
        }
        cleaned = cleaned.replaceAll("\\s+", " ");
        if (cleaned.length() <= maxLength) {
            return cleaned;
        }
        String truncated = cleaned.substring(0, maxLength).trim();
        int lastSpace = truncated.lastIndexOf(' ');
        return (lastSpace > 0 ? truncated.substring(0, lastSpace) : truncated).toUpperCase(Locale.ROOT).isBlank() ? fallback : truncated;
    }
}
