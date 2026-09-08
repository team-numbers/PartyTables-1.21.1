package net.team_numbers.party_tables.attachment;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.team_numbers.party_tables.util.ModRegister;

import java.util.function.Supplier;

public final class ModAttachments {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = ModRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES);

    public static final Supplier<AttachmentType<CardHand>> CARD_HAND =
        ATTACHMENT_TYPES.register("card_hand", () ->
            AttachmentType.builder((Supplier<CardHand>)CardHand::new)
                .serialize(CardHand.CODEC)
                .build()
        );

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    private ModAttachments() {}
}
