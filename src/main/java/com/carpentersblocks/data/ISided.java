package com.carpentersblocks.data;

import com.carpentersblocks.tileentity.TEBase;
import net.minecraft.util.EnumFacing;

public interface ISided {

    /**
     * Sets block direction.
     *
     * @param  TE the {@link TEBase}
     * @param  side the side
     * @return nothing
     */
    public void setDirection(TEBase TE, EnumFacing dir);

    /**
     * Gets block direction.
     *
     * @param  TE the {@link TEBase}
     * @return the {@link EnumFacing}
     */
    public EnumFacing getDirection(TEBase TE);

}
