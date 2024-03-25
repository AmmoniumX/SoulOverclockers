package com.ammonium.souloverclockers.soulpower;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.setup.Config;

public class ClientCapabilityData {
    private static int used;
    private static int cap;

    public static int getUsed() {
        return used;
    }
    public static int getCap() {
        return cap;
    }

    public static void updateCapabilityData(int newUsed, int newCap) {
        used = newUsed;
        cap = newCap;
    }
}
