package com.azane.spcurs.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class ReloadListenerRegistry
{
    private ReloadListenerRegistry() {
    }

    public static void register(PackType type, PreparableReloadListener listener) {
        register(type, listener, null);
    }

    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId) {
        register(type, listener, listenerId, List.of());
    }

    @ExpectPlatform
    public static void register(PackType type, PreparableReloadListener listener, @Nullable ResourceLocation listenerId, Collection<ResourceLocation> dependencies) {
        throw new AssertionError();
    }
}
