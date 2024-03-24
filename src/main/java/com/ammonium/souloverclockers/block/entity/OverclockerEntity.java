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
        if (multiplier >= MAX_MULTIPLIER) {
            multiplier = 2;
        }
        multiplier += 2;
        this.setChanged();
        this.sendUpdates();
        return multiplier;
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, OverclockerEntity pBlockEntity) {
        if(!pLevel.isClientSide) {
            // Check if powered by any block besides top block
            int maxSignal = 0;
            for (Direction direction : Direction.values()) {
                if (direction != Direction.UP) {
                    int signal = pLevel.getSignal(pPos.relative(direction), direction);
                    maxSignal = Math.max(signal, maxSignal);
                }
            }
            if (maxSignal > 0) {
                // Disable ticking if receiving redstone power
                SoulOverclockers.LOGGER.debug("Disabled due to redstone signal");
                if (pState.getValue(OverclockerBlock.LIT)) {
                    pState.setValue(OverclockerBlock.LIT, false);
                    pBlockEntity.setChanged();
                    pBlockEntity.sendUpdates();
                }
                return;
            }
            // Check if block above is block entity or overclocker
            BlockEntity above = pLevel.getBlockEntity(pPos.above());
            if (above == null || above instanceof OverclockerEntity) {
                SoulOverclockers.LOGGER.debug("Aborting: not a block entity or Overclocker");
                if (pState.getValue(OverclockerBlock.LIT)) {
                    pState.setValue(OverclockerBlock.LIT, false);
                    pBlockEntity.setChanged();
                    pBlockEntity.sendUpdates();
                }
                return;
            }
            // Check if tickable
            BlockEntityTicker<BlockEntity> ticker = above.getBlockState().getTicker(pLevel, (BlockEntityType<BlockEntity>) above.getType());
            if (ticker == null) {
                SoulOverclockers.LOGGER.debug("Aborting: not tickable");
                if (pState.getValue(OverclockerBlock.LIT)) {
                    pState.setValue(OverclockerBlock.LIT, false);
                    pBlockEntity.setChanged();
                    pBlockEntity.sendUpdates();
                }
                return;
            }
            if (!pState.getValue(OverclockerBlock.LIT)) {
                pState.setValue(OverclockerBlock.LIT, true);
                pBlockEntity.setChanged();
                pBlockEntity.sendUpdates();
            }
            // Tick (mult-1) times
            for(int i = 1; i < pBlockEntity.getMultiplier(); i++) {
                ticker.tick(pLevel, pPos.above(), above.getBlockState(), above);
            }
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
