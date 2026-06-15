package com.theblockheads.clienttweaks.mixin.nicknames;

import com.theblockheads.clienttweaks.nicknames.StyledNicknameSyncClient;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.client.renderer.entity.EntityRenderer.class)
public class EntityRendererNicknameMixin {
    @Redirect(
        method = "getNameTag(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/Component;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;getDisplayName()Lnet/minecraft/network/chat/Component;"
        )
    )
    private Component blockheads$styledNicknameNameTag(Entity entity) {
        if (entity instanceof Player player) {
            Component nickname = StyledNicknameSyncClient.get(player.getUUID());
            if (nickname != null) {
                return PlayerTeam.formatNameForTeam(player.getTeam(), nickname);
            }
        }
        return entity.getDisplayName();
    }
}
