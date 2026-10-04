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
import net.team_numbers.party_tables.network.codec.ModCodecs;

import java.util.OptionalInt;

public record SSelectCardPayload(int entityId, OptionalInt index) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SSelectCardPayload> TYPE =
        new CustomPacketPayload.Type<>(PartyTables.id("s_select_card"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SSelectCardPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SSelectCardPayload::entityId,
            ModCodecs.OPTIONAL_INT,
            SSelectCardPayload::index,
            SSelectCardPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SSelectCardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Entity entity = player.level().getEntity(payload.entityId());
            if (entity instanceof LivingEntity living) {
                var cardData = living.getData(ModAttachments.CARD_HAND);
                cardData.selectOrUnselect(living.getName().getString(), payload.index());
                living.syncData(ModAttachments.CARD_HAND);
            }
        });
    }
}
