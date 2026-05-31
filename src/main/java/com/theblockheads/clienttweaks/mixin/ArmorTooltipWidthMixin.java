package com.theblockheads.clienttweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class ArmorTooltipWidthMixin {

	private static final int ARMORTIP_PANEL_WIDTH = 54;
	private static final Class<?> ARMORTIP_UTIL = blockheads$armortipUtil();

	// Keep ArmorTip's 48px panel and 6px margin reserved even when another tooltip mixin
	// changes the width local before or after ArmorTip's own modifier.
	@ModifyVariable(
		method = "tooltip",
		ordinal = 4,
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;guiHeight()I",
			shift = At.Shift.AFTER)
	)
	private int blockheads$fixArmorTooltipWidth(int width,
			@Local(argsOnly = true) Font font,
			@Local(argsOnly = true) List<ClientTooltipComponent> components) {
		if (!blockheads$armortipShouldExtend() || font == null || components == null) return width;
		int textMax = 0;
		for (ClientTooltipComponent comp : components) {
			int w = comp.getWidth(font);
			if (w > textMax) textMax = w;
		}
		return Math.max(width, textMax + ARMORTIP_PANEL_WIDTH);
	}

	private static Class<?> blockheads$armortipUtil() {
		try {
			return Class.forName("net.bmjo.armortip.util.ArmortipUtil");
		} catch (ClassNotFoundException ignored) {
			return null;
		}
	}

	private static boolean blockheads$armortipShouldExtend() {
		if (ARMORTIP_UTIL == null) return false;
		try {
			return Boolean.TRUE.equals(ARMORTIP_UTIL.getMethod("shouldExtend").invoke(null));
		} catch (ReflectiveOperationException ignored) {
			return false;
		}
	}

}
