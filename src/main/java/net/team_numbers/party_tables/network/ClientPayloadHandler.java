package net.team_numbers.party_tables.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.team_numbers.party_tables.client.ClientCardGameState;

public class ClientPayloadHandler {

    public static void handleSyncOwnHand(SyncOwnHandPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            // クライアント側のシングルトン的な状態管理クラスに自分の手札をセット
            ClientCardGameState.setOwnHand(payload.hand());
//            // 開いているカードゲームのScreenがあれば再描画をトリガー
//            if (Minecraft.getInstance().screen instanceof com.example.cardmod.client.CardGameScreen screen) {
//                screen.onOwnHandUpdated();
//            }
        });
    }

    public static void handleSyncHandCount(SyncHandCountPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (SyncHandCountPayload.Entry entry : payload.entries()) {
                ClientCardGameState.setOpponentCardCount(entry.playerId(), entry.count());
            }
//            if (Minecraft.getInstance().screen instanceof com.example.cardmod.client.CardGameScreen screen) {
//                screen.onOpponentCountsUpdated();
//            }
        });
    }
}
