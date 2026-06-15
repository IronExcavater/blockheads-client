package com.theblockheads.clienttweaks.mixin.ipn;

import org.anti_ad.mc.ipnext.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(targets = "org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler$ItemSlotMonitor$Companion", remap = false)
public interface AutoRefillItemSlotMonitorCompanionInvoker {
    @Invoker("findCorrespondingSlot")
    Integer blockheads$findCorrespondingSlot(ItemStack checkingItem, ItemStack currentItem);
}
