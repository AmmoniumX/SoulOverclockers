package com.ammonium.souloverclockers.gui;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.soulpower.ClientCapabilityData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AttunerGuiOverlay {

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        NamedGuiOverlay overlay = event.getOverlay();
        // Example: Check if we're rendering the hotbar overlay
        if (overlay.id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            ItemStack mainHand = mc.player.getMainHandItem();
            ItemStack offHand = mc.player.getOffhandItem();
            // Check if the main hand item is the item you are interested in
            if (mainHand.is(SoulOverclockers.ATTUNER.get()) || offHand.is(SoulOverclockers.ATTUNER.get())) { // Replace YourMod.YOUR_ITEM with your specific item
                displaySoulPower(event.getPoseStack()); // Replace "Your Text Here" with your desired text
            }
        }
    }

    private static void displaySoulPower(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        String str = Component.translatable("gui.sp_message", (ClientCapabilityData.getUsed()+"/"+ClientCapabilityData.getCap()))
                .withStyle(ChatFormatting.DARK_PURPLE).getString();
        int strWidth = mc.font.width(str);

        int x = (width - strWidth) / 2;
        int y = height - 70;

        RenderSystem.enableBlend();

        mc.font.draw(poseStack, str, x, y, 0xAA00AA);
    }
}
