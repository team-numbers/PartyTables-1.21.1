package net.team_numbers.party_tables.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.team_numbers.party_tables.PartyTables;

@EventBusSubscriber(modid = PartyTables.MOD_ID)
public class ModNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1"); // プロトコルバージョン

        // C -> S
        registrar.playToServer(
            DrawCardRequestPayload.TYPE,
            DrawCardRequestPayload.STREAM_CODEC,
            ServerPayloadHandler::handleDrawCard
        );

        registrar.playToServer(
            PlayCardRequestPayload.TYPE,
            PlayCardRequestPayload.STREAM_CODEC,
            ServerPayloadHandler::handlePlayCard
        );

        // S -> C
        registrar.playToClient(
            SyncOwnHandPayload.TYPE,
            SyncOwnHandPayload.STREAM_CODEC,
            ClientPayloadHandler::handleSyncOwnHand
        );

        registrar.playToClient(
            SyncHandCountPayload.TYPE,
            SyncHandCountPayload.STREAM_CODEC,
            ClientPayloadHandler::handleSyncHandCount
        );
    }
}
