package com.ammonium.souloverclockers.soulpower;

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
