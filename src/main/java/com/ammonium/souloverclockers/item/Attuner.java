package com.ammonium.souloverclockers.item;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class Attuner extends LoreItem {
    public Attuner() {
        super(new Item.Properties().tab(SoulOverclockers.CREATIVE_TAB), "Right click overclockers to speed them up. Shift right click to slow them down");
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

        if (blockEntity != null && !(blockEntity instanceof OverclockerEntity)) return InteractionResult.FAIL;

        OverclockerEntity overclocker = (OverclockerEntity) blockEntity;
        assert overclocker != null;
        boolean shifting = player.isShiftKeyDown();
        int multiplier;

        if (shifting) {
            multiplier = overclocker.decreaseMultiplier();
        } else {
            multiplier = overclocker.increaseMultiplier();
        }
        String message = (shifting ? "Decreased" : "Increased") + " overclocker multiplier to " + multiplier;
        SoulOverclockers.LOGGER.info(message);
        player.sendSystemMessage(Component.literal(message));
        player.playNotifySound(SoundEvents.LEVER_CLICK, SoundSource.PLAYERS, 1.0f, (shifting ? 0.5f : 1.0f));
        return InteractionResult.PASS;
    }
}
