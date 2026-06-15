package com.theblockheads.clienttweaks.mixin.ipn;

import org.anti_ad.mc.ipnext.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler$ItemSlotMonitor", remap = false)
public interface AutoRefillItemSlotMonitorAccessor {
    @Accessor("checkingItem")
    ItemStack blockheads$getCheckingItem();

    @Accessor("currentItem")
    ItemStack blockheads$getCurrentItem();

    @Accessor("storedSlotId")
    int blockheads$getStoredSlotId();
}
