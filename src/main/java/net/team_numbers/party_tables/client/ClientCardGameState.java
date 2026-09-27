package net.team_numbers.party_tables.client;

public class ClientCardGameState {

    private static int selected = -1;

    public static void select(int i) {
        selected = i;
    }

    public static void deselect() {
        selected = -1;
    }

    public static int getSelected() {
        return selected;
    }
}
