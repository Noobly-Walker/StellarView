package net.povstalec.stellarview.api.celestials;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.StellarView;

import java.util.Random;

public class DistantGalaxyObject extends StellarObject
{
	public static final ResourceLocation SPIRAL_GALAXY_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/distant_galaxy/spiral_galaxy.png");

	protected float size;
	protected float rotation;

	public DistantGalaxyObject(ResourceLocation texture, float size, float rotation)
	{
		super(texture, size);
		this.rotation = rotation;
	}

	public DistantGalaxyObject(float size, float rotation)
	{
		this(SPIRAL_GALAXY_TEXTURE, size, rotation);
	}

	@Override
	protected boolean shouldBlend(ClientLevel level, Camera camera)
	{
		return true;
	}

	@Override
	protected float getRotation(ClientLevel level, float partialTicks) { return rotation; }
}
