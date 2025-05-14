package com.azane.spcurs.spawner;

import com.azane.spcurs.api.ISpcursPlugin;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ibm.icu.impl.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

public class SpcursCreature
{
    private static final EntityType<?> DEFAULT_CREATURE = EntityType.PIG;
    private static final long COMPLETE_TICK = Long.MAX_VALUE - 100;

    @Expose
    @SerializedName("id")
    private String id = null;
    @Expose
    @SerializedName("limit")
    private int limit = 0;
    @Expose
    @SerializedName("amount")
    private int amt = 0;
    @Expose
    @SerializedName("range")
    private float range = 0F;
    @Expose
    @SerializedName("interval")
    private int interval = 0;
    @Expose
    @SerializedName("effect")
    private JsonObject raw_effect;

    private boolean globalEffects;
    private final List<ISpcursPlugin> localEffects = new ArrayList<>();
    private EntityType<?> creature = null;

    public SpcursCreature(){}

    @Getter
    @AllArgsConstructor
    public static class CacheData
    {
        public static final Codec<CacheData> CODEC = RecordCodecBuilder.create(
            (instance)->instance.group(
                Codec.LONG.fieldOf("nextTick").forGetter(CacheData::getNextTick),
                Codec.INT.fieldOf("index").forGetter(CacheData::getIndex),
                Codec.INT.fieldOf("spawnCnt").forGetter(CacheData::getSpawnCnt),
                Codec.INT.fieldOf("deathCnt").forGetter(CacheData::getDeathCnt)
            ).apply(instance,CacheData::new)
        );

        private long nextTick;
        private int index;
        private int spawnCnt = 0;
        private int deathCnt = 0;
        private CacheData(int index,long nextTick)
        {
            this.index = index;
            this.nextTick = nextTick;
        }
    }
    public CacheData buildCache(int index)
    {
        return new CacheData(index,interval);
    }

    private boolean checkIncomplete()
    {
        return id == null || amt <= 0 || range <= 0F || interval <= 0 || limit <= 0;
    }



    public void spawn(ServerLevel serverLevel,BlockPos blockPos,SpcursSpawner spawner,CacheData cacheData)
    {
        if(cacheData.nextTick == COMPLETE_TICK)
            return;
        if(checkIncomplete())
        {
            cacheData.nextTick = COMPLETE_TICK;
            return;
        }
        RandomSource randomSource = serverLevel.getRandom();
        boolean spawnSuccess = false;
        for(int i = 0;i < limit;i++)
        {
            BlockPos bornPos = genRandomPos(serverLevel,randomSource,blockPos,spawner);

            if(bornPos == null)
                continue;

            //TODO: Peaceful Check? Light Check? Surface Check?
            /*
            if (!SpawnPlacements.checkSpawnRules(getCreature(), serverLevel, MobSpawnType.SPAWNER, bornPos, serverLevel.getRandom())) {
                continue;
            }
             */

            Entity entity = getCreature().create(serverLevel);
            if (entity == null)
            {
                nextSpawnTick(cacheData,true);
                break;
            }

            //TODO: do we need nearby check?
            /*
            int k = serverLevel.getEntitiesOfClass(entity.getClass(), (new AABB((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)(blockPos.getX() + 1), (double)(blockPos.getY() + 1), (double)(blockPos.getZ() + 1))).inflate((double)this.spawnRange)).size();
            if (k >= this.maxNearbyEntities) {
                this.delay(serverLevel, blockPos);
                return;
            }
            */

            entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomSource.nextFloat() * 360.0F, 0.0F);
            if (entity instanceof Mob mob)
            {
                //TODO: do we need to check liquid?
                /*
                if (!mob.checkSpawnRules(serverLevel, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(serverLevel))
                    continue;
                 */
                mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
            }

            if (!serverLevel.addFreshEntity(entity))
            {
                nextSpawnTick(cacheData,true);
                return;
            }

            if(entity instanceof LivingEntity living)
            {
                applyEffects(serverLevel,blockPos,living,spawner);
            }
            serverLevel.levelEvent(2004, blockPos, 0);
            serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, bornPos);
            if (entity instanceof Mob) {
                ((Mob)entity).spawnAnim();
            }
            spawnSuccess = true;
        }
        nextSpawnTick(cacheData,spawnSuccess);
    }
    private void nextSpawnTick(CacheData data,boolean spawnSuccess)
    {
        if(!spawnSuccess)
        {
            data.nextTick ++;
            return;
        }
        data.nextTick += interval;
    }
    private BlockPos genRandomPos(ServerLevel level,RandomSource randomSource,BlockPos blockPos,SpcursSpawner spawner)
    {
        //TODO:Maybe we need caches in the SpcursEntity to quickly get the available pos
        return new BlockPos(blockPos);
    }
    public EntityType<?> getCreature()
    {
        if(creature == null)
            creature = EntityType.byString(id).orElse(DEFAULT_CREATURE);
        return creature;
    }
    public void applyEffects(ServerLevel level, BlockPos blockPos, LivingEntity entity,SpcursSpawner spawner)
    {
        build();
        localEffects.forEach((I)->I.onEntityCreate(level,blockPos,entity));
        if(globalEffects)
            spawner.getGlobalEffects().forEach((I)->I.onEntityCreate(level,blockPos,entity));
    }
    public void build()
    {
        if(raw_effect == null)
            return;
        Pair<Boolean, JsonArray> pr = SpcursSpawner.getAsRawArray(raw_effect,null).orElse(null);
        globalEffects = pr == null || !pr.first;
        if(pr != null)
            localEffects.addAll(SpcursSpawner.buildFuncs(pr.second));
        raw_effect = null;
    }
}
