package com.carpentersblocks.util.stairs;

import com.carpentersblocks.block.BlockCarpentersStairs;
import com.carpentersblocks.data.Stairs;
import com.carpentersblocks.data.Stairs.Type;
import com.carpentersblocks.tileentity.TEBase;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;

public class StairsTransform {

    /**
     * Transforms stairs to connect with adjacent stairs.
     */
    public static int transformStairs(TEBase TE, int stairsID, BlockPos pos)
    {
        World world = TE.getWorldObj();

        Stairs stairs_XN = world.getBlock(x - 1, y, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x - 1, y, z)).getData()] : null;
        Stairs stairs_XP = world.getBlock(x + 1, y, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x + 1, y, z)).getData()] : null;
        Stairs stairs_YN = world.getBlock(x, y - 1, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y - 1, z)).getData()] : null;
        Stairs stairs_YP = world.getBlock(x, y + 1, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y + 1, z)).getData()] : null;
        Stairs stairs_ZN = world.getBlock(x, y, z - 1) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y, z - 1)).getData()] : null;
        Stairs stairs_ZP = world.getBlock(x, y, z + 1) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y, z + 1)).getData()] : null;

        /* Transform into normal side. */

        if (stairs_YN != null) {
            if (stairs_YN.stairsType.equals(Type.NORMAL_SIDE)) {
                return stairs_YN.stairsID;
            }
        }
        if (stairs_YP != null) {
            if (stairs_YP.stairsType.equals(Type.NORMAL_SIDE)) {
                return stairs_YP.stairsID;
            }
        }

        /* Transform into normal corner. */

        Stairs stairs = Stairs.stairsList[stairsID];

        if (stairs_ZN != null) {

            if (stairs_XN != null) {
                if (stairs_ZN.facings.contains(EnumFacing.WEST) && stairs_XN.facings.contains(EnumFacing.NORTH)) {
                    return stairs_XN.isPositive && stairs_ZN.isPositive ? Stairs.ID_NORMAL_INT_POS_NW : Stairs.ID_NORMAL_INT_NEG_NW;
                }
                if (stairs_ZN.facings.contains(EnumFacing.EAST) && stairs_XN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_XN.isPositive && stairs_ZN.isPositive ? Stairs.ID_NORMAL_EXT_POS_SE : Stairs.ID_NORMAL_EXT_NEG_SE;
                }
            }

            if (stairs_XP != null) {
                if (stairs_ZN.facings.contains(EnumFacing.EAST) && stairs_XP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_XP.isPositive && stairs_ZN.isPositive ? Stairs.ID_NORMAL_INT_POS_NE : Stairs.ID_NORMAL_INT_NEG_NE;
                }
                if (stairs_ZN.facings.contains(EnumFacing.WEST) && stairs_XP.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_XP.isPositive && stairs_ZN.isPositive ? Stairs.ID_NORMAL_EXT_POS_SW : Stairs.ID_NORMAL_EXT_NEG_SW;
                }
            }

        }

        if (stairs_ZP != null) {

            if (stairs_XN != null) {
                if (stairs_ZP.facings.contains(EnumFacing.WEST) && stairs_XN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_XN.isPositive && stairs_ZP.isPositive ? Stairs.ID_NORMAL_INT_POS_SW : Stairs.ID_NORMAL_INT_NEG_SW;
                }
                if (stairs_ZP.facings.contains(EnumFacing.EAST) && stairs_XN.facings.contains(EnumFacing.NORTH)) {
                    return stairs_XN.isPositive && stairs_ZP.isPositive ? Stairs.ID_NORMAL_EXT_POS_NE : Stairs.ID_NORMAL_EXT_NEG_NE;
                }
            }

            if (stairs_XP != null) {
                if (stairs_ZP.facings.contains(EnumFacing.EAST) && stairs_XP.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_XP.isPositive && stairs_ZP.isPositive ? Stairs.ID_NORMAL_INT_POS_SE : Stairs.ID_NORMAL_INT_NEG_SE;
                }
                if (stairs_ZP.facings.contains(EnumFacing.WEST) && stairs_XP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_XP.isPositive && stairs_ZP.isPositive ? Stairs.ID_NORMAL_EXT_POS_NW : Stairs.ID_NORMAL_EXT_NEG_NW;
                }
            }

        }

        if (stairs_XN != null) {

            if (stairs.facings.contains(EnumFacing.WEST)) {
                if (stairs_XN.facings.contains(EnumFacing.SOUTH) && !stairs_XN.facings.contains(EnumFacing.EAST)) {
                    return stairs_XN.isPositive ? Stairs.ID_NORMAL_INT_POS_SW : Stairs.ID_NORMAL_INT_NEG_SW;
                }
                if (stairs_XN.facings.contains(EnumFacing.NORTH) && !stairs_XN.facings.contains(EnumFacing.EAST)) {
                    return stairs_XN.isPositive ? Stairs.ID_NORMAL_INT_POS_NW : Stairs.ID_NORMAL_INT_NEG_NW;
                }
            }

            if (stairs.facings.contains(EnumFacing.EAST)) {
                if (stairs_XN.facings.contains(EnumFacing.SOUTH) && !stairs_XN.facings.contains(EnumFacing.EAST)) {
                    return stairs_XN.isPositive ? Stairs.ID_NORMAL_EXT_POS_SE : Stairs.ID_NORMAL_EXT_NEG_SE;
                }
                if (stairs_XN.facings.contains(EnumFacing.NORTH) && !stairs_XN.facings.contains(EnumFacing.EAST)) {
                    return stairs_XN.isPositive ? Stairs.ID_NORMAL_EXT_POS_NE : Stairs.ID_NORMAL_EXT_NEG_NE;
                }
            }

        }

        if (stairs_XP != null) {

            if (stairs.facings.contains(EnumFacing.WEST)) {
                if (stairs_XP.facings.contains(EnumFacing.SOUTH) && !stairs_XP.facings.contains(EnumFacing.WEST)) {
                    return stairs_XP.isPositive ? Stairs.ID_NORMAL_EXT_POS_SW : Stairs.ID_NORMAL_EXT_NEG_SW;
                }
                if (stairs_XP.facings.contains(EnumFacing.NORTH) && !stairs_XP.facings.contains(EnumFacing.WEST)) {
                    return stairs_XP.isPositive ? Stairs.ID_NORMAL_EXT_POS_NW : Stairs.ID_NORMAL_EXT_NEG_NW;
                }
            }

            if (stairs.facings.contains(EnumFacing.EAST)) {
                if (stairs_XP.facings.contains(EnumFacing.SOUTH) && !stairs_XP.facings.contains(EnumFacing.WEST)) {
                    return stairs_XP.isPositive ? Stairs.ID_NORMAL_INT_POS_SE : Stairs.ID_NORMAL_INT_NEG_SE;
                }
                if (stairs_XP.facings.contains(EnumFacing.NORTH) && !stairs_XP.facings.contains(EnumFacing.WEST)) {
                    return stairs_XP.isPositive ? Stairs.ID_NORMAL_INT_POS_NE : Stairs.ID_NORMAL_INT_NEG_NE;
                }
            }

        }

        if (stairs_ZN != null) {

            if (stairs.facings.contains(EnumFacing.NORTH)) {
                if (stairs_ZN.facings.contains(EnumFacing.EAST) && !stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_ZN.isPositive ? Stairs.ID_NORMAL_INT_POS_NE : Stairs.ID_NORMAL_INT_NEG_NE;
                }
                if (stairs_ZN.facings.contains(EnumFacing.WEST) && !stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_ZN.isPositive ? Stairs.ID_NORMAL_INT_POS_NW : Stairs.ID_NORMAL_INT_NEG_NW;
                }
            }

            if (stairs.facings.contains(EnumFacing.SOUTH)) {
                if (stairs_ZN.facings.contains(EnumFacing.EAST) && !stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_ZN.isPositive ? Stairs.ID_NORMAL_EXT_POS_SE : Stairs.ID_NORMAL_EXT_NEG_SE;
                }
                if (stairs_ZN.facings.contains(EnumFacing.WEST) && !stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    return stairs_ZN.isPositive ? Stairs.ID_NORMAL_EXT_POS_SW : Stairs.ID_NORMAL_EXT_NEG_SW;
                }
            }

        }

        if (stairs_ZP != null) {

            if (stairs.facings.contains(EnumFacing.NORTH)) {
                if (stairs_ZP.facings.contains(EnumFacing.EAST) && !stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_ZP.isPositive ? Stairs.ID_NORMAL_EXT_POS_NE : Stairs.ID_NORMAL_EXT_NEG_NE;
                }
                if (stairs_ZP.facings.contains(EnumFacing.WEST) && !stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_ZP.isPositive ? Stairs.ID_NORMAL_EXT_POS_NW : Stairs.ID_NORMAL_EXT_NEG_NW;
                }
            }

            if (stairs.facings.contains(EnumFacing.SOUTH)) {
                if (stairs_ZP.facings.contains(EnumFacing.EAST) && !stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_ZP.isPositive ? Stairs.ID_NORMAL_INT_POS_SE : Stairs.ID_NORMAL_INT_NEG_SE;
                }
                if (stairs_ZP.facings.contains(EnumFacing.WEST) && !stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    return stairs_ZP.isPositive ? Stairs.ID_NORMAL_INT_POS_SW : Stairs.ID_NORMAL_INT_NEG_SW;
                }
            }

        }

        return stairsID;
    }

    /**
     * Transforms adjacent stairs to connect to source stairs.
     */
    public static void transformAdjacentStairs(World world, int stairsID, BlockPos pos)
    {
        Stairs stairs_XN = world.getBlock(x - 1, y, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x - 1, y, z)).getData()] : null;
        Stairs stairs_XP = world.getBlock(x + 1, y, z) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x + 1, y, z)).getData()] : null;
        Stairs stairs_ZN = world.getBlock(x, y, z - 1) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y, z - 1)).getData()] : null;
        Stairs stairs_ZP = world.getBlock(x, y, z + 1) instanceof BlockCarpentersStairs ? Stairs.stairsList[((TEBase) world.getTileEntity(x, y, z + 1)).getData()] : null;

        Stairs stairs = Stairs.stairsList[stairsID];

        TEBase TE_XN = stairs_XN != null ? (TEBase) world.getTileEntity(x - 1, y, z) : null;
        TEBase TE_XP = stairs_XP != null ? (TEBase) world.getTileEntity(x + 1, y, z) : null;
        TEBase TE_ZN = stairs_ZN != null ? (TEBase) world.getTileEntity(x, y, z - 1) : null;
        TEBase TE_ZP = stairs_ZP != null ? (TEBase) world.getTileEntity(x, y, z + 1) : null;

        if (stairs.facings.contains(EnumFacing.WEST)) {

            if (stairs_ZN != null && stairs.isPositive == stairs_ZN.isPositive) {
                if (stairs_ZN.facings.contains(EnumFacing.NORTH)) {
                    TE_ZN.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_NW : Stairs.ID_NORMAL_EXT_NEG_NW);
                }
                if (stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    TE_ZN.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_SW : Stairs.ID_NORMAL_INT_NEG_SW);
                }
            }

            if (stairs_ZP != null && stairs.isPositive == stairs_ZP.isPositive) {
                if (stairs_ZP.facings.contains(EnumFacing.SOUTH)) {
                    TE_ZP.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_SW : Stairs.ID_NORMAL_EXT_NEG_SW);
                }
                if (stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    TE_ZP.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_NW : Stairs.ID_NORMAL_INT_NEG_NW);
                }
            }

        }

        if (stairs.facings.contains(EnumFacing.EAST)) {

            if (stairs_ZN != null && stairs.isPositive == stairs_ZN.isPositive) {
                if (stairs_ZN.facings.contains(EnumFacing.NORTH)) {
                    TE_ZN.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_NE : Stairs.ID_NORMAL_EXT_NEG_NE);
                }
                if (stairs_ZN.facings.contains(EnumFacing.SOUTH)) {
                    TE_ZN.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_SE : Stairs.ID_NORMAL_INT_NEG_SE);
                }
            }

            if (stairs_ZP != null && stairs.isPositive == stairs_ZP.isPositive) {
                if (stairs_ZP.facings.contains(EnumFacing.SOUTH)) {
                    TE_ZP.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_SE : Stairs.ID_NORMAL_EXT_NEG_SE);
                }
                if (stairs_ZP.facings.contains(EnumFacing.NORTH)) {
                    TE_ZP.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_NE : Stairs.ID_NORMAL_INT_NEG_NE);
                }
            }

        }

        if (stairs.facings.contains(EnumFacing.NORTH)) {

            if (stairs_XN != null && stairs.isPositive == stairs_XN.isPositive) {
                if (stairs_XN.facings.contains(EnumFacing.WEST)) {
                    TE_XN.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_NW : Stairs.ID_NORMAL_EXT_NEG_NW);
                }
                if (stairs_XN.facings.contains(EnumFacing.EAST)) {
                    TE_XN.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_NE : Stairs.ID_NORMAL_INT_NEG_NE);
                }
            }

            if (stairs_XP != null && stairs.isPositive == stairs_XP.isPositive) {
                if (stairs_XP.facings.contains(EnumFacing.EAST)) {
                    TE_XP.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_NE : Stairs.ID_NORMAL_EXT_NEG_NE);
                }
                if (stairs_XP.facings.contains(EnumFacing.WEST)) {
                    TE_XP.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_NW : Stairs.ID_NORMAL_INT_NEG_NW);
                }
            }

        }

        if (stairs.facings.contains(EnumFacing.SOUTH)) {

            if (stairs_XN != null && stairs.isPositive == stairs_XN.isPositive) {
                if (stairs_XN.facings.contains(EnumFacing.WEST)) {
                    TE_XN.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_SW : Stairs.ID_NORMAL_EXT_NEG_SW);
                }
                if (stairs_XN.facings.contains(EnumFacing.EAST)) {
                    TE_XN.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_SE : Stairs.ID_NORMAL_INT_NEG_SE);
                }
            }

            if (stairs_XP != null && stairs.isPositive == stairs_XP.isPositive) {
                if (stairs_XP.facings.contains(EnumFacing.EAST)) {
                    TE_XP.setData(stairs.isPositive ? Stairs.ID_NORMAL_EXT_POS_SE : Stairs.ID_NORMAL_EXT_NEG_SE);
                }
                if (stairs_XP.facings.contains(EnumFacing.WEST)) {
                    TE_XP.setData(stairs.isPositive ? Stairs.ID_NORMAL_INT_POS_SW : Stairs.ID_NORMAL_INT_NEG_SW);
                }
            }

        }
    }

}
