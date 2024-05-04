package net.povstalec.stellarview.api.celestials.orbiting;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.StellarView;
import net.povstalec.stellarview.api.celestials.StellarObject;

import java.util.Random;

public class Comet extends OrbitingCelestialObject
{
	public static final ResourceLocation COMET_WHITE_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_white.png");
	public static final ResourceLocation COMET_GOLD_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_gold.png");
	public static final ResourceLocation COMET_MINT_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_mint.png");
	public static final ResourceLocation COMET_SKY_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_sky.png");
	public static final ResourceLocation COMET_CYAN_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_cyan.png");
	public static final ResourceLocation COMET_BLUE_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_blue.png");
	public static final ResourceLocation COMET_MAGENTA_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_magenta.png");
	public static final ResourceLocation COMET_PINK_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_pink.png");

	protected final static float INITIAL_SIZE = 1;

	protected float maxSize;
	protected float startPoint = (float) (Math.PI/4); // start showing the comet at π/4 radians or 45°
	protected float endPoint = (float) (startPoint + Math.toRadians(this.angularVelocity * 20)); // end 20 days later

	public Comet(ResourceLocation texture, float maxSize)
	{
		super(texture, INITIAL_SIZE);
		this.maxSize = maxSize;
	}

	public Comet(float maxSize)
	{
		this(COMET_WHITE_TEXTURE, maxSize);
	}

	@Override
	protected boolean shouldBlend(ClientLevel level, Camera camera)
	{
		return true;
	}

	@Override
	protected boolean shouldRender(ClientLevel level, Camera camera)
	{
		float phi = this.getPhi(level, 0F);
		Boolean rend = this.startPoint > Math.sin(phi) && Math.sin(phi) < this.endPoint;
		return rend;
	}

	@Override
	protected float getSize(ClientLevel level, float partialTicks)
	{
		float phi = this.getPhi(level, 0F);
		float relativePhi = (phi - this.startPoint) / (this.endPoint - this.startPoint);
		float cometSize;
		if (relativePhi < 0.5f) { cometSize = INITIAL_SIZE + 2 * relativePhi * (maxSize - INITIAL_SIZE); }
		else { cometSize = maxSize - 2 * (relativePhi - 0.5f) * (maxSize - INITIAL_SIZE); }
		return cometSize;
	}
	
	@Override
	protected float getRotation(ClientLevel level, float partialTicks)
	{
		//Unlike supernovae, comets shouldn't rotate much.
		return (float) (Math.PI * startPoint);
	}

	@Override
	protected float getPhi(ClientLevel level, float partialTicks)
	{
		return (this.initialPhi + (float) Math.toRadians(angularVelocity * ((float) level.getDayTime() / 24000))) % (float) (Math.PI*2);
	}
}
