package com.carpentersblocks.item;

import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.entity.item.EntityCarpentersTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.util.EnumFacing.*;

public class ItemCarpentersTile extends Item {

    public ItemCarpentersTile()
    {
        setCreativeTab(CarpentersBlocks.creativeTab);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerIcons(IIconRegister iconRegister)
//    {
//        itemIcon = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "tile");
//    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) {

            return true;

        } else {

            BlockPos offsetPos = pos.add(
                    side.getFrontOffsetX(),
                    side.getFrontOffsetY(),
                    side.getFrontOffsetZ());

            if (!entityPlayer.canPlayerEdit(offsetPos, side, itemStack)) {

                return false;

            } else {

                EnumFacing offset_side = getOffsetSide(side, hitX, hitY, hitZ);

                EntityCarpentersTile entity = new EntityCarpentersTile(entityPlayer, world, offsetPos, side, offset_side, entityPlayer.isSneaking());

                if (entity != null && entity.onValidSurface()) {
                    world.spawnEntityInWorld(entity);
                    entity.playTileSound();
                    --itemStack.stackSize;
                }

                return true;
            }
        }
    }

    /**
     * Returns offset side relative to where on a block a player clicks.
     */
    private EnumFacing getOffsetSide(EnumFacing side, float hitX, float hitY, float hitZ)
    {
        EnumFacing offset_side = NORTH;

        float ratio = 0.20F;
        float invert_ratio = 1.0F - ratio;

        boolean center_clicked = hitX > ratio && hitX < invert_ratio && hitZ > ratio && hitZ < invert_ratio ||
                                 hitX > ratio && hitX < invert_ratio && hitY > ratio && hitY < invert_ratio ||
                                 hitZ > ratio && hitZ < invert_ratio && hitY > ratio && hitY < invert_ratio;

        if (!center_clicked) {

            switch (side) {
                case DOWN:
                case UP:

                    if (hitZ < 1 - hitX && hitZ < hitX) {
                        offset_side = NORTH;
                    } else if (hitZ > 1 - hitX && hitZ > hitX) {
                        offset_side = SOUTH;
                    } else if (hitX < 1 - hitZ && hitX < hitZ) {
                        offset_side = WEST;
                    } else {
                        offset_side = EAST;
                    }

                    break;
                case NORTH:
                case SOUTH:

                    if (hitX < 1 - hitY && hitX < hitY) {
                        offset_side = WEST;
                    } else if (hitX > 1 - hitY && hitX > hitY) {
                        offset_side = EAST;
                    } else if (hitY < 1 - hitX && hitY < hitX) {
                        offset_side = DOWN;
                    } else {
                        offset_side = UP;
                    }

                    break;
                case WEST:
                case EAST:

                    if (hitZ < 1 - hitY && hitZ < hitY) {
                        offset_side = NORTH;
                    } else if (hitZ > 1 - hitY && hitZ > hitY) {
                        offset_side = SOUTH;
                    } else if (hitY < 1 - hitZ && hitY < hitZ) {
                        offset_side = DOWN;
                    } else {
                        offset_side = UP;
                    }

                    break;
                default:

                    break;
            }

        }

        return offset_side;
    }

}
