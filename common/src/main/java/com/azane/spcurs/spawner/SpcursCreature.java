package com.azane.spcurs.spawner;

import com.azane.spcurs.api.ISpcursPlugin;
import com.azane.spcurs.lib.json.JsonSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ibm.icu.impl.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SpcursCreature
{
    private static final EntityType<?> DEFAULT_CREATURE = EntityType.PIG;

    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("amount")
    private int amt;
    @Expose
    @SerializedName("range")
    private float range;
    @Expose
    @SerializedName("interval")
    private float interval;
    @Expose
    @SerializedName("effect")
    private JsonObject raw_effect;

    private final List<ISpcursPlugin> effects = new ArrayList<>();
    private EntityType<?> creature = null;

    public SpcursCreature(){}

    public EntityType<?> getCreature()
    {
        if(creature == null)
            creature = EntityType.byString(id).orElse(DEFAULT_CREATURE);
        return creature;
    }
    public void applyEffects(ServerLevel level, BlockPos blockPos, LivingEntity entity)
    {
        effects.forEach((I)->I.onEntityCreate(level,blockPos,entity));
    }
    public void build(SpcursSpawner spawner)
    {
        Pair<Boolean, JsonArray> pr = SpcursSpawner.getAsRawArray(raw_effect,null).orElse(null);
        if(pr == null || !pr.first)
            effects.addAll(spawner.getGlobalEffects());
        if(pr != null)
            effects.addAll(SpcursSpawner.buildEffects(pr.second));
    }
}
