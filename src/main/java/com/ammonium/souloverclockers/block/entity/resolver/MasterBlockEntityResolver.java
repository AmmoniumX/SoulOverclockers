package com.ammonium.souloverclockers.block.entity.resolver;

import com.ammonium.souloverclockers.SoulOverclockers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class MasterBlockEntityResolver {

    private static List<MasterBlockEntityResolver> resolvers = null;

    public static void registerResolvers() {
        if (resolvers == null) {
            resolvers = new ArrayList<>();

            registerCompat("immersiveengineering", "com.ammonium.souloverclockers.block.entity.resolver.IEMasterBlockEntityResolver");
            // Add more compat in future.
        }
    }

    private static void registerCompat(String modId, String compatClassName) {
        try {
            if (ModList.get().isLoaded(modId)) {
                resolvers.add((MasterBlockEntityResolver) Class.forName(compatClassName).getConstructor().newInstance());
                SoulOverclockers.LOGGER.info("Compact Loaded for: {}", modId);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            SoulOverclockers.LOGGER.info("Compact Failed to load for: {}", modId);
        }
    }

    public static BlockEntity resolveMasterBlockEntity(BlockEntity be) {
        if (resolvers == null) {
            throw new IllegalStateException("Resolvers have not been registered.");
        }

        for (MasterBlockEntityResolver resolver : resolvers) {
            BlockEntity result = resolver.getMasterBlockEntity(be);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public abstract BlockEntity getMasterBlockEntity(BlockEntity be);
}
