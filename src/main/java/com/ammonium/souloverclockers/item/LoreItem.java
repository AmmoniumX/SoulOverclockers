package com.ammonium.souloverclockers.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LoreItem extends Item {
    private final Component loreText;
    public LoreItem(Properties pProperties, String lore) {
        super(pProperties);
        this.loreText = Component.literal(lore);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(loreText);
    }
}
