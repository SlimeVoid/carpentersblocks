package com.carpentersblocks.util;

import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.api.IWrappableBlock;
import com.carpentersblocks.block.BlockCoverable;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.handler.ChatHandler;
import com.carpentersblocks.util.handler.DyeHandler;
import com.carpentersblocks.util.handler.OverlayHandler;
import com.carpentersblocks.util.registry.FeatureRegistry;
import net.minecraft.block.*;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class BlockProperties {

    public final static SoundType stepSound         = new SoundType(CarpentersBlocks.MODID, 1.0F, 1.0F);
    public final static int       MASK_DEFAULT_ICON = 0x10;

    public static boolean isMetadataDefaultIcon(int metadata)
    {
        return (metadata & MASK_DEFAULT_ICON) > 0;
    }

    /**
     * Adds additional data to unused bits in ItemStack metadata to
     * identify special properties for ItemStack.
     * <p>
     * Tells {@link BlockCoverable} to retrieve block icon rather than
     * default blank icon.
     *
     * @param itemStack
     * @param mask
     */
    public static void prepareItemStackForRendering(ItemStack itemStack)
    {
        if (toBlock(itemStack) instanceof BlockCoverable) {
            itemStack.setItemDamage(itemStack.getItemDamage() | MASK_DEFAULT_ICON);
        }
    }

    /**
     * Checks whether tile entity has attribute.
     * <p>
     * This checks whether tile entity is null before checking
     * attribute to promote code reuse.
     *
     * @param  TE the {@link TEBase} reference
     * @param  attr the block attribute
     * @return <code>true</code> if block has attribute
     */
    public static boolean hasAttribute(TEBase TE, byte attr)
    {
        return TE != null && TE.hasAttribute(attr);
    }

    /**
     * Takes an ItemStack and returns block, or air block if ItemStack
     * does not contain a block.
     */
//    public static Block toBlock(ItemStack itemStack)
//    {
//        if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
//            return Block.getBlockFromItem(itemStack.getItem());
//        } else {
//            return Blocks.air;
//        }
//    }

    /**
     * Takes an ItemStack and returns blockstate, or air blockstate if ItemStack
     * does not contain a block.
     */
    public static IBlockState toBlockState(ItemStack itemStack)
    {
        if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
            return Block.getBlockFromItem(itemStack.getItem()).getStateFromMeta(itemStack.getMetadata());
        } else {
            return Blocks.air.getDefaultState();
        }
    }

    /**
     * Returns depth of side cover.
     */
    public static float getSideCoverDepth(TEBase TE, int side)
    {
        if (side == 1 && TE.hasAttribute(TE.ATTR_COVER[side])) {

            Block block = toBlock(getCover(TE, side));

            if (block.equals(Blocks.snow) || block.equals(Blocks.snow_layer)) {
                return 0.125F;
            }

        }

        return 0.0625F;
    }

    /**
     * Returns whether block rotates based on placement conditions.
     * The blocks that utilize this property are mostly atypical, and
     * must be added manually.
     */
    public static boolean blockRotates(ItemStack itemStack)
    {
        Block block = toBlock(itemStack);

        return block instanceof BlockQuartz ||
               block instanceof BlockRotatedPillar;
    }

    /**
     * Plays block sound.
     * Reduced volume is for damaging a block, versus full volume for placement or destruction.
     */
    public static void playBlockSound(World world, ItemStack itemStack, BlockPos pos, boolean reducedVolume)
    {
        if (itemStack != null) {

            Block block;

            if (itemStack.getItem() instanceof ItemBlock) {
                block = toBlock(itemStack);
            } else {
                block = Blocks.sand;
            }

            SoundType soundType = block.stepSound;
            float volume = (soundType.getVolume() + 1.0F) / (reducedVolume ? 8.0F : 2.0F);
            float pitch = soundType.getFrequency() * 0.8F;

            world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, soundType.getStepSound(), volume, pitch);

        }
    }

    /**
     * Returns cover {@link ItemStack}.
     * <p>
     * If cover {@link ItemStack#hasTagCompound()}, will replace {@link Item} with {@link Blocks#planks}.
     * <p>
     * This is needed to avoid calling properties for covers that have NBTTagCompounds,
     * which may rely on data that does not exist.
     */
    public static ItemStack getCover(TEBase TE, int side)
    {
        ItemStack itemStack = getCoverSafe(TE, side);
        Block block = toBlock(itemStack);

        // IWrappable blocks are assumed safe to return unaltered
        if (block instanceof BlockCoverable || block instanceof IWrappableBlock) {
            return itemStack;
        }

        return block.hasTileEntity(itemStack.getItemDamage()) ? new ItemStack(Blocks.planks) : itemStack;
    }

    /**
     * Returns the cover, or if no cover exists, will return the calling block type.
     *
     * @param  TE the {@link TEBase}
     * @param  side the side
     * @return the {@link ItemStack}
     */
    public static ItemStack getCoverSafe(TEBase TE, int side)
    {
        ItemStack itemStack = TE.getAttribute(TE.ATTR_COVER[side]);
        return itemStack != null ? itemStack : new ItemStack(TE.getBlockType());
    }

    /**
     * Returns whether block is a cover.
     */
    public static boolean isCover(ItemStack itemStack)
    {
        if (itemStack.getItem() instanceof ItemBlock && !isOverlay(itemStack)) {

            Block block = toBlock(itemStack);

            return block.renderAsNormalBlock() ||
                   block instanceof BlockSlab ||
                   block instanceof BlockPane ||
                   block instanceof BlockBreakable ||
                   FeatureRegistry.coverExceptions.contains(itemStack.getDisplayName()) ||
                   FeatureRegistry.coverExceptions.contains(ChatHandler.getDefaultTranslation(itemStack));

        }

        return false;
    }

    /**
     * Checks {@link OreDictionary} to determine if {@link ItemStack} contains
     * a dustGlowstone ore name.
     *
     * @return <code>true</code> if {@link ItemStack} contains dustGlowstone ore name
     */
    public static boolean isIlluminator(ItemStack itemStack)
    {
        if (itemStack != null) {
            for (int Id : OreDictionary.getOreIDs(itemStack)) {
                if (OreDictionary.getOreName(Id).equals("dustGlowstone")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if ItemStack is a dye.
     */
    public static boolean isDye(ItemStack itemStack, boolean allowWhite)
    {
        return itemStack.getItem() != null &&
               DyeHandler.isDye(itemStack, allowWhite);
    }

    /**
     * Returns whether ItemStack contains a valid overlay item or block.
     */
    public static boolean isOverlay(ItemStack itemStack)
    {
        return OverlayHandler.overlayMap.containsKey(itemStack.getDisplayName()) ||
               OverlayHandler.overlayMap.containsKey(ChatHandler.getDefaultTranslation(itemStack));
    }

    /**
     * Gets the first matching ore dictionary entry from the provided ore names.
     *
     * @param  itemStack the {@link ItemStack}
     * @param  name the OreDictionary name to check against
     * @return the first matching OreDictionary name, otherwise blank string
     */
    public static String getOreDictMatch(ItemStack itemStack, String ... name)
    {
        if (itemStack != null) {
            for (int Id : OreDictionary.getOreIDs(itemStack)) {
                for (String oreName : name) {
                    if (OreDictionary.getOreName(Id).equals(oreName)) {
                        return oreName;
                    }
                }
            }
        }

        return "";
    }

}
