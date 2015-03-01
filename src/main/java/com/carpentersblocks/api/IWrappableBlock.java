package com.carpentersblocks.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Implement this on your block class to gain full control over the way it behaves
 * as used as a Carpenter's Blocks cover, or to add compatibility that is not
 * otherwise possible.
 */
public interface IWrappableBlock {

    /**
     * Effectively overrides Block.colorMultiplier
     */
    @SideOnly(Side.CLIENT)
    public int getColorMultiplier(IBlockAccess iba, BlockPos pos, IBlockState state);

    /**
     * Effectively overrides Block.getIcon
     */
    //@SideOnly(Side.CLIENT)
    //public IIcon getIcon(IBlockAccess iba, BlockPos pos, int side, IBlockState state);

    /**
     * Effectively overrides Block.isProvidingWeakPower
     */
    public int getWeakRedstone(World world, BlockPos pos, IBlockState state);

    /**
     * Effectively overrides Block.isProvidingStrongPower
     */
    public int getStrongRedstone(World world, BlockPos pos, IBlockState state);

    /**
     * Effectively overrides Block.getHardness
     */
    public float getHardness(World world, BlockPos pos, IBlockState state);

    /**
     * Effectively overrides Block.getExplosionResistance
     */
    public float getBlastResistance(Entity entity, World world, BlockPos pos, double explosionX, double explosionY, double explosionZ, IBlockState state);

    /**
     * Effectively overrides Block.getFlammability
     */
    public int getFlammability(IBlockAccess iba, BlockPos pos, EnumFacing side, IBlockState state);

    /**
     * Effectively overrides Block.getFireSpreadSpeed
     */
    public int getFireSpread(IBlockAccess iba, BlockPos pos, EnumFacing side, IBlockState state);

    /**
     * Effectively overrides Block.isFireSource
     */
    public boolean sustainsFire(IBlockAccess iba, BlockPos pos, EnumFacing side, IBlockState state);

    /**
     * Effectively overrides Block.isWood
     */
    public boolean isLog(IBlockAccess iba, BlockPos pos, IBlockState state);

    /**
     * Effectively overrides Block.canEntityDestroy
     */
    public boolean canEntityDestroy(IBlockAccess iba, BlockPos pos, Entity e, IBlockState state);

}
