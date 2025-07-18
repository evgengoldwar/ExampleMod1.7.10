package com.EvgenWarGold.SpaceCraft.Core.Network;

import com.EvgenWarGold.SpaceCraft.Api.Network.IPacket;
import com.EvgenWarGold.SpaceCraft.Api.Network.IPacketReceiver;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;

public class PacketDynamic implements IPacket {
    private int type;
    private int dimID;
    private Object[] data;
    private ArrayList<Object> sendData;

    public PacketDynamic() {}

    public PacketDynamic(Entity entity) {
        assert entity instanceof IPacketReceiver : "Entity does not implement " + IPacketReceiver.class.getSimpleName();
        this.type = 0;
        this.dimID = entity.worldObj.provider.dimensionId;
        this.data = new Object[] { entity.getEntityId() };
        this.sendData = new ArrayList<>();
        ((IPacketReceiver) entity).getNetworkedData(this.sendData);
    }

    public PacketDynamic(TileEntity tile) {
        assert tile instanceof IPacketReceiver
            : "TileEntity does not implement " + IPacketReceiver.class.getSimpleName();
        this.type = 1;
        this.dimID = tile.getWorldObj().provider.dimensionId;
        this.data = new Object[] { tile.xCoord, tile.yCoord, tile.zCoord };
        this.sendData = new ArrayList<>();
        ((IPacketReceiver) tile).getNetworkedData(this.sendData);
    }

    @Override
    public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        buffer.writeInt(this.type);
        buffer.writeInt(this.dimID);

        switch (this.type) {
            case 0:
                buffer.writeInt((Integer) this.data[0]);
                break;
            case 1:
                buffer.writeInt((Integer) this.data[0]);
                buffer.writeInt((Integer) this.data[1]);
                buffer.writeInt((Integer) this.data[2]);
                break;
        }

        try {
            NetworkUtil.encodeData(buffer, this.sendData);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        this.type = buffer.readInt();
        this.dimID = buffer.readInt();

        World world = SpaceCraft.proxy.getWorldForID(this.dimID);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            world = MinecraftServer.getServer().worldServerForDimension(this.dimID);
        }

        if (world == null) {
            FMLLog.severe("Failed to get world for dimension ID: " + this.dimID);
        }

        switch (this.type) {
            case 0:
                this.data = new Object[1];
                this.data[0] = buffer.readInt();

                if (world != null) {
                    final Entity entity = world.getEntityByID((Integer) this.data[0]);

                    if (entity instanceof IPacketReceiver && buffer.readableBytes() > 0) {
                        ((IPacketReceiver) entity).decodePacketdata(buffer);
                    }
                }

                break;
            case 1:
                this.data = new Object[3];
                this.data[0] = buffer.readInt();
                this.data[1] = buffer.readInt();
                this.data[2] = buffer.readInt();

                if (world != null) {
                    final TileEntity tile = world
                        .getTileEntity((Integer) this.data[0], (Integer) this.data[1], (Integer) this.data[2]);

                    if (tile instanceof IPacketReceiver) {
                        ((IPacketReceiver) tile).decodePacketdata(buffer);
                    }
                }

                break;
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        this.handleData(Side.CLIENT, player);
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        this.handleData(Side.SERVER, player);
    }

    private void handleData(Side side, EntityPlayer player) {
        switch (this.type) {
            case 0:
                final Entity entity = player.worldObj.getEntityByID((Integer) this.data[0]);

                if (entity instanceof IPacketReceiver) {
                    ((IPacketReceiver) entity).handlePacketData(side, player);

                    // Treat any packet received by a server from a client as an update request
                    // specifically to that
                    // client
                    if (side == Side.SERVER && player instanceof EntityPlayerMP) {
                        SpaceCraft.packetPipeline.sendTo(new PacketDynamic(entity), (EntityPlayerMP) player);
                    }
                }

                break;
            case 1:
                final TileEntity tile = player.worldObj
                    .getTileEntity((Integer) this.data[0], (Integer) this.data[1], (Integer) this.data[2]);

                if (tile instanceof IPacketReceiver) {
                    ((IPacketReceiver) tile).handlePacketData(side, player);

                    // Treat any packet received by a server from a client as an update request
                    // specifically to that
                    // client
                    if (side == Side.SERVER && player instanceof EntityPlayerMP) {
                        SpaceCraft.packetPipeline.sendTo(new PacketDynamic(tile), (EntityPlayerMP) player);
                    }
                }

                break;
        }
    }
}
