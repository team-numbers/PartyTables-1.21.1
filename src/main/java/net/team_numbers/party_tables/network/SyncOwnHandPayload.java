package net.team_numbers.party_tables.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.attachment.CardHand;

public record SyncOwnHandPayload(CardHand hand) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncOwnHandPayload> TYPE =
        new CustomPacketPayload.Type<>(PartyTables.id("sync_own_hand"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncOwnHandPayload> STREAM_CODEC =
        StreamCodec.composite(
            CardHand.STREAM_CODEC,
            SyncOwnHandPayload::hand,
            SyncOwnHandPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
