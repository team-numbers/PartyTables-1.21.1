package net.team_numbers.party_tables.attachment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CardHand {

    // サーバー保存用（ワールドセーブに含める場合）
    public static final Codec<CardHand> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("cards").forGetter(h -> h.cards)
        ).apply(instance, CardHand::new)
    );

    // ネットワーク送信用（本人にフル送信する時に使う）
    public static final StreamCodec<RegistryFriendlyByteBuf, CardHand> STREAM_CODEC =
        StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()),
            h -> h.cards,
            CardHand::new
        );

    private final List<ItemStack> cards;

    public CardHand() {
        this.cards = new ArrayList<>();
    }

    public CardHand(List<ItemStack> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public ItemStack get(int index) {
        if (index < 0 || index >= cards.size()) return ItemStack.EMPTY;
        return cards.get(index);
    }

    public void addCard(ItemStack card) {
        cards.add(card);
    }

    public void addCards(List<ItemStack> newCards) {
        cards.addAll(newCards);
    }

    public Optional<ItemStack> playCard(int index) {
        if (index < 0 || index >= cards.size()) return Optional.empty();
        return Optional.of(cards.remove(index));
    }

    public boolean removeCard(ItemStack card) {
        for (int i = 0; i < cards.size(); i++) {
            if (ItemStack.isSameItemSameComponents(cards.get(i), card)) {
                cards.remove(i);
                return true;
            }
        }
        return false;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void clear() {
        cards.clear();
    }

    public List<ItemStack> getCardsView() {
        return Collections.unmodifiableList(cards);
    }

    public CardHand copy() {
        List<ItemStack> copied = new ArrayList<>(cards.size());
        for (ItemStack s : cards) copied.add(s.copy());
        return new CardHand(copied);
    }
}
