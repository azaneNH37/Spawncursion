package com.azane.spcurs.lib;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class TagSerializeHelper
{
    private TagSerializeHelper(){}

    public static <T> CompoundTag serialize(Codec<T> CODEC,T data)
    {
        return CODEC.encodeStart(NbtOps.INSTANCE,data).result().map(tag -> (CompoundTag)(tag)).orElse(new CompoundTag());
    }
    public static <T> Optional<T> deserialize(Codec<T> CODEC, CompoundTag nbt)
    {
        return CODEC.decode(NbtOps.INSTANCE,nbt).result().map(Pair::getFirst);
    }
    public static <T> ListTag serializeList(Codec<T> CODEC,List<T> data)
    {
        return Codec.list(CODEC).encodeStart(NbtOps.INSTANCE,data).result().map(tag -> (ListTag)(tag)).orElse(new ListTag());
    }
    public static <T> Optional<List<T>> deserializeList(Codec<T> CODEC,ListTag nbt)
    {
        return Codec.list(CODEC).decode(NbtOps.INSTANCE,nbt).result().map(Pair::getFirst);
    }
}
