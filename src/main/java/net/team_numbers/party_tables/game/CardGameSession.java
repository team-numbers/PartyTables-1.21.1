package net.team_numbers.party_tables.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.team_numbers.party_tables.attachment.CardHand;
import net.team_numbers.party_tables.attachment.ModAttachments;
import net.team_numbers.party_tables.block.entity.CardTableBlockEntity;
import net.team_numbers.party_tables.network.SyncHandCountPayload;
import net.team_numbers.party_tables.network.SyncOwnHandPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class CardGameSession {

    private final ServerLevel level;
    private final BlockPos tablePos;

    private final List<UUID> participants = new ArrayList<>();
    private final Deck deck = new Deck();
    private final List<ItemStack> field = new ArrayList<>(); // 場に出ているカード（捨て札山など)

    private int turnIndex = 0;
    private GameRule rule;

    public CardGameSession(ServerLevel level, BlockPos tablePos, GameRule rule) {
        this.level = level;
        this.tablePos = tablePos;
        this.rule = rule;
    }

    // ---- ライフサイクル ----

    public void addParticipant(ServerPlayer player) {
        if (!participants.contains(player.getUUID())) {
            participants.add(player.getUUID());
        }
    }

    public void removeParticipant(ServerPlayer player) {
        participants.remove(player.getUUID());
    }

    public List<ServerPlayer> getParticipants() {
        List<ServerPlayer> result = new ArrayList<>();
        for (UUID id : participants) {
            ServerPlayer p = level.getServer().getPlayerList().getPlayer(id);
            if (p != null) result.add(p);
        }
        return result;
    }

    /** ゲーム開始：山札を生成してシャッフルし、各participantに配る */
    public void startGame(List<ItemStack> fullDeckCards, int initialHandSize) {
        deck.reset(fullDeckCards);
        deck.shuffle();
        field.clear();
        turnIndex = 0;

        for (ServerPlayer player : getParticipants()) {
            CardHand hand = new CardHand();
            for (int i = 0; i < initialHandSize; i++) {
                Optional<ItemStack> drawn = deck.drawOne();
                drawn.ifPresent(hand::addCard);
            }
            player.setData(ModAttachments.CARD_HAND, hand);
        }

        broadcastHandCounts();
        broadcastFieldState();
    }

    // ---- ターン管理 ----

    public boolean isPlayerTurn(ServerPlayer player) {
        if (participants.isEmpty()) return false;
        UUID currentTurnPlayer = participants.get(turnIndex % participants.size());
        return currentTurnPlayer.equals(player.getUUID());
    }

    private void advanceTurn() {
        if (!participants.isEmpty()) {
            turnIndex = (turnIndex + 1) % participants.size();
        }
    }

    // ---- カード操作 ----

    /** 山札から1枚引いて本人の手札に加える。引けなければ空を返す */
    public ItemStack drawCardFor(ServerPlayer player) {
        Optional<ItemStack> drawnOpt = deck.drawOne();
        if (drawnOpt.isEmpty()) return ItemStack.EMPTY;

        ItemStack drawn = drawnOpt.get();
        CardHand hand = player.getData(ModAttachments.CARD_HAND);
        hand.addCard(drawn);
        player.setData(ModAttachments.CARD_HAND, hand);

        // 本人にフル同期
        PacketDistributor.sendToPlayer(
            player,
            new SyncOwnHandPayload(hand.copy())
        );

        advanceTurn(); // 「引いたら手番終了」のルールなら。ルールにより呼ばない場合も
        return drawn;
    }

    /**
     * 手札のindex番目のカードを場に出そうとする。
     * ルール検証(rule.canPlay)に通らなければfalseを返し、状態は変更しない。
     */
    public abstract boolean tryPlayCard(ServerPlayer player, int handIndex);
//    public boolean tryPlayCard(ServerPlayer player, int handIndex) {
//        CardHand hand = player.getData(ModAttachments.CARD_HAND);
//        ItemStack card = hand.get(handIndex);
//        if (card.isEmpty()) return false;
//
//        ItemStack topOfField = field.isEmpty() ? ItemStack.EMPTY : field.get(field.size() - 1);
//        if (!rule.canPlay(card, topOfField)) {
//            return false;
//        }
//
//        Optional<ItemStack> played = hand.playCard(handIndex);
//        if (played.isEmpty()) return false;
//
//        field.add(played.get());
//        player.setData(ModAttachments.CARD_HAND, hand);
//
//        // 本人にフル同期（手札が減ったことを反映）
//        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
//            player,
//            new SyncOwnHandPayload(hand.copy())
//        );
//
//        advanceTurn();
//        return true;
//    }

    // ---- 同期用データ組み立て ----

    public List<SyncHandCountPayload.Entry> buildCountEntries() {
        List<SyncHandCountPayload.Entry> entries = new ArrayList<>();
        for (ServerPlayer p : getParticipants()) {
            CardHand hand = p.getData(ModAttachments.CARD_HAND);
            entries.add(new SyncHandCountPayload.Entry(p.getUUID(), hand.size()));
        }
        return entries;
    }

    public void broadcastHandCounts() {
        var payload = new SyncHandCountPayload(buildCountEntries());
        for (ServerPlayer p : getParticipants()) {
            PacketDistributor.sendToPlayer(p, payload);
        }
    }

    public void broadcastFieldState() {
        // 場札は「全員に見える情報」なので専用Payloadを別途用意する
        // 例: new SyncFieldPayload(List.copyOf(field))
//        var payload = new SyncFieldPayload(List.copyOf(field));
//        for (ServerPlayer p : getParticipants()) {
//            PacketDistributor.sendToPlayer(p, payload);
//        }
    }

    // ---- テーブル位置からセッションを引く仕組み ----

    /**
     * 実際にはBlockEntity側でCardGameSessionを保持し、
     * BlockEntityを取得してそこからセッションを引く形にする。
     */
    public static CardGameSession getAt(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        if (serverLevel.getBlockEntity(pos) instanceof CardTableBlockEntity be) {
            return be.getSession();
        }
        return null;
    }

    public record SessionData(
        List<UUID> participants,
        List<ItemStack> deckCards,
        List<ItemStack> fieldCards,
        int turnIndex,
        String ruleId
    ) {
        public static final Codec<SessionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                UUIDUtil.CODEC.listOf().fieldOf("participants").forGetter(SessionData::participants),
                ItemStack.OPTIONAL_CODEC.listOf().fieldOf("deck").forGetter(SessionData::deckCards),
                ItemStack.OPTIONAL_CODEC.listOf().fieldOf("field").forGetter(SessionData::fieldCards),
                Codec.INT.fieldOf("turnIndex").forGetter(SessionData::turnIndex),
                Codec.STRING.fieldOf("ruleId").forGetter(SessionData::ruleId)
            ).apply(instance, SessionData::new)
        );
    }

    public SessionData toData() {
        return new SessionData(
            List.copyOf(participants),
            deck.toList(),
            List.copyOf(field),
            turnIndex,
            rule.id()
        );
    }

    public static CardGameSession fromData(ServerLevel level, BlockPos pos, SessionData data) {
        GameRule rule = GameRuleRegistry.byId(data.ruleId());
        CardGameSession session = switch (rule.id()) {
            case UnoRule.ID -> new UnoSession(level, pos, rule);
//            case BabanukiRule.ID -> new BabanukiSession(level, pos, rule);
            default -> throw new IllegalStateException("No session factory for rule: " + rule.id());
        };
        session.participants.addAll(data.participants());
        session.deck.reset(data.deckCards());
        session.field.addAll(data.fieldCards());
        session.turnIndex = data.turnIndex();
        return session;
    }

    public static class UnoSession extends CardGameSession {

        public UnoSession(ServerLevel level, BlockPos tablePos, GameRule rule) {
            super(level, tablePos, rule);
        }

        @Override
        public boolean tryPlayCard(ServerPlayer player, int handIndex) {
            // UnoRule.canPlay を使った既存ロジック
            return true;
        }
    }
}
