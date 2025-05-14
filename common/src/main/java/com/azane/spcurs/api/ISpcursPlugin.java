package com.azane.spcurs.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public interface ISpcursPlugin
{
    default void onSpcursCreate(ServerLevel level, BlockPos blockPos){}

    default void onSpcursDestroy(ServerLevel level,BlockPos blockPos){}

    default void onSpcursActivate(ServerLevel level,BlockPos blockPos){}

    default void onSpcursInactivate(ServerLevel level,BlockPos blockPos){}

    default void onEntityCreate(ServerLevel level, BlockPos blockPos, LivingEntity entity){}

    default void onActiveFixedTick(ServerLevel level,BlockPos blockPos,long fixedTick){}
}
