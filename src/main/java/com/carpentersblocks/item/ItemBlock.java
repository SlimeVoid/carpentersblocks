package com.carpentersblocks.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemBlock extends Item {

    /**
     * Places the block in the world and calls post-placement methods.
     */
    protected boolean placeBlock(World world, IBlockState state, EntityPlayer entityPlayer, ItemStack itemStack, BlockPos pos)
    {
        if (world.setBlockState(pos, state, 4)) {

            state.getBlock().onBlockPlacedBy(world, pos, state, entityPlayer, itemStack);
            //block.onPostBlockPlaced(world, x, y, z, 0);

            return true;

        } else {

            return false;

        }
    }

}
