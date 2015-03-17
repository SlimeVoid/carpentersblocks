package com.carpentersblocks.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import java.io.IOException;

public class TilePacket implements ICarpentersPacket {

    protected BlockPos pos;

    public TilePacket() {}

    /**
     * Creates a packet that passes x, y, z coordinates.
     */
    public TilePacket(BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public void processData(EntityPlayer entityPlayer, ByteBufInputStream bbis) throws IOException
    {
        this.pos = new BlockPos(bbis.readInt(), bbis.readInt(), bbis.readInt());
    }

    @Override
    public void appendData(ByteBuf buffer) throws IOException
    {
        buffer.writeInt(this.pos.getX());
        buffer.writeInt(this.pos.getY());
        buffer.writeInt(this.pos.getZ());
    }

}
