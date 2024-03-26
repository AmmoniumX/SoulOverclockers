package com.ammonium.souloverclockers.compat.jade;

import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import snownee.jade.api.IBlockComponentProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class OverclockerJadeProvider implements IBlockComponentProvider {
    public static final OverclockerJadeProvider INSTANCE = new OverclockerJadeProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof OverclockerEntity overclocker)) return;
        // Owner
        tooltip.add(Component.translatable("tooltip.overclocker.owner", overclocker.getOwnerName()).withStyle(ChatFormatting.GRAY));

        // Running Status
        if (overclocker.isRunning()) {
            tooltip.add(Component.translatable("tooltip.overclocker.status.running").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.overclocker.status.idle").withStyle(ChatFormatting.DARK_GRAY));
        }

        // Multiplier and energy usage
        tooltip.add(Component.translatable("gui.sp", overclocker.getMultiplier()).withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.append(Component.literal(" @ ").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.append(Component.translatable("tooltip.overclocker.energy", overclocker.getFETick()).withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    public ResourceLocation getUid() {
        return JadePlugin.OVERCLOCKER_COMPONENT;
    }
}
