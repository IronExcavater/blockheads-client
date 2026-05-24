package com.theblockheads.clienttweaks;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "blockheads_client_tweaks")
public class ClientTweaksConfig implements ConfigData {

	public static final int VANILLA_WIDTH = 80;
	public static final int DEFAULT_WIDTH = 70;
	public static final int MIN_WIDTH     = 40;
	public static final int MAX_WIDTH     = 105;

	public int creativeSearchWidth = DEFAULT_WIDTH;

	@Override
	public void validatePostLoad() {
		creativeSearchWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, creativeSearchWidth));
	}
}
