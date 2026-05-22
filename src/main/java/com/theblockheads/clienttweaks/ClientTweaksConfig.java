package com.theblockheads.clienttweaks;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "blockheads_client_tweaks")
public class ClientTweaksConfig implements ConfigData {

	public boolean narrowSearchBox = true;
	public int searchBoxWidth = 70;
}
