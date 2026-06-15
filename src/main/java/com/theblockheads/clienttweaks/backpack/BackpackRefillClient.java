package com.theblockheads.clienttweaks.backpack;

import com.theblockheads.clienttweaks.network.BackpackRefillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.anti_ad.mc.ipnext.item.ItemStackExtensionsKt;

public final class BackpackRefillClient {
    private BackpackRefillClient() {
    }

    public static void register() {
        PayloadTypeRegistry.serverboundPlay().register(BackpackRefillPayload.TYPE, BackpackRefillPayload.CODEC);
    }

    public static boolean requestBackpackRefill(org.anti_ad.mc.ipnext.item.ItemStack checkingItem, int targetSlotId) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || checkingItem == null || targetSlotId < 0 || !ClientPlayNetworking.canSend(BackpackRefillPayload.TYPE)) {
            return false;
        }

        ItemStack requestedStack = ItemStackExtensionsKt.getVanillaStack(checkingItem);
        if (requestedStack.isEmpty()) {
            return false;
        }

        ClientPlayNetworking.send(new BackpackRefillPayload(targetSlotId, requestedStack.copy()));
        return true;
    }
}
