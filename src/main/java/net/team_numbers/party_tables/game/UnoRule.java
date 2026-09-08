package net.team_numbers.party_tables.game;

import net.minecraft.world.item.ItemStack;

public class UnoRule implements GameRule {

    public static final String ID = "cardmod:uno";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public boolean canPlay(ItemStack card, ItemStack topOfField) {
        if (topOfField.isEmpty()) return true;

//        UnoCardData cardData = card.get(ModDataComponents.UNO_CARD_DATA.get());
//        UnoCardData topData = topOfField.get(ModDataComponents.UNO_CARD_DATA.get());
//        if (cardData == null || topData == null) return false;
//
//        return cardData.color() == topData.color()
//            || cardData.number() == topData.number()
//            || cardData.isWild();
        return true;
    }

    @Override
    public int initialHandSize() {
        return 7;
    }
}
