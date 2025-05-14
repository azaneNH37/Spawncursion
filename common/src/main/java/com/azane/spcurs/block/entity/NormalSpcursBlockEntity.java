package com.azane.spcurs.block.entity;

import com.azane.spcurs.registry.BlockEntityRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class NormalSpcursBlockEntity extends BlockEntity
{
    public NormalSpcursBlockEntity(BlockPos pos, BlockState blockState)
    {
        super(BlockEntityRegistries.NORMAL_SPCURS, pos, blockState);
    }
}
