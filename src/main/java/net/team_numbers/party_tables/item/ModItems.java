package net.team_numbers.party_tables.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.team_numbers.party_tables.util.ModRegister;

public final class ModItems {

    private static final DeferredRegister.Items ITEMS = ModRegister.createItems();

    public static final DeferredItem<CardItem> CARD = ITEMS.register(
        "card", () -> new CardItem(new Item.Properties().stacksTo(32))
    );


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    private ModItems() {}
}
