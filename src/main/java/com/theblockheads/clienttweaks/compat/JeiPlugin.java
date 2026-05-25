package com.theblockheads.clienttweaks.compat;

import java.util.List;

import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.InternalEnchantments;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRuntimeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

	private static final Identifier UID = Identifier.fromNamespaceAndPath(ClientTweaks.MOD_ID, "jei_plugin");

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerRuntime(IRuntimeRegistration registration) {
		List<ItemStack> hiddenStacks = registration.getIngredientManager()
			.getAllItemStacks()
			.stream()
			.filter(InternalEnchantments::shouldHide)
			.toList();

		if (!hiddenStacks.isEmpty()) {
			registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
		}
	}
}
