package com.azane.spcurs.genable.data.sc.goal;

import com.azane.cjsop.annotation.JsonClassTypeBinder;
import com.azane.spcurs.SpcursMod;
import com.azane.spcurs.genable.data.ISpcursPlugin;
import com.azane.spcurs.genable.data.sc.ScCreature;
import com.azane.spcurs.lib.RlHelper;
import com.azane.spcurs.util.TargetPredicateHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonClassTypeBinder(fullName = "goal.target.scc",simpleName = "tarscc",namespace = SpcursMod.MOD_ID)
public class GoalTargetScCreature implements ISpcursPlugin,IPersistantGoal
{
    @Getter
    @Expose(serialize = false,deserialize = false)
    public final ResourceLocation goalType = RlHelper.build(SpcursMod.MOD_ID,"goal.target.scc");

    @SerializedName("team")
    private int targetTeam;

    @Override
    public void onEntityCreate(ServerLevel level, BlockPos blockPos, LivingEntity entity)
    {
        applyGoalToEntity(level, blockPos, entity, false);
    }

    @Override
    public void applyGoalToEntity(ServerLevel level, BlockPos blockPos, LivingEntity entity, boolean isRecreate)
    {
        if(entity instanceof Mob mob)
        {
            mob.targetSelector.addGoal(0,new NearestAttackableTargetGoal<>(mob, LivingEntity.class,false, TargetPredicateHelper.createTeamPredicate(targetTeam)));
            if(!isRecreate)
                GoalPersistantHelper.mobStoreGoal(mob, this);
        }
    }
}
