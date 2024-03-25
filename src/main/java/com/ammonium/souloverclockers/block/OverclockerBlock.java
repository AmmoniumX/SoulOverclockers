package com.ammonium.souloverclockers.block;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class OverclockerBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public OverclockerBlock() {
        super(Properties.of(SoulOverclockers.machineBlock)
                .sound(SoundType.METAL)
                .strength(1.0f)
                .lightLevel(state -> 0)
        );
    }
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        if (!world.isClientSide && entity instanceof Player) {
            Player player = (Player) entity;
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof OverclockerEntity overclocker) {
                overclocker.setOwner(player.getUUID());
//                SoulOverclockers.LOGGER.debug("Pre-setting overclocker multiplier to 2");
                if (!overclocker.setMultiplier(2)) {
                    SoulOverclockers.LOGGER.debug("Failed to set overclocker multiplier");
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (!pLevel.isClientSide) {
                BlockEntity be = pLevel.getBlockEntity(pPos);
                if (be instanceof OverclockerEntity overclocker) {
                    ServerPlayer player = (ServerPlayer) overclocker.getPlayerOwner();
                    if (player == null) return;
                    player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
                        soulPower.removeUsed(overclocker.getMultiplier());
//                        SoulOverclockers.LOGGER.debug("Removed " + overclocker.getMultiplier() + " soul power. Now at " +
//                                soulPower.getUsed() + "/" + soulPower.getCapacity());
                        Messages.sendToPlayer(new CapabilitySyncPacket(soulPower.getUsed(), soulPower.getCapacity(),
                                overclocker.getOwnerUUID()), player);
                    });
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new OverclockerEntity(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : checkType(pBlockEntityType, SoulOverclockers.OVERCLOCKER_ENTITY.get(),
                (level, pos, state, blockEntity) -> OverclockerEntity.tick(level, pos, state, (OverclockerEntity) blockEntity));
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> checkType(BlockEntityType<T> blockEntityType, BlockEntityType<?> expectedType, BlockEntityTicker<? super T> ticker) {
        return blockEntityType == expectedType ? (BlockEntityTicker<T>) ticker : null;
    }
}
