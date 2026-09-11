package net.team_numbers.party_tables;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.team_numbers.party_tables.item.CardItem;
import net.team_numbers.party_tables.item.ModItems;
import net.team_numbers.party_tables.util.ModRegister;

import java.util.function.UnaryOperator;

public final class ModCreativeModeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = ModRegister.create(Registries.CREATIVE_MODE_TAB);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = registerSimple(
        "party_tables", "party_tables", Items.APPLE,
        (p, o) -> {
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "A"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "1"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "2"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "3"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "4"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "5"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "6"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "7"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "8"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "9"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "10"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "J"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "Q"));
            o.accept(CardItem.onlyType(ModItems.CARD.toStack(), "trump", "K"));
        }
    );

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerSimple(String name, String title, ItemLike icon, ItemLike... items) {
        return register(name, b -> b.title(Component.translatable("itemGroup." + title)).icon(() -> icon.asItem().getDefaultInstance()).displayItems((p, o) -> {
            for (ItemLike item : items) {
                o.accept(item);
            }
        }));
    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> registerSimple(String name, String title, ItemLike icon, CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        return register(name, b -> b.title(Component.translatable("itemGroup." + title)).icon(() -> icon.asItem().getDefaultInstance()).displayItems(displayItemsGenerator));
    }

    private static DeferredHolder<CreativeModeTab, CreativeModeTab> register(String name, UnaryOperator<CreativeModeTab.Builder> builderFunc) {
        return CREATIVE_MODE_TABS.register(name, () -> builderFunc.apply(CreativeModeTab.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
