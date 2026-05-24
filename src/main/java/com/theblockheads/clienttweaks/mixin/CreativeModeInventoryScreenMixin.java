package com.theblockheads.clienttweaks.mixin;

import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.ClientTweaksConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin {

	// tab_item_search.png (256×256, imageWidth=195) is drawn 1:1 at (leftPos, topPos).
	// The search slot occupies cols 80–169, rows 4–15. searchBox sits at (leftPos+82, topPos+6).
	private static final float SLOT_TOP_V      = 4f;
	private static final float SLOT_INTERIOR_U = 81f;
	private static final float SLOT_BORDER_U   = 169f;
	private static final float SLOT_FRAME_U    = 170f;
	private static final int   SLOT_HEIGHT     = 12;
	private static final int   SLOT_END        = 87;  // offset from searchBox.getX() to the vanilla right border
	private static final int   TEX_SIZE        = 256;

	@Shadow private EditBox searchBox;
	@Shadow private static CreativeModeTab selectedTab;

	// EditBox caches its text-area width at construction; @ModifyArg intercepts the
	// constructor argument so text clipping matches the configured width from the start.
	@ModifyArg(
		method = "init",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/EditBox;<init>(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/network/chat/Component;)V"),
		index = 3
	)
	private int blockheads$applySearchBoxWidth(int w) {
		return ClientTweaks.CONFIG.creativeSearchWidth;
	}

	@Inject(method = "extractBackground", at = @At("RETURN"))
	private void blockheads$adjustVisual(GuiGraphicsExtractor g, int mx, int my, float pt, CallbackInfo ci) {
		if (selectedTab == null || !this.searchBox.isVisible()) return;
		int w = ClientTweaks.CONFIG.creativeSearchWidth;
		if (w == ClientTweaksConfig.VANILLA_WIDTH) return;

		int x     = this.searchBox.getX();
		int slotY = this.searchBox.getY() - 2;
		Identifier tex = selectedTab.getBackgroundTexture();

		if (w < ClientTweaksConfig.VANILLA_WIDTH) {
			// srcWidth=1 tiles a single frame pixel across the cover region, keeping the
			// texture sample within the 195px panel boundary (panel ends at u=194).
			g.blit(RenderPipelines.GUI_TEXTURED, tex, x + w + 1, slotY, SLOT_FRAME_U, SLOT_TOP_V, SLOT_END - w, SLOT_HEIGHT, 1, SLOT_HEIGHT, TEX_SIZE, TEX_SIZE);
		} else {
			// srcWidth=1 tiles a single interior pixel to extend the slot rightward.
			g.blit(RenderPipelines.GUI_TEXTURED, tex, x + SLOT_END, slotY, SLOT_INTERIOR_U, SLOT_TOP_V, w - SLOT_END + 1, SLOT_HEIGHT, 1, SLOT_HEIGHT, TEX_SIZE, TEX_SIZE);
		}
		g.blit(RenderPipelines.GUI_TEXTURED, tex, x + w, slotY, SLOT_BORDER_U, SLOT_TOP_V, 1, SLOT_HEIGHT, TEX_SIZE, TEX_SIZE);
	}
}
