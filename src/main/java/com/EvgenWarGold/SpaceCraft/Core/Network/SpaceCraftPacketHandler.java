package com.EvgenWarGold.SpaceCraft.Core.Network;

import com.EvgenWarGold.SpaceCraft.Api.Network.IPacket;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;

@ChannelHandler.Sharable
public class SpaceCraftPacketHandler extends SimpleChannelInboundHandler<IPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception {
        final INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        final EntityPlayer player = SpaceCraft.proxy.getPlayerFromNetHandler(netHandler);

        switch (FMLCommonHandler.instance().getEffectiveSide()) {
            case CLIENT:
                msg.handleClientSide(player);
                break;
            case SERVER:
                msg.handleServerSide(player);
                break;
            default:
                break;
        }
    }
}
