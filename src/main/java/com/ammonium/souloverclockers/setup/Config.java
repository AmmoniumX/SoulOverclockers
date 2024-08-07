package com.ammonium.souloverclockers.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class Config {
    public static ForgeConfigSpec.IntValue MAX_MULTIPLIER;
    public static ForgeConfigSpec.IntValue FE_COST_MULTIPLIER;
    public static ForgeConfigSpec.IntValue OVERCLOCKER_FE_CAPACITY;
    public static ForgeConfigSpec.IntValue OVERCLOCKER_FE_TRANSFER;
    public static ForgeConfigSpec.IntValue BASE_SOUL_POWER;
    public static ForgeConfigSpec.BooleanValue REQUIRE_ONLINE;

    public static void register(){
        ForgeConfigSpec.Builder serverConfig = new ForgeConfigSpec.Builder();
        registerServerConfigs(serverConfig);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverConfig.build());
//        ForgeConfigSpec.Builder clientConfig = new ForgeConfigSpec.Builder();
//        registerClientConfigs(clientConfig);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientConfig.build());
    }

    private static void registerServerConfigs(ForgeConfigSpec.Builder config){
        config.comment("General configurations.")
                .push("server_config");
        MAX_MULTIPLIER = config
                .comment("Maximum multiplier allowed for overclockers. Big values may affect server performance!.")
                .defineInRange("max_multiplier", 64, 2, Integer.MAX_VALUE);
        FE_COST_MULTIPLIER = config
                .comment("Multiplier for FE overclocker cost. E.g, if set to 100, overclocking at 2x speed will cost 2*100=200 FE/tick. Set to 0 for no cost.")
                .defineInRange("fe_cost_multiplier", 100, 0, Integer.MAX_VALUE);
        OVERCLOCKER_FE_CAPACITY = config
                .comment("Internal FE capacity for overclocker. Should hold at least enough to run at maximum speed for 1 tick (MAX_MULTIPLIER*FE_COST_MULTIPLIER)")
                .defineInRange("overclocker_fe_capacity", 100000, 0, Integer.MAX_VALUE);
        OVERCLOCKER_FE_TRANSFER = config
                .comment("Max FE insertion speed for overclocker. Must be less than or equal to OVERCLOCKER_FE_CAPACITY")
                .defineInRange("overclocker_fe_transfer", 10000, 0, Integer.MAX_VALUE);
        BASE_SOUL_POWER = config
                .comment("Base Soul Power each player has when logging in, without any additional equipment. Big values may affect server performance!")
                .defineInRange("base_soul_power", 64, 2, Integer.MAX_VALUE);
        REQUIRE_ONLINE = config
                .comment("Whether the player has to be online or not for overclockers to work.")
                .define("require_online", true);
        config.pop();
    }

//    private static void registerClientConfigs(ForgeConfigSpec.Builder config){
//        config.comment("Client configurations.")
//                .push("display_config");
//        config.pop();
//    }

}
