package net.team_numbers.party_tables.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.team_numbers.party_tables.block.ModBlocks;
import net.team_numbers.party_tables.util.ModRegister;

import java.util.function.Supplier;

public final class ModBlockEntities {

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = ModRegister.create(Registries.BLOCK_ENTITY_TYPE);

    public static final Supplier<BlockEntityType<CardTableBlockEntity>> CARD_TABLE = BLOCK_ENTITIES.register(
        "card_table", () -> BlockEntityType.Builder.of(CardTableBlockEntity::new, ModBlocks.CARD_TABLE.get()).build(null)
    );

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }

    private ModBlockEntities() {}
}
