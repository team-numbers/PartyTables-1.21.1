package net.team_numbers.party_tables.game;

import net.minecraft.world.item.ItemStack;

public interface GameRule {

    /** 保存/復元用の一意なID。namespaceを含めた形式を推奨（例: "cardmod:uno"） */
    String id();

    /** 手札のカードを場の一番上のカードに対して出せるかどうか */
    boolean canPlay(ItemStack card, ItemStack topOfField);

    /** ゲーム開始時、各プレイヤーに配る初期手札の枚数 */
    int initialHandSize();

    /** 山札が�空になった時に捨て札（field）をシャッフルして山札に戻すかどうか等、ゲーム固有の挙動 */
    default boolean reshuffleFieldWhenDeckEmpty() {
        return true;
    }
}
