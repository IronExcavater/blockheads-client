package com.theblockheads.clienttweaks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientTweaksConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH =
			FabricLoader.getInstance().getConfigDir().resolve("blockheads-client-tweaks.json");

	private static ClientTweaksConfig current = new ClientTweaksConfig();

	// ── Creative Inventory ──────────────────────────────────────────────────
	public boolean narrowSearchBox = true;
	/** Width in pixels. Valid range: 40–200. Default matches IPN button clearance. */
	public int searchBoxWidth = 70;

	// ── Static accessors ────────────────────────────────────────────────────

	public static ClientTweaksConfig getCurrent() {
		return current;
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			current = new ClientTweaksConfig();
			save(current);
			return;
		}
		try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
			ClientTweaksConfig loaded = GSON.fromJson(reader, ClientTweaksConfig.class);
			current = loaded != null ? loaded : new ClientTweaksConfig();
		} catch (IOException e) {
			ClientTweaks.LOGGER.error("Failed to load config, using defaults", e);
			current = new ClientTweaksConfig();
		}
	}

	public static void save(ClientTweaksConfig config) {
		current = config;
		try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
			GSON.toJson(config, writer);
		} catch (IOException e) {
			ClientTweaks.LOGGER.error("Failed to save config", e);
		}
	}

	public ClientTweaksConfig copy() {
		ClientTweaksConfig copy = new ClientTweaksConfig();
		copy.narrowSearchBox = this.narrowSearchBox;
		copy.searchBoxWidth = this.searchBoxWidth;
		return copy;
	}
}
