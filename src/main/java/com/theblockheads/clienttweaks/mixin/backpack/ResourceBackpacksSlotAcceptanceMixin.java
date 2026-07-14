package com.theblockheads.clienttweaks.mixin.backpack;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(targets = "net.xstopho.resource_backpacks.client.slot.BackpackSlot")
public abstract class ResourceBackpacksSlotAcceptanceMixin {
    private static final TagKey<Item> BACKPACKS = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath("c", "backpacks")
    );
    private static final Set<Identifier> RESOURCE_BACKPACK_ITEMS = Set.of(
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_leather"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_copper"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_gold"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_iron"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_diamond"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_netherite"),
        Identifier.fromNamespaceAndPath("resource_backpacks", "backpack_end")
    );

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void blockheads$acceptBackpackItemsByTagOrId(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(BACKPACKS) || RESOURCE_BACKPACK_ITEMS.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()))) {
            cir.setReturnValue(true);
        }
    }
}
