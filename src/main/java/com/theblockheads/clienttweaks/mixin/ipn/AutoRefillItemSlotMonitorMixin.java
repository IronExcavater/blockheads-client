package com.theblockheads.clienttweaks.mixin.ipn;

import com.theblockheads.clienttweaks.backpack.BackpackRefillClient;
import org.anti_ad.mc.ipnext.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler$ItemSlotMonitor", remap = false)
public abstract class AutoRefillItemSlotMonitorMixin {
    private static final Object BLOCKHEADS_COMPANION = blockheads$findCompanion();

    @Inject(method = "handle", at = @At("HEAD"), cancellable = true, require = 0)
    private void blockheads$refillFromBackpack(CallbackInfo ci) {
        if (BLOCKHEADS_COMPANION == null) {
            return;
        }

        AutoRefillItemSlotMonitorAccessor monitor = (AutoRefillItemSlotMonitorAccessor) this;
        ItemStack checkingItem = monitor.blockheads$getCheckingItem();
        ItemStack currentItem = monitor.blockheads$getCurrentItem();

        Integer matchingSlot = ((AutoRefillItemSlotMonitorCompanionInvoker) BLOCKHEADS_COMPANION)
            .blockheads$findCorrespondingSlot(checkingItem, currentItem);
        if (matchingSlot != null) {
            return;
        }

        if (BackpackRefillClient.requestBackpackRefill(checkingItem, monitor.blockheads$getStoredSlotId())) {
            ci.cancel();
        }
    }

    private static Object blockheads$findCompanion() {
        try {
            Class<?> monitorClass = Class.forName("org.anti_ad.mc.ipnext.event.autorefill.AutoRefillHandler$ItemSlotMonitor");
            return monitorClass.getField("Companion").get(null);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}
