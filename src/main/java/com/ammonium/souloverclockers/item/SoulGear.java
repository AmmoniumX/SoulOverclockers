package com.ammonium.souloverclockers.item;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPower;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class SoulGear extends Item implements ICurioItem {
    private final int power;
    public SoulGear(int power) {
        super(new Item.Properties().tab(SoulOverclockers.CREATIVE_TAB).stacksTo(1));
        this.power = power;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("SoulPower")) {
            tooltip.add(Component.literal("When worn: increases Soul Power by ").withStyle(ChatFormatting.DARK_PURPLE)
                    .append(Component.translatable("gui.sp", tag.getInt("SoulPower")).withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        if (this.allowedIn(category)) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putInt("SoulPower", this.power);
            items.add(stack);
        }
    }
}
