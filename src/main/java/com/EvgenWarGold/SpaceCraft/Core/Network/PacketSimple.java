package com.EvgenWarGold.SpaceCraft.Core.Network;

import com.EvgenWarGold.SpaceCraft.Api.Client.Footprint;
import com.EvgenWarGold.SpaceCraft.Api.Network.IPacket;
import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystem;
import com.EvgenWarGold.SpaceCraft.Api.SpaceSystem.SpaceSystemRegistry;
import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import com.EvgenWarGold.SpaceCraft.Util.PlayerUtil;
import com.EvgenWarGold.SpaceCraft.Util.SCLog;
import com.google.common.collect.Lists;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketSimple extends Packet implements IPacket {

    public enum EnumSimplePacket {
        //Server
        S_COMPLETE_SPACE_SYSTEM_BODY_HANDSHAKE(Side.SERVER, String.class),
        //Client
        C_UPDATE_FOOTPRINT_LIST(Side.CLIENT, Long.class, Footprint[].class),
        C_FOOTPRINTS_REMOVED(Side.CLIENT, Long.class, BlockVec3.class),
        C_GET_SPACE_SYSTEM_BODY_LIST(Side.CLIENT);

        private final Side targetSide;
        private final Class<?>[] decodeAs;

        EnumSimplePacket(Side targetSide, Class<?>... decodeAs) {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide() {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses() {
            return this.decodeAs;
        }
    }

    private EnumSimplePacket type;

    private List<Object> data;
    private static String spamCheckString;

    public PacketSimple() {}

    public PacketSimple(EnumSimplePacket packetType, Object[] data) {
        this(packetType, Arrays.asList(data));
    }

    public PacketSimple(EnumSimplePacket packetType, List<Object> data) {
        if (packetType.getDecodeClasses().length != data.size()) {
            SCLog.info("Simple Packet Core found data length different than packet type");
            new RuntimeException().printStackTrace();
        }

        this.type = packetType;
        this.data = data;
    }

    @Override
    public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        buffer.writeInt(this.type.ordinal());

        try {
            NetworkUtil.encodeData(buffer, this.data);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
        this.type = EnumSimplePacket.values()[buffer.readInt()];

        try {
            if (this.type.getDecodeClasses().length > 0) {
                this.data = NetworkUtil.decodeData(this.type.getDecodeClasses(), buffer);
            }
            if (buffer.readableBytes() > 0) {
                SCLog.severe("Galacticraft packet length problem for packet type " + this.type.toString());
            }
        } catch (final Exception e) {
            System.err.println(
                "[Galacticraft] Error handling simple packet type: " + this.type.toString()
                    + " "
                    + buffer.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        switch (this.type) {
            case C_UPDATE_FOOTPRINT_LIST:
                final List<Footprint> printList = new ArrayList<>();
                final long chunkKey = (Long) this.data.get(0);
                for (int i = 1; i < this.data.size(); i++) {
                    final Footprint print = (Footprint) this.data.get(i);
                    if (!print.owner.equals(player.getCommandSenderName())) {
                        printList.add(print);
                    }
                }
                ClientProxy.footprintRenderer.setFootprints(chunkKey, printList);
                break;
            case C_FOOTPRINTS_REMOVED:
                final long chunkKey0 = (Long) this.data.get(0);
                final BlockVec3 position = (BlockVec3) this.data.get(1);
                final List<Footprint> footprintList = ClientProxy.footprintRenderer.footprints.get(chunkKey0);
                final List<Footprint> toRemove = new ArrayList<>();

                if (footprintList != null) {
                    for (final Footprint footprint : footprintList) {
                        if (footprint.position.x > position.x && footprint.position.x < position.x + 1
                            && footprint.position.z > position.z
                            && footprint.position.z < position.z + 1) {
                            toRemove.add(footprint);
                        }
                    }
                }

                if (!toRemove.isEmpty()) {
                    footprintList.removeAll(toRemove);
                    ClientProxy.footprintRenderer.footprints.put(chunkKey0, footprintList);
                }
                break;
            case C_GET_SPACE_SYSTEM_BODY_LIST:
                String str = "";

                for (final SpaceSystem cBody : SpaceSystemRegistry.getRegisteredPlanets().values()) {
                    str = str.concat(cBody.getUnlocalizedName() + ";");
                }
                SpaceCraft.packetPipeline.sendToServer(
                    new PacketSimple(EnumSimplePacket.S_COMPLETE_SPACE_SYSTEM_BODY_HANDSHAKE, new Object[] { str }));
                break;
        }
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        final EntityPlayerMP playerBase = PlayerUtil.getPlayerBaseServerFromPlayer(player, false);
        switch (this.type) {
            case S_COMPLETE_SPACE_SYSTEM_BODY_HANDSHAKE:
                final String completeList = (String) this.data.get(0);
                final List<String> clientObjects = Arrays.asList(completeList.split(";"));
                final List<String> serverObjects = Lists.newArrayList();
                String missingObjects = "";

                for (final SpaceSystem cBody : SpaceSystemRegistry.getRegisteredPlanets().values()) {
                    serverObjects.add(cBody.getUnlocalizedName());
                }

                for (final String str : serverObjects) {
                    if (!clientObjects.contains(str)) {
                        missingObjects = missingObjects.concat(str + "\n");
                    }
                }

                if (missingObjects.length() > 0) {
                    playerBase.playerNetServerHandler
                        .kickPlayerFromServer("Missing SpaceCraft SpaceSystem Objects:\n\n " + missingObjects);
                }

                break;
        }
    }

    @Override
    public void readPacketData(PacketBuffer var1) {
        this.decodeInto(null, var1);
    }

    @Override
    public void writePacketData(PacketBuffer var1) {
        this.encodeInto(null, var1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void processPacket(INetHandler var1) {
//        if (this.type != EnumSimplePacket.C_UPDATE_SPACESTATION_LIST
//            && this.type != EnumSimplePacket.C_UPDATE_PLANETS_LIST
//            && this.type != EnumSimplePacket.C_UPDATE_CONFIGS) {
//            return;
//        }
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            this.handleClientSide(FMLClientHandler.instance().getClientPlayerEntity());
        }
    }
}
