package com.carpentersblocks.block;

import com.carpentersblocks.data.Slab;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.tileentity.TECB;
import com.carpentersblocks.util.handler.EventHandler;
import com.carpentersblocks.util.registry.BlockRegistry;
import com.carpentersblocks.util.registry.IconRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockCarpentersBlock extends BlockCoverable {

    private static float[][] bounds = {
        { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F }, // FULL BLOCK
        { 0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F }, // SLAB WEST
        { 0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F }, // SLAB EAST
        { 0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F }, // SLAB DOWN
        { 0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F }, // SLAB UP
        { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F }, // SLAB NORTH
        { 0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F }  // SLAB SOUTH
    };

    public BlockCarpentersBlock(Material material)
    {
        super(material);
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    /**
//     * Returns a base icon that doesn't rely on blockIcon, which
//     * is set prior to texture stitch events.
//     */
//    public IIcon getIcon()
//    {
//        return IconRegistry.icon_uncovered_quartered;
//    }

    @Override
    /**
     * Alter type.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        int data = TE.getData();

        if (++data > Slab.SLAB_Z_POS) {
            data = Slab.BLOCK_FULL;
        }

        TE.setData(data);

        return true;
    }

    @Override
    /**
     * Alternate between full 1m cube and slab.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        int data = TE.getData();

        if (data == Slab.BLOCK_FULL) {
            switch (EventHandler.eventFace.getIndex())
            {
                case 0:
                    data = Slab.SLAB_Y_POS;
                    break;
                case 1:
                    data = Slab.SLAB_Y_NEG;
                    break;
                case 2:
                    data = Slab.SLAB_Z_POS;
                    break;
                case 3:
                    data = Slab.SLAB_Z_NEG;
                    break;
                case 4:
                    data = Slab.SLAB_X_POS;
                    break;
                case 5:
                    data = Slab.SLAB_X_NEG;
                    break;
            }
        } else {
            data = Slab.BLOCK_FULL;
        }

        TE.setData(data);

        return true;
    }

    @Override
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            int data = TE.getData();
            if (data < bounds.length) {
                setBlockBounds(bounds[data][0], bounds[data][1], bounds[data][2], bounds[data][3], bounds[data][4], bounds[data][5]);
            }
        }
    }

    @Override
    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisAlignedBB, List list, Entity entity)
    {
        setBlockBoundsBasedOnState(world, pos);
        super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
    }

    @Override
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, pos, state, entityLiving, itemStack);

        TEBase TE = getTileEntity(world, pos);

        if (TE != null) {

            int data = Slab.BLOCK_FULL;

            if (!entityLiving.isSneaking()) {

                /* Match block type with adjacent type if possible. */

                TEBase[] TE_list = getAdjacentTileEntities(world, pos);

                for (TEBase TE_current : TE_list) {
                    if (TE_current != null) {
                        if (TE_current.getBlockType().equals(this)) {
                            data = TE_current.getData();
                        }
                    }
                }

            }

            TE.setData(data);
        }
    }

    @Override
    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     */
    public boolean isSideSolid(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {

            if (isBlockSolid(blockAccess, pos, side)) {

                int data = TE.getData();

                if (data == Slab.BLOCK_FULL) {
                    return true;
                } else if (data == Slab.SLAB_Y_NEG && side == EnumFacing.DOWN) {
                    return true;
                } else if (data == Slab.SLAB_Y_POS && side == EnumFacing.UP) {
                    return true;
                } else if (data == Slab.SLAB_Z_NEG && side == EnumFacing.NORTH) {
                    return true;
                } else if (data == Slab.SLAB_Z_POS && side == EnumFacing.SOUTH) {
                    return true;
                } else if (data == Slab.SLAB_X_NEG && side == EnumFacing.WEST) {
                    return true;
                } else if (data == Slab.SLAB_X_POS && side == EnumFacing.EAST) {
                    return true;
                }

            }

        }

        return false;
    }

    /**
     * Called to determine whether to allow the a block to handle its own indirect power rather than using the default rules.
     * @param world The world
     * @param x The x position of this block instance
     * @param y The y position of this block instance
     * @param z The z position of this block instance
     * @param side The INPUT side of the block to be powered - ie the opposite of this block's output side
     * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
     */
    @Override
    public boolean shouldCheckWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            int data = TE.getData();
            return data == Slab.BLOCK_FULL;
        }

        return super.shouldCheckWeakPower(blockAccess, pos, side);
    }

    @Override
    /**
     * Compares dimensions and coordinates of two opposite
     * sides to determine whether they share faces.
     */
    protected boolean shareFaces(TEBase TE_adj, TEBase TE_src, EnumFacing side_adj, EnumFacing side_src)
    {
        if (TE_adj.getBlockType() == this) {

            setBlockBoundsBasedOnState(TE_src.getWorld(), TE_src.getPos());
            double[] bnds_src = { getBlockBoundsMinX(), getBlockBoundsMinY(), getBlockBoundsMinZ(), getBlockBoundsMaxX(), getBlockBoundsMaxY(), getBlockBoundsMaxZ() };
            setBlockBoundsBasedOnState(TE_adj.getWorld(), TE_adj.getPos());

            switch (side_src) {
                case DOWN:
                    return maxY == 1.0D && bnds_src[1] == 0.0D && minX == bnds_src[0] && maxX == bnds_src[3] && minZ == bnds_src[2] && maxZ == bnds_src[5];
                case UP:
                    return minY == 0.0D && bnds_src[4] == 1.0D && minX == bnds_src[0] && maxX == bnds_src[3] && minZ == bnds_src[2] && maxZ == bnds_src[5];
                case NORTH:
                    return maxZ == 1.0D && bnds_src[2] == 0.0D && minX == bnds_src[0] && maxX == bnds_src[3] && minY == bnds_src[1] && maxY == bnds_src[4];
                case SOUTH:
                    return minZ == 0.0D && bnds_src[5] == 1.0D && minX == bnds_src[0] && maxX == bnds_src[3] && minY == bnds_src[1] && maxY == bnds_src[4];
                case WEST:
                    return maxX == 1.0D && bnds_src[0] == 0.0D && minY == bnds_src[1] && maxY == bnds_src[4] && minZ == bnds_src[2] && maxZ == bnds_src[5];
                case EAST:
                    return minX == 0.0D && bnds_src[3] == 1.0D && minY == bnds_src[1] && maxY == bnds_src[4] && minZ == bnds_src[2] && maxZ == bnds_src[5];
                default:
                    return false;
            }

        }

        return super.shareFaces(TE_adj, TE_src, side_adj, side_src);
    }

    @Override
    public TileEntity createNewCarpentersTile(World world, int metadata) {
        return new TECB();
    }

    @Override
    /**
     * Returns whether block can support cover on side.
     */
    public boolean canCoverSide(TEBase TE, World world, BlockPos pos, int side)
    {
        return true;
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BlockRegistry.carpentersBlockRenderID;
    }

}
