package com.theblockheads.clienttweaks.mixin;

import java.util.List;
import java.util.Map;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class LullabyEnchantmentTooltipMixin {

	// Lullaby enchantments ship literal tooltip names; replace them with our translatable glyph labels.
	private static final Map<String, Component> REPLACEMENTS = Map.of(
		"Magnetic", Component.literal("\uDBA0\uDC13 Magnetic"),
		"Smelting", Component.literal("\uDBA0\uDC0A Smelting"),
		"Veinminer", Component.literal("\uDBA0\uDC08 Veinminer")
	);

	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void blockheads$replaceLullabyEnchantments(Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
		List<Component> lines = cir.getReturnValue();
		if (lines == null || lines.isEmpty()) return;

		for (int i = 0; i < lines.size(); i++) {
			Component replacement = REPLACEMENTS.get(lines.get(i).getString());
			if (replacement != null) lines.set(i, replacement);
		}
	}
}
