package net.team_numbers.party_tables.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.team_numbers.party_tables.PartyTables;

public record OpenScreenHandPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OpenScreenHandPayload> TYPE =
        new CustomPacketPayload.Type<>(PartyTables.id("open_screen_hand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenScreenHandPayload> STREAM_CODEC =
        StreamCodec.unit(new OpenScreenHandPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
