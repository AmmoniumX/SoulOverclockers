package com.ammonium.souloverclockers.tag;

import com.ammonium.souloverclockers.SoulOverclockers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    public static final TagKey<Block> CANT_ACCELERATE = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation("industrialforegoingsouls", "cant_accelerate"));
    public static final TagKey<Block> CANT_OVERCLOCK = TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(SoulOverclockers.MODID, "cant_overclock"));
}
