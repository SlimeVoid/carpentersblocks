package com.carpentersblocks.tileentity;

import com.carpentersblocks.block.BlockCarpentersDaylightSensor;

public class TECarpentersDaylightSensor extends TEBase {

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
    public void update()
    {
        if (worldObj != null && !worldObj.isRemote && worldObj.getTotalWorldTime() % 20L == 0L)
        {
            if (getBlockType() != null) {
                if (blockType instanceof BlockCarpentersDaylightSensor) {
                    ((BlockCarpentersDaylightSensor) blockType).updateLightLevel(this.getWorld(), this.getPos());
                }
            }
        }
    }

}
