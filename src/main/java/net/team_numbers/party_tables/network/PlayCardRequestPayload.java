package net.team_numbers.party_tables.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.team_numbers.party_tables.PartyTables;

public record PlayCardRequestPayload(BlockPos tablePos, int handIndex) implements CustomPacketPayload {

    public static final Type<PlayCardRequestPayload> TYPE =
        new Type<>(PartyTables.id("play_card_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayCardRequestPayload> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PlayCardRequestPayload::tablePos,
            ByteBufCodecs.VAR_INT,
            PlayCardRequestPayload::handIndex,
            PlayCardRequestPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
