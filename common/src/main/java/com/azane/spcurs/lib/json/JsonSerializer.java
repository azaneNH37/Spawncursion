package com.azane.spcurs.lib.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class JsonSerializer
{
    public static final Type LIST_JOBJ = new TypeToken<List<JsonObject>>(){}.getType();

    private JsonSerializer(){}

    public static Gson GSON_NORMAL = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
        .registerTypeAdapter(LIST_JOBJ,new JObjListDeserializer())
        .create();

    public static ResourceLocation getAsResourceLocation(JsonObject object, String key, @Nullable ResourceLocation fallback) {
        if (object.has(key)) {
            return new ResourceLocation(GsonHelper.convertToString(object.get(key), key));
        } else {
            return fallback;
        }
    }

    public static class JObjListDeserializer implements JsonDeserializer<List<JsonObject>>{

        @Override
        public List<JsonObject> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
        {
            return jsonElement.getAsJsonArray().asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).toList();
        }
    }
}
