package net.team_numbers.party_tables;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = PartyTables.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = PartyTables.MOD_ID, value = Dist.CLIENT)
public final class PartyTablesClient {


    public PartyTablesClient(ModContainer container) {
//        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        PartyTables.LOGGER.info("HELLO FROM CLIENT SETUP");
        PartyTables.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
