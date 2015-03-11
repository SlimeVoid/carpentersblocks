package com.carpentersblocks.tileentity;

import com.carpentersblocks.block.BlockCoverable;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.handler.DesignHandler;
import com.carpentersblocks.util.protection.IProtected;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public abstract class TEBase extends TileEntity implements IProtected, IUpdatePlayerListBox {

    private static final String TAG_ATTR          = "cbAttribute";
    private static final String TAG_ATTR_LIST     = "cbAttrList";
    private static final String TAG_METADATA      = "cbMetadata";
    private static final String TAG_OWNER         = "cbOwner";
    private static final String TAG_CHISEL_DESIGN = "cbChiselDesign";
    private static final String TAG_DESIGN        = "cbDesign";

    public static final byte[]  ATTR_COVER        = {  0,  1,  2,  3,  4,  5,  6 };
    public static final byte[]  ATTR_DYE          = {  7,  8,  9, 10, 11, 12, 13 };
    public static final byte[]  ATTR_OVERLAY      = { 14, 15, 16, 17, 18, 19, 20 };
    public static final byte    ATTR_ILLUMINATOR  = 21;
    public static final byte    ATTR_PLANT        = 22;
    public static final byte    ATTR_SOIL         = 23;
    public static final byte    ATTR_FERTILIZER   = 24;
    public static final byte    ATTR_UPGRADE      = 25;

    /** Map holding all block attributes. */
    protected Map<Byte, ItemStack> cbAttrMap = new HashMap<Byte, ItemStack>();

    /** Chisel design for each side and base block. */
    protected String[] cbChiselDesign = { "", "", "", "", "", "", "" };

    /** Holds specific block information like facing, states, etc. */
    protected short cbMetadata;

    /** Design name. */
    protected String cbDesign = "";

    /** Owner of tile entity. */
    protected String cbOwner = "";

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        cbAttrMap.clear();

        if (nbt.hasKey("owner")) {
            MigrationHelper.updateMappingsOnRead(this, nbt);
        } else {
            NBTTagList nbttaglist = nbt.getTagList(TAG_ATTR_LIST, 10);
            for (int idx = 0; idx < nbttaglist.tagCount(); ++idx) {
                NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(idx);
                ItemStack tempStack = ItemStack.loadItemStackFromNBT(nbt1);
                tempStack.stackSize = 1; // All ItemStacks pre-3.2.7 DEV R3 stored original stack sizes, reduce them here.
                byte attrId = (byte) (nbt1.getByte(TAG_ATTR) & 255);
                cbAttrMap.put(attrId, tempStack);
            }

            for (int idx = 0; idx < 7; ++idx) {
                cbChiselDesign[idx] = nbt.getString(TAG_CHISEL_DESIGN + "_" + idx);
            }

            cbMetadata = nbt.getShort(TAG_METADATA);
            cbDesign = nbt.getString(TAG_DESIGN);
            cbOwner = nbt.getString(TAG_OWNER);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        NBTTagList itemstack_list = new NBTTagList();

        Iterator iterator = cbAttrMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            NBTTagCompound nbt1 = new NBTTagCompound();
            nbt1.setByte(TAG_ATTR, (Byte) entry.getKey());
            ((ItemStack)entry.getValue()).writeToNBT(nbt1);
            itemstack_list.appendTag(nbt1);
        }
        nbt.setTag(TAG_ATTR_LIST, itemstack_list);

        for (int idx = 0; idx < 7; ++idx) {
            nbt.setString(TAG_CHISEL_DESIGN + "_" + idx, cbChiselDesign[idx]);
        }

        nbt.setShort(TAG_METADATA, cbMetadata);
        nbt.setString(TAG_DESIGN, cbDesign);
        nbt.setString(TAG_OWNER, cbOwner);
    }

    @Override
    /**
     * Overridden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), nbt);
    }

    @Override
    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());

        /*
         * Block.getLightValue() is called often when rendering, requiring
         * expensive tile entity lookups. When data is loaded, update the
         * light value cache for this entity to speed up the render process.
         */
        ((BlockCoverable)getBlockType()).updateLightValue(this.getWorld(), this.getPos());

        if (worldObj.isRemote) {
            worldObj.markBlockForUpdate(this.getPos());
            // TODO: Rename to updateAllLightByTypes
            worldObj.checkLight(this.getPos());
        }
    }

    /**
     * Called from Chunk.setBlockIDWithMetadata, determines if this tile entity should be re-created when the ID, or Metadata changes.
     * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
     *
     * @param world Current world
     * @param pos Original block position
     * @param oldBlock the original Block
     * @param newBlock the new Block
     * @return True to remove the old tile entity, false to keep it in tact {and create a new one if the new values specify to}
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldBlock, IBlockState newBlock)
    {
        /*
         * This is a curious method.
         *
         * Essentially, when doing most block logic server-side, changes
         * to blocks will momentarily "flash" to their default state
         * when rendering client-side.  This is most noticeable when adding
         * or removing covers for the first time.
         *
         * Making the tile entity refresh only when the block is first created
         * is not only reasonable, but fixes this behavior.
         */
        return oldBlock != newBlock;
    }

    @Override
    /**
     * Called when the chunk this TileEntity is on is Unloaded.
     */
    public void onChunkUnload()
    {
        int hash = BlockCoverable.hashCoords(this.getPos());
        BlockCoverable.cache.remove(hash);
    }

    /**
     * Sets owner of tile entity.
     */
    @Override
    public void setOwner(UUID uuid)
    {
        cbOwner = uuid.toString();
        markDirty();
    }

    @Override
    public String getOwner()
    {
        return cbOwner;
    }

    public boolean hasAttribute(byte attrId)
    {
        return cbAttrMap.containsKey(attrId);
    }

    public ItemStack getAttribute(byte attrId)
    {
        return cbAttrMap.get(attrId);
    }

    public ItemStack getAttributeForDrop(byte attrId)
    {
        ItemStack itemStack = cbAttrMap.get(attrId);

        // If cover, check for rotation and restore default metadata
        if (attrId <= ATTR_COVER[6]) {
            setDefaultMetadata(itemStack);
        }

        return itemStack;
    }

    /**
     * Will restore cover to default state before returning {@link ItemStack}.
     * <p>
     * Corrects log rotation, among other things.
     *
     * @param  itemStack the {@link ItemStack}
     * @return the cover {@link ItemStack} in it's default state
     */
    private ItemStack setDefaultMetadata(ItemStack itemStack)
    {
        IBlockState block = BlockProperties.toBlockState(itemStack);

        // Correct rotation metadata before dropping block
        if (BlockProperties.blockRotates(itemStack) || block.getBlock() instanceof BlockDirectional)
        {
            int dmgDrop = block.getBlock().damageDropped(block);
            Item itemDrop = block.getBlock().getItemDropped(block, this.getWorld().rand, /* Fortune */ 0);

            /* Check if block drops itself, and, if so, correct the damage value to the block's default. */

            if (itemDrop != null && itemDrop.equals(itemStack.getItem()) && dmgDrop != itemStack.getItemDamage()) {
                itemStack.setItemDamage(dmgDrop);
            }
        }

        return itemStack;
    }

    public void addAttribute(byte attrId, ItemStack itemStack)
    {
        if (!hasAttribute(attrId) && itemStack != null) {

            ItemStack reducedStack = null;
            if (itemStack != null) {
                reducedStack = ItemStack.copyItemStack(itemStack);
                reducedStack.stackSize = 1;
            }

            cbAttrMap.put(attrId, reducedStack);

            World world = this.getWorld();
            if (world != null) {

                IBlockState block = itemStack == null ? this.getWorld().getBlockState(this.getPos()) : BlockProperties.toBlockState(itemStack);

                if (attrId < 7) {
                    int metadata = itemStack == null ? 0 : itemStack.getItemDamage();
                    if (attrId == ATTR_COVER[6]) {
                        // TODO :: addAttribute
                        // world.setBlockState(this.getPos(), block);//.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata, 0);
                    }
                    world.notifyNeighborsOfStateChange(this.getPos(), block.getBlock());
                } else if (attrId == ATTR_PLANT | attrId == ATTR_SOIL) {
                    world.notifyNeighborsOfStateChange(this.getPos(), block.getBlock());
                }

                if (attrId == ATTR_FERTILIZER) {
                    /* Play sound when fertilizing plants.. though I've never heard it before. */
                    getWorld().playAuxSFX(2005, this.getPos(), 0);
                }

                world.markBlockForUpdate(this.getPos());

            }

            this.markDirty();

        }
    }

    /**
     * Will remove the attribute from map once block drop is complete.
     * <p>
     * Should only be called externally by {@link BlockCoverable#onBlockEventReceived}.
     *
     * @param attrId
     */
    public void onAttrDropped(byte attrId)
    {
        cbAttrMap.remove(attrId);
        this.getWorld().markBlockForUpdate(this.getPos());
        markDirty();
    }

    /**
     * Initiates block drop event, which will remove attribute from tile entity.
     *
     * @param  attrId the attribute ID
     */
    public void createBlockDropEvent(byte attrId)
    {
        this.getWorld().addBlockEvent(this.getPos(), getBlockType(), BlockCoverable.EVENT_ID_DROP_ATTR, attrId);
    }

    public void removeAttributes(int side)
    {
        createBlockDropEvent(ATTR_COVER[side]);
        createBlockDropEvent(ATTR_DYE[side]);
        createBlockDropEvent(ATTR_OVERLAY[side]);

        if (side == 6) {
            createBlockDropEvent(ATTR_ILLUMINATOR);
        }
    }

    /**
     * Returns whether block has pattern.
     */
    public boolean hasChiselDesign(int side)
    {
        return DesignHandler.listChisel.contains(getChiselDesign(side));
    }

    /**
     * Returns pattern.
     */
    public String getChiselDesign(int side)
    {
        return cbChiselDesign[side];
    }

    /**
     * Sets pattern.
     */
    public boolean setChiselDesign(int side, String iconName)
    {
        if (!cbChiselDesign.equals(iconName)) {
            cbChiselDesign[side] = iconName;
            this.getWorld().markBlockForUpdate(this.getPos());
            return true;
        }

        return false;
    }

    public void removeChiselDesign(int side)
    {
        if (!cbChiselDesign.equals("")) {
            cbChiselDesign[side] = "";
            this.getWorld().markBlockForUpdate(this.getPos());
        }
    }

    /**
     * Gets block-specific data.
     *
     * @return the data
     */
    public int getData()
    {
        return cbMetadata & 0xffff;
    }

    /**
     * Sets block-specific data.
     */
    public boolean setData(int data)
    {
        if (data != getData()) {
            cbMetadata = (short) data;
            this.getWorld().markBlockForUpdate(this.getPos());
            return true;
        }

        return false;
    }

    public boolean hasDesign()
    {
        return DesignHandler.getListForType(getBlockDesignType()).contains(cbDesign);
    }

    public String getDesign()
    {
        return cbDesign;
    }

    public boolean setDesign(String name)
    {
        if (!cbDesign.equals(name)) {
            cbDesign = name;
            this.getWorld().markBlockForUpdate(this.getPos());
            return true;
        }

        return false;
    }

    public boolean removeDesign()
    {
        return this.setDesign("");
    }

    public String getBlockDesignType()
    {
        String name = getBlockType().getUnlocalizedName();
        return name.substring(new String("tile.blockCarpenters").length()).toLowerCase();
    }

    public boolean setNextDesign()
    {
        return this.setDesign(DesignHandler.getNext(getBlockDesignType(), cbDesign));
    }

    public boolean setPrevDesign()
    {
        return this.setDesign(DesignHandler.getPrev(getBlockDesignType(), cbDesign));
    }

    /**
     * Sets block metadata without causing a render update.
     * <p>
     * As part of mimicking a cover block, the metadata must be changed
     * to better represent the cover properties.
     * <p>
     * This is normally followed up by calling {@link setMetadataFromCover}.
     */
    public void setMetadata(int metadata)
    {
        // TODO :: Replace setMetadata
        // this.getWorld().setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata, 4);
    }

    /**
     * Restores default metadata for block from base cover.
     */
    public void restoreMetadata()
    {
        // TODO :: Replace restore
        // int metadata = hasAttribute(ATTR_COVER[6]) ? getAttribute(ATTR_COVER[6]).getItemDamage() : 0;
        // this.getWorld().setBlockMetadataWithNotify(this.getPos(), metadata, 4);
    }

}
