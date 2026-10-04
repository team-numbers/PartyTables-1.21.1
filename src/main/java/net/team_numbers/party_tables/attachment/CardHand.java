package net.team_numbers.party_tables.attachment;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.mojang.math.Axis;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.team_numbers.party_tables.client.CardInfo;
import net.team_numbers.party_tables.item.CardItem;
import net.team_numbers.party_tables.network.codec.ModCodecs;
import net.team_numbers.party_tables.util.Plane;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.IntStream;

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
            CardSlot.STREAM_CODEC.apply(ByteBufCodecs.list()),
            h -> h.cards,
            ByteBufCodecs.BOOL,
            h -> h.show,
            ByteBufCodecs.map(HashBiMap::create, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT),
            h -> h.select,
            CardHand::new
        );

    public static final Supplier<CardHand> SIMPLE_FACTORY = CardHand::new;

    private final List<CardSlot> cards;
    private boolean show;
    private final BiMap<String, Integer> select;

    private CardHand(List<CardSlot> cards, boolean show, BiMap<String, Integer> select) {
        this.cards = cards;
        this.show = show;
        this.select = select;
    }

    public CardHand() {
        this.cards = Lists.newArrayList();
        this.select = HashBiMap.create();
    }

    public List<CardInfo> getCards() {
        var indexMap = select.inverse();
        return IntStream.range(0, cards.size())
            .mapToObj(i -> {
                var entry = cards.get(i);
                String name = indexMap.get(i);
                return new CardInfo(entry.type(), entry.card(), entry.moveDist(), name);
            }).toList();
    }

    public List<Plane> cardPlanes(Vec3 pos) {
        return cardPlanes(1F ,1F, pos, pos.add(0F, -0.5F, 0F));
    }

    public List<Plane> cardPlanes(float width, float height, Vec3 pos, Vec3 rot) {
        List<Plane> planes = Lists.newArrayList();
        int n = this.cards.size();
        if (n > 0) {
            int i = 0;
            for (var entry : cards) {
                float angle = getAngle(i, n);
                planes.add(Plane.ofXY(
                    pos, width, height, rot, Axis.ZP.rotation(Mth.DEG_TO_RAD * angle),
                    new Vec3(0, -entry.moveDist / 100.0F, 0))
                );
                ++i;
            }
        }
        return planes;
    }

    public static float getAngle(int index, int n) {
        float totalAngle = 150F;
        float startAngle = -totalAngle / 2F;
        if (n > 1) {
            return startAngle + (totalAngle / n - 1) * index;
        } else {
            return  0;
        }
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public void show(ItemStack itemStack) {
        this.show = true;
        var data = CardItem.get(itemStack);
        data.forEach((card) -> {
            this.cards.add(new CardSlot(card.type(), card.card(), 0));
        });
    }

    public void hide() {
        this.show = false;
        this.cards.clear();
        this.select.clear();
    }

    public Map.Entry<String, String> pick(int index) {
        var card = this.cards.remove(index);
        return Map.entry(card.type(), card.card());
    }

    public void append(String type, String card) {
        this.cards.add(new CardSlot(type, card, 0));
    }

    public void pickCardSlide(int index, double moveDist) {
        this.cards.set(index, this.cards.get(index).move(moveDist));
    }

    public void selectOrUnselect(String name, OptionalInt index) {
        this.select.compute(name, (k, oldValue) -> index.isPresent() ? index.getAsInt() : null);
    }

    record CardSlot(String type, String card, double moveDist) {
        public static final StreamCodec<ByteBuf, CardSlot> STREAM_CODEC =
            StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                CardSlot::type,
                ByteBufCodecs.STRING_UTF8,
                CardSlot::card,
                ByteBufCodecs.DOUBLE,
                CardSlot::moveDist,
                CardSlot::new
            );

        public CardSlot move(double moveDist) {
            return new CardSlot(type, card, moveDist);
        }
    }
}
