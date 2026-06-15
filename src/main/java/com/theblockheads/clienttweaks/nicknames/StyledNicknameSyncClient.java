package com.theblockheads.clienttweaks.nicknames;

import com.theblockheads.clienttweaks.ClientTweaksConstants;
import com.theblockheads.clienttweaks.network.StyledNicknamePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class StyledNicknameSyncClient {
    private static final Map<UUID, Component> NICKNAMES = new ConcurrentHashMap<>();

    private StyledNicknameSyncClient() {
    }

    public static void register() {
        PayloadTypeRegistry.clientboundPlay().register(StyledNicknamePayload.TYPE, StyledNicknamePayload.CODEC);
        ClientTweaksConstants.LOGGER.info("Styled nickname nametag client sync registered");

        ClientPlayNetworking.registerGlobalReceiver(StyledNicknamePayload.TYPE, (payload, context) ->
            context.client().execute(() -> {
                NICKNAMES.clear();
                NICKNAMES.putAll(payload.nicknames());
            }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> NICKNAMES.clear());
    }

    public static Component get(UUID uuid) {
        return NICKNAMES.get(uuid);
    }
}
