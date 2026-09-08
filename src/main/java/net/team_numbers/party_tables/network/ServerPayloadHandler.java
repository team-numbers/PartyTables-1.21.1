package net.team_numbers.party_tables.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.team_numbers.party_tables.attachment.CardHand;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.game.CardGameSession;

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
                CardHand hand = player.getData(ModAttachments.CARD_HAND);
                PacketDistributor.sendToPlayer(player, new SyncOwnHandPayload(hand.copy()));
                return;
            }

            session.broadcastHandCounts();
            session.broadcastFieldState();
        });
    }
}
