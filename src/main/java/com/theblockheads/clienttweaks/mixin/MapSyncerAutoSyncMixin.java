package com.theblockheads.clienttweaks.mixin;

import com.theblockheads.clienttweaks.ClientTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MapSyncerAutoSyncMixin {

	// Short delay after joining before the first sync, so the connection has settled.
	@Unique
	private static final int INITIAL_SYNC_DELAY_TICKS = 200;

	@Unique
	private LocalPlayer blockheads$syncedPlayer;

	@Unique
	private int blockheads$ticksUntilSync;

	@Inject(method = "tick", at = @At("TAIL"))
	@SuppressWarnings("resource") // mixin target cast, not an owned resource
	private void blockheads$autoSyncMapSyncer(CallbackInfo ci) {
		if (!ClientTweaks.CONFIG.mapSyncerAutoSyncEnabled) return;

		Minecraft client = (Minecraft)(Object)this;
		LocalPlayer player = client.player;
		if (player == null || client.isLocalServer()) {
			blockheads$syncedPlayer = null;
			return;
		}

		if (player != blockheads$syncedPlayer) {
			blockheads$syncedPlayer = player;
			blockheads$ticksUntilSync = INITIAL_SYNC_DELAY_TICKS;
			return;
		}

		if (--blockheads$ticksUntilSync > 0) return;

		blockheads$ticksUntilSync = ClientTweaks.CONFIG.mapSyncerAutoSyncIntervalMinutes * 20 * 60;
		player.connection.sendCommand("mapsyncer sync all");
	}
}
