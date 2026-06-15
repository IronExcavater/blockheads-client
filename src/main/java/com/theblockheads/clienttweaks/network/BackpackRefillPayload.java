package com.theblockheads.clienttweaks.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record BackpackRefillPayload(int targetSlotId, ItemStack requestedStack) implements CustomPacketPayload {
    public static final Type<BackpackRefillPayload> TYPE =
        new Type<>(Identifier.fromNamespaceAndPath("blockheads_server", "backpack_refill"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BackpackRefillPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        BackpackRefillPayload::targetSlotId,
        ItemStack.OPTIONAL_STREAM_CODEC,
        BackpackRefillPayload::requestedStack,
        BackpackRefillPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
