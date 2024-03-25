package com.ammonium.souloverclockers.soulpower;

import com.ammonium.souloverclockers.setup.Config;
import net.minecraft.nbt.CompoundTag;

public class SoulPower {
    private int capacity = Config.BASE_SOUL_POWER.get();
    private int used = 0;

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }


    public void addCapacity(int toAdd) {
        this.capacity += toAdd;
    }

    public void removeCapacity(int toRemove) {
        this.capacity -= toRemove;
    }

    /**
     * Use soul power.
     * @param toAdd How much more soul power is to use
     * @return true if there is enough capacity left, false if not
     */
    public boolean addUsed(int toAdd) {
        if (this.used + toAdd <= this.capacity) {
            this.used += toAdd;
            return true;
        }
        return false;
    }

    public void removeUsed(int toRemove) {
        this.used -= toRemove;
        if (this.used < 0) {
            this.used = 0;
        }
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getUsed() {
        return this.used;
    }

    public void copyFrom(SoulPower source) {
        this.capacity = source.getCapacity();
        this.used = source.getUsed();
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("soul_power_used", this.used);
        nbt.putInt("soul_power_capacity", this.capacity);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.used = nbt.getInt("soul_power_used");
        this.capacity = nbt.getInt("soul_power_capacity");
    }
}
