package net.team_numbers.party_tables.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.team_numbers.party_tables.attachment._CardHand;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.game.CardGameSession;
import net.team_numbers.party_tables.network.payload.OpenScreenHandPayload;
import net.team_numbers.party_tables.network.payload.SCloseCardHandPayload;
import net.team_numbers.party_tables.network.payload.SSelectCardPayload;

public class ServerPayloadHandler {

    public static void handleDrawCard(DrawCardRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            CardGameSession session = CardGameSession.getAt(player.level(), payload.tablePos());
            if (session == null) return;
            if (!session.isPlayerTurn(player)) return;

            // drawCardFor内で本人へのSyncOwnHandPayload送信まで完結させている
            ItemStack drawn = session.drawCardFor(player);
            if (drawn.isEmpty()) return;

            session.broadcastHandCounts();
        });
    }

    public static void handlePlayCard(PlayCardRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            CardGameSession session = CardGameSession.getAt(player.level(), payload.tablePos());
            if (session == null) return;
            if (!session.isPlayerTurn(player)) return;

            boolean ok = session.tryPlayCard(player, payload.handIndex());
            if (!ok) {
                // ルール違反 or 不正値 → 本人にだけ現状の手札を再送して矛盾を解消
                _CardHand hand = player.getData(ModAttachments._CARD_HAND);
                PacketDistributor.sendToPlayer(player, new SyncOwnHandPayload(hand.copy()));
                return;
            }

            session.broadcastHandCounts();
            session.broadcastFieldState();
        });
    }

    public static void handleSelectCard(SSelectCardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof LivingEntity living) {
                System.out.println("Server:" + living.getName().getString() + ", " + payload.index());
                var cardData = living.getData(ModAttachments.CARD_HAND);
                cardData.selectOrUnselect(living.getName().getString(), payload.index());
                living.syncData(ModAttachments.CARD_HAND);
            }
        });
    }

    public static void handleCloseCardHand(SCloseCardHandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            var cardData = player.getData(ModAttachments.CARD_HAND);
            cardData.hide();
            player.syncData(ModAttachments.CARD_HAND);
        });
    }
}
