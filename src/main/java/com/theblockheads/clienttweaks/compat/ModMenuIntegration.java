package com.theblockheads.clienttweaks.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.ClientTweaksConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.literal("Blockheads Client Tweaks"))
				.setSavingRunnable(ClientTweaks::saveConfig);

			builder.getOrCreateCategory(Component.literal("Creative Inventory"))
				.addEntry(builder.entryBuilder()
					.startIntSlider(
						Component.literal("Creative Search Box Width"),
						ClientTweaks.CONFIG.creativeSearchWidth,
						ClientTweaksConfig.MIN_WIDTH,
						ClientTweaksConfig.MAX_WIDTH)
					.setDefaultValue(ClientTweaksConfig.DEFAULT_WIDTH)
					.setTextGetter(val -> val == ClientTweaksConfig.VANILLA_WIDTH
						? Component.literal("Vanilla (" + val + " px)")
						: Component.literal(val + " px"))
					.setTooltip(Component.literal(
						"Width of the creative search box. Vanilla = " + ClientTweaksConfig.VANILLA_WIDTH + " px."))
					.setSaveConsumer(val -> ClientTweaks.CONFIG.creativeSearchWidth = val)
					.build());

			builder.getOrCreateCategory(Component.literal("Map Syncing"))
				.addEntry(builder.entryBuilder()
					.startBooleanToggle(
						Component.literal("Auto-sync World Map"),
						ClientTweaks.CONFIG.mapSyncerAutoSyncEnabled)
					.setDefaultValue(true)
					.setTooltip(Component.literal(
						"Periodically runs '/mapsyncer sync all' so your Xaero World Map stays in sync with the server's shared map data."))
					.setSaveConsumer(val -> ClientTweaks.CONFIG.mapSyncerAutoSyncEnabled = val)
					.build())
				.addEntry(builder.entryBuilder()
					.startIntSlider(
						Component.literal("Sync Interval"),
						ClientTweaks.CONFIG.mapSyncerAutoSyncIntervalMinutes,
						ClientTweaksConfig.MAPSYNCER_AUTO_SYNC_MIN_INTERVAL,
						ClientTweaksConfig.MAPSYNCER_AUTO_SYNC_MAX_INTERVAL)
					.setDefaultValue(ClientTweaksConfig.MAPSYNCER_AUTO_SYNC_DEFAULT_INTERVAL)
					.setTextGetter(val -> Component.literal(val + (val == 1 ? " minute" : " minutes")))
					.setTooltip(Component.literal("How often to request a map sync from the server."))
					.setSaveConsumer(val -> ClientTweaks.CONFIG.mapSyncerAutoSyncIntervalMinutes = val)
					.build());

			return builder.build();
		};
	}
}
