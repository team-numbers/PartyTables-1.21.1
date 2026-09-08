package net.team_numbers.party_tables.game;

import net.minecraft.world.item.ItemStack;

import java.util.*;

public class Deck {
    private final Deque<ItemStack> cards = new ArrayDeque<>();

    public void reset(List<ItemStack> newCards) {
        cards.clear();
        for (ItemStack s : newCards) cards.addLast(s.copy());
    }

    public void shuffle() {
        List<ItemStack> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
    }

    public Optional<ItemStack> drawOne() {
        return Optional.ofNullable(cards.pollFirst());
    }

    public List<ItemStack> toList() {
        return cards.stream().toList();
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
