package com.theblockheads.clienttweaks.waypoints;

import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;

import java.util.UUID;

public final class BlockheadsSharedWaypoint extends Waypoint {
    private final UUID id;
    private final String scope;
    private final String owner;
    private final boolean canEdit;

    public BlockheadsSharedWaypoint(UUID id, int x, int y, int z, String name, String initials, int color, String scope, String owner, boolean canEdit) {
        super(x, y, z, name, initials, WaypointColor.fromIndex(Math.floorMod(color, 16)), WaypointPurpose.NORMAL, true, true);
        this.id = id;
        this.scope = scope;
        this.owner = owner;
        this.canEdit = canEdit;
        setVisibility(WaypointVisibilityType.LOCAL);
    }

    public UUID id() {
        return id;
    }

    public String scope() {
        return scope;
    }

    public String owner() {
        return owner;
    }

    public boolean canEditShared() {
        return canEdit;
    }
}
