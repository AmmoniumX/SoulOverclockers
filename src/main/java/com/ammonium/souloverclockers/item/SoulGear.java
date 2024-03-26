package com.ammonium.souloverclockers.item;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPower;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SoulGear extends LoreItem implements ICurioItem {
    public SoulGear() {
        super(new Item.Properties().tab(SoulOverclockers.CREATIVE_TAB).stacksTo(1), "Wear to increase your Soul Power.");
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // ticking logic here
    }

//    @Override
//    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
//        Level level = slotContext.entity().getLevel();
//        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
//        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
//        player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
//            if (stack.getTag() == null || !stack.getTag().contains("SoulPower")) return;
//            soulPower.addCapacity(stack.getTag().getInt("SoulPower"));
//            syncCapabilities(serverLevel, soulPower, player);
//        });
//        ICurioItem.super.onEquip(slotContext, prevStack, stack);
//    }
//
//    @Override
//    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
//        Level level = slotContext.entity().getLevel();
//        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;
//        if (!(slotContext.entity() instanceof ServerPlayer player)) return;
//        player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
//            if (stack.getTag() == null || !stack.getTag().contains("SoulPower")) return;
//            soulPower.removeCapacity(stack.getTag().getInt("SoulPower"));
//            syncCapabilities(serverLevel, soulPower, player);
//        });
//        ICurioItem.super.onUnequip(slotContext, newStack, stack);
//    }
//
//    public void syncCapabilities(ServerLevel level, SoulPower soulPower, ServerPlayer player) {
//        if (level.isClientSide()) return;
//        SoulOverclockers.LOGGER.debug("Syncing soul power "+soulPower.getUsed()+"/"+soulPower.getCapacity());
//        Messages.sendToPlayer(new CapabilitySyncPacket(soulPower.getUsed(), soulPower.getCapacity(), player.getUUID()), player);
//    }
}
