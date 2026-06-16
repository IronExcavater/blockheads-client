package com.theblockheads.clienttweaks.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class BlockheadsServerStatRegistry {
	private static final String SERVER_NAMESPACE = "blockheads_server";

	private BlockheadsServerStatRegistry() {
	}

	public static void register() {
		registerCustomStat("distance_land");
		registerCustomStat("distance_sea");
		registerCustomStat("distance_air");
		registerCustomStat("grown_crops");
		registerCustomStat("buried_treasure");
		registerCustomStat("vaults_opened");
		registerCustomStat("hostile_kills");
		registerCustomStat("wither_kills");
		registerCustomStat("evoker_kills");
		registerCustomStat("elder_guardian_kills");
		registerCustomStat("shulker_kills");
		registerCustomStat("piglin_brute_kills");
		registerCustomStat("unique_foods_eaten");
		registerCustomStat("xp_held");
		registerCustomStat("xp_gained");
	}

	private static void registerCustomStat(String path) {
		Identifier id = Identifier.fromNamespaceAndPath(SERVER_NAMESPACE, path);
		if (!BuiltInRegistries.CUSTOM_STAT.containsKey(id)) {
			Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
		}
	}
}
