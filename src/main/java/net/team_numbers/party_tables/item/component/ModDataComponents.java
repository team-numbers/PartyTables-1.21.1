package net.team_numbers.party_tables.item.component;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.team_numbers.party_tables.util.ModRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = ModRegister.createDataComponents();



    private static <T> DataComponentType<T>  register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> dataComponentType = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> dataComponentType);
        return dataComponentType;
    }

}
