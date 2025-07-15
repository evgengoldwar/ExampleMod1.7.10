package com.EvgenWarGold.SpaceCraft.Core.Command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandDimTp extends CommandBase {

    @Override
    public String getCommandName() {
        return "dimtp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dimtp <dimensionId> [x] [y] [z]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) throw new WrongUsageException(getCommandUsage(sender));

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        int dim = parseInt(sender, args[0]);
        double x = args.length > 1 ? parseDouble(sender, args[1]) : player.posX;
        double y = args.length > 2 ? parseDouble(sender, args[2]) : player.posY;
        double z = args.length > 3 ? parseDouble(sender, args[3]) : player.posZ;

        if (player.dimension != dim) {
            player.travelToDimension(dim);
            player.setPositionAndUpdate(x, y, z);
        } else {
            player.setPositionAndUpdate(x, y, z);
        }
    }
}
