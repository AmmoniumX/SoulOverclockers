package com.ammonium.souloverclockers.block.entity;

import net.minecraftforge.energy.EnergyStorage;

public class InsertOnlyEnergyStorage extends EnergyStorage {
    public InsertOnlyEnergyStorage(int capacity, int rate) {
        super(capacity, rate, rate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        // prevent energy extraction
        return 0;
    }

    protected int secureExtractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            energy -= energyExtracted;
        return energyExtracted;
    }
}
