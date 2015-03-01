package com.carpentersblocks.block;

import com.carpentersblocks.data.ISided;
import com.carpentersblocks.tileentity.TEBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSided extends BlockCoverable {

    private ISided data = null;

    public BlockSided(Material material, ISided data)
    {
        super(material);
        this.data = data;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    {
        setBlockBoundsBasedOnState(world, pos);
        return super.getCollisionBoundingBox(world, pos, state);
    }

    @Override
    /**
     * Checks to see if you can place this block can be placed on that side of a block: BlockLever overrides
     */
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side)
    {
        if (canAttachToSide(side)) {
            BlockPos offsetPos = pos.add(-side.getFrontOffsetX(), -side.getFrontOffsetY(), -side.getFrontOffsetZ();
            return world.getBlockState(offsetPos).getBlock().isSideSolid(world, offsetPos, side);
        } else {
            return false;
        }
    }

    @Override
    /**
     * Called when a block is placed using its ItemBlock. Args: World, pos, side, hitX, hitY, hitZ, block metadata
     */
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase entity)
    {
        return this.getStateFromMeta(meta);
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
            int meta = state.getBlock().getMetaFromState(state);
            data.setDirection(TE, EnumFacing.getFront(meta));
        }
    }

    /**
     * Called after a block is placed
     */
    @Override
    public void onPostBlockPlaced(World world, BlockPos pos, int metadata)
    {
        /*
         * Part of world.setBlock() involves updating neighbors.  Since we
         * prevent this in ItemBlockSided, we'll invoke it here.
         */

        world.notifyNeighborsOfStateChange(pos, this);
    }

    @Override
    /**
     * How many world ticks before ticking
     */
    public int tickRate(World world)
    {
        return 20;
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: pos, neighbor blockID
     */
    public void onNeighborBlockChange(World world, BlockPos pos, Block block)
    {
        super.onNeighborBlockChange(world, pos, block);

        if (!world.isRemote) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null && !canPlaceBlockOnSide(world, pos, data.getDirection(TE).ordinal())) {
                destroyBlock(world, pos, true);
            }
        }
    }

    /**
     * Notifies relevant blocks of a change in power output.
     *
     * @param  world
     * @param  x
     * @param  y
     * @param  z
     * @return nothing
     */
    public void notifyBlocksOfPowerChange(World world, BlockPos pos)
    {
        /* Notify strong power change. */

        world.notifyBlockChange(pos, this);

        /* Notify weak power change. */

        if (canProvidePower()) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null) {
                ForgeDirection dir = data.getDirection(TE);
                world.notifyBlocksOfNeighborChange(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, this);
            } else {

                /* When block is destroyed, notify neighbors in all directions. */

                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    world.notifyBlocksOfNeighborChange(x - dir.offsetX, y - dir.offsetY, z - dir.offsetZ, this);
                }

            }
        }
    }

    @Override
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        int power = super.isProvidingWeakPower(blockAccess, pos, side);

        if (canProvidePower()) {
            TEBase TE = getTileEntity(blockAccess, pos);
            if (TE != null) {
                int tempPower = getPowerOutput(TE);
                if (tempPower > power) {
                    power = tempPower;
                }
            }
        }

        return power;
    }

    @Override
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, pos,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        int power = super.isProvidingStrongPower(blockAccess, pos, side);

        if (canProvidePower()) {
            TEBase TE = getTileEntity(blockAccess, pos);
            if (TE != null) {
                if (side == data.getDirection(TE).ordinal()) {
                    int tempPower = getPowerOutput(TE);
                    if (tempPower > power) {
                        power = tempPower;
                    }
                }
            }
        }

        return power;
    }

    @Override
    /**
     * Ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    public void breakBlock(World world, BlockPos pos, Block block, int metadata)
    {
        if (canProvidePower()) {
            notifyBlocksOfPowerChange(world, pos);
        }

        super.breakBlock(world, pos, block, metadata);
    }

    /**
     * Gets block-specific power level from 0 to 15.
     *
     * @param  TE  the {@link TEBase}
     * @return the power output
     */
    public int getPowerOutput(TEBase TE)
    {
        return 0;
    }

    /**
     * Whether block can be attached to specified side of another block.
     *
     * @param  side the side
     * @return whether side is supported
     */
    public boolean canAttachToSide(EnumFacing side)
    {
        return true;
    }

}
