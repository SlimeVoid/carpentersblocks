package com.carpentersblocks.proxy;

import com.carpentersblocks.CarpentersBlocksCachedResources;
import com.carpentersblocks.entity.item.EntityCarpentersTile;
import com.carpentersblocks.renderer.entity.RenderCarpentersTile;
import com.carpentersblocks.util.handler.OptifineHandler;
import com.carpentersblocks.util.handler.ShadersHandler;
import com.carpentersblocks.util.registry.IconRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new IconRegistry());
        CarpentersBlocksCachedResources.INSTANCE.init();

        if (FMLClientHandler.instance().hasOptifine()) {
            OptifineHandler.init();
        }

        ShadersHandler.init();

        /* Register entity renderers */

        RenderingRegistry.registerEntityRenderingHandler(EntityCarpentersTile.class, new RenderCarpentersTile(FMLClientHandler.instance().getClient().getRenderManager()));
    }

}
