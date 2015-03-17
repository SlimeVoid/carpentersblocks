package com.carpentersblocks.network;

import com.carpentersblocks.util.EntityLivingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.io.IOException;

public class PacketActivateBlock extends TilePacket {

    private EnumFacing side;

    public PacketActivateBlock() {}

    public PacketActivateBlock(BlockPos pos, EnumFacing side)
    {
        super(pos);
        this.side = side;
    }

    @Override
    public void processData(EntityPlayer entityPlayer, ByteBufInputStream bbis) throws IOException
    {
        super.processData(entityPlayer, bbis);

        ItemStack itemStack = entityPlayer.getHeldItem();
        this.side = EnumFacing.getFront(bbis.readInt());

        IBlockState state = entityPlayer.worldObj.getBlockState(this.pos);
        boolean result = state.getBlock().onBlockActivated(entityPlayer.worldObj, this.pos, state, entityPlayer, side, 1.0F, 1.0F, 1.0F);

        if (!result) {
            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                itemStack.onItemUse(entityPlayer, entityPlayer.worldObj, this.pos, this.side, 1.0F, 1.0F, 1.0F);
                EntityLivingUtil.decrementCurrentSlot(entityPlayer);
            }
        }
    }

    @Override
    public void appendData(ByteBuf buffer) throws IOException
    {
        super.appendData(buffer);
        buffer.writeInt(side.getIndex());
    }

}
