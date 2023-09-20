package com.ammonium.souloverclockers.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class LoreBlockItem extends BlockItem {
    private final Component loreText;

    public LoreBlockItem(Block block, Properties properties, String loreText) {
        super(block, properties);
        this.loreText = Component.literal(loreText);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(loreText);
    }
}
