package net.team_numbers.party_tables.client;

import net.minecraft.client.Minecraft;
import net.team_numbers.party_tables.client.gui.CardHandScreen;

public final class ModClientHandler {

    public static void openCardHand() {
        Minecraft.getInstance().setScreen(new CardHandScreen());
    }

}
