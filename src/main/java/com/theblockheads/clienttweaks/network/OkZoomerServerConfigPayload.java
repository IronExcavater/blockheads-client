package com.theblockheads.clienttweaks.network;

import com.theblockheads.clienttweaks.ClientTweaksConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OkZoomerServerConfigPayload(String spyglassMode, String zoomOverlay) implements CustomPacketPayload {
	public static final Type<OkZoomerServerConfigPayload> TYPE = new Type<>(
			Identifier.fromNamespaceAndPath(ClientTweaksConstants.MOD_ID, "ok_zoomer_server_config"));

	public static final StreamCodec<RegistryFriendlyByteBuf, OkZoomerServerConfigPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			OkZoomerServerConfigPayload::spyglassMode,
			ByteBufCodecs.STRING_UTF8,
			OkZoomerServerConfigPayload::zoomOverlay,
			OkZoomerServerConfigPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
