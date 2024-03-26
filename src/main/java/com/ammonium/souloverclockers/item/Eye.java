package com.ammonium.souloverclockers.item;

import com.ammonium.souloverclockers.SoulOverclockers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Eye extends Item {
    public Eye() {
        super(new Item.Properties().tab(SoulOverclockers.CREATIVE_TAB).stacksTo(16));
    }
}
