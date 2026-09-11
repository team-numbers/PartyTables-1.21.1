package net.team_numbers.party_tables;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.block.ModBlocks;
import net.team_numbers.party_tables.block.entity.ModBlockEntities;
import net.team_numbers.party_tables.game.GameRuleRegistry;
import net.team_numbers.party_tables.game.UnoRule;
import net.team_numbers.party_tables.item.ModItems;
import org.slf4j.Logger;

@Mod(PartyTables.MOD_ID)
public final class PartyTables {

    public static final String MOD_ID = "party_tables";

    public static final Logger LOGGER = LogUtils.getLogger();

    public PartyTables(IEventBus modEventBus, ModContainer modContainer) {


        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        ModItems.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GameRuleRegistry.register(new UnoRule());
//            GameRuleRegistry.register(new BabanukiRule());
        });
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }
}
