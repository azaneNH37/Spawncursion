package com.azane.spcurs.lib.json;

import com.azane.spcurs.SpcursMod;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonLoader extends SimplePreparableReloadListener<Map<ResourceLocation, JsonObject>>
{
    private final String FOLDER;
    private final Gson GSON;
    private final IJsonStorage STORAGE;

    public JsonLoader(String folder, IJsonStorage storage, Gson gson)
    {
        this.FOLDER = folder;
        this.STORAGE = storage;
        this.GSON = gson;
    }

    @Override
    protected Map<ResourceLocation, JsonObject> prepare(ResourceManager pResourceManager, ProfilerFiller profiler)
    {
        Map<ResourceLocation, JsonObject> data = new HashMap<>();
        pResourceManager.listResources(FOLDER, rl -> rl.getPath().endsWith(".json"))
            .forEach((resourceLocation,resourc) -> {
                try {
                    List<Resource> resources = pResourceManager.getResourceStack(resourceLocation);
                    for (Resource resource : resources) {
                        try (InputStream stream = resource.open();
                             Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                            JsonObject json = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                            data.put(resourceLocation, json);
                        }
                    }
                } catch (IOException | JsonParseException e) {
                    SpcursMod.LOGGER.error("Failed to load resource: {}", resourceLocation, e);
                }
            });
        return data;
    }

    @Override
    protected void apply(Map<ResourceLocation,JsonObject> prepared, ResourceManager resourceManager, ProfilerFiller profiler)
    {
        STORAGE.setData(prepared);
    }
}
