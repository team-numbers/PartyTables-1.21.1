package net.team_numbers.party_tables.network;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;

public record SyncHandCountPayload(List<Entry> entries) implements CustomPacketPayload {

    public record Entry(UUID playerId, int count) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC =
            StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, Entry::playerId,
                ByteBufCodecs.VAR_INT, Entry::count,
                Entry::new
            );
    }

    public static final CustomPacketPayload.Type<SyncHandCountPayload> TYPE =
        new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath("cardmod", "sync_hand_count")
        );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHandCountPayload> STREAM_CODEC =
        StreamCodec.composite(
            Entry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SyncHandCountPayload::entries,
            SyncHandCountPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
