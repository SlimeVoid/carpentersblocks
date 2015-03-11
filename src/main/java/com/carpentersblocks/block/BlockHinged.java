package com.carpentersblocks.block;

import com.carpentersblocks.data.Hinge;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.handler.ChatHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public abstract class BlockHinged extends BlockCoverable {

    public BlockHinged(Material material)
    {
        super(material);
    }

    /**
     * Determines whether the bottom-most hinge requires a solid block underneath it.
     * @return the result
     */
    protected boolean requiresFoundation()
    {
        return true;
    }

    @Override
    /**
     * Alters hinge side.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        int hinge = Hinge.getHinge(TE);

        setHingeSide(TE, hinge == Hinge.HINGE_LEFT ? Hinge.HINGE_RIGHT : Hinge.HINGE_LEFT);

        return true;
    }

    @Override
    /**
     * Alters hinge type and redstone behavior.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        if (entityPlayer.isSneaking()) {

            int rigidity = Hinge.getRigidity(TE) == Hinge.HINGED_NONRIGID ? Hinge.HINGED_RIGID : Hinge.HINGED_NONRIGID;

            setHingeRigidity(TE, rigidity);

            switch (rigidity) {
                case Hinge.HINGED_NONRIGID:
                    ChatHandler.sendMessageToPlayer("message.activation_wood.name", entityPlayer);
                    break;
                case Hinge.HINGED_RIGID:
                    ChatHandler.sendMessageToPlayer("message.activation_iron.name", entityPlayer);
            }

            return true;

        }

        return false;
    }

    @Override
    /**
     * Opens or closes hinge on right click.
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ, ActionResult actionResult)
    {
        if (!activationRequiresRedstone(TE)) {
            setHingeState(TE, Hinge.getState(TE) == Hinge.STATE_OPEN ? Hinge.STATE_CLOSED : Hinge.STATE_OPEN);
            actionResult.setAltered().setNoSound();
        }
    }

    /**
     * Returns whether hinge requires redstone activation.
     */
    private boolean activationRequiresRedstone(TEBase TE)
    {
        return Hinge.getRigidity(TE) == Hinge.HINGED_RIGID;
    }

    /**
     * Returns a list of hinge tile entities that make up either a single hinge or two connected double hinges.
     */
    private List<TEBase> getHingePieces(TEBase TE)
    {
        List<TEBase> list = new ArrayList<TEBase>();
        World world = TE.getWorld();

        int piece = Hinge.getPiece(TE);
        int facing = Hinge.getFacing(TE);
        int hinge = Hinge.getHinge(TE);
        int neighbor_offset = piece == Hinge.PIECE_TOP ? -1 : 1;

        /* Add source hinge pieces */

        list.add(TE);

        TEBase TE_neighbor = getTileEntity(world, TE.getPos().add(0, neighbor_offset, 0));

        if (TE_neighbor == null) {
            return list;
        } else {
            list.add(TE_neighbor);
        }

        /* Begin searching for and adding other neighboring pieces. */

        TEBase TE_ZN = getTileEntity(world, TE.getPos().add(0, 0, -1));
        TEBase TE_ZP = getTileEntity(world, TE.getPos().add(0, 0, 1));
        TEBase TE_XN = getTileEntity(world, TE.getPos().add(-1, 0, 0));
        TEBase TE_XP = getTileEntity(world, TE.getPos().add(1, 0, 0));

        switch (facing)
        {
            case Hinge.FACING_XN:

                if (TE_ZN != null) {
                    if (piece == Hinge.getPiece(TE_ZN) && facing == Hinge.getFacing(TE_ZN) && hinge == Hinge.HINGE_LEFT && Hinge.getHinge(TE_ZN) == Hinge.HINGE_RIGHT)
                    {
                        list.add(TE_ZN);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(0, neighbor_offset, -1)));
                    }
                }
                if (TE_ZP != null) {
                    if (piece == Hinge.getPiece(TE_ZP) && facing == Hinge.getFacing(TE_ZP) && hinge == Hinge.HINGE_RIGHT && Hinge.getHinge(TE_ZP) == Hinge.HINGE_LEFT)
                    {
                        list.add(TE_ZP);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(0, neighbor_offset, 1)));
                    }
                }
                break;

            case Hinge.FACING_XP:

                if (TE_ZN != null) {
                    if (piece == Hinge.getPiece(TE_ZN) && facing == Hinge.getFacing(TE_ZN) && hinge == Hinge.HINGE_RIGHT && Hinge.getHinge(TE_ZN) == Hinge.HINGE_LEFT)
                    {
                        list.add(TE_ZN);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(0, neighbor_offset, -1)));
                    }
                }
                if (TE_ZP != null) {
                    if (piece == Hinge.getPiece(TE_ZP) && facing == Hinge.getFacing(TE_ZP) && hinge == Hinge.HINGE_LEFT && Hinge.getHinge(TE_ZP) == Hinge.HINGE_RIGHT)
                    {
                        list.add(TE_ZP);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(0, neighbor_offset, 1)));
                    }
                }
                break;

            case Hinge.FACING_ZN:
            {

                if (TE_XN != null) {
                    if (piece == Hinge.getPiece(TE_XN) && facing == Hinge.getFacing(TE_XN) && hinge == Hinge.HINGE_RIGHT && Hinge.getHinge(TE_XN) == Hinge.HINGE_LEFT)
                    {
                        list.add(TE_XN);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(-1, neighbor_offset, 0)));
                    }
                }
                if (TE_XP != null) {
                    if (piece == Hinge.getPiece(TE_XP) && facing == Hinge.getFacing(TE_XP) && hinge == Hinge.HINGE_LEFT && Hinge.getHinge(TE_XP) == Hinge.HINGE_RIGHT)
                    {
                        list.add(TE_XP);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(1, neighbor_offset, 0)));
                    }
                }
                break;
            }
            case Hinge.FACING_ZP:

                if (TE_XN != null) {
                    if (piece == Hinge.getPiece(TE_XN) && facing == Hinge.getFacing(TE_XN) && hinge == Hinge.HINGE_LEFT && Hinge.getHinge(TE_XN) == Hinge.HINGE_RIGHT)
                    {
                        list.add(TE_XN);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(-1, neighbor_offset, 0)));
                    }
                }
                if (TE_XP != null) {
                    if (piece == Hinge.getPiece(TE_XP) && facing == Hinge.getFacing(TE_XP) && hinge == Hinge.HINGE_RIGHT && Hinge.getHinge(TE_XP) == Hinge.HINGE_LEFT)
                    {
                        list.add(TE_XP);
                        list.add((TEBase) world.getTileEntity(TE.getPos().add(1, neighbor_offset, 0)));
                    }
                }
                break;

        }

        return list;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos)
    {
        if (world.getBlockState(pos).getBlock().equals(this)) {
            this.setBlockBoundsBasedOnState(world, pos);
        }

        return super.getSelectedBoundingBox(world, pos);
    }

    @Override
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state)
    {
        if (world.getBlockState(pos).equals(this)) {
            setBlockBoundsBasedOnState(world, pos);
        }

        return super.getCollisionBoundingBox(world, pos, state);
    }

    @Override
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {

            int facing = Hinge.getFacing(TE);
            int hinge = Hinge.getHinge(TE);
            boolean isOpen = Hinge.getState(TE) == Hinge.STATE_OPEN;

            float x_low = 0.0F;
            float z_low = 0.0F;
            float x_high = 1.0F;
            float z_high = 1.0F;

            switch (facing)
            {
                case Hinge.FACING_XN:
                    if (!isOpen) {
                        x_low = 0.8125F;
                    } else if (hinge == Hinge.HINGE_RIGHT) {
                        z_high = 0.1875F;
                    } else {
                        z_low = 0.8125F;
                    }
                    break;
                case Hinge.FACING_XP:
                    if (!isOpen) {
                        x_high = 0.1875F;
                    } else if (hinge == Hinge.HINGE_RIGHT) {
                        z_low = 0.8125F;
                    } else {
                        z_high = 0.1875F;
                    }
                    break;
                case Hinge.FACING_ZN:
                    if (!isOpen) {
                        z_low = 0.8125F;
                    } else if (hinge == Hinge.HINGE_RIGHT) {
                        x_low = 0.8125F;
                    } else {
                        x_high = 0.1875F;
                    }
                    break;
                case Hinge.FACING_ZP:
                    if (!isOpen) {
                        z_high = 0.1875F;
                    } else if (hinge == Hinge.HINGE_RIGHT) {
                        x_high = 0.1875F;
                    } else {
                        x_low = 0.8125F;
                    }
                    break;
            }

            setBlockBounds(x_low, 0.0F, z_low, x_high, 1.0F, z_high);

        }
    }

    /**
     * Cycle hinge state.
     * Will update all connecting hinge pieces.
     */
    public void setHingeState(TEBase TE, int state)
    {
        List<TEBase> hingePieces = getHingePieces(TE);
        for (TEBase piece : hingePieces) {
            Hinge.setState(piece, state, piece == TE);
        }
    }

    /**
     * Updates hinge type.
     * Will also update adjoining hinge piece.
     */
    public void setHingeType(TEBase TE, int type)
    {
        Hinge.setType(TE, type);
        updateAdjoiningPiece(TE);
    }

    /**
     * Set hinge rigidity.
     * Will update all connecting hinge pieces.
     */
    public void setHingeRigidity(TEBase TE, int rigidity)
    {
        List<TEBase> hingePieces = getHingePieces(TE);
        for (TEBase piece : hingePieces) {
            Hinge.setRigidity(piece, rigidity);
        }
    }

    /**
     * Updates hinge hinge side.
     * Will also update adjoining hinge piece.
     */
    public void setHingeSide(TEBase TE, int side)
    {
        Hinge.setHingeSide(TE, side);
        updateAdjoiningPiece(TE);
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block)
    {
        if (!world.isRemote) {

            TEBase TE = getTileEntity(world, pos);

            if (TE != null) {

                boolean isOpen = Hinge.getState(TE) == Hinge.STATE_OPEN;

                /* Check if hinge piece is orphaned. */

                if (Hinge.getPiece(TE) == Hinge.PIECE_BOTTOM) {
                    if (!world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(this)) {
                        destroyBlock(world, pos, false);
                        return;
                    } else if (requiresFoundation() && !World.doesBlockHaveSolidTopSurface(world, pos.add(0, -1, 0))) {
                        destroyBlock(world, pos, true);
                        return;
                    }
                } else if (!world.getBlockState(pos.add(0, -1, 0)).getBlock().equals(this)) {
                    destroyBlock(world, pos, false);
                    return;
                }

                /*
                 * Create list of hinge pieces and check state of each so
                 * that they act as a single entity regardless of which
                 * hinge piece receives this event.
                 */

                boolean isPowered = false;
                List<TEBase> hingePieces = getHingePieces(TE);
                for (TEBase piece : hingePieces) {
                    if (piece != null) {
                        if (world.isBlockIndirectlyGettingPowered(piece.getPos()) > 0) {
                            isPowered = true;
                        }
                    }
                }

                /* Set block open or closed. */

                if (block != null && block.canProvidePower() && isPowered != isOpen) {
                    setHingeState(TE, isOpen ? Hinge.STATE_CLOSED : Hinge.STATE_OPEN);
                }

            }

        }

        super.onNeighborBlockChange(world, pos, state, block);
    }

    /**
     * Updates state, hinge and type for adjoining hinge piece.
     */
    private void updateAdjoiningPiece(TEBase TE)
    {
        int state = Hinge.getState(TE);
        int hinge = Hinge.getHinge(TE);
        int type = Hinge.getType(TE);
        int rigidity = Hinge.getRigidity(TE);

        boolean isTop = Hinge.getPiece(TE) == Hinge.PIECE_TOP;

        World world = TE.getWorld();

        TEBase TE_adj;
        if (isTop) {
            TE_adj = (TEBase) world.getTileEntity(TE.getPos().add(0, -1, 0));
        } else {
            TE_adj = (TEBase) world.getTileEntity(TE.getPos().add(0, 1, 0));
        }

        Hinge.setState(TE_adj, state, false);
        Hinge.setHingeSide(TE_adj, hinge);
        Hinge.setType(TE_adj, type);
        Hinge.setRigidity(TE_adj, rigidity);
    }

}
