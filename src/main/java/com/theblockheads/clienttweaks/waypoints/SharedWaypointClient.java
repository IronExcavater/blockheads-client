package com.theblockheads.clienttweaks.waypoints;

import com.theblockheads.clienttweaks.ClientTweaksConstants;
import com.theblockheads.clienttweaks.network.SharedWaypointsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class SharedWaypointClient {
    private static final String SET_NAME = "Blockheads Shared";
    private static final Map<UUID, InsertedWaypoint> INSERTED = new HashMap<>();

    private SharedWaypointClient() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(SharedWaypointsPayload.TYPE, SharedWaypointsPayload.CODEC);
        ClientTweaksConstants.LOGGER.info("Shared waypoint client sync registered");

        ClientPlayNetworking.registerGlobalReceiver(SharedWaypointsPayload.TYPE, (payload, context) ->
            context.client().execute(() -> apply(payload)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> clear());
    }

    public static boolean isShared(Waypoint waypoint) {
        return waypoint instanceof BlockheadsSharedWaypoint;
    }

    public static boolean canEdit(Waypoint waypoint) {
        return waypoint instanceof BlockheadsSharedWaypoint shared && shared.canEditShared();
    }

    public static boolean isAvailable() {
        return FabricLoader.getInstance().isModLoaded("xaerominimap");
    }

    private static void apply(SharedWaypointsPayload payload) {
        if (!isAvailable()) {
            return;
        }
        MinimapSession session = session();
        if (session == null) {
            return;
        }

        clearInserted();
        for (SharedWaypointsPayload.Entry entry : payload.waypoints()) {
            MinimapWorld world = worldFor(session, entry);
            if (world == null) {
                continue;
            }
            WaypointSet set = getOrCreateSet(world);
            BlockheadsSharedWaypoint waypoint = new BlockheadsSharedWaypoint(
                entry.id(),
                entry.x(),
                entry.y(),
                entry.z(),
                entry.name(),
                entry.initials(),
                entry.color(),
                entry.scope(),
                entry.owner(),
                entry.canEdit()
            );
            set.add(waypoint);
            INSERTED.put(entry.id(), new InsertedWaypoint(set, waypoint));
        }
    }

    private static void clear() {
        clearInserted();
        INSERTED.clear();
    }

    private static void clearInserted() {
        INSERTED.values().forEach(inserted -> inserted.set().remove(inserted.waypoint()));
        INSERTED.clear();
    }

    private static MinimapSession session() {
        return BuiltInHudModules.MINIMAP.getCurrentSession() instanceof MinimapSession session ? session : null;
    }

    private static WaypointSet getOrCreateSet(MinimapWorld world) {
        WaypointSet set = world.getWaypointSet(SET_NAME);
        if (set == null) {
            world.addWaypointSet(SET_NAME);
            set = world.getWaypointSet(SET_NAME);
        }
        return set == null ? world.getCurrentWaypointSet() : set;
    }

    private static MinimapWorld worldFor(MinimapSession session, SharedWaypointsPayload.Entry entry) {
        ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, entry.dimension());
        MinimapWorld current = session.getWorldManager().getCurrentWorld();
        if (current != null && dimension.equals(current.getDimId())) {
            return current;
        }
        MinimapWorld fromRoot = findWorld(session.getWorldManager().getCurrentRootContainer(), dimension);
        if (fromRoot != null) {
            return fromRoot;
        }
        MinimapWorld auto = session.getWorldManager().getAutoWorld();
        return auto != null && dimension.equals(auto.getDimId()) ? auto : null;
    }

    private static MinimapWorld findWorld(MinimapWorldContainer container, ResourceKey<Level> dimension) {
        if (container == null) {
            return null;
        }
        for (MinimapWorld world : container.getWorlds()) {
            if (dimension.equals(world.getDimId())) {
                return world;
            }
        }
        Iterator<MinimapWorldContainer> iterator = container.getSubContainers().iterator();
        while (iterator.hasNext()) {
            MinimapWorld world = findWorld(iterator.next(), dimension);
            if (world != null) {
                return world;
            }
        }
        return null;
    }

    private record InsertedWaypoint(WaypointSet set, BlockheadsSharedWaypoint waypoint) {
    }
}
