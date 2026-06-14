package com.theblockheads.clienttweaks.mixin;

import com.theblockheads.clienttweaks.ClientTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Suppresses MapSyncer's "Progress: x/y", "Sync completed!" and "Sync cancelled" chat
 * messages when the "/mapsyncer silence" toggle is on. @Pseudo + require=0 so this
 * degrades silently if MapSyncer is missing or its internals change.
 */
@Pseudo
@Mixin(targets = "com.mapsyncer.client.SyncProgressTracker")
public abstract class MapSyncerSyncProgressMixin {

	@Inject(method = "displayProgress()V", at = @At("HEAD"), cancellable = true, require = 0)
	private static void blockheads$silenceProgress(CallbackInfo ci) {
		if (ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced) {
			ci.cancel();
		}
	}

	@Inject(method = "completeWithCount(I)V", at = @At("HEAD"), cancellable = true, require = 0)
	private static void blockheads$silenceComplete(int count, CallbackInfo ci) {
		if (ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced) {
			ci.cancel();
		}
	}

	@Inject(method = "cancelTracking()V", at = @At("HEAD"), cancellable = true, require = 0)
	private static void blockheads$silenceCancel(CallbackInfo ci) {
		if (ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced) {
			ci.cancel();
		}
	}
}
