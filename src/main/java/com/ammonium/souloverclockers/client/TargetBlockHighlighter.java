package com.ammonium.souloverclockers.client;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid = SoulOverclockers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TargetBlockHighlighter {
    private static final Color HIGHLIGHT_COLOR = new Color(255, 0, 255, 255);

    @SubscribeEvent
    public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
        BlockPos blockPos = event.getTarget().getBlockPos();
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        // Check if the player is looking at the Overclocker block
        if (world.getBlockState(blockPos).getBlock() == SoulOverclockers.OVERCLOCKER_BLOCK.get()) {
            // Get the Overclocker block entity
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity == null) {
                SoulOverclockers.LOGGER.error("Overclocker block entity is null!");
                return;
            }
            if (blockEntity instanceof OverclockerEntity overclocker) {
                BlockPos targetBlockPos = overclocker.getTargetPos();
                if (targetBlockPos == null || world.getBlockState(targetBlockPos).isAir()) {
                    return;
                }
                // Render the block outline
//                SoulOverclockers.LOGGER.debug("Rendering block outline at {}", targetBlockPos);
                PoseStack poseStack = event.getPoseStack();
                Vec3 cameraPos = event.getCamera().getPosition();
                MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//                VertexConsumer buffer = bufferSource.getBuffer(RenderType.LINES);
                VertexConsumer buffer = bufferSource.getBuffer(CustomRenderType.LINES);
                RenderHelper.renderHighLightedBlockOutline(poseStack, buffer, targetBlockPos, cameraPos, HIGHLIGHT_COLOR);
                bufferSource.endBatch();
            } else {
                SoulOverclockers.LOGGER.error("Overclocker block entity is not an instance of OverclockerEntity!");
            }
        }


    }

}
