package net.team_numbers.party_tables.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.team_numbers.party_tables.PartyTables;

public record DrawCardRequestPayload(BlockPos tablePos) implements CustomPacketPayload {

    public static final Type<DrawCardRequestPayload> TYPE =
        new Type<>(PartyTables.id("draw_card_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrawCardRequestPayload> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            DrawCardRequestPayload::tablePos,
            DrawCardRequestPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
