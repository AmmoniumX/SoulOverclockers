package com.ammonium.souloverclockers.listener;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.item.SoulGear;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Config;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@Mod.EventBusSubscriber(modid = SoulOverclockers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerEventListener {
    @SubscribeEvent
    public static void sendImc(InterModEnqueueEvent evt) {
        SoulOverclockers.LOGGER.debug("Registering curios slots");
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().size(1).build());
    }

}
