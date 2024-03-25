package com.ammonium.souloverclockers.listener;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulOverclockers.MODID)
public class ServerEventListener {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> Messages.sendToPlayer(new CapabilitySyncPacket(soulPower.getUsed(), soulPower.getCapacity(), player.getUUID()), player));
    }

}
