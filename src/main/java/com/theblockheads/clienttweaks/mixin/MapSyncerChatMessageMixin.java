package com.theblockheads.clienttweaks.mixin;

import com.theblockheads.clienttweaks.compat.MapSyncerMessageFilter;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public abstract class MapSyncerChatMessageMixin {

	@Inject(method = "handleSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"), cancellable = true)
	private void blockheads$silenceMapSyncerSystemMessage(Component message, boolean remote, CallbackInfo ci) {
		if (MapSyncerMessageFilter.shouldSilence(message)) {
			ci.cancel();
		}
	}

	@Inject(method = "handleOverlay(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
	private void blockheads$silenceMapSyncerOverlay(Component message, CallbackInfo ci) {
		if (MapSyncerMessageFilter.shouldSilence(message)) {
			ci.cancel();
		}
	}
}
