package com.carpentersblocks.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

@SideOnly(Side.CLIENT)
public class BlockHandlerCarpentersButton extends BlockHandlerBase {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderBlocks)
    {
        renderBlocks.setRenderBounds(0.3125F, 0.375F, 0.375F, 0.6875F, 0.625F, 0.625F);
        super.renderInventoryBlock(block, metadata, modelID, renderBlocks);
    }

    @Override
    /**
     * Renders block
     */
    protected void renderCarpentersBlock(BlockPos pos)
    {
        renderBlocks.renderAllFaces = true;
        super.renderCarpentersBlock(x, y, z);
        renderBlocks.renderAllFaces = false;
    }

}
