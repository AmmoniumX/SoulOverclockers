package com.ammonium.souloverclockers.compat.jade;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.ammonium.souloverclockers.block.OverclockerBlock;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    public static final ResourceLocation OVERCLOCKER_COMPONENT = new ResourceLocation(SoulOverclockers.MODID, "overclocker_component");

    @Override
    public void register(IWailaCommonRegistration registration) {
        // register data providers here
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(OverclockerJadeProvider.INSTANCE, OverclockerBlock.class);
    }
}