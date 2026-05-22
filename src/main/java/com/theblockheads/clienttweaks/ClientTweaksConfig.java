package com.theblockheads.clienttweaks;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "blockheads_client_tweaks")
public class ClientTweaksConfig implements ConfigData {

	public static final int VANILLA_WIDTH = 0;
	public static final int DEFAULT_WIDTH = 70;

	/** Width of the creative search box in pixels. 0 = vanilla (no override). */
	public int creativeSearchWidth = DEFAULT_WIDTH;

	@Override
	public void validatePostLoad() {
		if (creativeSearchWidth < 0 || creativeSearchWidth > 200) {
			creativeSearchWidth = DEFAULT_WIDTH;
		}
	}
}
