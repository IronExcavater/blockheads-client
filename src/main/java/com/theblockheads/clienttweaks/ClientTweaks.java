package com.theblockheads.clienttweaks;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientTweaks implements ClientModInitializer {

	public static final String MOD_ID = "blockheads_client_tweaks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ClientTweaksConfig CONFIG;

	@Override
	public void onInitializeClient() {
		CONFIG = AutoConfig.register(ClientTweaksConfig.class, GsonConfigSerializer::new).getConfig();
	}

	public static void saveConfig() {
		AutoConfig.getConfigHolder(ClientTweaksConfig.class).save();
	}
}
