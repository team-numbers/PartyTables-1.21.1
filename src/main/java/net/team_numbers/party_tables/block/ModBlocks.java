package net.team_numbers.party_tables.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.team_numbers.party_tables.util.ModRegister;

public final class ModBlocks {

    private static final DeferredRegister.Blocks BLOCKS = ModRegister.createBlocks();

    public static final DeferredBlock<CardTableBlock> CARD_TABLE = BLOCKS.register(
        "card_table", () -> new CardTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK))
    );



    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }

    private ModBlocks() {}
}
