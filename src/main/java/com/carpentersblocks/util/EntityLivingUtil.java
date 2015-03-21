package com.carpentersblocks.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.EnumFacing;

public class EntityLivingUtil {

    /**
     * Decrements the {@link EntityPlayer}'s currently held {@link ItemStack}.
     *
     * @param entityPlayer the {@link EntityPlayer}
     */
    public static void decrementCurrentSlot(EntityPlayer entityPlayer)
    {
        ItemStack itemStack = entityPlayer.getCurrentEquippedItem();

        if (itemStack != null) {
            if (!entityPlayer.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
            }
        }
    }

    /**
     * Gets {@link EntityLivingBase} rotation as an arbitrary direction value from 0 to 3.
     * <p>
     * This defaults to facing opposite of the entity.
     *
     * @param entityLiving the {@link EntityLivingBase}
     * @return the rotation value
     */
    public static int getRotationValue(EntityLivingBase entity)
    {
        return MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
    }

    /**
     * Gets the {@link EnumFacing} for a player rotation value returned
     * by {@link #getRotationValue(EntityLivingBase)}, which by default will
     * be the opposite facing of the {@link Entity}.
     *
     * @param  rot the rotation value
     * @return the {@link EnumFacing}
     */
    public static EnumFacing getRotationFacing(int rot)
    {
        return EnumFacing.getFront(rot);
    }

    /**
     * Gets player facing as {@link EnumFacing}.
     *
     * @param  entityPlayer the {@link EntityLivingBase}
     * @return the {@link EnumFacing}
     */
    public static EnumFacing getFacing(EntityLivingBase entity)
    {
        int rot = getRotationValue(entity);
        return getRotationFacing(rot).getOpposite();
    }

}
