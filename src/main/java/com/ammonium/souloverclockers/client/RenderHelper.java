package com.ammonium.souloverclockers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class RenderHelper {

    // Code modified from github:McJtyMods/McJtyLib
    public static void renderHighLightedBlockOutline(PoseStack poseStack, VertexConsumer buffer, BlockPos blockPos, Vec3 cameraPos, Color color) {

        float mx = (float) (blockPos.getX() - cameraPos.x);
        float my = (float) (blockPos.getY() - cameraPos.y);
        float mz = (float) (blockPos.getZ() - cameraPos.z);

        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float a = color.getAlpha() / 255.0f;

        Matrix4f matrix = poseStack.last().pose();

        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my + 1, mz).color(r, g, b, a).endVertex();

        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx + 1, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my, mz + 1).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, mx, my + 1, mz + 1).color(r, g, b, a).endVertex();
    }
}
