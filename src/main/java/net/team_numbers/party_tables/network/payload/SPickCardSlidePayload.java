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

public record SPickCardSlidePayload(int entityId, int index, double moveDist) implements CustomPacketPayload {

    public static final Type<SPickCardSlidePayload> TYPE =
        new Type<>(PartyTables.id("s_pick_slide_card"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SPickCardSlidePayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SPickCardSlidePayload::entityId,
            ByteBufCodecs.VAR_INT,
            SPickCardSlidePayload::index,
            ByteBufCodecs.DOUBLE,
            SPickCardSlidePayload::moveDist,
            SPickCardSlidePayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SPickCardSlidePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof LivingEntity living) {
                var cardData = living.getData(ModAttachments.CARD_HAND);
                cardData.pickCardSlide(payload.index(), payload.moveDist());
                living.syncData(ModAttachments.CARD_HAND);
            }
        });
    }
}
