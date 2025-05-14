package com.azane.spcurs.registry;

import com.azane.spcurs.block.NormalSpcursBlock;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class BlockRegistries
{
    public static final Block NORMAL_SPCURS;

    static {
        NORMAL_SPCURS = register("normal_spcurs",()->new NormalSpcursBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5f)));
    }

    @ExpectPlatform
    private static Block register(String key, Supplier<? extends Block> blockSupply) {
        throw new AssertionError();
    }
}
