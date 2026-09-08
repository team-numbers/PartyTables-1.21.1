package net.team_numbers.party_tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.team_numbers.party_tables.block.entity.CardTableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CardTableBlock extends Block implements EntityBlock {

    public CardTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, net.minecraft.core.BlockPos pos,
                                               net.minecraft.world.entity.player.Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (level.getBlockEntity(pos) instanceof CardTableBlockEntity be && player instanceof ServerPlayer sp) {
            if (!be.hasActiveGame()) {
                // ゲーム未開始なら「参加待ち」状態としてセッションだけ作る、
                // あるいはGUIを開いてルール選択させる、など仕様次第
            } else {
                be.tryJoin(sp);
            }
            // 手札を表示するクライアント側Screenを開く処理をここで呼ぶ
            // 例: NetworkHooks的な仕組み or 独自のOpenScreenPayloadをsp宛に送る
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CardTableBlockEntity(blockPos, blockState);
    }
}
