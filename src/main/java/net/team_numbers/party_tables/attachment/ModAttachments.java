package net.team_numbers.party_tables.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.team_numbers.party_tables.util.ModRegister;

import java.util.function.Supplier;

public final class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = ModRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static final Supplier<AttachmentType<_CardHand>> _CARD_HAND =
        ATTACHMENT_TYPES.register("card_hand_", () ->
            AttachmentType.builder((Supplier<_CardHand>) _CardHand::new)
                .serialize(_CardHand.CODEC)
                .build()
        );

    public static final Supplier<AttachmentType<CardHand>> CARD_HAND =
        ATTACHMENT_TYPES.register("card_hand", () ->
            AttachmentType.builder(CardHand.SIMPLE_FACTORY).sync(
                (holder, player) -> {
                    return true;
                }, CardHand.STREAM_CODEC
            ).build()
        );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private ModAttachments() {}
}
