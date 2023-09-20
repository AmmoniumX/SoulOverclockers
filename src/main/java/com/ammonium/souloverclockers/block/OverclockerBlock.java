package com.ammonium.souloverclockers.block;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class OverclockerBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public OverclockerBlock() {
        super(Properties.of(SoulOverclockers.machineBlock)
                .sound(SoundType.METAL)
                .strength(1.0f)
                .lightLevel(state -> 0)
                .dynamicShape()
                .noOcclusion()
        );
    }
//    @Override
//    public BlockState getStateForPlacement(BlockPlaceContext context) {
//        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
//    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new OverclockerEntity(pPos, pState);
    }
}
