package com.ammonium.souloverclockers.soulpower;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoulPowerProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<SoulPower> SOUL_POWER = CapabilityManager.get(new CapabilityToken<SoulPower>() { });
    private SoulPower soulPower = null;
    private final LazyOptional<SoulPower> optional = LazyOptional.of(this::createSoulPower);

    private SoulPower createSoulPower() {
        if (soulPower == null) {
            this.soulPower = new SoulPower();
        }
        return this.soulPower;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == SOUL_POWER) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createSoulPower().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createSoulPower().loadNBTData(nbt);
    }
}
