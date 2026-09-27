package net.team_numbers.party_tables.client;

import net.team_numbers.party_tables.attachment._CardHand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientCardGameState_ {

    // 自分自身の手札（フルデータ）
    private static _CardHand ownHand = new _CardHand();

    // 他プレイヤーの手札枚数のみ（UUID -> 枚数）
    private static final Map<UUID, Integer> opponentCardCounts = new ConcurrentHashMap<>();

    private ClientCardGameState_() {}

    // ---- 自分の手札 ----

    public static void setOwnHand(_CardHand hand) {
        ownHand = hand;
    }

    public static _CardHand getOwnHand() {
        return ownHand;
    }

    public static void clearOwnHand() {
        ownHand = new _CardHand();
    }

    // ---- 相手の枚数 ----

    public static void setOpponentCardCount(UUID playerId, int count) {
        if (count <= 0) {
            opponentCardCounts.remove(playerId);
        } else {
            opponentCardCounts.put(playerId, count);
        }
    }

    public static int getOpponentCardCount(UUID playerId) {
        return opponentCardCounts.getOrDefault(playerId, 0);
    }

    public static Map<UUID, Integer> getAllOpponentCounts() {
        return Map.copyOf(opponentCardCounts);
    }

    public static void clearOpponentCounts() {
        opponentCardCounts.clear();
    }

    // ---- ゲーム終了・退出時のリセット ----

    public static void resetAll() {
        clearOwnHand();
        clearOpponentCounts();
    }
}
