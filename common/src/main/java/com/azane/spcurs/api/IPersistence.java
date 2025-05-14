package com.azane.spcurs.api;

import net.minecraft.nbt.CompoundTag;

public interface IPersistence
{
    void load(CompoundTag tag);

    default CompoundTag saveAll()
    {
        CompoundTag compoundTag = new CompoundTag();
        save(compoundTag);
        return compoundTag;
    }
    void save(CompoundTag tag);
}
