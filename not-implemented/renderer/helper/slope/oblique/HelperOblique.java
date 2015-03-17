package com.carpentersblocks.renderer.helper.slope.oblique;

import com.carpentersblocks.data.Slope;
import com.carpentersblocks.renderer.helper.RenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.EnumFacing;

@SideOnly(Side.CLIENT)
public class HelperOblique extends RenderHelper {

    /**
     * Renders the given texture to interior oblique on the bottom sloped face.
     */
    public static void renderIntObliqueYNeg(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        double uMI = uTR - (uTR - uTL) / 2;
        double vMI = rotation % 2 == 0 ? vTL : (vBR - (vBR - vBL) / 2);

        switch (slopeID) {
            case Slope.ID_OBL_INT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBR, vBR, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uMI, vMI, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBL, vBL, NORTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMax, uMI, vMI, SOUTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uBL, vBL, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBR, vBR, SOUTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMin, uBR, vBR, NORTHWEST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uMI, vMI, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMax, uBL, vBL, SOUTHEAST);
                break;
            case Slope.ID_OBL_INT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uBL, vBL, SOUTHWEST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uBR, vBR, NORTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMax, uMI, vMI, SOUTHEAST);
                break;
        }
    }

    /**
     * Renders the given texture to interior oblique on the top sloped face.
     */
    public static void renderIntObliqueYPos(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        boolean altRot = rotation % 2 == 0;
        double uMI = !altRot ? uBL : (uTR - (uTR - uTL) / 2);
        double vMI = altRot ? vBR : (vBR - (vBR - vBL) / 2);

        switch (slopeID) {
            case Slope.ID_OBL_INT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMin, uTL, vTL, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMin, zMin, uMI, vMI, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTR, vTR, SOUTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_SW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTR, vTR, SOUTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTL, vTL, NORTHWEST);
                setupVertex(renderBlocks, xMin, yMin, zMax, uMI, vMI, SOUTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_NE:
                setupVertex(renderBlocks, xMax, yMax, zMax, uTL, vTL, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMin, zMin, uMI, vMI, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMin, uTR, vTR, NORTHWEST);
                break;
            case Slope.ID_OBL_INT_POS_SE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uMI, vMI, SOUTHEAST);
                setupVertex(renderBlocks, xMax, yMax, zMin, uTR, vTR, NORTHEAST);
                setupVertex(renderBlocks, xMin, yMax, zMax, uTL, vTL, SOUTHWEST);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique bottom face on right.
     */
    public static void renderExtObliqueYNegLeft(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        double uTOP_MIDDLE = uTR;
        double uTOP_RIGHT_MIDDLE = uTOP_MIDDLE - (uTR - uTL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_NEG_NW:
                setupVertex(renderBlocks, xMid, yMax, zMid,       uTOP_MIDDLE, vTR, TOP_CENTER );
                setupVertex(renderBlocks, xMax, yMax, zMin, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT   );
                setupVertex(renderBlocks, xMax, yMin, zMax,       uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                break;
            case Slope.ID_OBL_EXT_NEG_SW:
                setupVertex(renderBlocks, xMin, yMax, zMin, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT   );
                setupVertex(renderBlocks, xMax, yMin, zMin,       uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid,       uTOP_MIDDLE, vTR, TOP_CENTER );
                break;
            case Slope.ID_OBL_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax,       uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid,       uTOP_MIDDLE, vTR, TOP_CENTER );
                setupVertex(renderBlocks, xMax, yMax, zMax, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT   );
                break;
            case Slope.ID_OBL_EXT_NEG_SE:
                setupVertex(renderBlocks, xMin, yMax, zMax, uTOP_RIGHT_MIDDLE, vTL, TOP_LEFT   );
                setupVertex(renderBlocks, xMin, yMin, zMin,       uTOP_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMid, yMax, zMid,       uTOP_MIDDLE, vTR, TOP_CENTER );
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique bottom face on right.
     */
    public static void renderExtObliqueYNegRight(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        double uTOP_MIDDLE = uTR - (uTR - uTL) / 2;
        double uTOP_LEFT_MIDDLE = uTOP_MIDDLE - (uTR - uTL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_NEG_NW:
                setupVertex(renderBlocks, xMin, yMax, zMax,      uTOP_MIDDLE, vTR, TOP_RIGHT  );
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER );
                setupVertex(renderBlocks, xMax, yMin, zMax, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                break;
            case Slope.ID_OBL_EXT_NEG_SW:
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER );
                setupVertex(renderBlocks, xMax, yMin, zMin, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMax,      uTOP_MIDDLE, vTR, TOP_RIGHT  );
                break;
            case Slope.ID_OBL_EXT_NEG_NE:
                setupVertex(renderBlocks, xMin, yMin, zMax, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMin, yMax, zMin,      uTOP_MIDDLE, vTR, TOP_RIGHT  );
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER );
                break;
            case Slope.ID_OBL_EXT_NEG_SE:
                setupVertex(renderBlocks, xMid, yMax, zMid, uTOP_LEFT_MIDDLE, vTL, TOP_CENTER );
                setupVertex(renderBlocks, xMin, yMin, zMin, uTOP_LEFT_MIDDLE, vBL, BOTTOM_LEFT);
                setupVertex(renderBlocks, xMax, yMax, zMin,      uTOP_MIDDLE, vTR, TOP_RIGHT  );
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique top face on left.
     */
    public static void renderExtObliqueYPosLeft(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        double uMI1 = uBR; // u middle coordinate, left triangle
        double uMI2 = uMI1 - (uBR - uBL) / 2; // u middle coordinate, right triangle

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uMI1, vTL, TOP_LEFT     );
                setupVertex(renderBlocks, xMax, yMin, zMin, uMI2, vBL, BOTTOM_LEFT  );
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                break;
            case Slope.ID_OBL_EXT_POS_SW:
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMax, yMax, zMin, uMI1, vTL, TOP_LEFT     );
                setupVertex(renderBlocks, xMin, yMin, zMin, uMI2, vBL, BOTTOM_LEFT  );
                break;
            case Slope.ID_OBL_EXT_POS_NE:
                setupVertex(renderBlocks, xMax, yMin, zMax, uMI2, vBL, BOTTOM_LEFT  );
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMax, zMax, uMI1, vTL, TOP_LEFT     );
                break;
            case Slope.ID_OBL_EXT_POS_SE:
                setupVertex(renderBlocks, xMin, yMax, zMin, uMI1, vTL, TOP_LEFT     );
                setupVertex(renderBlocks, xMin, yMin, zMax, uMI2, vBL, BOTTOM_LEFT  );
                setupVertex(renderBlocks, xMid, yMin, zMid, uMI1, vBR, BOTTOM_CENTER);
                break;
        }
    }

    /**
     * Renders the given texture to exterior oblique top face on right.
     */
    public static void renderExtObliqueYPosRight(RenderBlocks renderBlocks, int slopeID, double x, double y, double z, IIcon icon)
    {
        prepareRender(renderBlocks, EnumFacing.NORTH, x, y, z, icon);

        double uBOTTOM_MIDDLE = uBR - (uBR - uBL) / 2;
        double uBOTTOM_LEFT_MIDDLE = uBOTTOM_MIDDLE - (uBR - uBL) / 2;

        double xMid = xMax - (xMax - xMin) / 2;
        double zMid = zMax - (zMax - zMin) / 2;

        switch (slopeID) {
            case Slope.ID_OBL_EXT_POS_NW:
                setupVertex(renderBlocks, xMax, yMax, zMax, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT     );
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMin, zMax,      uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT );
                break;
            case Slope.ID_OBL_EXT_POS_SW:
                setupVertex(renderBlocks, xMax, yMin, zMax,      uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT );
                setupVertex(renderBlocks, xMax, yMax, zMin, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT     );
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                break;
            case Slope.ID_OBL_EXT_POS_NE:
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMin, yMin, zMin,      uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT );
                setupVertex(renderBlocks, xMin, yMax, zMax, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT     );
                break;
            case Slope.ID_OBL_EXT_POS_SE:
                setupVertex(renderBlocks, xMid, yMin, zMid, uBOTTOM_LEFT_MIDDLE, vBL, BOTTOM_CENTER);
                setupVertex(renderBlocks, xMax, yMin, zMin,      uBOTTOM_MIDDLE, vBR, BOTTOM_RIGHT );
                setupVertex(renderBlocks, xMin, yMax, zMin, uBOTTOM_LEFT_MIDDLE, vTL, TOP_LEFT     );
                break;
        }
    }

}
