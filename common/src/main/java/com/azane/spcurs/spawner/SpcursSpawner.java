package com.azane.spcurs.spawner;

import com.azane.spcurs.api.ISpcursPlugin;
import com.azane.spcurs.lib.FileNameExtractHelper;
import com.azane.spcurs.lib.json.IJsonStorage;
import com.azane.spcurs.lib.json.JsonSerializer;
import com.azane.spcurs.spawner.func.effect.EffectAttributeModifier;
import com.azane.spcurs.spawner.func.effect.EffectEffectAdder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Class used for the load and storage of all the spawner configs from the jsons in datapacks
 */
public class SpcursSpawner
{
    public static final SpcursSpawner.SpawnerStorage STORAGE = new SpawnerStorage();

    private final ResourceLocation rl;
    private ResourceLocation parent;
    private BuildStats stats;

    private int replaceable;
    private JsonArray[] raw_array;

    @Getter
    private final List<ISpcursPlugin> globalEffects = new ArrayList<>();
    @Getter
    private final List<SpcursCreature> creatures = new ArrayList<>();
    @Getter
    private final List<ISpcursPlugin> functions = new ArrayList<>();

    enum BuildStats{
        RAW,
        COMPLETE,
        FAIL
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
            funcMap.put(new ResourceLocation("spcurs:effect"), EffectEffectAdder.class);
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
    public static List<ISpcursPlugin> buildFuncs(JsonArray array)
    {
        if(array == null)
            return new ArrayList<>();
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
    public static List<SpcursCreature> buildCreatures(JsonArray array)
    {
        if(array == null)
            return new ArrayList<>();
        List<JsonObject> jobjs = JsonSerializer.GSON_NORMAL.fromJson(array,JsonSerializer.LIST_JOBJ);
        List<SpcursCreature> res = new ArrayList<>();
        jobjs.forEach(obj->{
            SpcursCreature cls = JsonSerializer.GSON_NORMAL.fromJson(obj, SpcursCreature.class);
            res.add(cls);
        });
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
    private boolean inherit(SpcursSpawner parent)
    {
        if(parent == null)
        {
            this.stats = BuildStats.FAIL;
            return false;
        }
        else {
            if((this.replaceable & (1<<RawArrayType.EFFECT.id)) == 0)
                this.globalEffects.addAll(parent.globalEffects);
            if((this.replaceable & (1<<RawArrayType.CREATURE.id)) == 0)
                this.creatures.addAll(parent.creatures);
            if((this.replaceable & (1<<RawArrayType.FUNCTION.id)) == 0)
                this.functions.addAll(parent.functions);
        }
        return true;
    }
    private void localBuild()
    {
        this.globalEffects.addAll(buildFuncs(raw_array[RawArrayType.EFFECT.id]));
        this.creatures.addAll(buildCreatures(raw_array[RawArrayType.CREATURE.id]));
        this.functions.addAll(buildFuncs(raw_array[RawArrayType.FUNCTION.id]));
    }
    private SpcursSpawner buildEntry(Set<ResourceLocation> familyTree)
    {
        if(this.stats == BuildStats.COMPLETE)
            return this;
        else if(this.stats == BuildStats.FAIL)
            return null;

        if(this.parent != null)
        {
            if(familyTree.contains(this.parent))
            {
                this.stats = BuildStats.FAIL;
                return null;
            }
            familyTree.add(this.parent);
            SpcursSpawner father = STORAGE.getSpcurs(this.parent).orElse(null);
            if(father == null)
            {
                this.stats = BuildStats.FAIL;
                return null;
            }
            father = father.buildEntry(familyTree);
            if(!this.inherit(father))
            {
                this.stats = BuildStats.FAIL;
                return null;
            }
        }
        localBuild();
        this.stats = BuildStats.COMPLETE;
        return this;
    }
    public void build()
    {
        if(this.stats != BuildStats.RAW)
            return;
        buildEntry(new HashSet<>(Set.of(this.rl)));
    }
    public Optional<SpcursSpawner> buildAndGet()
    {
        build();
        return this.stats == BuildStats.COMPLETE ? Optional.of(this) : Optional.empty();
    }

}
