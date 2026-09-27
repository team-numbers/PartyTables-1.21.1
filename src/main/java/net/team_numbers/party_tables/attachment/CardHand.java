package net.team_numbers.party_tables.attachment;

import com.google.common.collect.Lists;
import com.mojang.math.Axis;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.team_numbers.party_tables.item.CardItem;
import net.team_numbers.party_tables.network.codec.ModCodecs;
import net.team_numbers.party_tables.util.Plane;
import net.team_numbers.party_tables.util.RotatablePlane;

import java.util.List;
import java.util.Map;
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
            ModCodecs.entry(Map::entry, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8).apply(ByteBufCodecs.list()),
            h -> h.cards,
            ByteBufCodecs.BOOL,
            h -> h.show,
            CardHand::new
        );

    public static final Supplier<CardHand> SIMPLE_FACTORY = CardHand::new;

    private final List<Map.Entry<String, String>> cards;
    private boolean show;

    public CardHand(List<Map.Entry<String, String>> cards, boolean show) {
        this.cards = cards;
        this.show = show;
    }

    public CardHand() {
        this.cards = Lists.newArrayList();
    }

    public List<Map.Entry<String, String>> getCards() {
        return this.cards;
    }

    public List<Plane> cardPlanes(Vec3 pos) {
        return cardPlanes(1F ,1F, pos, pos.add(0F, -0.5F, 0F));
    }

    public List<Plane> cardPlanes(float width, float height, Vec3 pos, Vec3 rot) {
        List<Plane> planes = Lists.newArrayList();
        int n = this.cards.size();
        if (n > 0) {
            int i = 0;
            float totalAngle = 150F;
            float startAngle = -totalAngle / 2F;
            for (Map.Entry<String, String> entry : cards) {
                float angle;
                if (n > 1) {
                    angle = startAngle + (totalAngle / n - 1) * i;
                } else {
                    angle = 0;
                }
                planes.add(Plane.ofXY(pos, width, height, rot, Axis.ZP.rotation(Mth.DEG_TO_RAD * angle)));
                ++i;
            }
        }
        return planes;
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    public void resetCards() {
        this.cards.clear();
    }

    public boolean toggleShow() {
        this.show = !this.show;
        System.out.println(this.show ? "Showing" : "Hiding");
        return this.show;
    }

    public void set(ItemStack itemStack) {
        var data = CardItem.get(itemStack);
        data.forEach((card) -> {
            this.cards.add(Map.entry(card.type(), card.card()));
        });
    }
}
