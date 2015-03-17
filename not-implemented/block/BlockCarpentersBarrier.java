package com.carpentersblocks.block;

import com.carpentersblocks.data.Barrier;
import com.carpentersblocks.data.Gate;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockCarpentersBarrier extends BlockCoverable {

    public BlockCarpentersBarrier(Material material)
    {
        super(material);
    }

    @Override
    /**
     * Toggles post.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        Barrier.setPost(TE, Barrier.getPost(TE) == Barrier.HAS_POST ? Barrier.NO_POST : Barrier.HAS_POST);

        return true;
    }

    @Override
    /**
     * Alters barrier type or sub-type.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        int type = Barrier.getType(TE);

        if (entityPlayer.isSneaking()) {

            /*
             * Cycle through sub-types
             */
            if (type <= Barrier.TYPE_VANILLA_X3) {
                if (++type > Barrier.TYPE_VANILLA_X3) {
                    type = Barrier.TYPE_VANILLA;
                }
            }

        } else {

            /*
             * Cycle through barrier types
             */
            if (type <= Barrier.TYPE_VANILLA_X3) {
                type = Barrier.TYPE_PICKET;
            } else if (++type > Barrier.TYPE_WALL) {
                type = Barrier.TYPE_VANILLA;
            }

        }

        Barrier.setType(TE, type);

        return true;
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

            /* Match block type with adjacent type if possible. */

            TEBase[] TE_list = getAdjacentTileEntities(world, pos);

            for (TEBase TE_current : TE_list) {

                if (TE_current != null) {

                    Block block = TE_current.getBlockType();

                    if (block.equals(this)) {
                        Barrier.setType(TE, Barrier.getType(TE_current));
                    } else if (block.equals(BlockRegistry.blockCarpentersGate)) {
                        Barrier.setType(TE, Gate.getType(TE_current));
                    }

                }

            }

        }
    }

    @Override
    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, pos, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB axisAlignedBB, List list, Entity entity)
    {
        boolean connect_ZN = canConnectBarrierTo(world, pos.add( 0, 0,-1), EnumFacing.SOUTH);
        boolean connect_ZP = canConnectBarrierTo(world, pos.add( 0, 0, 1), EnumFacing.NORTH);
        boolean connect_XN = canConnectBarrierTo(world, pos.add(-1, 0, 0), EnumFacing.EAST);
        boolean connect_XP = canConnectBarrierTo(world, pos.add( 1, 0, 0), EnumFacing.WEST);

        float x_Low = 0.375F;
        float x_High = 0.625F;
        float z_Low = 0.375F;
        float z_High = 0.625F;

        if (connect_ZN) {
            z_Low = 0.0F;
        }

        if (connect_ZP) {
            z_High = 1.0F;
        }

        if (connect_ZN || connect_ZP)
        {
            setBlockBounds(x_Low, 0.0F, z_Low, x_High, 1.5F, z_High);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        z_Low = 0.375F;
        z_High = 0.625F;

        if (connect_XN) {
            x_Low = 0.0F;
        }

        if (connect_XP) {
            x_High = 1.0F;
        }

        if (connect_XN || connect_XP || !connect_ZN && !connect_ZP)
        {
            setBlockBounds(x_Low, 0.0F, z_Low, x_High, 1.5F, z_High);
            super.addCollisionBoxesToList(world, pos, state, axisAlignedBB, list, entity);
        }

        if (connect_ZN) {
            z_Low = 0.0F;
        }

        if (connect_ZP) {
            z_High = 1.0F;
        }

        setBlockBounds(x_Low, 0.0F, z_Low, x_High, 1.0F, z_High);
    }

    @Override
    /**
     * Updates the blocks bounds based on its current state. Args: world, pos
     */
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);
        int type = Barrier.getType(TE);

        boolean connect_ZN = canConnectBarrierTo(blockAccess, pos.add( 0, 0,-1), EnumFacing.SOUTH);
        boolean connect_ZP = canConnectBarrierTo(blockAccess, pos.add( 0, 0, 1), EnumFacing.NORTH);
        boolean connect_XN = canConnectBarrierTo(blockAccess, pos.add(-1, 0, 0), EnumFacing.EAST);
        boolean connect_XP = canConnectBarrierTo(blockAccess, pos.add( 1, 0, 0), EnumFacing.WEST);

        float x_Low = 0.0F;
        float x_High = 1.0F;
        float z_Low = 0.0F;
        float z_High = 1.0F;

        if (type <= Barrier.TYPE_VANILLA_X3) {

            x_Low = 0.375F;
            x_High = 0.625F;
            z_Low = 0.375F;
            z_High = 0.625F;

            if (connect_ZN) {
                z_Low = 0.0F;
            }

            if (connect_ZP) {
                z_High = 1.0F;
            }

            if (connect_XN) {
                x_Low = 0.0F;
            }

            if (connect_XP) {
                x_High = 1.0F;
            }

        } else {

            x_Low = 0.25F;
            x_High = 0.75F;
            z_Low = 0.25F;
            z_High = 0.75F;

            if (connect_ZN) {
                z_Low = 0.0F;
            }

            if (connect_ZP) {
                z_High = 1.0F;
            }

            if (connect_XN) {
                x_Low = 0.0F;
            }

            if (connect_XP) {
                x_High = 1.0F;
            }

            if (connect_ZN && connect_ZP && !connect_XN && !connect_XP) {
                x_Low = 0.3125F;
                x_High = 0.6875F;
            } else if (!connect_ZN && !connect_ZP && connect_XN && connect_XP) {
                z_Low = 0.3125F;
                z_High = 0.6875F;
            }

        }

        setBlockBounds(x_Low, 0.0F, z_Low, x_High, 1.0F, z_High);
    }

    /**
     * Returns true if block can connect to specified side of neighbor block.
     */
    public boolean canConnectBarrierTo(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        IBlockState state = blockAccess.getBlockState(pos);

        if (state.getBlock().equals(this) || state.getBlock().equals(BlockRegistry.blockCarpentersGate)) {
            return true;
        } else {
            return state.getBlock().isSideSolid(blockAccess, pos, side);
        }
    }

    /**
     * Checks if the block is a solid face on the given side, used by placement logic.
     *
     * @param blockAccess The current world
     * @param pos the BlockPos
     * @param side The side to check
     * @return True if the block is solid on the specified side.
     */
    @Override
    public boolean isSideSolid(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return side.equals(EnumFacing.UP);
    }

    @Override
    /**
     * Determines if a torch can be placed on the top surface of this block.
     */
    public boolean canPlaceTorchOnTop(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: world, pos, side
     */
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BlockRegistry.carpentersBarrierRenderID;
    }

}
