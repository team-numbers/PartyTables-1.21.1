package net.team_numbers.party_tables.entity;

import com.mojang.math.Axis;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.util.Ray;

import java.util.Optional;

public final class CardRayCasts {

    public static Optional<SelectCard> rayCardPlayers(Level level, Player owner, Ray ray) {
        var list = level.getNearbyPlayers(
            TargetingConditions.forNonCombat(),
            owner,
            owner.getBoundingBox().inflate(6F, 4F, 6F)
        );
        for (var player : list) {
            var result = rayCard(player, ray);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }


    private static Optional<SelectCard> rayCard(Player player, Ray ray) {
        var cardHand = player.getData(ModAttachments.CARD_HAND);
        var rot = Axis.YP.rotationDegrees(180.0F - player.yBodyRot);
        float tmp0 = -.5F;
        var centerPos = new Vec3(0F, 0F, tmp0);
        var planes = cardHand.cardPlanes(.875F, 1F, centerPos, new Vec3(0F, 0.5F, 0F));

        int i = planes.size() - 1;
        for (var plane : planes.reversed()) {
            var pos = plane.rot(rot).scale(0.9375F).add(player.position().add(0F, 1.501F, 0F));
            if (pos.clip(ray.start(), ray.end()).isPresent()) {
                return Optional.of(new SelectCard(player, i));
            }
            --i;
        }
        return Optional.empty();
    }
}
