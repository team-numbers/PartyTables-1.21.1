package net.team_numbers.party_tables.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.team_numbers.party_tables.PartyTables;

public record SCloseCardHandPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SCloseCardHandPayload> TYPE =
        new CustomPacketPayload.Type<>(PartyTables.id("s_close_card_hand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SCloseCardHandPayload> STREAM_CODEC =
        StreamCodec.unit(new SCloseCardHandPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
