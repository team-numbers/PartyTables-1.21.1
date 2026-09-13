package net.team_numbers.party_tables.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.team_numbers.party_tables.PartyTables;

public interface ModRegister {

    static <T> DeferredRegister<T> create(Registry<T> registry) {
        return DeferredRegister.create(registry, PartyTables.MOD_ID);
    }

    static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> key) {
        return DeferredRegister.create(key, PartyTables.MOD_ID);
    }

    static DeferredRegister.Items createItems() {
        return DeferredRegister.createItems(PartyTables.MOD_ID);
    }

    static DeferredRegister.Blocks createBlocks() {
        return DeferredRegister.createBlocks(PartyTables.MOD_ID);
    }

    static DeferredRegister.DataComponents createDataComponents() {
        return DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, PartyTables.MOD_ID);
    }

}
