package com.azane.spcurs.spawner;

import com.azane.spcurs.api.ISpcursPlugin;
import com.azane.spcurs.lib.FileNameExtractHelper;
import com.azane.spcurs.lib.json.IJsonStorage;
import com.azane.spcurs.lib.json.JsonSerializer;
import com.azane.spcurs.spawner.func.effect.EffectAttributeModifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.*;

public class SpcursSpawner
{
    private static final SpcursSpawner.SpawnerStorage STORAGE = new SpawnerStorage();

    private final ResourceLocation rl;
    private ResourceLocation parent;
    private BuildStats stats;

    private int replaceable;
    private JsonArray[] raw_array;

    private final List<ISpcursPlugin> globalEffects = new ArrayList<>();

    enum BuildStats{
        RAW,
        COMPLETE,
        FAIL,
        BROKEN
    }
    enum RawArrayType{
        EFFECT(0,"effect"),
        CREATURE(1,"creature"),
        FUNCTION(2,"function");
        public final String name;
        public final int id;
        RawArrayType(int id,String name) { this.id = id; this.name = name;}
    }

    private SpcursSpawner(ResourceLocation rl)
    {
        this.rl = rl;
    }

    public static class SpawnerStorage implements IJsonStorage
    {
        private final Map<ResourceLocation,SpcursSpawner> spcurss = new HashMap<>();
        private final Map<ResourceLocation, Type> funcMap = new HashMap<>();

        public SpawnerStorage()
        {
            funcMap.put(new ResourceLocation("spcurs:attribute"), EffectAttributeModifier.class);
        }
        @Override
        public void setData(Map<ResourceLocation, JsonObject> data)
        {
            data.forEach((rl,jsonObject)->{
                ResourceLocation newrl = new ResourceLocation(rl.getNamespace(), FileNameExtractHelper.getHeadFolderFiltered(rl.getPath()));
                if(!spcurss.containsKey(newrl))
                    spcurss.put(newrl,create(newrl,jsonObject));
            });
        }
        public Optional<Type> getFunc(ResourceLocation rl)
        {
            return funcMap.containsKey(rl) ? Optional.of(funcMap.get(rl)) : Optional.empty();
        }
        public Optional<SpcursSpawner> getSpcurs(ResourceLocation rl)
        {
            return spcurss.containsKey(rl) ? Optional.of(spcurss.get(rl)) : Optional.empty();
        }
    }
    public static Optional<Pair<Boolean,JsonArray>> getAsRawArray(JsonObject jobj, String memname)
    {
        JsonObject obj = jobj;
        if(memname != null){
            obj = GsonHelper.getAsJsonObject(jobj,memname,null);
            if(obj == null)return Optional.empty();
        }
        JsonArray obj2 = GsonHelper.getAsJsonArray(obj,"array",null);
        if(obj2 == null)return Optional.empty();
        boolean b = GsonHelper.getAsBoolean(obj,"replaceable",true);
        return Optional.of(Pair.of(b,obj2));
    }
    public static List<ISpcursPlugin> buildEffects(JsonArray array)
    {
        List<JsonObject> jobjs = JsonSerializer.GSON_NORMAL.fromJson(array,JsonSerializer.LIST_JOBJ);
        List<ISpcursPlugin> res = new ArrayList<>();
        jobjs.forEach((obj -> {
            ResourceLocation tmprl = JsonSerializer.getAsResourceLocation(obj,"type",null);
            if(tmprl != null)
            {
                STORAGE.getFunc(tmprl).ifPresent((type -> {
                    Object cls = JsonSerializer.GSON_NORMAL.fromJson(obj,type);
                    if(cls instanceof ISpcursPlugin)
                        res.add((ISpcursPlugin) cls);
                }));
            }
        }));
        return res;
    }
    private static SpcursSpawner create(ResourceLocation rl,JsonObject jobj)
    {
        SpcursSpawner tmp = new SpcursSpawner(rl);
        tmp.parent = JsonSerializer.getAsResourceLocation(jobj,"parent",null);
        tmp.stats = BuildStats.RAW;
        tmp.replaceable = 0;
        tmp.raw_array = new JsonArray[RawArrayType.values().length];
        Arrays.stream(RawArrayType.values()).forEach(type -> getAsRawArray(jobj, type.name).ifPresent((pr)->{
            tmp.replaceable |= pr.first ? (1<<type.id) : 0;
            tmp.raw_array[type.id] = pr.second;
        }));
        return tmp;
    }
    private Optional<SpcursSpawner> buildParent(ResourceLocation parent,Set<ResourceLocation> familyTree)
    {
        SpcursSpawner father = STORAGE.getSpcurs(parent).orElse(null);
        return Optional.empty();
    }
    public void build()
    {
        if(this.stats != BuildStats.RAW)
            return;

    }

    public static SpawnerStorage getStorage()
    {
        return STORAGE;
    }

    public List<ISpcursPlugin> getGlobalEffects()
    {
        return globalEffects;
    }
}
