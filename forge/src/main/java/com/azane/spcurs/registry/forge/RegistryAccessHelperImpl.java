package com.azane.spcurs.registry.forge;

import com.azane.spcurs.SpcursMod;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class RegistryAccessHelperImpl
{
    public static RegistryAccess gainRealtimeAccess()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if(server == null)
        {
            SpcursMod.LOGGER.error("Wrongly invoke server logic about RegistryAccess!");
            throw new IllegalStateException("未在服务端环境中调用！");
        }
        return server.registryAccess();
    }
}
