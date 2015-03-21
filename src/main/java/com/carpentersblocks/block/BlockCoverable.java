package com.carpentersblocks.block;

import com.carpentersblocks.api.ICarpentersChisel;
import com.carpentersblocks.api.ICarpentersHammer;
import com.carpentersblocks.api.IWrappableBlock;
import com.carpentersblocks.renderer.helper.FancyFluidsHelper;
import com.carpentersblocks.renderer.helper.ParticleHelper;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.EntityLivingUtil;
import com.carpentersblocks.util.handler.DesignHandler;
import com.carpentersblocks.util.handler.EventHandler;
import com.carpentersblocks.util.handler.OverlayHandler;
import com.carpentersblocks.util.handler.OverlayHandler.Overlay;
import com.carpentersblocks.util.protection.PlayerPermissions;
import com.carpentersblocks.util.registry.FeatureRegistry;
import com.carpentersblocks.util.registry.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public abstract class BlockCoverable extends BlockContainer {

    /** Block drop event for dropping attribute. */
    public static int EVENT_ID_DROP_ATTR = 0;

    /** Indicates during getDrops that block instance should not be dropped. */
    protected final IBlockState METADATA_DROP_ATTR_ONLY = Blocks.air.getDefaultState();

    /** Whether breakBlock() should drop block attributes. */
    private boolean enableDrops = false;

    /** Caches light values. */
    public static Map<Integer,Integer> cache = new HashMap<Integer,Integer>();

    /**
     * Stores actions taken on a block in order to properly play sounds,
     * decrement player inventory, and to determine if a block was altered.
     */
    protected class ActionResult {

        public ItemStack itemStack;
        public boolean playSound = true;
        public boolean altered = false;
        public boolean decInv = false;

        public ActionResult setSoundSource(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public ActionResult setNoSound() {
            playSound = false;
            return this;
        }

        public ActionResult setAltered() {
            altered = true;
            return this;
        }

        public ActionResult decInventory() {
            decInv = true;
            return this;
        }

    }

    /**
     * Class constructor.
     *
     * @param material
     */
    public BlockCoverable(Material material)
    {
        super(material);
    }

    //@SideOnly(Side.CLIENT)
    //@Override
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    //public void registerBlockIcons(IIconRegister iconRegister) { }

    //@SideOnly(Side.CLIENT)
    /**
     * Returns a base icon that doesn't rely on blockIcon, which
     * is set prior to texture stitch events.
     *
     * @return default icon
     */
    //public IIcon getIcon()
    //{
    //    return IconRegistry.icon_uncovered_solid;
    //}

    //@SideOnly(Side.CLIENT)
    //@Override
    /**
     * Returns the icon on the side given the block metadata.
     * <p>
     * Due to the amount of control needed over this, vanilla calls will always return an invisible icon.
     */
    //public IIcon getIcon(EnumFacing side, int metadata)
    //{
    //    if (BlockProperties.isMetadataDefaultIcon(metadata)) {
    //        return getIcon();
    //    }

        /*
         * This icon is a mask (or something) for redstone wire.
         * We use it here because it renders an invisible icon.
         *
         * Using an invisible icon is important because sprint particles are
         * hard-coded and will always grab particle icons using this method.
         * We'll throw our own sprint particles in EventHandler.class.
         */

    //    return BlockRedstoneWire.getRedstoneWireIcon("cross_overlay");
    //}

    //@SideOnly(Side.CLIENT)
    //@Override
    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, pos, side
     */
//    public IIcon getIcon(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
//    {
//        TEBase TE = getTileEntity(blockAccess, pos);
//        ItemStack itemStack = BlockProperties.getCover(TE, 6);
//        Block block = BlockProperties.toBlock(itemStack);
//
//        return block instanceof BlockCoverable ? getIcon() : getWrappedIcon(block, blockAccess, pos, side, itemStack.getItemDamage());
//    }


    //private static IIcon getWrappedIcon(Block b, IBlockAccess iba, BlockPos pos, EnumFacing side, int meta)
    //{
    //    return b instanceof IWrappableBlock ? ((IWrappableBlock)b).getIcon(iba, pos, side, b, meta) : b.getIcon(side, meta);
    //}

    /**
     * For South-sided blocks, rotates and sets the block bounds using
     * the provided EnumFacing.
     *
     * @param  dir the rotated {@link EnumFacing}
     */
    protected void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, EnumFacing dir)
    {
        switch (dir) {
            case DOWN:
                setBlockBounds(minX, 1.0F - maxZ, minY, maxX, 1.0F - minZ, maxY);
                break;
            case UP:
                setBlockBounds(minX, minZ, minY, maxX, maxZ, maxY);
                break;
            case NORTH:
                setBlockBounds(1.0F - maxX, minY, 1.0F - maxZ, 1.0F - minX, maxY, 1.0F - minZ);
                break;
            case EAST:
                setBlockBounds(minZ, minY, 1.0F - maxX, maxZ, maxY, 1.0F - minX);
                break;
            case WEST:
                setBlockBounds(1.0F - maxZ, minY, minX, 1.0F - minZ, maxY, maxX);
                break;
            default:
                setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                break;
        }
    }

    /**
     * Called when block event is received.
     *<p>
     * For the context of this mod, this is used for dropping block attributes
     * like covers, overlays, dyes, or any other ItemStack.
     *<p>
     * In order for external classes to call the protected method
     * {@link Block#dropBlockAsItem(World,BlockPos,IBlockState,int) dropBlockAsItem},
     * they create a block event with parameters itemId and metadata, allowing
     * the {@link ItemStack} to be recreated and dropped.
     *
     * @param  world the {@link World}
     * @param  pos the BlockPos
     * @param  state the IBlockState
     * @param  eventId the eventId
     * @param  param the event parameter
     * @return true if event was handled
     */
    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventId, int param /*attrId*/)
    {
        if (eventId == EVENT_ID_DROP_ATTR)
        {
            TEBase TE = getSimpleTileEntity(world, pos);
            if (TE != null && TE.hasAttribute((byte) param))
            {
                ItemStack itemStack = TE.getAttributeForDrop((byte) param);
                dropBlockAsItem(world, pos, BlockProperties.toBlockState(itemStack), 0);
                TE.onAttrDropped((byte) param);
                return true;
            }
        }

        return super.onBlockEventReceived(world, pos, state, eventId, param);
    }

    /**
     * Drops block as {@link ItemStack} and notifies relevant systems of
     * block removal.  Block attributes will drop later in destruction.
     * <p>
     * This is usually called when a {@link #onNeighborBlockChange(World, BlockPos, IBlockState, Block) neighbor changes}.
     *
     * @param world the {@link World}
     * @param pos the position of the block
     * @param dropBlock whether block {@link ItemStack} is dropped
     */
    protected void destroyBlock(World world, BlockPos pos, boolean dropBlock)
    {
        IBlockState state = world.getBlockState(pos);
        if (dropBlock) {
            dropBlockAsItem(world, pos, state, 0);
        }
        world.setBlockToAir(pos);
    }

    /**
     * Returns an item stack containing a single instance of the current block type. 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot be harvested should return null.
     */
    protected ItemStack getItemDrop(World world, IBlockState state)
    {
        int fortune = 1;
        return new ItemStack(getItemDropped(state, world.rand, fortune), 1, state.getBlock().getMetaFromState(state));
    }

    /**
     * Returns adjacent, similar tile entities that can be used for duplicating
     * block properties like dye color, pattern, style, etc.
     *
     * @param  world the world reference
     * @param  pos the position of the block
     * @return an array of adjacent, similar tile entities
     * @see {@link TEBase}
     */
    protected TEBase[] getAdjacentTileEntities(World world, BlockPos pos)
    {
        return new TEBase[] {
            getSimpleTileEntity(world, pos.down()),
            getSimpleTileEntity(world, pos.up()),
            getSimpleTileEntity(world, pos.add(0, 0, -1)),
            getSimpleTileEntity(world, pos.add(0, 0, 1)),
            getSimpleTileEntity(world, pos.add(-1, 0, 0)),
            getSimpleTileEntity(world, pos.add(1, 0, 0))
        };
    }

    /**
     * Returns tile entity if block tile entity is instanceof TEBase.
     *
     * Used for generic purposes such as getting pattern, dye color, or
     * cover of another Carpenter's block.  Is also used if block
     * no longer exists, such as when breaking a block and ejecting
     * attributes.
     */
    protected TEBase getSimpleTileEntity(IBlockAccess blockAccess, BlockPos pos)
    {
        TileEntity TE = blockAccess.getTileEntity(pos);
        return (TE instanceof TEBase) ? (TEBase) TE : null;
    }

    /**
     * Returns tile entity if block tile entity is instanceof TEBase and
     * also belongs to this block type.
     */
    protected TEBase getTileEntity(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getSimpleTileEntity(blockAccess, pos);
        return TE != null && blockAccess.getBlockState(pos).getBlock().isAssociatedBlock(this) ? TE : null;
    }

    /**
     * Returns whether player is allowed to activate this block.
     */
    protected boolean canPlayerActivate(TEBase TE, EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    /**
     * Called when the block is clicked by a player. Args: pos, entityPlayer
     */
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer entityPlayer)
    {
        if (world.isRemote) {
            return;
        }

        TEBase TE = getTileEntity(world, pos);

        if (TE == null) {
            return;
        } else if (!PlayerPermissions.canPlayerEdit(TE, TE.getPos(), entityPlayer)) {
            return;
        }

        ItemStack itemStack = entityPlayer.getCurrentEquippedItem();

        if (itemStack == null) {
            return;
        }

        int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[EventHandler.eventFace.getIndex()]) ? EventHandler.eventFace.getIndex() : 6;
        Item item = itemStack.getItem();

        if (item instanceof ICarpentersHammer && ((ICarpentersHammer)item).canUseHammer(world, entityPlayer)) {

            ActionResult actionResult = new ActionResult();
            preOnBlockClicked(TE, world, pos, entityPlayer, actionResult);

            if (!actionResult.altered) {
                if (entityPlayer.isSneaking()) {
                    popAttribute(TE, EnumFacing.getFront(effectiveSide));
                } else {
                    onHammerLeftClick(TE, entityPlayer);
                }
                actionResult.setAltered();
            } else {
                onNeighborBlockChange(world, pos, world.getBlockState(pos), this);
                world.notifyNeighborsOfStateChange(pos, this);
            }

        } else if (item instanceof ICarpentersChisel && ((ICarpentersChisel)item).canUseChisel(world, entityPlayer)) {

            if (entityPlayer.isSneaking() && TE.hasChiselDesign(effectiveSide)) {
                TE.removeChiselDesign(effectiveSide);
            } else if (TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                onChiselClick(TE, EnumFacing.getFront(effectiveSide), true);
            }

        }
    }

    /**
     * Pops attribute in hard-coded order.
     *
     * @param TE
     * @param facing
     */
    private void popAttribute(TEBase TE, EnumFacing facing)
    {
        int side = facing.getIndex();
        if (TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
            TE.createBlockDropEvent(TE.ATTR_ILLUMINATOR);
        } else if (TE.hasAttribute(TE.ATTR_OVERLAY[side])) {
            TE.createBlockDropEvent(TE.ATTR_OVERLAY[side]);
        } else if (TE.hasAttribute(TE.ATTR_DYE[side])) {
            TE.createBlockDropEvent(TE.ATTR_DYE[side]);
        } else if (TE.hasAttribute(TE.ATTR_COVER[side])) {
            TE.createBlockDropEvent(TE.ATTR_COVER[side]);
            TE.removeChiselDesign(side);
        }
    }

    @Override
    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote) {

            return true;

        } else {

            TEBase TE = getTileEntity(world, pos);

            if (TE == null) {
                return false;
            }

            if (!canPlayerActivate(TE, entityPlayer)) {
                return false;
            }

            // Allow block to change TE if needed before altering attributes
            TE = getTileEntityForBlockActivation(TE);
            ActionResult actionResult = new ActionResult();

            preOnBlockActivated(TE, entityPlayer, side, hitX, hitY, hitZ, actionResult);

            // If no prior event occurred, try regular activation
            if (!actionResult.altered) {

                if (PlayerPermissions.canPlayerEdit(TE, TE.getPos(), entityPlayer)) {

                    ItemStack itemStack = entityPlayer.getCurrentEquippedItem();

                    if (itemStack != null) {

                        /* Sides 0-5 are side covers, and 6 is the base block. */
                        int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[side.getIndex()]) ? side.getIndex() : 6;

                        if (itemStack.getItem() instanceof ICarpentersHammer && ((ICarpentersHammer)itemStack.getItem()).canUseHammer(world, entityPlayer)) {

                            if (onHammerRightClick(TE, entityPlayer)) {
                                actionResult.setAltered();
                            }

                        } else if (ItemRegistry.enableChisel && itemStack.getItem() instanceof ICarpentersChisel && ((ICarpentersChisel)itemStack.getItem()).canUseChisel(world, entityPlayer)) {

                            if (TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                                if (onChiselClick(TE, EnumFacing.getFront(effectiveSide), false)) {
                                    actionResult.setAltered();
                                }
                            }

                        } else if (FeatureRegistry.enableCovers && BlockProperties.isCover(itemStack)) {

                            IBlockState coverstate = BlockProperties.toBlockState(itemStack);

                            /* Will handle blocks that save directions using only y axis (pumpkin) */
                            int metadata = coverstate.getBlock() instanceof BlockDirectional ? MathHelper.floor_double(entityPlayer.rotationYaw * 4.0F / 360.0F + 2.5D) & 3 : itemStack.getMetadata();

                            /* Will handle blocks that save directions using all axes (logs, quartz) */
                            if (BlockProperties.blockRotates(itemStack)) {
                                //int rot = Direction.rotateOpposite[EntityLivingUtil.getRotationValue(entityPlayer)];
                                //EnumFacing side_interpolated = entityPlayer.rotationPitch < -45.0F ? 0 : entityPlayer.rotationPitch > 45 ? 1 : rot == 0 ? 3 : rot == 1 ? 4 : rot == 2 ? 2 : 5;
                                coverstate = coverstate.getBlock().onBlockPlaced(world, TE.getPos(), entityPlayer.getHorizontalFacing(), hitX, hitY, hitZ, metadata, entityPlayer);
                                metadata = coverstate.getBlock().getMetaFromState(coverstate);
                            }

                            ItemStack tempStack = itemStack.copy();
                            tempStack.setItemDamage(metadata);

                            /* Base cover should always be checked. */

                            if (effectiveSide == 6 && (!canCoverSide(TE, world, TE.getPos(), 6) || TE.hasAttribute(TE.ATTR_COVER[6]))) {
                                effectiveSide = side.getIndex();
                            }

                            if (canCoverSide(TE, world, TE.getPos(), effectiveSide) && !TE.hasAttribute(TE.ATTR_COVER[effectiveSide])) {
                                TE.addAttribute(TE.ATTR_COVER[effectiveSide], tempStack);
                                actionResult.setAltered().decInventory().setSoundSource(itemStack);
                            }

                        } else if (entityPlayer.isSneaking()) {

                            if (FeatureRegistry.enableIllumination && BlockProperties.isIlluminator(itemStack)) {
                                if (!TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
                                    TE.addAttribute(TE.ATTR_ILLUMINATOR, itemStack);
                                    actionResult.setAltered().decInventory().setSoundSource(itemStack);
                                }
                            } else if (FeatureRegistry.enableOverlays && BlockProperties.isOverlay(itemStack)) {
                                if (!TE.hasAttribute(TE.ATTR_OVERLAY[effectiveSide]) && (effectiveSide < 6 && TE.hasAttribute(TE.ATTR_COVER[effectiveSide]) || effectiveSide == 6)) {
                                    TE.addAttribute(TE.ATTR_OVERLAY[effectiveSide], itemStack);
                                    actionResult.setAltered().decInventory().setSoundSource(itemStack);
                                }
                            } else if (FeatureRegistry.enableDyeColors && BlockProperties.isDye(itemStack, false)) {
                                if (!TE.hasAttribute(TE.ATTR_DYE[effectiveSide])) {
                                    TE.addAttribute(TE.ATTR_DYE[effectiveSide], itemStack);
                                    actionResult.setAltered().decInventory().setSoundSource(itemStack);
                                }
                            }

                        }
                    }
                }
            }

            if (!actionResult.altered) {

                // If no prior or regular event occurred, try a post event
                postOnBlockActivated(TE, entityPlayer, side, hitX, hitY, hitZ, actionResult);

            } else {

                if (actionResult.itemStack == null) {
                    actionResult.setSoundSource(BlockProperties.getCover(TE, 6));
                }
                damageItemWithChance(world, entityPlayer);
                onNeighborBlockChange(world, TE.getPos(), state, this);
                world.notifyNeighborsOfStateChange(TE.getPos(), this);

            }

            if (actionResult.playSound) {
                BlockProperties.playBlockSound(TE.getWorld(), actionResult.itemStack, TE.getPos(), false);
            }

            if (actionResult.decInv) {
                EntityLivingUtil.decrementCurrentSlot(entityPlayer);
            }

            return actionResult.altered;

        }
    }

    /**
     * Cycles through chisel patterns.
     */
    public boolean onChiselClick(TEBase TE, EnumFacing side, boolean leftClick)
    {
        String design = TE.getChiselDesign(side.getIndex());
        String designAdj = "";

        if (design.equals("")) {

            World world = TE.getWorld();

            /* Match pattern with adjacent pattern if possible. */

            TEBase[] TE_list = getAdjacentTileEntities(world, TE.getPos());

            for (TEBase TE_current : TE_list) {
                if (TE_current != null) {
                    TE_current.getBlockType();
                    if (TE_current.hasChiselDesign(side.getIndex())) {
                        design = TE_current.getChiselDesign(side.getIndex());
                        designAdj = design;
                    }
                }
            }

        }

        if (designAdj.equals("")) {
            design = leftClick ? DesignHandler.getPrev("chisel", design) : DesignHandler.getNext("chisel", design);
        }

        if (!design.equals("")) {
            TE.setChiselDesign(side.getIndex(), design);
        }

        return true;
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: pos, neighbor blockID
     */
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block)
    {
        /* This will check for and eject side covers that are obstructed. */

        if (!world.isRemote) {
            TEBase TE = getTileEntity(world, pos);
            if (TE != null) {
                for (int side = 0; side < 6; ++side) {
                    if (TE.hasAttribute(TE.ATTR_COVER[side])) {
                        if (!canCoverSide(TE, world, pos, side)) {
                            TE.removeAttributes(side);
                            continue;
                        }
                        EnumFacing dir = EnumFacing.getFront(side);
                        if (world.isSideSolid(pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ()), dir.getOpposite()) && isSideSolid(world, pos, dir)) {
                            TE.removeAttributes(side);
                        }
                    }
                }
            }
        }
    }

    @Override
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess blockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);
        int power = 0;

        /* Indirect power is provided by any cover. */

        if (TE != null) {
            for (int idx = 0; idx < 7; ++idx) {
                if (TE.hasAttribute(TE.ATTR_COVER[idx])) {
                    IBlockState coverstate = BlockProperties.toBlockState(BlockProperties.getCover(TE, idx));
                    int tempPower = coverstate.getBlock().isProvidingWeakPower(blockAccess, pos, coverstate, side);
                    if (tempPower > power) {
                        power = tempPower;
                    }
                }
            }
        }

        return power;
    }

    @Override
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, pos,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess blockAccess, BlockPos pos, IBlockState state, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);
        int power = 0;

        /* Strong power is provided by the base cover, or a side cover if one exists. */


        int effectiveSide = side.getOpposite().getIndex();
        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[effectiveSide])) {
            IBlockState coverstate = BlockProperties.toBlockState(BlockProperties.getCover(TE, effectiveSide));
            int tempPower = coverstate.getBlock().isProvidingWeakPower(blockAccess, pos, coverstate, side);
            if (tempPower > power) {
                power = tempPower;
            }
        } else if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            IBlockState coverstate = BlockProperties.toBlockState(BlockProperties.getCover(TE, 6));
            int tempPower = coverstate.getBlock().isProvidingWeakPower(blockAccess, pos, coverstate, side);
            if (tempPower > power) {
                power = tempPower;
            }
        }


        return power;
    }

    /**
     * Indicates whether block destruction should be suppressed when block is clicked.
     * Will return true if player is holding a Carpenter's tool in creative mode.
     */
    protected boolean suppressDestroyBlock(EntityPlayer entityPlayer)
    {
        ItemStack itemStack = entityPlayer.getHeldItem();

        if (itemStack != null) {
            Item item = itemStack.getItem();
            return entityPlayer.capabilities.isCreativeMode && item != null && (item instanceof ICarpentersHammer || item instanceof ICarpentersChisel);
        }

        return false;
    }

    @Override
    /**
     * Called when a player removes a block.
     * This controls block break behavior when a player in creative mode left-clicks on block while holding a Carpenter's Hammer
     */
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer entityPlayer, boolean willHarvest)
    {
        if (!suppressDestroyBlock(entityPlayer)) {
            return super.removedByPlayer(world, pos, entityPlayer, willHarvest);
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Spawn a digging particle effect in the world, this is a wrapper
     * around EffectRenderer.addBlockHitEffects to allow the block more
     * control over the particles. Useful when you have entirely different
     * texture sheets for different sides/locations in the world.
     *
     * @param world The current world
     * @param target The target the player is looking at {x/y/z/side/sub}
     * @param effectRenderer A reference to the current effect renderer.
     * @return True to prevent vanilla digging particles form spawning.
     */
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        TEBase TE = getTileEntity(world, target.getBlockPos());

        if (TE != null) {

            int effectiveSide = TE.hasAttribute(TE.ATTR_COVER[target.sideHit.getIndex()]) ? target.sideHit.getIndex() : 6;
            ItemStack itemStack = BlockProperties.getCover(TE, effectiveSide);

            if (BlockProperties.hasAttribute(TE, TE.ATTR_OVERLAY[effectiveSide])) {
                Overlay overlay = OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[effectiveSide]));
                if (OverlayHandler.coversFullSide(overlay, target.sideHit.getIndex())) {
                    itemStack = overlay.getItemStack();
                }
            }

            IBlockState coverstate = BlockProperties.toBlockState(itemStack);

            double xOffset = world.rand.nextDouble() * (coverstate.getBlock().getBlockBoundsMaxX() - coverstate.getBlock().getBlockBoundsMinX() - 0.1F * 2.0F) + 0.1F + coverstate.getBlock().getBlockBoundsMinX();
            double yOffset = world.rand.nextDouble() * (coverstate.getBlock().getBlockBoundsMaxY() - coverstate.getBlock().getBlockBoundsMinY() - 0.1F * 2.0F) + 0.1F + coverstate.getBlock().getBlockBoundsMinY();
            double zOffset = world.rand.nextDouble() * (coverstate.getBlock().getBlockBoundsMaxZ() - coverstate.getBlock().getBlockBoundsMinZ() - 0.1F * 2.0F) + 0.1F + coverstate.getBlock().getBlockBoundsMinZ();

            xOffset += target.getBlockPos().getX();
            yOffset += target.getBlockPos().getY();
            zOffset += target.getBlockPos().getZ();

            switch (target.sideHit.getIndex()) {
                case 0:
                    yOffset = target.getBlockPos().getY() + coverstate.getBlock().getBlockBoundsMinY() - 0.1D;
                    break;
                case 1:
                    yOffset = target.getBlockPos().getY() + coverstate.getBlock().getBlockBoundsMaxY() + 0.1D;
                    break;
                case 2:
                    zOffset = target.getBlockPos().getZ() + coverstate.getBlock().getBlockBoundsMinZ() - 0.1D;
                    break;
                case 3:
                    zOffset = target.getBlockPos().getZ() + coverstate.getBlock().getBlockBoundsMaxZ() + 0.1D;
                    break;
                case 4:
                    xOffset = target.getBlockPos().getX() + coverstate.getBlock().getBlockBoundsMinX() - 0.1D;
                    break;
                case 5:
                    xOffset = target.getBlockPos().getX() + coverstate.getBlock().getBlockBoundsMaxX() + 0.1D;
                    break;
            }

            // TODO:: Particles
            // ParticleHelper.addHitEffect(TE, target, xOffset, yOffset, zOffset, itemStack, effectRenderer);

            return true;

        }

        return super.addHitEffects(world, target, effectRenderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Renders block destruction effects.
     * This is controlled to prevent block destroy effects if left-clicked with a Carpenter's Hammer while player is in creative mode.
     *
     * Returns false to display effects.  True suppresses them (backwards).
     */
    public boolean addDestroyEffects(World world, BlockPos pos, EffectRenderer effectRenderer)
    {
        /*
         * We don't have the ability to accurately determine the entity that is
         * hitting the block. So, instead we're guessing based on who is
         * closest. This should be adequate most of the time.
         */

        TEBase TE = getTileEntity(world, pos);

        if (TE != null) {

            EntityPlayer entityPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 6.5F);

            if (entityPlayer != null) {
                if (!suppressDestroyBlock(entityPlayer)) {
                    // TODO:: Particles
                    // ParticleHelper.addDestroyEffect(world, pos, BlockProperties.getCover(TE, 6), effectRenderer);
                } else {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Hashes block coordinates for use in caching values.
     *
     * @param pos the BlockPos
     * @return
     */
    public static int hashCoords(BlockPos pos)
    {
        int hash = 3;
        hash = 97 * hash + pos.getX();
        hash = 97 * hash + pos.getY();
        hash = 97 * hash + pos.getZ();
        return hash;
    }

    /**
     * Gets the current light value based on covers and illumination.
     *
     * @param  blockAccess the {@link IBlockAccess} object
     * @param  pos the BlockPos
     * @return a light value from 0 to 15
     */
    protected int getCurrentLightValue(IBlockAccess blockAccess, BlockPos pos)
    {
        int lightValue = 0;
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null)
        {
            if (FeatureRegistry.enableIllumination && TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
                lightValue = 15;
            } else {
                for (int side = 0; side < 7; ++side) {
                    if (TE.hasAttribute(TE.ATTR_COVER[side])) {
                        ItemStack itemStack = BlockProperties.getCover(TE, side);
                        int tempLight = getLightValue(TE, BlockProperties.toBlockState(itemStack).getBlock(), itemStack.getItemDamage());
                        if (tempLight > lightValue) {
                            lightValue = tempLight;
                        }
                    }
                }
            }
        }

        return lightValue;
    }

    /**
     * Updates cached light value for block.
     * <b>
     * Several blocks may call for light values at once, and
     * grabbing the tile entity each time eats into performance.
     *
     * @param blockAccess the {@link IBlockAccess} object
     * @param pos the BlockPos
     */
    public void updateLightValue(IBlockAccess blockAccess, BlockPos pos)
    {
        int lightValue = getCurrentLightValue(blockAccess, pos);
        cache.put(hashCoords(pos), lightValue);
    }

    /**
     * Returns light value for block using two methods.  First, it
     * checks if the static light value is not zero.  If zero, it checks
     * using the block metadata.
     *
     * @param  TE the {@link TEBase}
     * @param  block the {@link Block}
     * @param  metadata the block metadata
     * @return a light value from 0 to 15
     */
    protected int getLightValue(TEBase TE, Block block, int metadata)
    {
        /* Grab static light value */

        int lightValue = block.getLightValue();

        /* Try grabbing more accurate lighting using metadata */

        if (lightValue == 0) {
            TE.setMetadata(metadata);
            lightValue = block.getLightValue(TE.getWorld(), TE.getPos());
            TE.restoreMetadata();
        }

        return lightValue;
    }

    @Override
    /**
     * Returns light value based on cover or side covers.
     */
    public final int getLightValue(IBlockAccess blockAccess, BlockPos pos)
    {
        int hash = hashCoords(pos);
        if (cache.containsKey(hash)) {
            return cache.get(hash);
        }

        return 0;
    }

    @Override
    /**
     * Returns the block hardness at a location. Args: world, pos
     */
    public float getBlockHardness(World world, BlockPos pos)
    {
        TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).getHardness(world, pos, state) : state.getBlock().getBlockHardness(world, pos);
        }

        return blockHardness;
    }

    @Override
    /**
     * Chance that fire will spread and consume this block.
     */
    public int getFlammability(IBlockAccess blockAccess, BlockPos pos, EnumFacing face)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).getFlammability(blockAccess, pos, face, state) : Blocks.fire.getFlammability(blockAccess, pos, face);
        }

        return super.getFlammability(blockAccess, pos, face);
    }

    @Override
    /**
     * Called when fire is updating on a neighbor block.
     */
    public int getFireSpreadSpeed(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).getFireSpread(blockAccess, pos, side, state) : Blocks.fire.getFlammability(blockAccess, pos, side);
        }

        return super.getFireSpreadSpeed(blockAccess, pos, side);
    }

    @Override
    /**
     * Currently only called by fire when it is on top of this block.
     * Returning true will prevent the fire from naturally dying during updating.
     * Also prevents fire from dying from rain.
     */
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).sustainsFire(world, pos, side, state) : state.getBlock().isFireSource(world, pos, side);
        }

        return false;
    }

    @Override
    /**
     * Location sensitive version of getExplosionRestance
     */
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
    {
        TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).getBlastResistance(exploder, world, pos, explosion.getPosition().xCoord, explosion.getPosition().yCoord, explosion.getPosition().zCoord, state) : state.getBlock().getExplosionResistance(world, pos, exploder, explosion);
        }

        return this.getExplosionResistance(exploder);
    }

    @Override
    /**
     * Returns whether block is wood
     */
    public boolean isWood(IBlockAccess blockAccess, BlockPos pos)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            ItemStack is = BlockProperties.getCover(TE, 6);
            IBlockState state = BlockProperties.toBlockState(is);
            return state.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)state.getBlock()).isLog(blockAccess, pos, state) : state.getBlock().isWood(blockAccess, pos);
        }

        return super.isWood(blockAccess, pos);
    }

    /**
     * Determines if this block is can be destroyed by the specified entities normal behavior.
     */
    @Override
    public boolean canEntityDestroy(IBlockAccess blockAccess, BlockPos pos, Entity entity)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            IBlockState coverstate = BlockProperties.toBlockState(BlockProperties.getCover(TE, 6));
            if (entity instanceof EntityWither) {
                return !coverstate.getBlock().equals(Blocks.bedrock) && !coverstate.getBlock().equals(Blocks.end_portal) && !coverstate.getBlock().equals(Blocks.end_portal_frame) && !coverstate.getBlock().equals(Blocks.command_block);
            } else if (entity instanceof EntityDragon) {
                return !coverstate.getBlock().equals(Blocks.obsidian) && !coverstate.getBlock().equals(Blocks.end_stone) && !coverstate.getBlock().equals(Blocks.bedrock);
            }
        }

        return super.canEntityDestroy(blockAccess, pos, entity);
    }

    @Override
    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, pos, entity
     */
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity)
    {
        TEBase TE = getTileEntity(world, pos);

        if (BlockProperties.hasAttribute(TE, TE.ATTR_COVER[6])) {
            BlockProperties.toBlockState(BlockProperties.getCover(TE, 6)).getBlock().onEntityCollidedWithBlock(world, pos, entity);
        }
    }

    /**
     * Spawns EntityItem in the world for the given ItemStack if the world is not remote.
     */
    private void dropBlock(World world, BlockPos pos, IBlockState state, int fortune)
    {
        // Clear metadata for Carpenter's blocks
        if (state.getBlock() instanceof BlockCoverable) {
            state = state.getBlock().getDefaultState();
        }

        super.dropBlockAsItem(world, pos, state, fortune);
    }

    @Override
    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        /*
         * When fluids break a block, they automatically call for blocks to drop items.
         * We only allow item drops in breakBlock() or when called elsewhere within this
         * mod, so check first.
         */
        if (enableDrops) {
            super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);
        } else {
            dropBlock(world, pos, state, fortune);
        }
    }

    @Override
    /**
     * Ejects contained items into the world, and notifies neighbors of an update, as appropriate
     */
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        // Drop attributes
        enableDrops = true;
        for (ItemStack itemStack : getDrops(world, pos, METADATA_DROP_ATTR_ONLY, 0)) {
            dropBlock(world, pos, state, 0);
        }
        enableDrops = false;

        // Remove cached light value
        cache.remove(hashCoords(pos));

        super.breakBlock(world, pos, state);
    }

    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param pos the BlockPos
     * @param state Current IBlockState
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
        TEBase TE = getSimpleTileEntity(world, pos);

        if (TE != null) {
            for (int idx = 0; idx < 7; ++idx) {
                if (TE.hasAttribute(TE.ATTR_COVER[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_COVER[idx]));
                }
                if (TE.hasAttribute(TE.ATTR_OVERLAY[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_OVERLAY[idx]));
                }
                if (TE.hasAttribute(TE.ATTR_DYE[idx])) {
                    ret.add(TE.getAttributeForDrop(TE.ATTR_DYE[idx]));
                }
            }
            if (TE.hasAttribute(TE.ATTR_ILLUMINATOR)) {
                ret.add(TE.getAttributeForDrop(TE.ATTR_ILLUMINATOR));
            }
        }

        return ret;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        TEBase TE = getTileEntity(world, pos);

        if (TE != null) {
            if (TE.hasAttribute(TE.ATTR_COVER[6])) {
                IBlockState coverstate = BlockProperties.toBlockState(BlockProperties.getCover(TE, 6));
                state.getBlock().randomDisplayTick(world, pos, coverstate, random);
            }
            if (TE.hasAttribute(TE.ATTR_OVERLAY[6])) {
                if (OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[6])).equals(Overlay.MYCELIUM)) {
                    Blocks.mycelium.randomDisplayTick(world, pos, state, random);
                }
            }
        }
    }

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a solid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param blockAccess The current world
     * @param pos the BlockPos
     * @param side The direction relative to the given position the plant wants to be, typically its UP
     * @param plantable The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    @Override
    public boolean canSustainPlant(IBlockAccess blockAccess, BlockPos pos, EnumFacing side, IPlantable plantable)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {

            /* If side is not solid, it can't sustain a plant. */

            if (!isSideSolid(blockAccess, pos, side)) {
                return false;
            }

            /*
             * Add base block, top block, and both of their associated
             * overlays to judge whether plants can be supported on block.
             */

            List<IBlockState> blocks = new ArrayList<IBlockState>();

            for (int side1 = 1; side1 < 7; side1 += 5) {
                if (TE.hasAttribute(TE.ATTR_COVER[side1])) {
                    blocks.add(BlockProperties.toBlockState(BlockProperties.getCover(TE, side1)));
                }
                if (TE.hasAttribute(TE.ATTR_OVERLAY[side1])) {
                    blocks.add(BlockProperties.toBlockState(OverlayHandler.getOverlayType(TE.getAttribute(TE.ATTR_OVERLAY[side1])).getItemStack()));
                }
            }

            /* Add types using cover material */

            Material material = BlockProperties.toBlockState(BlockProperties.getCover(TE, 6)).getBlock().getMaterial();
            if (material.equals(Material.grass)) {
                blocks.add(Blocks.grass.getDefaultState());
            } else if (material.equals(Material.ground)) {
                blocks.add(Blocks.dirt.getDefaultState());
            } else if (material.equals(Material.sand)) {
                blocks.add(Blocks.sand.getDefaultState());
            }

            switch (plantable.getPlantType(blockAccess, pos.add(0, 1, 0)))
            {
                case Desert: return blocks.contains(Blocks.sand.getDefaultState());
                case Nether: return blocks.contains(Blocks.soul_sand.getDefaultState());
                case Plains: return blocks.contains(Blocks.grass.getDefaultState()) || blocks.contains(Blocks.dirt.getDefaultState());
                case Beach:
                    boolean isBeach = blocks.contains(Blocks.grass.getDefaultState()) || blocks.contains(Blocks.dirt.getDefaultState()) || blocks.contains(Blocks.sand.getDefaultState());
                    boolean hasWater = blockAccess.getBlockState(pos.add(-1, 0, 0)).getBlock().getMaterial() == Material.water ||
                                       blockAccess.getBlockState(pos.add( 1, 0, 0)).getBlock().getMaterial() == Material.water ||
                                       blockAccess.getBlockState(pos.add( 0, 0,-1)).getBlock().getMaterial() == Material.water ||
                                       blockAccess.getBlockState(pos.add( 0, 0, 1)).getBlock().getMaterial() == Material.water;
                    return isBeach && hasWater;
                default:
                    break;
            }
        }

        return super.canSustainPlant(blockAccess, pos, side, plantable);
    }

    @Override
    /**
     * Returns whether this block is considered solid.
     */
    public boolean isBlockSolid(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        TEBase TE = getTileEntity(blockAccess, pos);

        if (TE != null) {
            return !TE.hasAttribute(TE.ATTR_COVER[6]) || BlockProperties.toBlockState(BlockProperties.getCover(TE, 6)).getBlock().isOpaqueCube();
        } else {
            return false;
        }
    }

    @Override
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        if (!world.isRemote) {

            TEBase TE = getTileEntity(world, pos);

            if (TE != null) {
                TE.setOwner(entityLiving.getUniqueID());
            }

        }
    }

    @Override
    /**
     * Gets the hardness of block at the given coordinates in the given world, relative to the ability of the given
     * EntityPlayer.
     */
    public float getPlayerRelativeBlockHardness(EntityPlayer entityPlayer, World world, BlockPos pos)
    {
        /* Don't damage block if holding Carpenter's tool. */

        ItemStack itemStack = entityPlayer.getHeldItem();

        if (itemStack != null) {
            Item item = itemStack.getItem();
            if (item instanceof ICarpentersHammer || item instanceof ICarpentersChisel) {
                return -1;
            }
        }

        /* Return block hardness of cover. */

        TEBase TE = getTileEntity(world, pos);

        if (TE != null) {
            return ForgeHooks.blockStrength(BlockProperties.toBlockState(BlockProperties.getCover(TE, 6)), entityPlayer, world, pos);
        } else {
            return super.getPlayerRelativeBlockHardness(entityPlayer, world, pos);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess blockAccess, BlockPos pos, int renderPass)
    {
        TEBase TE = getTileEntity(blockAccess, pos);
        if (TE != null)
        {
            ItemStack itemStack = BlockProperties.getCover(TE, 6);
            IBlockState coverstate = BlockProperties.toBlockState(itemStack);
            if (!(coverstate.getBlock() instanceof BlockCoverable)) {
                return coverstate.getBlock() instanceof IWrappableBlock ? ((IWrappableBlock)coverstate.getBlock()).getColorMultiplier(blockAccess, pos, coverstate) : coverstate.getBlock().colorMultiplier(blockAccess, pos);
            }
        }

        return super.colorMultiplier(blockAccess, pos, renderPass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: world, pos, side
     */
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        // Side checks in out-of-range areas will crash
        if (pos.getY() > 0 && pos.getY() < ((World) blockAccess).getHeight())
        {
            TEBase TE = getTileEntity(blockAccess, pos);
            if (TE != null) {
                EnumFacing side_adj = side.getOpposite();

                TEBase TE_adj = (TEBase) blockAccess.getTileEntity(pos);
                TEBase TE_src = (TEBase) blockAccess.getTileEntity(pos.add(side_adj.getFrontOffsetX(), side_adj.getFrontOffsetY(), side_adj.getFrontOffsetZ()));

                if (TE_adj.getBlockType().isSideSolid(blockAccess, pos, side_adj) == TE_src.getBlockType().isSideSolid(blockAccess, pos.add(side_adj.getFrontOffsetX(), side_adj.getFrontOffsetY(), side_adj.getFrontOffsetZ()), side)) {

                    if (shareFaces(TE_adj, TE_src, side_adj, side)) {

                        IBlockState block_adj = BlockProperties.toBlockState(BlockProperties.getCover(TE_adj, 6));
                        IBlockState block_src = BlockProperties.toBlockState(BlockProperties.getCover(TE_src, 6));

                        if (!TE_adj.hasAttribute(TE.ATTR_COVER[6])) {
                            return TE_src.hasAttribute(TE.ATTR_COVER[6]);
                        } else {
                            if (!TE_src.hasAttribute(TE.ATTR_COVER[6]) && block_adj.getBlock().getBlockLayer().ordinal() == 0) {
                                return !block_adj.getBlock().isOpaqueCube();
                            } else if (TE_src.hasAttribute(TE.ATTR_COVER[6]) && block_src.getBlock().isOpaqueCube() == block_adj.getBlock().isOpaqueCube() && block_src.getBlock().getBlockLayer().ordinal() == block_adj.getBlock().getBlockLayer().ordinal()) {
                                return false;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return super.shouldSideBeRendered(blockAccess, pos, side);
    }

    @Override
    /**
     * Determines if this block should render in this pass.
     */
    public boolean canRenderInLayer(EnumWorldBlockLayer layer)
    {
        ForgeHooksClient.setRenderPass(layer.ordinal());
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns which pass this block be rendered on. 0 for solids and 1 for alpha.
     */
    public EnumWorldBlockLayer getBlockLayer()
    {
        /*
         * Alpha properties of block or cover depend on this returning a value
         * of 1, so it's the default value.  However, when rendering in player
         * hand we'll encounter sorting artifacts, and thus need to enforce
         * opaque rendering, or 0.
         */

        if (ForgeHooksClient.getWorldRenderPass() < 0) {
            return EnumWorldBlockLayer.SOLID;
        } else {
            return EnumWorldBlockLayer.CUTOUT_MIPPED;
        }
    }

    /**
     * Returns whether two blocks share faces.
     * Primarily for slopes, stairs and slabs.
     */
    protected boolean shareFaces(TEBase TE_adj, TEBase TE_src, EnumFacing side_adj, EnumFacing side_src)
    {
        return TE_adj.getBlockType().isSideSolid(TE_adj.getWorld(), TE_adj.getPos(), side_adj) &&
               TE_src.getBlockType().isSideSolid(TE_src.getWorld(), TE_src.getPos(), side_src);
    }

    @Override
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            return false;
        }

        if (FeatureRegistry.enableRoutableFluids) {
            Class<?> clazz = FancyFluidsHelper.getCallerClass();
            if (clazz != null) {
                for (Class clazz1 : FancyFluidsHelper.liquidClasses) {
                    if (clazz.isAssignableFrom(clazz1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Should block use the brightest neighbor light value as its own
     */
    @Override
    public boolean getUseNeighborBrightness()
    {
        return true;
    }

    @Override
    /**
     * Called whenever the block is added into the world. Args: world, pos
     */
    public void onBlockAdded(World world, BlockPos pos, IBlockState state)
    {
        world.setTileEntity(pos, createNewTileEntity(world, 0));
        updateLightValue(world, pos);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return this.createNewCarpentersTile(world, metadata);
    }

    public abstract TileEntity createNewCarpentersTile(World world, int metadata);

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    /**
     * This method is configured on as as-needed basis.
     * It's calling order is not guaranteed.
     */
    protected void preOnBlockClicked(TEBase TE, World world, BlockPos pos, EntityPlayer entityPlayer, ActionResult actionResult) {}

    /**
     * Called before cover or decoration checks are performed.
     */
    protected void preOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ, ActionResult actionResult) {}

    /**
     * Called if cover and decoration checks have been performed but
     * returned no changes.
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ, ActionResult actionResult) {}

    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer)
    {
        return false;
    }

    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer)
    {
        return false;
    }

    protected void damageItemWithChance(World world, EntityPlayer entityPlayer)
    {
        Item item = entityPlayer.getCurrentEquippedItem().getItem();

        if (item instanceof ICarpentersHammer) {
            ((ICarpentersHammer) item).onHammerUse(world, entityPlayer);
        } else if (item instanceof ICarpentersChisel) {
            ((ICarpentersChisel) item).onChiselUse(world, entityPlayer);
        }
    }

    /**
     * Returns whether side of block supports a cover.
     */
    protected boolean canCoverSide(TEBase TE, World world, BlockPos pos, int side)
    {
        return side == 6;
    }

    /**
     * Allows a tile entity called during block activation to be changed before
     * altering attributes like cover, dye, overlay, etc.
     * <p>
     * Primarily offered for the garage door, when open, to swap the top piece
     * with the bottom piece for consistency.
     *
     * @param  TE the originating {@link TEBase}
     * @return a swapped in {@link TEBase}, or the passed in {@link TEBase}
     */
    protected TEBase getTileEntityForBlockActivation(TEBase TE)
    {
        return TE;
    }

}
