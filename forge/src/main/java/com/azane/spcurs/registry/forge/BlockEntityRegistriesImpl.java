package com.azane.spcurs.registry.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.BiFunction;

import static com.azane.spcurs.SpcursMod.MOD_ID;

public class BlockEntityRegistriesImpl
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static <T extends BlockEntity> BlockEntityType<T> register(String key, BiFunction<BlockPos, BlockState, T> entitySupply, Block... validBlocks)
    {
        return BLOCK_ENTITIES.register(key, () -> BlockEntityType.Builder.of(entitySupply::apply, validBlocks).build(null)).get();
    }
}
