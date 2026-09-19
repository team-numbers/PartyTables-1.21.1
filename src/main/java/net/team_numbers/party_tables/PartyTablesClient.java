package net.team_numbers.party_tables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.team_numbers.party_tables.client.layer.LayerCardHand;

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

    @SubscribeEvent
    static void onAddLayer(EntityRenderersEvent.AddLayers event) {
        addPlayerLayer(event, PlayerSkin.Model.WIDE);
        addPlayerLayer(event, PlayerSkin.Model.SLIM);
//        for (EntityType<?> entityType : event.getEntityTypes()) {
//            addEntityLayer(event, event.getRenderer(entityType));
//        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model skin) {
        var renderer = evt.getSkin(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new LayerCardHand<>(livingRenderer));
        }
    }
}
