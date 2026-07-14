package com.theblockheads.clienttweaks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record WaypointSyncPayload(
    boolean delete,
    UUID id,
    String name,
    String initials,
    String dimension,
    int x,
    int y,
    int z,
    int color,
    String setName
) implements CustomPacketPayload {
    public static final Type<WaypointSyncPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("blockheads_client_tweaks", "xaero_waypoint_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WaypointSyncPayload> CODEC =
        StreamCodec.ofMember(WaypointSyncPayload::write, WaypointSyncPayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(delete);
        buf.writeUUID(id);
        buf.writeUtf(name);
        buf.writeUtf(initials);
        buf.writeUtf(dimension);
        buf.writeVarInt(x);
        buf.writeVarInt(y);
        buf.writeVarInt(z);
        buf.writeVarInt(color);
        buf.writeUtf(setName);
    }

    private static WaypointSyncPayload read(RegistryFriendlyByteBuf buf) {
        return new WaypointSyncPayload(
            buf.readBoolean(),
            buf.readUUID(),
            buf.readUtf(),
            buf.readUtf(),
            buf.readUtf(),
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readUtf()
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
