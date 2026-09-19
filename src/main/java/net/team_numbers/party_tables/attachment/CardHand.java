package net.team_numbers.party_tables.attachment;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.team_numbers.party_tables.item.CardItem;
import net.team_numbers.party_tables.item.component.ModDataComponents;

import java.util.List;
import java.util.function.Supplier;

public class CardHand {

//    // サーバー保存用（ワールドセーブに含める場合）
//    public static final Codec<CardHand> CODEC = RecordCodecBuilder.create(instance ->
//        instance.group(
//            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("cards").forGetter(h -> h.cards)
//        ).apply(instance, _CardHand::new)
//    );

    // ネットワーク送信用（本人にフル送信する時に使う）
    public static final StreamCodec<RegistryFriendlyByteBuf, CardHand> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
            h -> h.cards,
            CardHand::new
        );

    public static final Supplier<CardHand> SIMPLE_FACTORY = CardHand::new;

    private final List<String> cards;
    private boolean show;

    public CardHand(List<String> cards) {
        this.cards = cards;
    }

    public CardHand() {
        this.cards = Lists.newArrayList();
    }

    public List<String> getCards() {
        return this.cards;
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public void resetCards() {
        this.cards.clear();
    }

    public boolean toggleShow() {
        this.show = !this.show;
        return this.show;
    }

    public void set(ItemStack itemStack) {
        var data = CardItem.get(itemStack);
        data.forEach((card) -> {
            this.cards.add(card.type() + "," + card.card());
        });
    }
}
