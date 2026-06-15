package com.theblockheads.clienttweaks.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class BlockheadsServerStatRegistry {
	private static final String SERVER_NAMESPACE = "blockheads_server";

	private BlockheadsServerStatRegistry() {
	}

	public static void register() {
		registerCustomStat("grown_crops");
		registerCustomStat("distance_blocks");
	}

	private static void registerCustomStat(String path) {
		Identifier id = Identifier.fromNamespaceAndPath(SERVER_NAMESPACE, path);
		if (!BuiltInRegistries.CUSTOM_STAT.containsKey(id)) {
			Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
		}
	}
}
