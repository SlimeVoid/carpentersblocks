package com.carpentersblocks.data;

import com.carpentersblocks.tileentity.TEBase;
import net.minecraftforge.common.util.ForgeDirection;

public class DaylightSensor implements ISided {

    /**
     * 16-bit data components:
     *
     * [000000] [000] [00]        [0]      [0000]
     * Unused   Dir   Sensitivity Polarity LightLevel
     */

    public static final byte POLARITY_POSITIVE = 0;
    public static final byte POLARITY_NEGATIVE = 1;

    public static final byte SENSITIVITY_SLEEP    = 0;
    public static final byte SENSITIVITY_MONSTERS = 1;
    public static final byte SENSITIVITY_DYNAMIC  = 2;

    /**
     * Returns direction.
     */
    @Override
    public ForgeDirection getDirection(TEBase TE)
    {
        int side = (TE.getData() & 0x380) >> 7;
        return ForgeDirection.getOrientation(side);
    }

    /**
     * Sets direction.
     */
    @Override
    public void setDirection(TEBase TE, ForgeDirection dir)
    {
        int temp = (TE.getData() & ~0x380) | (dir.ordinal() << 7);
        TE.setData(temp);
    }

    /**
     * Returns light level.
     */
    public int getLightLevel(TEBase TE)
    {
        return TE.getData() & 0xf;
    }

    /**
     * Sets light level.
     */
    public void setLightLevel(TEBase TE, int lightLevel)
    {
        int temp = (TE.getData() & ~0xf) | lightLevel;
        TE.setData(temp);
    }

    /**
     * Returns polarity.
     */
    public int getPolarity(TEBase TE)
    {
        return (TE.getData() & 0x10) >> 4;
    }

    /**
     * Sets polarity.
     */
    public void setPolarity(TEBase TE, int state)
    {
        int temp = (TE.getData() & ~0x10) | (state << 4);
        TE.setData(temp);
    }

    /**
     * Returns sensitivity.
     */
    public int getSensitivity(TEBase TE)
    {
        return (TE.getData() & 0x60) >> 5;
    }

    /**
     * Sets sensitivity.
     */
    private void setSensitivity(TEBase TE, int sensitivity)
    {
        int temp = (TE.getData() & ~0x60) | (sensitivity << 5);
        TE.setData(temp);
    }

    /**
     * Sets sensor to next sensitivity level.
     * Returns new sensitivity.
     */
    public int setNextSensitivity(TEBase TE)
    {
        int sensitivity = getSensitivity(TE);

        if (++sensitivity > 2) {
            sensitivity = 0;
        }

        setSensitivity(TE, sensitivity);

        return sensitivity;
    }

    /**
     * Returns the operational state of the daylight sensor.
     *
     * @param  TE the {@link TEBase}
     * @return true if sensor is outputting redstone current
     */
    public boolean isActive(TEBase TE)
    {
        return getRedstoneOutput(TE) > 0;
    }

    /**
     * Gets redstone output based on sensor polarity, sensitivity, and light level.
     *
     * @param  TE the {@link TEBase}
     * @return redstone output between 0 and 15
     */
    public int getRedstoneOutput(TEBase TE)
    {
        boolean posPolarity = getPolarity(TE) == POLARITY_POSITIVE;
        int output = 0;
        int lightLevel = getLightLevel(TE);
        int sensitivity = getSensitivity(TE);

        if (sensitivity == SENSITIVITY_SLEEP) {
            boolean active = posPolarity ? lightLevel > 11 : lightLevel <= 11;
            if (active) {
                output = 15;
            }
        } else if (sensitivity == SENSITIVITY_MONSTERS) {
            boolean active = posPolarity ? lightLevel > 7 : lightLevel <= 7;
            if (active) {
                output = 15;
            }
        } else {
            output = posPolarity ? lightLevel : 15 - lightLevel;
        }

        return output;
    }

}
