package net.team_numbers.party_tables.game;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class GameRuleRegistry {

    private static final Map<String, GameRule> RULES = new LinkedHashMap<>();

    private GameRuleRegistry() {}

    /**
     * ルールを登録する。modの初期化処理(FMLCommonSetupEvent等)から呼ぶ。
     * @throws IllegalStateException 同じIDが既に登録されている場合
     */
    public static void register(GameRule rule) {
        String id = rule.id();
        if (RULES.containsKey(id)) {
            throw new IllegalStateException("Duplicate GameRule id: " + id);
        }
        RULES.put(id, rule);
    }

    /**
     * IDからルールを取得する。見つからない場合は例外(保存データ破損時に早期発見するため)。
     */
    public static GameRule byId(String id) {
        GameRule rule = RULES.get(id);
        if (rule == null) {
            throw new IllegalArgumentException("Unknown GameRule id: " + id);
        }
        return rule;
    }

    /**
     * 保存データ復元時など、壊れたデータでクラッシュさせたくない場面用のOptional版。
     */
    public static Optional<GameRule> byIdSafe(String id) {
        return Optional.ofNullable(RULES.get(id));
    }

    public static Map<String, GameRule> getAll() {
        return Map.copyOf(RULES);
    }
}
