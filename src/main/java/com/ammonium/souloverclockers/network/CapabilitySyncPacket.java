package com.ammonium.souloverclockers.network;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.soulpower.ClientCapabilityData;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

public class CapabilitySyncPacket {
    private final int used;
    private final int capacity;
    private final UUID playerUUID; // Ensure you target the correct player

    public CapabilitySyncPacket(int used, int capacity, UUID playerUUID) {
        this.used = used;
        this.capacity = capacity;
        this.playerUUID = playerUUID;
    }

    public static void encode(CapabilitySyncPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.used);
        buffer.writeInt(msg.capacity);
        buffer.writeUUID(msg.playerUUID);
    }

    public static CapabilitySyncPacket decode(FriendlyByteBuf buffer) {
        return new CapabilitySyncPacket(buffer.readInt(), buffer.readInt(), buffer.readUUID());
    }

    public static class Handler {
        public static void handle(final CapabilitySyncPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // Handle the packet, updating the client-side capability data
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null) {
                    Player player = mc.level.getPlayerByUUID(message.playerUUID);
                    if (player != null) {
                        // Assuming you have a method to update the client-side capability data
//                        SoulOverclockers.LOGGER.debug("Syncing capability to player...");
                        ClientCapabilityData.updateCapabilityData(message.used, message.capacity);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
