package com.ammonium.souloverclockers.item;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import com.ammonium.souloverclockers.soulpower.SoulPower;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class Attuner extends LoreItem {
    public Attuner() {
        super(new Item.Properties().tab(SoulOverclockers.CREATIVE_TAB).stacksTo(1), "Hold to view Soul Power. Right click overclockers to speed up. Shift right click to slow down");
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        BlockPos pos = pContext.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Player player = pContext.getPlayer();
        assert player != null;
        SoulPower soulPower = player.getCapability(SoulPowerProvider.SOUL_POWER).orElse(null);
        if (soulPower == null) return InteractionResult.FAIL;

        if (blockEntity != null && !(blockEntity instanceof OverclockerEntity)) return InteractionResult.FAIL;

        OverclockerEntity overclocker = (OverclockerEntity) blockEntity;
        if (overclocker == null) return InteractionResult.FAIL;

        // Check if player is the overclocker's owner
        if (!player.getUUID().equals(overclocker.getOwnerUUID())) {
            SoulOverclockers.LOGGER.info("You are not this overclocker's owner.");
            player.sendSystemMessage(Component.literal("You are not this overclocker's owner."));
            player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1.0f, 0.5f);
            return InteractionResult.FAIL;
        }

        boolean shifting = player.isShiftKeyDown();
        int multiplier;

        if (shifting) {
            multiplier = overclocker.decreaseMultiplier();
        } else {
            multiplier = overclocker.increaseMultiplier();
        }
        String message = (shifting ? "Decreased" : "Increased") + " overclocker multiplier to " + multiplier +". "+
                ChatFormatting.DARK_PURPLE + I18n.get("gui.sp_message") + " " +
                soulPower.getUsed() + "/" + soulPower.getCapacity() + I18n.get("gui.sp_symbol");
        SoulOverclockers.LOGGER.info(message);
        player.sendSystemMessage(Component.literal(message));
        player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1.0f, (shifting ? 0.5f : 1.0f));
        return InteractionResult.SUCCESS;
    }
}
