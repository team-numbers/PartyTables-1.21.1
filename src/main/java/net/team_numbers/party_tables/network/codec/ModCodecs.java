package net.team_numbers.party_tables.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.common.util.FriendlyByteBufUtil;

import java.util.Map;
import java.util.OptionalInt;
import java.util.function.BiFunction;

public class ModCodecs {

    private ModCodecs() {}

    public static <B extends ByteBuf, K, V, E extends Map.Entry<K, V>> StreamCodec<B, E> entry(
        final BiFunction<? super K, ? super V, ? extends E> factory,
        final StreamCodec<? super B, K> keyCodec,
        final StreamCodec<? super B, V> valueCodec
    ) {
        return new StreamCodec<>() {
            @Override
            public E decode(B b) {
                K k = keyCodec.decode(b);
                V v = valueCodec.decode(b);
                return factory.apply(k, v);
            }

            @Override
            public void encode(B o, E e) {
                keyCodec.encode(o, e.getKey());
                valueCodec.encode(o, e.getValue());
            }
        };
    }
    public static final StreamCodec<ByteBuf, OptionalInt> OPTIONAL_INT = new StreamCodec<>() {
        public OptionalInt decode(ByteBuf b) {
            return b.readBoolean() ? OptionalInt.of(b.readInt()) : OptionalInt.empty();
        }

        public void encode(ByteBuf b, OptionalInt e) {
            b.writeBoolean(e.isPresent());
            if (e.isPresent()) {
                b.writeInt(e.getAsInt());
            }
        }
    };
}
