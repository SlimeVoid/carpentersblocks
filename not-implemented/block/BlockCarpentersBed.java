package com.carpentersblocks.block;

import com.carpentersblocks.data.Bed;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.handler.ChatHandler;
import com.carpentersblocks.util.registry.BlockRegistry;
import com.carpentersblocks.util.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.Random;

public class BlockCarpentersBed extends BlockCoverable {

    public BlockCarpentersBed(Material material)
    {
        super(material);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    /**
//     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
//     * is the only chance you get to register icons.
//     */
//    public void registerBlockIcons(IIconRegister iconRegister)
//    {
//        IconRegistry.icon_bed_pillow = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "bed/bed_pillow");
//    }

    @Override
    /**
     * Determines if this block is classified as a Bed, Allowing
     * players to sleep in it, though the block has to specifically
     * perform the sleeping functionality in it's activated event.
     */
    public boolean isBed(IBlockAccess blockAccess, BlockPos pos, Entity entity)
    {
        return true;
    }

    @Override
    /**
     * Cycle backward through bed designs.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        TE.setPrevDesign();
        Bed.getOppositeTE(TE).setDesign(TE.getDesign());
        return true;
    }

    @Override
    /**
     * Cycle forward through bed designs.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        if (entityPlayer.isSneaking()) {
            TE.removeDesign();
            Bed.getOppositeTE(TE).removeDesign();
        } else {
            TE.setNextDesign();
            Bed.getOppositeTE(TE).setDesign(TE.getDesign());
        }

        return true;
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ, ActionResult actionResult)
    {
        actionResult.setAltered();
        World world = TE.getWorld();

        int x = TE.getPos().getX();
        int y = TE.getPos().getY();
        int z = TE.getPos().getZ();

        if (!Bed.isHeadOfBed(TE)) {

            TEBase TE_opp = Bed.getOppositeTE(TE);

            if (TE_opp != null) {
                x = TE_opp.getPos().getX();
                z = TE_opp.getPos().getZ();
            } else {
                return;
            }

        }
        BlockPos bedPartPos = new BlockPos(x, y, z);

        if (world.provider.canRespawnHere() && world.getBiomeGenForCoords(bedPartPos) != BiomeGenBase.hell) {

            if (Bed.isOccupied(TE)) {

                EntityPlayer entityPlayer1 = null;
                Iterator iterator = world.playerEntities.iterator();

                while (iterator.hasNext()) {

                    EntityPlayer entityPlayer2 = (EntityPlayer)iterator.next();

                    if (entityPlayer2.isPlayerSleeping()) {

                        BlockPos chunkCoordinates = entityPlayer2.playerLocation;

                        if (chunkCoordinates.getX() == x && chunkCoordinates.getY() == y && chunkCoordinates.getZ() == z) {
                            entityPlayer1 = entityPlayer2;
                        }

                    }

                }

                if (entityPlayer1 != null) {
                    ChatHandler.sendMessageToPlayer("tile.bed.occupied", entityPlayer, false);
                    return;
                }

                setBedOccupied(world, bedPartPos, entityPlayer, false);

            }

            EnumStatus enumstatus = entityPlayer.trySleep(bedPartPos);

            if (enumstatus == EnumStatus.OK) {

                setBedOccupied(world, bedPartPos, entityPlayer, true);

            } else {

                if (enumstatus == EnumStatus.NOT_POSSIBLE_NOW) {
                    ChatHandler.sendMessageToPlayer("tile.bed.noSleep", entityPlayer, false);
                } else if (enumstatus == EnumStatus.NOT_SAFE) {
                    ChatHandler.sendMessageToPlayer("tile.bed.notSafe", entityPlayer, false);
                }

            }

        } else {

            destroyBlock(world, bedPartPos, false);
            world.newExplosion((Entity)null, x + 0.5F, y + 0.5F, z + 0.5F, 5.0F, true, true);

        }
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState blockstate, Block block)
    {
        if (!world.isRemote) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null) {
                if (Bed.getOppositeTE(TE) == null) {
                    destroyBlock(world, pos, false);
                }
            }
        }

        super.onNeighborBlockChange(world, pos, blockstate, block);
    }

    @Override
    /**
     * Returns the items to drop on destruction.
     */
    public Item getItemDropped(IBlockState state, Random random, int par2)
    {
        return ItemRegistry.itemCarpentersBed;
    }

    @Override
    /**
     * Called when a user either starts or stops sleeping in the bed.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param player The player or camera entity, null in some cases.
     * @param occupied True if we are occupying the bed, or false if they are stopping use of the bed
     */
    public void setBedOccupied(IBlockAccess blockAccess, BlockPos pos, EntityPlayer player, boolean occupied)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null && !TE.getWorld().isRemote) {

            Bed.setOccupied(TE, occupied);

            TEBase TE_opp = Bed.getOppositeTE(TE);

            if (TE_opp != null) {
                Bed.setOccupied(TE_opp, occupied);
            }

        }
    }

    @Override
    /**
     * Returns the direction of the block. Same values that
     * are returned by BlockDirectional
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @return Bed direction
     */
    public EnumFacing getBedDirection(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        switch (Bed.getDirection(TE))
        {
            case NORTH:
                return EnumFacing.getFront(0);
            case SOUTH:
                return EnumFacing.getFront(2);
            case WEST:
                return EnumFacing.getFront(3);
            default:
                return EnumFacing.getFront(1);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    public Item getItem(World world, BlockPos pos)
    {
        return ItemRegistry.itemCarpentersBed;
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return BlockRegistry.carpentersBedRenderID;
    }

    @Override
    public EnumFacing[] getValidRotations(World worldObj, BlockPos pos)
    {
        EnumFacing[] axises = {EnumFacing.UP, EnumFacing.DOWN};
        return axises;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        // to correctly support archimedes' ships mod:
        // if Axis is DOWN, block rotates to the left, north -> west -> south -> east
        // if Axis is UP, block rotates to the right:  north -> east -> south -> west

        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TEBase)
        {
            TEBase cbTile = (TEBase)tile;
            EnumFacing direction = Bed.getDirection(cbTile);
            switch (axis)
            {
                case UP:
                {
                    switch (direction)
                    {
                        case NORTH:{Bed.setDirection(cbTile, 1); break;}
                        case EAST:{Bed.setDirection(cbTile, 2); break;}
                        case SOUTH:{Bed.setDirection(cbTile, 3); break;}
                        case WEST:{Bed.setDirection(cbTile, 0); break;}
                        default: break;
                    }
                    break;
                }
                case DOWN:
                {
                    switch (direction)
                    {
                        case NORTH:{Bed.setDirection(cbTile, 3); break;}
                        case EAST:{Bed.setDirection(cbTile, 0); break;}
                        case SOUTH:{Bed.setDirection(cbTile, 1); break;}
                        case WEST:{Bed.setDirection(cbTile, 2); break;}
                        default: break;
                    }
                    break;
                }
                default: return false;
            }
            return true;
        }
        return false;
    }

}
