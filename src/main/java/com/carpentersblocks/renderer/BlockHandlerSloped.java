package com.carpentersblocks.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import static com.carpentersblocks.renderer.helper.VertexHelper.*;

@SideOnly(Side.CLIENT)
public abstract class BlockHandlerSloped extends BlockHandlerBase {

    /** Returns whether side is sloped face. */
    public static boolean isSideSloped;

    /** Identifies which render helper to use. */
    protected int renderID = 0;

    /** Holds AO values for all six faces. */
    protected float[][] ao = new float[6][4];

    /** Holds brightness values for all six faces. */
    protected int[][] brightness = new int[6][4];

    /** Holds offset AO values for all six faces. */
    protected float[][] offset_ao = new float[6][4];

    /** Holds offset brightness values for all six faces. */
    protected int[][] offset_brightness = new int[6][4];

    /**
     * Sets renderID to identify which RenderHelper to use
     * and passes control to delegateSideRender().
     */
    protected final void setIDAndRender(ItemStack itemStack, int renderID, int x, int y, int z, int side)
    {
        this.renderID = renderID;
        delegateSideRender(itemStack, x, y, z, side);
    }

    /**
     * Prepares lighting for block and offset faces.
     */
    protected void prepareLighting(ItemStack itemStack)
    {
        for (int side = 0; side < 6; ++side) {
            populateLighting(itemStack, side);
            populateOffsetLighting(itemStack, side);
        }
    }

    /**
     * Fills ambient occlusion and brightness tables.
     */
    private void populateLighting(ItemStack itemStack, int side)
    {
        switch (side)
        {
            case DOWN:
                lightingHelper.setupLightingYNeg(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
            case UP:
                lightingHelper.setupLightingYPos(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
            case NORTH:
                lightingHelper.setupLightingZNeg(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
            case SOUTH:
                lightingHelper.setupLightingZPos(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
            case WEST:
                lightingHelper.setupLightingXNeg(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
            case EAST:
                lightingHelper.setupLightingXPos(itemStack, TE.xCoord, TE.yCoord, TE.zCoord);
                break;
        }

        if (renderBlocks.enableAO)
        {
            ao[side][0] = lightingHelper.ao[TOP_LEFT];
            ao[side][1] = lightingHelper.ao[BOTTOM_LEFT];
            ao[side][2] = lightingHelper.ao[BOTTOM_RIGHT];
            ao[side][3] = lightingHelper.ao[TOP_RIGHT];
            brightness[side][0] = renderBlocks.brightnessTopLeft;
            brightness[side][1] = renderBlocks.brightnessBottomLeft;
            brightness[side][2] = renderBlocks.brightnessBottomRight;
            brightness[side][3] = renderBlocks.brightnessTopRight;
        }
    }

    /**
     * Fills ambient occlusion and brightness tables for offset block coordinates.
     */
    private void populateOffsetLighting(ItemStack itemStack, int side)
    {
        double renderTemp;

        switch (side)
        {
            case DOWN:
                renderTemp = renderBlocks.renderMinY;
                renderBlocks.renderMinY = 0.0D;
                lightingHelper.setupLightingYNeg(itemStack, TE.xCoord, TE.yCoord + 1, TE.zCoord);
                renderBlocks.renderMinY = renderTemp;
                break;
            case UP:
                renderTemp = renderBlocks.renderMaxY;
                renderBlocks.renderMaxY = 1.0D;
                lightingHelper.setupLightingYPos(itemStack, TE.xCoord, TE.yCoord - 1, TE.zCoord);
                renderBlocks.renderMaxY = renderTemp;
                break;
            case NORTH:
                renderTemp = renderBlocks.renderMinZ;
                renderBlocks.renderMinZ = 0.0D;
                lightingHelper.setupLightingZNeg(itemStack, TE.xCoord, TE.yCoord, TE.zCoord + 1);
                renderBlocks.renderMinZ = renderTemp;
                break;
            case SOUTH:
                renderTemp = renderBlocks.renderMaxZ;
                renderBlocks.renderMaxZ = 1.0D;
                lightingHelper.setupLightingZPos(itemStack, TE.xCoord, TE.yCoord, TE.zCoord - 1);
                renderBlocks.renderMaxZ = renderTemp;
                break;
            case WEST:
                renderTemp = renderBlocks.renderMinX;
                renderBlocks.renderMinX = 0.0D;
                lightingHelper.setupLightingXNeg(itemStack, TE.xCoord + 1, TE.yCoord, TE.zCoord);
                renderBlocks.renderMinX = renderTemp;
                break;
            case EAST:
                renderTemp = renderBlocks.renderMaxX;
                renderBlocks.renderMaxX = 1.0D;
                lightingHelper.setupLightingXPos(itemStack, TE.xCoord - 1, TE.yCoord, TE.zCoord);
                renderBlocks.renderMaxX = renderTemp;
                break;
        }

        if (renderBlocks.enableAO)
        {
            offset_ao[side][0] = lightingHelper.ao[TOP_LEFT];
            offset_ao[side][1] = lightingHelper.ao[BOTTOM_LEFT];
            offset_ao[side][2] = lightingHelper.ao[BOTTOM_RIGHT];
            offset_ao[side][3] = lightingHelper.ao[TOP_RIGHT];
            offset_brightness[side][0] = renderBlocks.brightnessTopLeft;
            offset_brightness[side][1] = renderBlocks.brightnessBottomLeft;
            offset_brightness[side][2] = renderBlocks.brightnessBottomRight;
            offset_brightness[side][3] = renderBlocks.brightnessTopRight;
        }
    }

    /**
     * A renderSide method that happens after checking if side cover is rendering.
     */
    abstract void renderBaseSide(int x, int y, int z, int side, IIcon icon);

    @Override
    /**
     * Renders side.
     */
    protected final void render(int x, int y, int z, int side, IIcon icon)
    {
        if (coverRendering != 6) {
            super.render(x, y, z, side, icon);
        } else {
            renderBaseSide(x, y, z, side, icon);
        }
    }

    /**
     * Renders the base cover block.
     */
    abstract void renderBaseBlock(ItemStack itemStack, int x, int y, int z);

    @Override
    /**
     * Renders the block.
     */
    protected final void renderBlock(ItemStack itemStack, int x, int y, int z)
    {
        if (coverRendering != 6) {
            super.renderBlock(itemStack, x, y, z);
        } else {
            renderBaseBlock(itemStack, x, y, z);
        }
    }

}
