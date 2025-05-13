package com.azane.spcurs.spawner;

import com.azane.spcurs.api.ISpcursPlugin;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class SpcursFunc implements ISpcursPlugin
{
    @Expose
    @SerializedName("type")
    private ResourceLocation type;

}
