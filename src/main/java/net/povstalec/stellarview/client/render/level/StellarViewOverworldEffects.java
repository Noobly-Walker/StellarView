package net.povstalec.stellarview.client.render.level;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.StellarView;
import net.povstalec.stellarview.api.StellarViewSpecialEffects;
import net.povstalec.stellarview.api.celestial_objects.Moon;
import net.povstalec.stellarview.common.config.StellarViewConfig;

public class StellarViewOverworldEffects extends StellarViewSpecialEffects
{
	public static final ResourceLocation OVERWORLD_EFFECTS = new ResourceLocation("overworld");
	
	public StellarViewOverworldEffects()
	{
		super(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false);
		
		this.vanillaSun();
		//this.vanillaMoon();
		this.celestialObject(new Moon.DefaultMoon());
		this.skybox(new ResourceLocation(StellarView.MODID, "textures/environment/overworld_skybox/overworld"));
	}
	
	@Override
	public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog)
    {
		if(StellarViewConfig.replace_vanilla.get())
		{
			// Milky Way is here because Players must have the option to use config, otherwise it would be in the constructor
			this.milkyWay(StellarViewConfig.milky_way_x.get(), StellarViewConfig.milky_way_y.get(), StellarViewConfig.milky_way_z.get(),
					Math.toRadians(StellarViewConfig.milky_way_alpha.get()), Math.toRadians(StellarViewConfig.milky_way_beta.get()), Math.toRadians(StellarViewConfig.milky_way_gamma.get()));
			super.renderSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);
		}
		
        return StellarViewConfig.replace_vanilla.get();
    }
	
	//TODO Use this again
	/*public double starWidthFunction(double aLocation, double bLocation, double sinRandom, double cosRandom, double sinTheta, double cosTheta, double sinPhi, double cosPhi)
	{
		if(StellarViewConfig.enable_black_hole.get())
			return cosPhi  > 0.0 ? cosPhi * 8 *(bLocation * cosRandom + aLocation * sinRandom) : bLocation * cosRandom + aLocation * sinRandom;
		
		return bLocation * cosRandom + aLocation * sinRandom;
	}*/
}
