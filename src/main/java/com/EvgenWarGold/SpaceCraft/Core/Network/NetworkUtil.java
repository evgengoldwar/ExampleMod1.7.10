package com.EvgenWarGold.SpaceCraft.Core.Network;

import com.EvgenWarGold.SpaceCraft.Api.Client.Footprint;
import com.EvgenWarGold.SpaceCraft.Api.Vector.Vector3;
import com.EvgenWarGold.SpaceCraft.Util.SCLog;
import com.EvgenWarGold.SpaceCraft.Util.VersionUtil;
import com.google.common.math.DoubleMath;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class NetworkUtil {
    @SuppressWarnings("unchecked")
    public static void encodeData(ByteBuf buffer, Collection<Object> sendData) throws IOException {
        for (final Object dataValue : sendData) {
            if (dataValue instanceof Integer) {
                buffer.writeInt((Integer) dataValue);
            } else if (dataValue instanceof Float) {
                buffer.writeFloat((Float) dataValue);
            } else if (dataValue instanceof Double) {
                buffer.writeDouble((Double) dataValue);
            } else if (dataValue instanceof Byte) {
                buffer.writeByte((Byte) dataValue);
            } else if (dataValue instanceof Boolean) {
                buffer.writeBoolean((Boolean) dataValue);
            } else if (dataValue instanceof String) {
                ByteBufUtils.writeUTF8String(buffer, (String) dataValue);
            } else if (dataValue instanceof Short) {
                buffer.writeShort((Short) dataValue);
            } else if (dataValue instanceof Long) {
                buffer.writeLong((Long) dataValue);
            } else if (dataValue instanceof NBTTagCompound) {
                NetworkUtil.writeNBTTagCompound((NBTTagCompound) dataValue, buffer);
            } else if (dataValue instanceof Entity) {
                buffer.writeInt(((Entity) dataValue).getEntityId());
            } else if (dataValue instanceof Vector3) {
                buffer.writeDouble(((Vector3) dataValue).x);
                buffer.writeDouble(((Vector3) dataValue).y);
                buffer.writeDouble(((Vector3) dataValue).z);
            } else if (dataValue instanceof byte[]) {
                buffer.writeInt(((byte[]) dataValue).length);
                for (int i = 0; i < ((byte[]) dataValue).length; i++) {
                    buffer.writeByte(((byte[]) dataValue)[i]);
                }
            } else if (dataValue instanceof UUID) {
                buffer.writeLong(((UUID) dataValue).getLeastSignificantBits());
                buffer.writeLong(((UUID) dataValue).getMostSignificantBits());
            } else if (dataValue instanceof Collection) {
                NetworkUtil.encodeData(buffer, (Collection<Object>) dataValue);
            } else if (dataValue instanceof Integer[]array) {
                buffer.writeInt(array.length);

                for (final Integer element : array) {
                    buffer.writeInt(element);
                }
            } else if (dataValue instanceof String[]array) {
                buffer.writeInt(array.length);

                for (final String element : array) {
                    ByteBufUtils.writeUTF8String(buffer, element);
                }
            } else if (dataValue instanceof Footprint[]array) {
                buffer.writeInt(array.length);

                for (final Footprint element : array) {
                    buffer.writeInt(element.dimension);
                    buffer.writeFloat((float) element.position.x);
                    buffer.writeFloat((float) element.position.y + 1);
                    buffer.writeFloat((float) element.position.z);
                    buffer.writeFloat(element.rotation);
                    buffer.writeShort(element.age);
                    ByteBufUtils.writeUTF8String(buffer, element.owner);
                }
            } else {
                if (dataValue == null) {
                    SCLog.severe("Cannot construct PacketSimple with null data, this is a bug.");
                }
                SCLog.info("Could not find data type to encode!: " + dataValue);
            }
        }
    }

    public static ArrayList<Object> decodeData(Class<?>[] types, ByteBuf buffer) {
        final ArrayList<Object> objList = new ArrayList<>();

        for (final Class<?> clazz : types) {
            if (clazz.equals(Integer.class)) {
                objList.add(buffer.readInt());
            } else if (clazz.equals(Float.class)) {
                objList.add(buffer.readFloat());
            } else if (clazz.equals(Double.class)) {
                objList.add(buffer.readDouble());
            } else if (clazz.equals(Byte.class)) {
                objList.add(buffer.readByte());
            } else if (clazz.equals(Boolean.class)) {
                objList.add(buffer.readBoolean());
            } else if (clazz.equals(String.class)) {
                objList.add(ByteBufUtils.readUTF8String(buffer));
            } else if (clazz.equals(Short.class)) {
                objList.add(buffer.readShort());
            } else if (clazz.equals(Long.class)) {
                objList.add(buffer.readLong());
            } else if (clazz.equals(byte[].class)) {
                final byte[] bytes = new byte[buffer.readInt()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buffer.readByte();
                }
                objList.add(bytes);
            } else if (clazz.equals(NBTTagCompound.class)) {
                try {
                    objList.add(NetworkUtil.readNBTTagCompound(buffer));
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else if (clazz.equals(UUID.class)) {
                objList.add(new UUID(buffer.readLong(), buffer.readLong()));
            } else if (clazz.equals(Vector3.class)) {
                objList.add(new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
            } else if (clazz.equals(Integer[].class)) {
                final int size = buffer.readInt();

                for (int i = 0; i < size; i++) {
                    objList.add(buffer.readInt());
                }
            } else if (clazz.equals(String[].class)) {
                final int size = buffer.readInt();

                for (int i = 0; i < size; i++) {
                    objList.add(ByteBufUtils.readUTF8String(buffer));
                }
            } else if (clazz.equals(Footprint[].class)) {
                final int size = buffer.readInt();

                for (int i = 0; i < size; i++) {
                    objList.add(
                        new Footprint(
                            buffer.readInt(),
                            new Vector3(buffer.readFloat(), buffer.readFloat(), buffer.readFloat()),
                            buffer.readFloat(),
                            buffer.readShort(),
                            ByteBufUtils.readUTF8String(buffer)));
                }
            }
        }

        return objList;
    }

    public static Object getFieldValueFromStream(Field field, ByteBuf buffer, World world) throws IOException {
        final Class<?> dataValue = field.getType();

        if (dataValue.equals(int.class)) {
            return buffer.readInt();
        }
        if (dataValue.equals(float.class)) {
            return buffer.readFloat();
        }
        if (dataValue.equals(double.class)) {
            return buffer.readDouble();
        } else if (dataValue.equals(byte.class)) {
            return buffer.readByte();
        } else if (dataValue.equals(boolean.class)) {
            return buffer.readBoolean();
        } else if (dataValue.equals(String.class)) {
            return ByteBufUtils.readUTF8String(buffer);
        } else if (dataValue.equals(short.class)) {
            return buffer.readShort();
        } else if (dataValue.equals(Long.class)) {
            return buffer.readLong();
        } else if (dataValue.equals(NBTTagCompound.class)) {
            return NetworkUtil.readNBTTagCompound(buffer);
        } else if (dataValue.equals(Vector3.class)) {
            return new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        } else if (dataValue.equals(UUID.class)) {
            return new UUID(buffer.readLong(), buffer.readLong());
        } else if (dataValue.equals(byte[].class)) {
            final byte[] bytes = new byte[buffer.readInt()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = buffer.readByte();
            }
            return bytes;
        } else {
            Class<?> c = dataValue;

            while (c != null) {
                if (c.equals(Entity.class)) {
                    return world.getEntityByID(buffer.readInt());
                }

                c = c.getSuperclass();
            }
        }

        throw new NullPointerException("Field type not found: " + field.getType().getSimpleName());
    }

    public static ItemStack readItemStack(ByteBuf buffer) throws IOException {
        ItemStack itemstack = null;
        final short itemID = buffer.readShort();

        if (itemID >= 0) {
            final byte stackSize = buffer.readByte();
            final short meta = buffer.readShort();
            itemstack = new ItemStack(Item.getItemById(itemID), stackSize, meta);
            itemstack.stackTagCompound = NetworkUtil.readNBTTagCompound(buffer);
        }

        return itemstack;
    }

    public static void writeItemStack(ItemStack itemStack, ByteBuf buffer) throws IOException {
        if (itemStack == null) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(Item.getIdFromItem(itemStack.getItem()));
            buffer.writeByte(itemStack.stackSize);
            buffer.writeShort(itemStack.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (itemStack.getItem().isDamageable() || itemStack.getItem().getShareTag()) {
                nbttagcompound = itemStack.stackTagCompound;
            }

            NetworkUtil.writeNBTTagCompound(nbttagcompound, buffer);
        }
    }

    public static NBTTagCompound readNBTTagCompound(ByteBuf buffer) throws IOException {
        final short dataLength = buffer.readShort();

        if (dataLength < 0) {
            return null;
        }
        final byte[] compressedNBT = new byte[dataLength];
        buffer.readBytes(compressedNBT);
        return VersionUtil.decompressNBT(compressedNBT);
    }

    public static void writeNBTTagCompound(NBTTagCompound nbt, ByteBuf buffer) throws IOException {
        if (nbt == null) {
            buffer.writeShort(-1);
        } else {
            final byte[] compressedNBT = CompressedStreamTools.compress(nbt);
            buffer.writeShort((short) compressedNBT.length);
            buffer.writeBytes(compressedNBT);
        }
    }

    public static boolean fuzzyEquals(Object a, Object b) {
        if (a == null != (b == null)) {
            return false;
        }
        if (a == null) {
            return true;
        }
        if (a instanceof Float && b instanceof Float) {
            final float af = (Float) a;
            final float bf = (Float) b;
            return af == bf || Math.abs(af - bf) < 0.01F;
        } else if (a instanceof Double && b instanceof Double) {
            return DoubleMath.fuzzyEquals((Double) a, (Double) b, 0.01);
        } else if (a instanceof Entity a2 && b instanceof Entity b2) {
            return fuzzyEquals(a2.getEntityId(), b2.getEntityId());
        } else if (a instanceof Vector3 a2 && b instanceof Vector3 b2) {
            return fuzzyEquals(a2.x, b2.x) && fuzzyEquals(a2.y, b2.y) && fuzzyEquals(a2.z, b2.z);
        } else {
            return a.equals(b);
        }
    }

    public static Object cloneNetworkedObject(Object a) {
        // We only need to clone mutable objects
        if (a instanceof FluidTank prevTank) {
            FluidStack prevFluid = prevTank.getFluid();
            prevFluid = prevFluid == null ? null : prevFluid.copy();
            return new FluidTank(prevFluid, prevTank.getCapacity());
        }
        return a;
    }
}
