package com.ammonium.souloverclockers.block.entity;

import com.ammonium.souloverclockers.SoulOverclockers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OverclockerEntity extends BlockEntity {
    public OverclockerEntity(BlockPos pPos, BlockState pBlockState) {
        super(SoulOverclockers.OVERCLOCKER_ENTITY.get(), pPos, pBlockState);
    }
}
