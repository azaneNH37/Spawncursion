package com.azane.spcurs.lib.json;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface IJsonStorage
{
    void setData(Map<ResourceLocation, JsonObject> data);
}
