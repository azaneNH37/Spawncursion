package com.azane.spcurs.registry;

import com.azane.spcurs.block.entity.NormalSpcursBlockEntity;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BlockEntityRegistries
{
    public static final BlockEntityType<NormalSpcursBlockEntity> NORMAL_SPCURS;

    static {
        NORMAL_SPCURS = register("normal_spcurs",NormalSpcursBlockEntity::new,BlockRegistries.NORMAL_SPCURS);
    }

    @ExpectPlatform
    private static <T extends BlockEntity> BlockEntityType<T> register(String key, BiFunction<BlockPos, BlockState,T> entitySupply, Block... validBlocks) {

        throw new AssertionError();
    }
}
