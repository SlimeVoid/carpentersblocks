package com.carpentersblocks.renderer.helper;

import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.ModLogger;
import com.carpentersblocks.util.registry.FeatureRegistry;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import static net.minecraft.util.EnumFacing.*;

@SideOnly(Side.CLIENT)
public class FancyFluidsHelper {

    public final static Class[] liquidClasses = { BlockLiquid.class, IFluidBlock.class};
    private final static int CALLER_SUN = 0;
    private final static int CALLER_SEC = 1;
    private static int callMethod = -1;

    public static Class getCallerClass()
    {
        if (callMethod < 0)
        {
            try {
                sun.reflect.Reflection.getCallerClass(2);
                callMethod = CALLER_SUN;
            } catch (Exception E) {
                try {
                    new SecurityManager() { Class clazz = getClassContext()[2]; };
                    callMethod = CALLER_SEC;
                } catch (Exception E1) {
                    FeatureRegistry.enableRoutableFluids = false;
                    ModLogger.log(Level.WARN, "Routable fluids failed: %s", E1.getMessage());
                };
            };
        }

        switch (callMethod)
        {
            case CALLER_SUN:
                return sun.reflect.Reflection.getCallerClass(4);
            case CALLER_SEC:
                return new SecurityManager() { Class clazz = getClassContext()[4]; }.clazz;
            default:
                return null;
        }
    }

    /**
     * Renders fancy fluid.
     *
     * @param  TE the {@link TEBase}
     * @param  renderBlocks the {@link BlockRendererDispatcher}
     * @param  pos
     * @return true if fluid rendered in space
     */
    public static boolean render(TEBase TE, BlockRendererDispatcher renderBlocks, BlockPos pos)
    {
        ItemStack itemStack = getFluidBlock(FMLClientHandler.instance().getWorldClient(), pos);

        if (itemStack != null) {

            IBlockState state = BlockProperties.toBlockState(itemStack);
            //int metadata = itemStack.getItemDamage();

            if (state.getBlock().canRenderInLayer(MinecraftForgeClient.getRenderLayer()))
            {
                if (!state.getBlock().hasTileEntity(state))
                {
                    // TODO:: Lighting Helper
                    //LightingHelper lightingHelper = new LightingHelper(renderBlocks);
                    //lightingHelper.setupLightingYPos(itemStack, pos);
                    //lightingHelper.setupColor(pos, 1, 16777215, null);
                    double fluidHeight = (state.getBlock() instanceof BlockLiquid ? 1.0D - 1.0F / 9.0F : 0.875F) - 0.0010000000474974513D;
                    //renderBlocks.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, fluidHeight, 1.0D);
                    //RenderHelper.renderFaceYPos(renderBlocks, pos, state.getBlock().getIcon(1, metadata));
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Gets nearby, routable fluid block.
     *
     * @param  blockAccess the {@link IBlockAccess}
     * @param  pos
     * @return a nearby fluid {@link ItemStack}, or null if no routable fluid exists
     */
    public static ItemStack getFluidBlock(IBlockAccess blockAccess, BlockPos pos)
    {
        int[][] offsetXZ = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {-1, 1}, {1, 1}, {1, -1}};

        EnumFacing[][][] route = {
                { { NORTH } },
                { { SOUTH } },
                { { WEST  } },
                { { EAST  } },
                { { NORTH, SOUTH, WEST }, { WEST, EAST, NORTH } },
                { { SOUTH, NORTH, WEST }, { WEST, EAST, SOUTH } },
                { { SOUTH, NORTH, EAST }, { EAST, WEST, SOUTH } },
                { { NORTH, SOUTH, EAST }, { EAST, WEST, NORTH } },
        };

        for (int idx = 0; idx < offsetXZ.length; ++idx) {

            IBlockState state = blockAccess.getBlockState(pos.add(offsetXZ[idx][0], 0, offsetXZ[idx][1]));
            Class clazz = state.getBlock().getClass();

            boolean isLiquid = false;
            for (int idx1 = 0; idx1 < liquidClasses.length; ++idx1) {
                if (liquidClasses[idx1].isAssignableFrom(clazz)) {
                    isLiquid = true;
                    break;
                }
            }

            if (isLiquid) {
                if (idx < 4) {
                    if (!blockAccess.isSideSolid(pos, route[idx][0][0], false)) {
                        IBlockState stateSide = blockAccess.getBlockState(pos.add(offsetXZ[idx][0], 0, offsetXZ[idx][1]));
                        return new ItemStack(state.getBlock(), stateSide.getBlock().getMetaFromState(stateSide));
                    }
                } else {
                    for (int routeIdx = 0; routeIdx < 2; ++routeIdx) {
                        if (!blockAccess.isSideSolid(pos, route[idx][routeIdx][0], false)) {
                            int[] bridgeXZ = { pos.getX() + route[idx][routeIdx][0].getFrontOffsetX(), pos.getZ() + route[idx][routeIdx][0].getFrontOffsetZ() };
                            if (!blockAccess.isSideSolid(new BlockPos(bridgeXZ[0], pos.getY(), bridgeXZ[1]), route[idx][routeIdx][1], false) && !blockAccess.isSideSolid(new BlockPos(bridgeXZ[0], pos.getY(), bridgeXZ[1]), route[idx][routeIdx][2], false)) {
                                IBlockState stateSide = blockAccess.getBlockState(pos.add(offsetXZ[idx][0], 0, offsetXZ[idx][1]));
                                return new ItemStack(state.getBlock(), stateSide.getBlock().getMetaFromState(stateSide));
                            }
                        }
                    }
                }
            }

        }

        return null;
    }

}
