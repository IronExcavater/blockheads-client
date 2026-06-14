package com.theblockheads.clienttweaks.compat;

import com.mojang.brigadier.context.CommandContext;
import com.theblockheads.clienttweaks.ClientTweaks;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

/**
 * Adds a "silence" toggle under MapSyncer's existing "/mapsyncer" command.
 * Brigadier merges command trees registered under the same root literal, so this
 * extends MapSyncer's tree without needing to depend on its command classes.
 */
public class MapSyncerSilenceCommand {

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommands.literal("mapsyncer")
				.then(ClientCommands.literal("silence")
					.executes(MapSyncerSilenceCommand::toggle))));
	}

	private static int toggle(CommandContext<FabricClientCommandSource> context) {
		boolean silenced = !ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced;
		ClientTweaks.CONFIG.mapSyncerSyncFeedbackSilenced = silenced;
		ClientTweaks.saveConfig();

		context.getSource().sendFeedback(Component.literal(silenced
			? "MapSyncer sync messages silenced. Run '/mapsyncer silence' again to unsilence."
			: "MapSyncer sync messages unsilenced."));
		return 1;
	}
}
