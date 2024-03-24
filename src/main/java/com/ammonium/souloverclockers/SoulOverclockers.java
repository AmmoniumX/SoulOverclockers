package com.ammonium.souloverclockers;

import com.ammonium.souloverclockers.block.OverclockerBlock;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import com.ammonium.souloverclockers.item.LoreBlockItem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SoulOverclockers.MODID)
public class SoulOverclockers {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "souloverclockers";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Items deferred register
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Blocks deferred register
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // BlockEntities deferred register
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab("souloverclockers") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(OVERCLOCKER_BLOCK.get());
        }
    };
    public static final Material machineBlock = new Material(MaterialColor.METAL, false, true, true, true, false, false, PushReaction.BLOCK);
    private static <T extends Block> RegistryObject<T> registerLoreBlock(String name, Supplier<T> block, String lore) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerLoreBlockItem(name, toReturn, SoulOverclockers.CREATIVE_TAB, lore);
        return toReturn;
    }
    private static <T extends Block> RegistryObject<Item> registerLoreBlockItem(String name, RegistryObject<T> block,
                                                                                CreativeModeTab tab, String lore) {
        return ITEMS.register(name, () -> new LoreBlockItem(block.get(),
                new Item.Properties().tab(tab), lore));
    }

    // Create soul overclocker block entity
    public static final RegistryObject<Block> OVERCLOCKER_BLOCK = registerLoreBlock("overclocker",
            OverclockerBlock::new, "Accelerates machines placed directly above it! Requires");
    public static final RegistryObject<BlockEntityType<OverclockerEntity>> OVERCLOCKER_ENTITY =
            BLOCK_ENTITIES.register("overclocker", () -> BlockEntityType.Builder.of(OverclockerEntity::new,
                    OVERCLOCKER_BLOCK.get()).build(null));
    public SoulOverclockers()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register block entities
        BLOCK_ENTITIES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
