package com.azane.spcurs.spawner;

import com.azane.spcurs.api.IPersistence;
import com.azane.spcurs.lib.TagSerializeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

public class SpcursEntity implements IPersistence
{
    private ResourceLocation spawnerID;

    private SpcursSpawner spawner;
    private boolean available;

    private boolean active;
    private long ticks;
    private long activeTicks;

    private final List<Integer> toSpawnIndex = new LinkedList<>();
    private final List<SpcursCreature.CacheData> spawnData = new ArrayList<>();

    //TODO: Maybe we can introduce in-game modifier here, but it's kind of hard to keep it permanently
    public SpcursEntity(ResourceLocation rl)
    {
        spawnerID = rl;
        spawner = SpcursSpawner.STORAGE.getSpcurs(rl).flatMap(SpcursSpawner::buildAndGet).orElse(null);
        ticks = 0;
        activeTicks = 0;
        build();
    }

    private void build()
    {
        if(spawner == null)
        {
            available = false;
            return;
        }
        toSpawnIndex.clear();
        spawnData.clear();
        int id = 0;
        for(SpcursCreature creature : spawner.getCreatures())
            spawnData.add(creature.buildCache(id++));
        spawnData.forEach(this::insertToSpawn);
        updateAvailable();
    }

    private void updateAvailable()
    {
        active = false;
        available = spawner != null && !toSpawnIndex.isEmpty() && toSpawnIndex.size() == spawnData.size() && toSpawnIndex.size() == spawner.getCreatures().size();
    }
    private boolean checkForSpawn()
    {
        return spawnData.get(toSpawnIndex.getFirst()).getNextTick() <= activeTicks;
    }
    private void insertToSpawn(SpcursCreature.CacheData cache)
    {
        ListIterator<Integer> it = toSpawnIndex.listIterator();
        while(it.hasNext())
        {
            if (cache.getNextTick() < spawnData.get(it.next()).getNextTick()) {
                it.previous();
                it.add(cache.getIndex());
                return;
            }
        }
        toSpawnIndex.addLast(cache.getIndex());
    }

    private boolean isNearPlayer(Level level, BlockPos blockPos) {
        //TODO:Unfinished
        return level.hasNearbyAlivePlayer((double)blockPos.getX() + (double)0.5F, (double)blockPos.getY() + (double)0.5F, (double)blockPos.getZ() + (double)0.5F, 32.0D);
    }

    public void tick(ServerLevel level,BlockPos blockPos)
    {
        if(!available)
            return;
        ticks++;
        if(ticks%20 == 0)
            fixedTick(level,blockPos);
        if(active)
            activeTick(level,blockPos);
    }
    public void fixedTick(ServerLevel level,BlockPos blockPos)
    {
        boolean newActive = isNearPlayer(level,blockPos);
        if(active != newActive)
            spawner.getFunctions().forEach((I)-> {
                if(newActive)
                    I.onSpcursActivate(level,blockPos);
                else
                    I.onSpcursInactivate(level,blockPos);
            });
        active = newActive;
    }

    public void activeTick(ServerLevel level, BlockPos blockPos)
    {
        activeTicks++;
        if(activeTicks%20 == 0)
            spawner.getFunctions().forEach((I)->I.onActiveFixedTick(level,blockPos,activeTicks/20));
        while (checkForSpawn())
        {
            int index = toSpawnIndex.removeFirst();
            SpcursCreature.CacheData cache = spawnData.get(index);
            SpcursCreature creature = spawner.getCreatures().get(index);
            creature.spawn(level,blockPos,spawner,cache);
            insertToSpawn(cache);
        }
    }

    @Override
    public void load(CompoundTag tag)
    {
        spawnerID = new ResourceLocation(tag.getString("spawnerID"));
        if(spawner == null)
            spawner = SpcursSpawner.STORAGE.getSpcurs(spawnerID).flatMap(SpcursSpawner::buildAndGet).orElse(null);
        activeTicks = tag.getLong("activeTicks");
        ticks = tag.getLong("ticks");
        toSpawnIndex.clear();
        toSpawnIndex.addAll(Arrays.stream(tag.getIntArray("toSpawn")).boxed().toList());
        spawnData.clear();
        spawnData.addAll(TagSerializeHelper.deserializeList(SpcursCreature.CacheData.CODEC,(ListTag)tag.get("spawnData")).orElse(List.of()));
        updateAvailable();
    }

    @Override
    public void save(CompoundTag tag)
    {
        tag.putString("spawnerID",spawnerID.toString());
        tag.putLong("activeTicks",activeTicks);
        tag.putLong("ticks",ticks);
        tag.putIntArray("toSpawn",toSpawnIndex);
        tag.put("spawnData", TagSerializeHelper.serializeList(SpcursCreature.CacheData.CODEC,spawnData));
    }
}