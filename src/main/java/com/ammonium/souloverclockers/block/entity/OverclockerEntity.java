package com.ammonium.souloverclockers.block.entity;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.OverclockerBlock;
import com.ammonium.souloverclockers.block.entity.resolver.MasterBlockEntityResolver;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Config;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPower;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import com.ammonium.souloverclockers.tag.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


public class OverclockerEntity extends BlockEntity implements IEnergyStorage {


    private boolean running = false;
    private int multiplier = 0;
    private int tickCounter = 0;
    private UUID ownerUUID;
    private String ownerName;
    private @Nullable BlockPos targetPos = null;
    private final InsertOnlyEnergyStorage energyStorage = new InsertOnlyEnergyStorage(Config.OVERCLOCKER_FE_CAPACITY.get(), Config.OVERCLOCKER_FE_TRANSFER.get());
    private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    public OverclockerEntity(BlockPos pPos, BlockState pBlockState) {
        super(SoulOverclockers.OVERCLOCKER_ENTITY.get(), pPos, pBlockState);
    }
    public void setOwner(UUID ownerUUID, String ownerName) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.setChanged();
        this.sendUpdates();
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Nullable
    public String getOwnerName() {
        return ownerName;
    }

    public void setTargetPos(@Nullable BlockPos targetPos) {
        this.targetPos = targetPos;
        this.setChanged();
        this.sendUpdates();
    }

    @Nullable
    public BlockPos getTargetPos() {
        return targetPos;
    }

    // Energy Storage methods
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return energyStorage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return energyStorage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return energyStorage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return energyStorage.canReceive();
    }

    public int getMultiplier() {
        return multiplier;
    }

    public int increaseMultiplier() {
//        SoulOverclockers.LOGGER.debug("Increasing multiplier");
        if (this.level == null || this.level.isClientSide) return -1; // Only access from server side
        int newMult = Math.min(multiplier+2, Config.MAX_MULTIPLIER.get());
        if (newMult != multiplier) {
            AtomicBoolean success = new AtomicBoolean(false);
            Player player = getPlayerOwner();
            if (player == null) return -1;

            player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
                // Add new value
                boolean addSuccess = soulPower.addUsed(newMult - multiplier);
                if (addSuccess) {
                    syncCapabilities(this.level, soulPower);
                }
                success.set(addSuccess);
            });
            // Change multiplier in case of success and update
            if (success.get()) {
                multiplier = newMult;
                this.setChanged();
                this.sendUpdates();
            }
        }
        return multiplier;
    }
    public int decreaseMultiplier() {
//        SoulOverclockers.LOGGER.debug("Decreasing multiplier");
        if (this.level == null || this.level.isClientSide) return -1; // Only access from server side
        int newMult = Math.max(multiplier-2, 0);
        if (newMult != multiplier) {
            AtomicBoolean success = new AtomicBoolean(false);
            Player player = getPlayerOwner();
            if (player == null) return -1;

            player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
                // Decrease value
                soulPower.removeUsed(multiplier - newMult);
                syncCapabilities(this.level, soulPower);
            });
            multiplier = newMult;
            this.setChanged();
            this.sendUpdates();
        }
        return multiplier;
    }
    public boolean setMultiplier(int toSet) {
//        SoulOverclockers.LOGGER.debug("Setting multiplier to "+toSet);
        if (this.level == null || this.level.isClientSide) return false; // Only access from server side

        AtomicBoolean toReturn = new AtomicBoolean(false);
        Player player = getPlayerOwner();
        if (player == null) return false;

        player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
            // Remove old value
            if (multiplier != 0) {
                soulPower.removeUsed(multiplier);
            }
            // Add new value
            boolean success = soulPower.addUsed(toSet);
            if(success) {
                multiplier = toSet;
                this.sendUpdates();
                this.setChanged();
                syncCapabilities(this.level, soulPower);
            }
            toReturn.set(success);
        });

        return toReturn.get();
    }

    public void syncCapabilities(Level level, SoulPower soulPower) {
        if (level.isClientSide) return;
//        SoulOverclockers.LOGGER.debug("Syncing soul power "+soulPower.getUsed()+"/"+soulPower.getCapacity());
        Messages.sendToPlayer(new CapabilitySyncPacket(soulPower.getUsed(), soulPower.getCapacity(), this.ownerUUID), (ServerPlayer) getPlayerOwner());
    }

    public int getFETick() {
        return this.multiplier * Config.FE_COST_MULTIPLIER.get();
    }

    public void setRunning(boolean running) {
        if (this.running != running) {
            this.running = running;
            this.setChanged();
            this.sendUpdates();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    /** Get the owner of this Overclocker
     * @return player or null if player is offline
     */
    @Nullable
    public Player getPlayerOwner() {
        if (level == null || this.getOwnerUUID() == null) return null;
        return level.getPlayerByUUID(this.getOwnerUUID());
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, OverclockerEntity pBlockEntity) {
        if (!pLevel.isClientSide) {

            // When not running (idle), only run tick function every 1 second
            if (!pBlockEntity.running) {
                pBlockEntity.tickCounter++;
                if (pBlockEntity.tickCounter < 20) return;
                pBlockEntity.tickCounter = 0;
            }

            // Check if player is online
            boolean shouldBeLit = false;

            // Check if powered by any block besides top block
            int maxSignal = getMaxSignalExcludingTop(pLevel, pPos);

            // Search for target block entity, either directly on top or through master block entity
            BlockEntity target = pLevel.getBlockEntity(pPos.above());
            if (target == null) {
                BlockEntity masterBe = MasterBlockEntityResolver.resolveMasterBlockEntity(target);
                if (masterBe != null) {
                    target = masterBe;
                }
            }

            pBlockEntity.setTargetPos(target != null ? target.getBlockPos() : null);
            if (target == null) return;


            BlockState targetBlockState = target.getBlockState();
//            targetBlockState.getTags().anyMatch()
            BlockEntityTicker<BlockEntity> ticker = targetBlockState.getTicker(pLevel, (BlockEntityType<BlockEntity>) target.getType());

            // Check if satisfies all rules for running
            boolean canRun = !(Config.REQUIRE_ONLINE.get() && pBlockEntity.getPlayerOwner() == null) &&
                    pBlockEntity.getMultiplier() != 0 && maxSignal == 0 && !(target instanceof OverclockerEntity) &&
                    ticker != null && !(targetBlockState.is(BlockTags.CANT_ACCELERATE) && !(targetBlockState.is(BlockTags.CANT_OVERCLOCK)));

            if (canRun) {
                // Check if tickable
                // Tick (mult-1) times
//                    SoulOverclockers.LOGGER.debug("Block energy: "+pBlockEntity.energyStorage.getEnergyStored());
                int toExtract = pBlockEntity.getMultiplier() * Config.FE_COST_MULTIPLIER.get();
                int testExtract = pBlockEntity.energyStorage.secureExtractEnergy(toExtract, true);
//                    SoulOverclockers.LOGGER.debug("Test extracted "+testExtract+" out of "+toExtract);
                if (toExtract == testExtract) {
                    shouldBeLit = true;
                    pBlockEntity.energyStorage.secureExtractEnergy(toExtract, false);
                    for (int i = 1; i < pBlockEntity.getMultiplier(); i++) {
                        ticker.tick(pLevel, pPos.above(), target.getBlockState(), target);
                    }
                }
            }
            pBlockEntity.setRunning(shouldBeLit);
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
//            SoulOverclockers.LOGGER.debug("Updating LIT state");
            pLevel.setBlock(pPos, pState.setValue(OverclockerBlock.LIT, shouldBeLit), 3);
            pBlockEntity.setChanged();
            pBlockEntity.sendUpdates();
            pLevel.updateNeighborsAt(pPos, pState.getBlock());
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setChanged() {
        super.setChanged();
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
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("multiplier", this.multiplier);
        tag.put("energy", energyStorage.serializeNBT());
        if (ownerUUID != null && ownerName != null) {
            tag.putUUID("ownerUUID", ownerUUID);
            tag.putString("ownerName", ownerName);
        }
        tag.putBoolean("hasTarget", targetPos != null);
        if (targetPos != null) {
            tag.putInt("targetX", targetPos.getX());
            tag.putInt("targetY", targetPos.getY());
            tag.putInt("targetZ", targetPos.getZ());
        }
        return tag;
    }
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.multiplier = tag.getInt("multiplier");
        energyStorage.deserializeNBT(tag.get("energy"));
        if (tag.contains("ownerUUID") && tag.contains("ownerName")) {
            ownerUUID = tag.getUUID("ownerUUID");
            ownerName = tag.getString("ownerName");
        }
        if (tag.contains("hasTarget")) {
            if (tag.getBoolean("hasTarget")) {
                targetPos = new BlockPos(tag.getInt("targetX"), tag.getInt("targetY"), tag.getInt("targetZ"));
            } else {
                targetPos = null;
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("multiplier", this.multiplier);
        tag.put("energy", energyStorage.serializeNBT());
        if (ownerUUID != null && ownerName != null) {
            tag.putUUID("ownerUUID", ownerUUID);
            tag.putString("ownerName", ownerName);
        }
        tag.putBoolean("hasTarget", targetPos != null);
        if (targetPos != null) {
            tag.putInt("targetX", targetPos.getX());
            tag.putInt("targetY", targetPos.getY());
            tag.putInt("targetZ", targetPos.getZ());
        }
    }
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.multiplier = tag.getInt("multiplier");
        energyStorage.deserializeNBT(tag.get("energy"));
        if (tag.contains("ownerUUID") && tag.contains("ownerName")) {
            ownerUUID = tag.getUUID("ownerUUID");
            ownerName = tag.getString("ownerName");
        }
        if (tag.contains("hasTarget")) {
            if (tag.getBoolean("hasTarget")) {
                targetPos = new BlockPos(tag.getInt("targetX"), tag.getInt("targetY"), tag.getInt("targetZ"));
            } else {
                targetPos = null;
            }
        }
    }
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energy.invalidate();
    }
}
