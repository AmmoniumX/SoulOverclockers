package com.ammonium.souloverclockers.block.entity.resolver;

import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class IEMasterBlockEntityResolver extends MasterBlockEntityResolver {
    @Override
    public BlockEntity getMasterBlockEntity(BlockEntity be) {
        if (be instanceof MultiblockPartBlockEntity<?>) {
            return ((MultiblockPartBlockEntity<?>) be).master();
        }
        return null;
    }
}
