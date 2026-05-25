package com.theblockheads.clienttweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.theblockheads.clienttweaks.ToolTrimTooltipUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(GuiGraphicsExtractor.class)
public abstract class ArmorTooltipWidthMixin {

	private static final Class<?> ARMORTIP_UTIL = blockheads$armortipUtil();

	// ArmorTip adds a fixed 54px for its armor panel (48 SIZE + 6 MARGIN) but computes that
	// against whatever width vanilla produced. If vanilla only counted the title line instead of
	// all lines, every modded enchantment / trim line that's longer than the title gets clipped.
	// Fix: recompute the true max across every ClientTooltipComponent and ensure width >= that max.
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
		if (!blockheads$needsExtraWidth() || font == null || components == null) return width;
		int textMax = 0;
		for (ClientTooltipComponent comp : components) {
			int w = comp.getWidth(font);
			if (w > textMax) textMax = w;
		}
		return Math.max(width, textMax);
	}

	private boolean blockheads$needsExtraWidth() {
		if (blockheads$armortipShouldExtend()) return true;

		ItemStack focused = blockheads$armortipFocusedItem();
		return focused != null && ToolTrimTooltipUtil.isTemplateBook(focused);
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

	private static ItemStack blockheads$armortipFocusedItem() {
		if (ARMORTIP_UTIL == null) return null;
		try {
			Object focused = ARMORTIP_UTIL.getMethod("getFocusedItem").invoke(null);
			return focused instanceof ItemStack stack ? stack : null;
		} catch (ReflectiveOperationException ignored) {
			return null;
		}
	}
}
