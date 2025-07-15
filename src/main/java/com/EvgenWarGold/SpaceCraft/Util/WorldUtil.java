package com.EvgenWarGold.SpaceCraft.Util;

import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class WorldUtil {

    public static Vector3 getFootprintPosition(World world, float rotation, Vector3 startPosition,
                                               BlockVec3 playerCenter) {
        final Vector3 position = startPosition.clone();
        final float footprintScale = 0.375F;

        int mainPosX = position.intX();
        final int mainPosY = position.intY();
        int mainPosZ = position.intZ();

        // If the footprint is hovering over air...
        final Block b1 = world.getBlock(mainPosX, mainPosY, mainPosZ);
        if (b1 != null && b1.isAir(world, mainPosX, mainPosY, mainPosZ)) {
            position.x += playerCenter.x - mainPosX;
            position.z += playerCenter.z - mainPosZ;

            // If the footprint is still over air....
            final Block b2 = world.getBlock(position.intX(), position.intY(), position.intZ());
            if (b2 != null && b2.isAir(world, position.intX(), position.intY(), position.intZ())) {
                for (final ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                    if (direction != ForgeDirection.DOWN && direction != ForgeDirection.UP) {
                        final Block b3 = world
                            .getBlock(mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ);
                        if (b3 != null && !b3
                            .isAir(world, mainPosX + direction.offsetX, mainPosY, mainPosZ + direction.offsetZ)) {
                            position.x += direction.offsetX;
                            position.z += direction.offsetZ;
                            break;
                        }
                    }
                }
            }
        }

        mainPosX = position.intX();
        mainPosZ = position.intZ();

        final double x0 = Math.sin((45 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x1 = Math.sin((135 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x2 = Math.sin((225 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double x3 = Math.sin((315 - rotation) * Math.PI / 180.0D) * footprintScale + position.x;
        final double z0 = Math.cos((45 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z1 = Math.cos((135 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z2 = Math.cos((225 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;
        final double z3 = Math.cos((315 - rotation) * Math.PI / 180.0D) * footprintScale + position.z;

        final double xMin = Math.min(Math.min(x0, x1), Math.min(x2, x3));
        final double xMax = Math.max(Math.max(x0, x1), Math.max(x2, x3));
        final double zMin = Math.min(Math.min(z0, z1), Math.min(z2, z3));
        final double zMax = Math.max(Math.max(z0, z1), Math.max(z2, z3));

        if (xMin < mainPosX) {
            position.x += mainPosX - xMin;
        }

        if (xMax > mainPosX + 1) {
            position.x -= xMax - (mainPosX + 1);
        }

        if (zMin < mainPosZ) {
            position.z += mainPosZ - zMin;
        }

        if (zMax > mainPosZ + 1) {
            position.z -= zMax - (mainPosZ + 1);
        }

        return position;
    }
}
