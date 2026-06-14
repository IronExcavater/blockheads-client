package com.theblockheads.clienttweaks.compat;

import com.theblockheads.clienttweaks.ClientTweaks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class MapSyncerMessageFilter {

	private static final String MAPSYNCER_TRANSLATION_PREFIX = "mapsyncer.";

	private MapSyncerMessageFilter() {
	}

	public static boolean shouldSilence(Component message) {
		return ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced && containsMapSyncerTranslation(message);
	}

	private static boolean containsMapSyncerTranslation(Component component) {
		ComponentContents contents = component.getContents();
		if (contents instanceof TranslatableContents translatable) {
			if (translatable.getKey().startsWith(MAPSYNCER_TRANSLATION_PREFIX)) {
				return true;
			}

			for (Object arg : translatable.getArgs()) {
				if (arg instanceof Component argComponent && containsMapSyncerTranslation(argComponent)) {
					return true;
				}
			}
		}

		for (Component sibling : component.getSiblings()) {
			if (containsMapSyncerTranslation(sibling)) {
				return true;
			}
		}

		return false;
	}
}
