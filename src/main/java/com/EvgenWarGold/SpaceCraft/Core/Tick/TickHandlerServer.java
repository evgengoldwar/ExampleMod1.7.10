package com.EvgenWarGold.SpaceCraft.Core.Tick;

import com.EvgenWarGold.SpaceCraft.Api.Client.Footprint;
import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3;
import com.EvgenWarGold.SpaceCraft.Api.Vector.BlockVec3Dim;
import com.EvgenWarGold.SpaceCraft.Core.Network.PacketSimple;
import com.EvgenWarGold.SpaceCraft.SpaceCraft;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TickHandlerServer {

    public static Map<Integer, Map<Long, List<Footprint>>> serverFootprintMap = new HashMap<>();
    public static List<BlockVec3Dim> footprintBlockChanges = Lists.newArrayList();
    private static long tickCount;

    public static void restart() {
        TickHandlerServer.serverFootprintMap.clear();
        TickHandlerServer.tickCount = 0L;
    }

    public static void addFootprint(long chunkKey, Footprint print, int dimID) {
        Map<Long, List<Footprint>> footprintMap = TickHandlerServer.serverFootprintMap.get(dimID);
        List<Footprint> footprints;

        if (footprintMap == null) {
            footprintMap = new HashMap<>();
            footprints = new ArrayList<>();
        } else {
            footprints = footprintMap.get(chunkKey);

            if (footprints == null) {
                footprints = new ArrayList<>();
            }
        }

        footprints.add(print);
        footprintMap.put(chunkKey, footprints);
        TickHandlerServer.serverFootprintMap.put(dimID, footprintMap);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        // Prevent issues when clients switch to LAN servers
        if (server == null) {
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            if (TickHandlerServer.tickCount % 100 == 0) {
                final WorldServer[] worlds = server.worldServers;

                for (final WorldServer world : worlds) {
                    final ChunkProviderServer chunkProviderServer = world.theChunkProviderServer;

                    final Map<Long, List<Footprint>> footprintMap = TickHandlerServer.serverFootprintMap
                        .get(world.provider.dimensionId);

                    if (footprintMap != null) {
                        boolean mapChanged = false;

                        if (chunkProviderServer != null) {
                            for (Chunk chunk : chunkProviderServer.loadedChunks) {
                                final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition);

                                final List<Footprint> footprints = footprintMap.get(chunkKey);

                                if (footprints != null) {
                                    final List<Footprint> toRemove = new ArrayList<>();

                                    for (int j = 0; j < footprints.size(); j++) {
                                        footprints.get(j).age += 100;

                                        if (footprints.get(j).age >= Footprint.MAX_AGE) {
                                            toRemove.add(footprints.get(j));
                                        }
                                    }

                                    if (!toRemove.isEmpty()) {
                                        footprints.removeAll(toRemove);
                                    }

                                    footprintMap.put(chunkKey, footprints);
                                    mapChanged = true;

                                    SpaceCraft.packetPipeline
                                        .sendToDimension(
                                            new PacketSimple(
                                                PacketSimple.EnumSimplePacket.C_UPDATE_FOOTPRINT_LIST,
                                                new Object[] { chunkKey,
                                                    footprints.toArray(
                                                        new Footprint[footprints.size()]) }),
                                            world.provider.dimensionId);
                                }
                            }
                        }

                        if (mapChanged) {
                            TickHandlerServer.serverFootprintMap.put(world.provider.dimensionId, footprintMap);
                        }
                    }
                }
            }

            if (!footprintBlockChanges.isEmpty()) {
                for (final BlockVec3Dim targetPoint : footprintBlockChanges) {
                    final WorldServer[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;

                    for (final WorldServer world : worlds) {
                        if (world.provider.dimensionId == targetPoint.dim) {
                            final long chunkKey = ChunkCoordIntPair.chunkXZ2Int(targetPoint.x >> 4, targetPoint.z >> 4);
                            SpaceCraft.packetPipeline.sendToAllAround(
                                new PacketSimple(
                                    PacketSimple.EnumSimplePacket.C_FOOTPRINTS_REMOVED,
                                    new Object[] { chunkKey,
                                        new BlockVec3(targetPoint.x, targetPoint.y, targetPoint.z) }),
                                new NetworkRegistry.TargetPoint(
                                    targetPoint.dim,
                                    targetPoint.x,
                                    targetPoint.y,
                                    targetPoint.z,
                                    50));
                        }
                    }
                }

                footprintBlockChanges.clear();
            }
            TickHandlerServer.tickCount++;

            if (TickHandlerServer.tickCount >= Long.MAX_VALUE) {
                TickHandlerServer.tickCount = 0;
            }
        }
    }
}
