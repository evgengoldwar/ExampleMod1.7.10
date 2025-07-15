package com.EvgenWarGold.SpaceCraft.Api.Network;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public interface IPacketReceiver {
    void getNetworkedData(ArrayList<Object> sendData);

    void decodePacketdata(ByteBuf buffer);

    void handlePacketData(Side side, EntityPlayer player);
}
