package net.team_numbers.party_tables.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.attachment.ModAttachments;

public record SPickCardPayload(int entityId, int index) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SPickCardPayload> TYPE =
        new CustomPacketPayload.Type<>(PartyTables.id("s_pick_card"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SPickCardPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SPickCardPayload::entityId,
            ByteBufCodecs.VAR_INT,
            SPickCardPayload::index,
            SPickCardPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPickCardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof LivingEntity living) {
                var cardData = living.getData(ModAttachments.CARD_HAND);
                var card = cardData.pick(payload.index());
                living.syncData(ModAttachments.CARD_HAND);

                var pickerCardData = player.getData(ModAttachments.CARD_HAND);
                pickerCardData.append(card.getKey(), card.getValue());
                player.syncData(ModAttachments.CARD_HAND);
            }
        });
    }
}
