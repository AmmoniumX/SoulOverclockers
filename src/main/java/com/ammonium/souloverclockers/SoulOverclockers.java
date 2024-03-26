package com.ammonium.souloverclockers;

import com.ammonium.souloverclockers.block.OverclockerBlock;
import com.ammonium.souloverclockers.block.entity.OverclockerEntity;
import com.ammonium.souloverclockers.item.*;
import com.ammonium.souloverclockers.network.CapabilitySyncPacket;
import com.ammonium.souloverclockers.recipe.NBTCraftingRecipe;
import com.ammonium.souloverclockers.setup.Config;
import com.ammonium.souloverclockers.setup.Messages;
import com.ammonium.souloverclockers.soulpower.SoulPower;
import com.ammonium.souloverclockers.soulpower.SoulPowerProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
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

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
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

    // Register objects

    public static final RegistryObject<Item> EYE = ITEMS.register("eye", Eye::new);

    private static RegistryObject<Item> registerSoulGear(String name, int power) {
        return ITEMS.register(name, () -> new SoulGear(power));
    }
    public static final RegistryObject<Item> RING = registerSoulGear("ring", 16);
    public static final RegistryObject<Item> RING_ADV = registerSoulGear("ring_adv", 32);
    public static final RegistryObject<Item> AMULET = registerSoulGear("amulet", 16);
    public static final RegistryObject<Item> AMULET_ADV = registerSoulGear("amulet_adv", 32);

    public static final RegistryObject<Item> ATTUNER = ITEMS.register("attuner", Attuner::new);
    public static final RegistryObject<Block> OVERCLOCKER_BLOCK = registerLoreBlock("overclocker",
            OverclockerBlock::new, "Accelerates machines placed directly above it! Requires Soul Power and Forge Energy");
    public static final RegistryObject<BlockEntityType<OverclockerEntity>> OVERCLOCKER_ENTITY =
            BLOCK_ENTITIES.register("overclocker", () -> BlockEntityType.Builder.of(OverclockerEntity::new,
                    OVERCLOCKER_BLOCK.get()).build(null));

    public static final RegistryObject<RecipeSerializer<?>> NBT_CRAFTING_RECIPE = RECIPE_SERIALIZERS.register("crafting_shaped_nbt", NBTCraftingRecipe.Serializer::new);

    public SoulOverclockers()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        Config.register();
        // Register deferred registers
        BLOCK_ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
        Messages.register();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents
    {
        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
            SoulOverclockers.LOGGER.debug("(PlayerLoggedInEvent) Refreshing soul power");
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            refreshSoulPower(player);
        }

        @SubscribeEvent
        public static void onCurioChange(CurioChangeEvent event) {
            SoulOverclockers.LOGGER.debug("(CurioChangeEvent) Refreshing soul power");
            if (!(event.getEntity() instanceof ServerPlayer player)) return;
            refreshSoulPower(player);
        }

        public static void refreshSoulPower(ServerPlayer player) {
            AtomicInteger capacity = new AtomicInteger(Config.BASE_SOUL_POWER.get());
            ICuriosHelper helper = CuriosApi.getCuriosHelper();

            helper.getEquippedCurios(player).ifPresent(handler -> {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stackInSlot = handler.getStackInSlot(i);
                    CompoundTag tag = stackInSlot.getTag();

                    if (tag != null && tag.contains("SoulPower")) {
                        int power = tag.getInt("SoulPower");
                        capacity.addAndGet(power);
                    }
                }
            });

            player.getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(soulPower -> {
                int newCap = capacity.get();
                SoulOverclockers.LOGGER.debug("Setting refreshed soul power to "+newCap);
                soulPower.setCapacity(newCap);
                Messages.sendToPlayer(new CapabilitySyncPacket(soulPower.getUsed(), newCap, player.getUUID()), player);
            });
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                if (!event.getObject().getCapability(SoulPowerProvider.SOUL_POWER).isPresent()) {
                    event.addCapability(new ResourceLocation(SoulOverclockers.MODID, "properties"), new SoulPowerProvider());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(oldStore -> {
                    event.getOriginal().getCapability(SoulPowerProvider.SOUL_POWER).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(SoulPower.class);
        }

    }
}
