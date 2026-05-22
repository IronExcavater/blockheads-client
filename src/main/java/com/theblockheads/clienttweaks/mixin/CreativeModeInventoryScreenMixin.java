package com.theblockheads.clienttweaks.mixin;

import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.ClientTweaksConfig;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

	@Shadow
	private EditBox searchBox;

	@Inject(method = "init", at = @At("TAIL"))
	private void blockheads$applySearchBoxWidth(CallbackInfo ci) {
		int width = ClientTweaks.CONFIG.creativeSearchWidth;
		if (width != ClientTweaksConfig.VANILLA_WIDTH && searchBox != null) {
			searchBox.setWidth(width);
		}
	}
}
