package net.team_numbers.party_tables.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.team_numbers.party_tables.client.ClientCardGameState_;
import net.team_numbers.party_tables.client.ModClientHandler;
import net.team_numbers.party_tables.network.payload.OpenScreenHandPayload;

public class ClientPayloadHandler {

    public static void handleSyncOwnHand(SyncOwnHandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // クライアント側のシングルトン的な状態管理クラスに自分の手札をセット
            ClientCardGameState_.setOwnHand(payload.hand());
//            // 開いているカードゲームのScreenがあれば再描画をトリガー
//            if (Minecraft.getInstance().screen instanceof com.example.cardmod.client.CardGameScreen screen) {
//                screen.onOwnHandUpdated();
//            }
        });
    }

    public static void handleSyncHandCount(SyncHandCountPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (SyncHandCountPayload.Entry entry : payload.entries()) {
                ClientCardGameState_.setOpponentCardCount(entry.playerId(), entry.count());
            }
//            if (Minecraft.getInstance().screen instanceof com.example.cardmod.client.CardGameScreen screen) {
//                screen.onOpponentCountsUpdated();
//            }
        });
    }

    public static void handleOpenScreenHand(OpenScreenHandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ModClientHandler.openCardHand();
        });
    }
}
