package com.azane.spcurs.registry;

import com.azane.spcurs.SpcursMod;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.RegistryAccess;

public final class RegistryAccessHelper
{
    private static RegistryAccess registryAccess = null;

    private RegistryAccessHelper(){}

    public static void updateAccess(RegistryAccess access)
    {
        registryAccess = access;
    }
    public static RegistryAccess gainAccess()
    {
        if(registryAccess == null)
            SpcursMod.LOGGER.error("No registry access!");
        return registryAccess;
    }

    @ExpectPlatform
    public static RegistryAccess gainRealtimeAccess()
    {
        throw new AssertionError();
    }
}
