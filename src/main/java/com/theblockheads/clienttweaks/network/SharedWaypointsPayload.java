package com.theblockheads.clienttweaks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SharedWaypointsPayload(List<Entry> waypoints) implements CustomPacketPayload {
    public static final Type<SharedWaypointsPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("blockheads_client_tweaks", "shared_waypoints"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SharedWaypointsPayload> CODEC =
        StreamCodec.ofMember(SharedWaypointsPayload::write, SharedWaypointsPayload::read);

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(waypoints.size());
        for (Entry waypoint : waypoints) {
            waypoint.write(buf);
        }
    }

    private static SharedWaypointsPayload read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Entry> waypoints = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            waypoints.add(Entry.read(buf));
        }
        return new SharedWaypointsPayload(waypoints);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record Entry(
        UUID id,
        String name,
        String initials,
        Identifier dimension,
        int x,
        int y,
        int z,
        int color,
        String scope,
        String owner,
        boolean canEdit
    ) {
        private void write(RegistryFriendlyByteBuf buf) {
            buf.writeUUID(id);
            buf.writeUtf(name);
            buf.writeUtf(initials);
            buf.writeUtf(dimension.toString());
            buf.writeVarInt(x);
            buf.writeVarInt(y);
            buf.writeVarInt(z);
            buf.writeVarInt(color);
            buf.writeUtf(scope);
            buf.writeUtf(owner);
            buf.writeBoolean(canEdit);
        }

        private static Entry read(RegistryFriendlyByteBuf buf) {
            return new Entry(
                buf.readUUID(),
                buf.readUtf(),
                buf.readUtf(),
                Identifier.parse(buf.readUtf()),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readUtf(),
                buf.readUtf(),
                buf.readBoolean()
            );
        }
    }
}
