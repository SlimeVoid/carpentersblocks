package com.carpentersblocks.util.handler;

import com.carpentersblocks.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class OptifineHandler {

    public static boolean enableOptifineIntegration = false;
    private static Method getColorMultiplier;

    /**
     * Initializes Optifine integration.
     * If reflection fails, will return false.
     */
    public static void init()
    {
        try {
            Class<?> CustomColorizer = Class.forName("CustomColorizer");
            getColorMultiplier = CustomColorizer.getMethod("getColorMultiplier", Block.class, IBlockAccess.class, int.class, int.class, int.class);
            ModLogger.log(Level.INFO, "Optifine integration successful.");
            enableOptifineIntegration = true;
        } catch (Exception e) {
            ModLogger.log(Level.WARN, "Optifine integration failed: " + e.getMessage());
        }
    }

    public static int getColorMultiplier(Block block, IBlockAccess blockAccess, BlockPos pos)
    {
        int colorMultiplier = block.colorMultiplier(blockAccess, pos);
        try {
            colorMultiplier = (Integer) getColorMultiplier.invoke(null, block, blockAccess, pos);
        } catch (Exception e) {
            ModLogger.log(Level.WARN, "Block custom coloring failed, disabling Optifine integration: " + e.getMessage());
            enableOptifineIntegration = false;
        }

        return colorMultiplier;
    }

}
