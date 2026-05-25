package com.theblockheads.clienttweaks.mixin;

import java.util.Collection;
import java.util.Set;

import com.theblockheads.clienttweaks.InternalEnchantments;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

	@Shadow private Collection<ItemStack> displayItems;
	@Shadow private Set<ItemStack> displayItemsSearchTab;

	@Inject(method = "buildContents", at = @At("RETURN"))
	private void blockheads$hideInternalEnchantments(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CallbackInfo ci) {
		this.displayItems.removeIf(InternalEnchantments::shouldHide);
		this.displayItemsSearchTab.removeIf(InternalEnchantments::shouldHide);
	}
}
