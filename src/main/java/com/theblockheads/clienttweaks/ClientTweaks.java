package com.theblockheads.clienttweaks;

import com.theblockheads.clienttweaks.backpack.BackpackRefillClient;
import com.theblockheads.clienttweaks.compat.MapSyncerSilenceCommand;
import com.theblockheads.clienttweaks.compat.OkZoomerServerOverrides;
import com.theblockheads.clienttweaks.nicknames.StyledNicknameSyncClient;
import com.theblockheads.clienttweaks.network.OkZoomerServerConfigPayload;
import com.theblockheads.clienttweaks.registry.BlockheadsServerStatRegistry;
import com.theblockheads.clienttweaks.trinkets.TrinketGliderHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;

public class ClientTweaks implements ClientModInitializer {
	public static final String MOD_ID = ClientTweaksConstants.MOD_ID;

	public static ClientTweaksConfig CONFIG;

	@Override
	public void onInitializeClient() {
		CONFIG = AutoConfig.register(ClientTweaksConfig.class, GsonConfigSerializer::new).getConfig();
		PayloadTypeRegistry.clientboundPlay().register(OkZoomerServerConfigPayload.TYPE, OkZoomerServerConfigPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(OkZoomerServerConfigPayload.TYPE, (payload, context) ->
				context.client().execute(() -> OkZoomerServerOverrides.apply(payload.spyglassMode(), payload.zoomOverlay())));
		MapSyncerSilenceCommand.register();
		BlockheadsServerStatRegistry.register();
		StyledNicknameSyncClient.register();

		if (FabricLoader.getInstance().isModLoaded("inventoryprofilesnext")) {
			BackpackRefillClient.register();
		}

		if (FabricLoader.getInstance().isModLoaded("trinkets_updated")) {
			TrinketGliderHandler.register();
		}

		ClientTweaksConstants.LOGGER.info("The Block Client Tweaks initialized");
	}

	public static void saveConfig() {
		AutoConfig.getConfigHolder(ClientTweaksConfig.class).save();
	}
}
