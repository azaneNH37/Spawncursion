package com.azane.spcurs.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.azane.spcurs.SpcursMod;

@Mod(SpcursMod.MOD_ID)
public final class SpcursModForge
{
    public SpcursModForge() {

        // Run our common setup.
        SpcursMod.init();
    }
}
