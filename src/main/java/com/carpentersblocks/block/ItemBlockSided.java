package com.carpentersblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlockSided extends ItemBlock {

    public ItemBlockSided(Block block)
    {
        super(block);
    }

    @Override
    /**
     * Called to actually place the block, after the location is determined
     * and all permission checks have been made.
     *
     * @param stack The item stack that was used to place the block. This can be changed inside the method.
     * @param player The player who is placing the block. Can be null if the block is not being placed by a player.
     * @param side The side the player (or machine) right-clicked on.
     */
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state)
    {
        /*
         * We need to use a flag that doesn't call for a block update ([flag & 1] updates).
         *
         * If the world updates, there's a chance the block will pop off the wall before
         * the server is able to set the block direction.  This happens when the world
         * notifies neighbors of the block update, and they in turn update this block.
         *
         * Instead, we'll notify neighbors when it's safe to do so -- in BlockSided using
         * onPostBlockPlaced().
         */

        if (!world.setBlockState(pos, state, 0)) {
            return false;
        }

        if (world.getBlockState(pos).getBlock() == this.getBlock()) {
            block.onBlockPlacedBy(world, pos, state, player, stack);
            //block.onPostBlockPlaced(world, x, y, z, metadata);
        }

        return true;
    }

}
