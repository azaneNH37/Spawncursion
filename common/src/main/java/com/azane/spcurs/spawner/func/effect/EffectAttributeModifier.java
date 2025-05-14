package com.azane.spcurs.spawner.func.effect;

import com.azane.spcurs.registry.RegistryAccessHelper;
import com.azane.spcurs.spawner.SpcursFunc;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class EffectAttributeModifier extends SpcursFunc
{
    public static final Attribute DEFAULT_ATTR = Attributes.MAX_HEALTH;

    @Expose
    @SerializedName("attribute")
    private ResourceLocation attr = null;
    @Expose
    @SerializedName("operation")
    private String op = null;
    @Expose
    @SerializedName("value")
    private double val = 0;

    private Attribute attribute = null;
    private AttributeModifier modifier = null;

    public EffectAttributeModifier(){}

    private boolean checkIncomplete()
    {
        return attr == null || op == null || val == 0;
    }

    public void onEntityCreate(ServerLevel level, BlockPos blockPos, LivingEntity entity)
    {
        if(attribute == null)
        {
            //TODO: Maybe we need to throw the warning but need to be careful with mounts of logs
            if(checkIncomplete())
                return;
            attribute = RegistryAccessHelper.gainRealtimeAccess().registryOrThrow(Registries.ATTRIBUTE).get(attr);
            if(attribute == null)
                attribute = DEFAULT_ATTR;
            UUID uuid = UUID.randomUUID();
            //TODO: We need to introduce other types of Operations! Well, interesting tasks. Already have basic ideas.
            //TODO: we share the attr_modifier among all the entities created. Could be dangerous,Umh? Need Observation.
            modifier = new AttributeModifier(uuid,uuid.toString(),val,
               switch (op)
               {
                   case "ark:direct_add","ADDITION"->  AttributeModifier.Operation.ADDITION;
                   case "ark:direct_mul","MULTIPLY_BASE" -> AttributeModifier.Operation.MULTIPLY_BASE;
                   case "ark:final_mul","MULTIPLY_TOTAL" -> AttributeModifier.Operation.MULTIPLY_TOTAL;
                   default -> AttributeModifier.Operation.ADDITION;
               }
            );
        }
        AttributeInstance attrIns = entity.getAttribute(attribute);
        if(attrIns != null)
            attrIns.addPermanentModifier(modifier);
    }
}
