package com.theblockheads.clienttweaks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record StyledNicknamePayload(Map<UUID, Component> nicknames) implements CustomPacketPayload {
    public static final Type<StyledNicknamePayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("blockheads_client_tweaks", "styled_nicknames"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StyledNicknamePayload> CODEC =
        StreamCodec.ofMember(StyledNicknamePayload::write, StyledNicknamePayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(nicknames.size());
        for (Map.Entry<UUID, Component> entry : nicknames.entrySet()) {
            buf.writeUUID(entry.getKey());
            ComponentSerialization.STREAM_CODEC.encode(buf, entry.getValue());
        }
    }

    private static StyledNicknamePayload read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<UUID, Component> nicknames = new HashMap<>(size);
        for (int index = 0; index < size; index++) {
            UUID uuid = buf.readUUID();
            Component nickname = ComponentSerialization.STREAM_CODEC.decode(buf);
            nicknames.put(uuid, nickname);
        }
        return new StyledNicknamePayload(nicknames);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
