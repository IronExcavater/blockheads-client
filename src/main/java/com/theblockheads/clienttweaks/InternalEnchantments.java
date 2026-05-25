package com.theblockheads.clienttweaks;

import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public final class InternalEnchantments {

	private static final Set<Identifier> HIDDEN = Set.of(
		Identifier.parse("nova_structures:jockey/spawn_chicken_jockey"),
		Identifier.parse("nova_structures:jockey/spawn_stray_horseman"),
		Identifier.parse("nova_structures:boss_behaviour"),
		Identifier.parse("nova_structures:boss_nether"),
		Identifier.parse("nova_structures:librarian_level_checker"),
		Identifier.parse("nova_structures:shulker_boss"),
		Identifier.parse("nova_structures:shulker_miniboss"),
		Identifier.parse("nova_structures:tavern_level_checker")
	);

	private InternalEnchantments() {
	}

	public static boolean shouldHide(ItemStack stack) {
		return containsHidden(stack.get(DataComponents.STORED_ENCHANTMENTS)) || containsHidden(stack.get(DataComponents.ENCHANTMENTS));
	}

	private static boolean containsHidden(ItemEnchantments enchantments) {
		if (enchantments == null || enchantments.isEmpty()) return false;

		for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
			ResourceKey<Enchantment> key = entry.getKey().unwrapKey().orElse(null);
			if (key != null && HIDDEN.contains(key.identifier())) return true;
		}

		return false;
	}
}
