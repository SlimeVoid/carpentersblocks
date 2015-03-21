package com.carpentersblocks.util.handler;

import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.network.ICarpentersPacket;
import com.carpentersblocks.network.PacketActivateBlock;
import com.carpentersblocks.network.PacketEnrichPlant;
//import com.carpentersblocks.network.PacketSlopeSelect;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketHandler {

    private final static List<Class> packetCarrier;
    static {
        packetCarrier = new ArrayList<Class>();
        packetCarrier.add(PacketEnrichPlant.class);
        //packetCarrier.add(PacketSlopeSelect.class);
        packetCarrier.add(PacketActivateBlock.class);
    }

    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) throws IOException
    {
        ByteBufInputStream bbis = new ByteBufInputStream(event.packet.payload());
        EntityPlayer entityPlayer = ((NetHandlerPlayServer) event.handler).playerEntity;
        int packetId = bbis.readInt();

        try {
            ICarpentersPacket packetClass = (ICarpentersPacket) packetCarrier.get(packetId).newInstance();
            packetClass.processData(entityPlayer, bbis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bbis.close();
    }

    public static void sendPacketToServer(ICarpentersPacket packet)
    {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(packetCarrier.indexOf(packet.getClass()));
        PacketBuffer packetBuffer = new PacketBuffer(buffer);;

        try {
            packet.appendData(buffer);
        } catch (IOException e) { }

        CarpentersBlocks.channel.sendToServer(new FMLProxyPacket(new C17PacketCustomPayload(CarpentersBlocks.MODID, packetBuffer)));
    }

}
