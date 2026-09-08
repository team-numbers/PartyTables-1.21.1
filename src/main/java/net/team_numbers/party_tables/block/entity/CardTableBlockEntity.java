package net.team_numbers.party_tables.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.attachment.CardHand;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.game.CardGameSession;
import net.team_numbers.party_tables.network.SyncOwnHandPayload;

import javax.annotation.Nullable;

public class CardTableBlockEntity extends BlockEntity {

    @Nullable
    private CardGameSession session;

    public CardTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CARD_TABLE.get(), pos, state);
    }

    // ---- セッションアクセス ----

    @Nullable
    public CardGameSession getSession() {
        return session;
    }

    public boolean hasActiveGame() {
        return session != null;
    }

    /**
     * 新しいゲームを開始する。既にゲームが進行中なら失敗を返す。
     */
//    public boolean startNewGame(GameRule rule, java.util.List<net.minecraft.world.item.ItemStack> fullDeck, int initialHandSize) {
//        if (session != null) return false;
//        if (!(level instanceof ServerLevel serverLevel)) return false;
//
//        this.session = new CardGameSession(serverLevel, worldPosition, rule);
//        setChanged();
//        return true;
//    }

    /**
     * プレイヤーがテーブルを右クリックしたときの参加処理。
     * 実際の右クリック判定・GUIオープンはBlockクラス側から呼ぶ想定。
     */
    public boolean tryJoin(ServerPlayer player) {
        if (session == null) return false;
        session.addParticipant(player);
        setChanged();
        return true;
    }

    public void leave(ServerPlayer player) {
        if (session == null) return;
        session.removeParticipant(player);
        setChanged();

        // 全員抜けたらセッションを破棄してテーブルを空き状態に戻す
        if (session.getParticipants().isEmpty()) {
            endGame();
        }
    }

    /**
     * ゲーム終了処理。手札に残っているカードの扱い(消滅/ドロップ/山札に戻す等)は
     * ここでルールに応じて実装する。
     */
    public void endGame() {
        if (session == null) return;

        for (ServerPlayer p : session.getParticipants()) {
            p.setData(ModAttachments.CARD_HAND, new CardHand());
            // クライアントにも空の手札を通知
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                p, new SyncOwnHandPayload(new CardHand())
            );
        }

        this.session = null;
        setChanged();
    }

    // ---- 永続化 ----

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (session != null) {
            var result = CardGameSession.SessionData.CODEC
                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), session.toData());
            result.resultOrPartial(err ->
                PartyTables.LOGGER.error("Failed to save CardGameSession: {}", err)
            ).ifPresent(encoded -> tag.put("Session", encoded));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Session") && level instanceof ServerLevel serverLevel) {
            var result = CardGameSession.SessionData.CODEC
                .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("Session"));
            result.resultOrPartial(err ->
                PartyTables.LOGGER.error("Failed to load CardGameSession: {}", err)
            ).ifPresent(data ->
                this.session = CardGameSession.fromData(serverLevel, worldPosition, data)
            );
        }
    }

    // チャンクがアンロードされる際にクライアントへ送るための初期データ同期
    // (場札の見た目をクライアントで描画するために使う。手札そのものはここに含めない)
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (session != null) {
            tag.putBoolean("HasGame", true);
            // 場札(全員に見える情報)だけをタグに詰める例
//            var fieldResult = net.minecraft.world.item.ItemStack.OPTIONAL_CODEC.listOf()
//                .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), session.getFieldView());
//            fieldResult.resultOrPartial(err -> {}).ifPresent(encoded -> tag.put("Field", encoded));
        } else {
            tag.putBoolean("HasGame", false);
        }
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
