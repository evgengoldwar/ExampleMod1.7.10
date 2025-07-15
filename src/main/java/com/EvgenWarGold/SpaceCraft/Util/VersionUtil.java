package com.EvgenWarGold.SpaceCraft.Util;

import com.EvgenWarGold.SpaceCraft.Core.Obfuscation.MethodObfuscationEntry;
import com.EvgenWarGold.SpaceCraft.Core.Obfuscation.ObfuscationEntry;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;

public class VersionUtil {
    private static boolean deobfuscated = true;
    private static final HashMap<Integer, Object> reflectionCache = Maps.newHashMap();
    private static final HashMap<String, ObfuscationEntry> nodemap = Maps.newHashMap();
    private static final String KEY_CLASS_NBT_SIZE_TRACKER = "nbtSizeTracker";
    private static final String KEY_CLASS_COMPRESSED_STREAM_TOOLS = "compressedStreamTools";
    private static final String KEY_METHOD_PLAYER_FOR_NAME = "getPlayerForUsername";
    private static final String KEY_METHOD_DECOMPRESS_NBT = "decompress";

    static {
        try {
            deobfuscated = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        } catch (final Exception e) {
            e.printStackTrace();
        }

        nodemap.put(
            KEY_CLASS_COMPRESSED_STREAM_TOOLS,
            new ObfuscationEntry("net/minecraft/nbt/CompressedStreamTools"));
        nodemap.put(KEY_CLASS_NBT_SIZE_TRACKER, new ObfuscationEntry("net/minecraft/nbt/NBTSizeTracker"));
        nodemap.put(KEY_METHOD_DECOMPRESS_NBT, new MethodObfuscationEntry("func_152457_a", "func_152457_a", ""));
        nodemap.put(KEY_METHOD_PLAYER_FOR_NAME, new MethodObfuscationEntry("func_152612_a", "func_152612_a", ""));
    }

    public static NBTTagCompound decompressNBT(byte[] compressedNBT) {
        try {
            Class<?> c0 = (Class<?>) reflectionCache.get(4);
            Method m = (Method) reflectionCache.get(6);
            if (c0 == null) {
                c0 = Class.forName(getNameDynamic(KEY_CLASS_NBT_SIZE_TRACKER).replace('/', '.'));
                reflectionCache.put(4, c0);
            }
            if (m == null) {
                final Class<?> c = Class
                    .forName(getNameDynamic(KEY_CLASS_COMPRESSED_STREAM_TOOLS).replace('/', '.'));
                m = c.getMethod(getNameDynamic(KEY_METHOD_DECOMPRESS_NBT), byte[].class, c0);
                reflectionCache.put(6, m);
            }
            final Object nbtSizeTracker = c0.getConstructor(long.class).newInstance(2097152L);
            return (NBTTagCompound) m.invoke(null, compressedNBT, nbtSizeTracker);
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static String getNameDynamic(String keyName) {
        try {
            if (deobfuscated) {
                return getName(keyName);
            }
            return getObfName(keyName);
        } catch (final NullPointerException e) {
            System.err.println("Could not find key: " + keyName);
            throw e;
        }
    }

    public static EntityPlayerMP getPlayerForUsername(MinecraftServer server, String username) {
        try {
            Method m = (Method) reflectionCache.get(12);

            if (m == null) {
                final Class<?> c = server.getConfigurationManager().getClass();
                m = c.getMethod(getNameDynamic(KEY_METHOD_PLAYER_FOR_NAME), String.class);
                reflectionCache.put(12, m);
            }

            return (EntityPlayerMP) m.invoke(server.getConfigurationManager(), username);
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    public static GameProfile constructGameProfile(UUID uuid, String strName) {
        try {
            Class<?> c = (Class<?>) reflectionCache.get(19);
            if (c == null) {
                c = Class.forName("com.mojang.authlib.GameProfile");
                reflectionCache.put(19, c);
            }
            return (GameProfile) c.getConstructor(UUID.class, String.class).newInstance(uuid, strName);
        } catch (final Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    private static String getName(String keyName) {
        return nodemap.get(keyName).name;
    }

    private static String getObfName(String keyName) {
        return nodemap.get(keyName).obfuscatedName;
    }
}
