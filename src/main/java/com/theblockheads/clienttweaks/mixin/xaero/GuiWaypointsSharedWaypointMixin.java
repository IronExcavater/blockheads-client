package com.theblockheads.clienttweaks.mixin.xaero;

import com.theblockheads.clienttweaks.waypoints.SharedWaypointClient;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.minimap.waypoints.Waypoint;

import java.util.ArrayList;

@Pseudo
@Mixin(targets = "xaero.common.gui.GuiWaypoints", remap = false)
public abstract class GuiWaypointsSharedWaypointMixin {
    @Shadow(remap = false)
    private Button editButton;

    @Shadow(remap = false)
    private Button deleteButton;

    @Shadow(remap = false)
    private ArrayList<Waypoint> getSelectedWaypointsList() {
        throw new AssertionError();
    }

    @Inject(method = "updateButtons()V", at = @At("TAIL"), require = 0, remap = false)
    private void blockheads$protectSharedWaypoints(CallbackInfo ci) {
        ArrayList<Waypoint> selectedWaypoints = getSelectedWaypointsList();
        boolean containsSharedWaypoint = selectedWaypoints.stream().anyMatch(SharedWaypointClient::isShared);
        if (!containsSharedWaypoint) {
            return;
        }

        if (editButton != null) {
            editButton.active = false;
        }
        if (deleteButton != null) {
            deleteButton.active = false;
        }
    }
}
