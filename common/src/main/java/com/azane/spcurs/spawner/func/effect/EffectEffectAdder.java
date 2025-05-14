package com.azane.spcurs.spawner.func.effect;

import com.azane.spcurs.registry.RegistryAccessHelper;
import com.azane.spcurs.spawner.SpcursFunc;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class EffectEffectAdder extends SpcursFunc
{
    public static final MobEffect DEFAULT_EFFECT = MobEffects.HEAL;

    @Expose
    @SerializedName("effectID")
    private ResourceLocation rl = null;
    @Expose
    @SerializedName("level")
    private int effect_level = -10;
    @Expose
    @SerializedName("duration")
    private int duration = -1;
    @Expose
    @SerializedName("visible")
    private boolean visible = false;

    private MobEffect effect = null;

    public EffectEffectAdder(){}

    private boolean checkIncomplete()
    {
        return rl == null || effect_level == -10;
    }

    public void onEntityCreate(ServerLevel level, BlockPos blockPos, LivingEntity entity)
    {
        if(effect == null)
        {
            if(checkIncomplete())
                return;
            effect = BuiltInRegistries.MOB_EFFECT.get(rl);
            if(effect == null)
                effect = DEFAULT_EFFECT;
        }
        entity.addEffect(new MobEffectInstance(effect,duration,effect_level-1,false,visible,false));
    }
}
