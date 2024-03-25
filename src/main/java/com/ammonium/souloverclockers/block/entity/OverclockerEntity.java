package com.ammonium.souloverclockers.block.entity;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.OverclockerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class OverclockerEntity extends BlockEntity {
    private int multiplier = 2;
    private final int MAX_MULTIPLIER = 128;
    public OverclockerEntity(BlockPos pPos, BlockState pBlockState) {
        super(SoulOverclockers.OVERCLOCKER_ENTITY.get(), pPos, pBlockState);
    }

    public int getMultiplier() {
        return multiplier;
    }
    public int increaseMultiplier() {
        multiplier = Math.min(multiplier+2, MAX_MULTIPLIER);
        this.setChanged();
        this.sendUpdates();
        return multiplier;
    }
    public int decreaseMultiplier() {
        multiplier = Math.max(multiplier-2, 2);
        this.setChanged();
        this.sendUpdates();
        return multiplier;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, OverclockerEntity pBlockEntity) {
        if (!pLevel.isClientSide) {
            boolean shouldBeLit = false;
            // Check if powered by any block besides top block
            int maxSignal = getMaxSignalExcludingTop(pLevel, pPos);

            // Check if block above is block entity or overclocker
            BlockEntity above = pLevel.getBlockEntity(pPos.above());

            if (maxSignal == 0 && above != null && !(above instanceof OverclockerEntity)) {
                // Check if tickable
                BlockEntityTicker<BlockEntity> ticker = above.getBlockState().getTicker(pLevel, (BlockEntityType<BlockEntity>) above.getType());
                if (ticker != null) {
                    shouldBeLit = true;
                    // Tick (mult-1) times
                    for (int i = 1; i < pBlockEntity.getMultiplier(); i++) {
                        ticker.tick(pLevel, pPos.above(), above.getBlockState(), above);
                    }
                }
            }

            // Update LIT state
            updateLitState(pLevel, pPos, pState, pBlockEntity, shouldBeLit);
        }
    }
    private static int getMaxSignalExcludingTop(Level pLevel, BlockPos pPos) {
        int maxSignal = 0;
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                int signal = pLevel.getSignal(pPos.relative(direction), direction);
                maxSignal = Math.max(signal, maxSignal);
            }
        }
        return maxSignal;
    }
    private static void updateLitState(Level pLevel, BlockPos pPos, BlockState pState, OverclockerEntity pBlockEntity, boolean shouldBeLit) {
        if (pState.getValue(OverclockerBlock.LIT) != shouldBeLit && pLevel.getBlockState(pPos).getValue(OverclockerBlock.LIT) != shouldBeLit) {
            pLevel.setBlock(pPos, pState.setValue(OverclockerBlock.LIT, shouldBeLit), 3);
            pBlockEntity.setChanged();
            pBlockEntity.sendUpdates();
            pLevel.updateNeighborsAt(pPos, pState.getBlock());
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("multiplier", this.multiplier);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.load(Objects.requireNonNull(pkt.getTag()));
    }
    public void sendUpdates() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("multiplier")) {
            this.multiplier = tag.getInt("multiplier");
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("multiplier", this.multiplier);
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("multiplier")) {
            this.multiplier = tag.getInt("multiplier");
        }
    }
}
