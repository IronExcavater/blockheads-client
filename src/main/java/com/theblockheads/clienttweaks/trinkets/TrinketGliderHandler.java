package com.theblockheads.clienttweaks.trinkets;

import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Lets a glider item (e.g. an Elytra) worn in the Trinkets "chest/cape" slot
 * grant flight, mirroring vanilla's chestplate-slot elytra behaviour
 * including its durability wear rate.
 */
public final class TrinketGliderHandler {

    private TrinketGliderHandler() {}

    public static void register() {
        EntityElytraEvents.CUSTOM.register(TrinketGliderHandler::useTrinketGlider);
    }

    private static boolean useTrinketGlider(LivingEntity entity, boolean tickElytra) {
        TrinketAttachment attachment = TrinketsApi.getAttachment(entity);
        Optional<TrinketSlotAccess> slot = attachment.findFirst(TrinketGliderHandler::isGlider);
        if (slot.isEmpty()) {
            return false;
        }

        if (tickElytra) {
            TrinketsApi.hurtAndBreakItemStack(slot.get().get(), 1, entity, slot.get());
        }
        return true;
    }

    private static boolean isGlider(ItemStack stack) {
        return stack.has(DataComponents.GLIDER) && !stack.nextDamageWillBreak();
    }
}
