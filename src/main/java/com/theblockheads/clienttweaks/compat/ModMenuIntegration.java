package com.theblockheads.clienttweaks.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.theblockheads.clienttweaks.ClientTweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ClientTweaksConfig config = AutoConfig.getConfigHolder(ClientTweaksConfig.class).getConfig();

			ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Component.literal("Blockheads Client Tweaks"))
				.setSavingRunnable(() -> AutoConfig.getConfigHolder(ClientTweaksConfig.class).save());

			ConfigEntryBuilder entries = builder.entryBuilder();
			ConfigCategory category = builder.getOrCreateCategory(Component.literal("Creative Inventory"));

			category.addEntry(entries.startBooleanToggle(
					Component.literal("Narrow creative search box"), config.narrowSearchBox)
				.setDefaultValue(true)
				.setTooltip(Component.literal("Prevents the search box from overlapping IPN sort buttons"))
				.setSaveConsumer(val -> config.narrowSearchBox = val)
				.build());

			category.addEntry(entries.startIntSlider(
					Component.literal("Search box width"), config.searchBoxWidth, 40, 200)
				.setDefaultValue(70)
				.setTooltip(Component.literal("Width in pixels (40–200)"))
				.setSaveConsumer(val -> config.searchBoxWidth = val)
				.build());

			return builder.build();
		};
	}
}
