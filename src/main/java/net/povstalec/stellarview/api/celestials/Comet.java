package net.povstalec.stellarview.api.celestials;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.StellarView;

import java.util.Random;

public class Comet extends StellarObject
{
	public static final ResourceLocation COMET_WHITE_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_white.png");
	public static final ResourceLocation COMET_GOLD_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_gold.png");
	public static final ResourceLocation COMET_MINT_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_mint.png");
	public static final ResourceLocation COMET_SKY_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_sky.png");
	public static final ResourceLocation COMET_CYAN_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_cyan.png");
	public static final ResourceLocation COMET_BLUE_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_blue.png");
	public static final ResourceLocation COMET_MAGENTA_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_magenta.png");
	public static final ResourceLocation COMET_PINK_TEXTURE = new ResourceLocation(StellarView.MODID, "textures/environment/sky_effect/comet/comet_pink.png");

	public static final ResourceLocation[] cometColors = {
			COMET_WHITE_TEXTURE,
			COMET_GOLD_TEXTURE,
			COMET_MINT_TEXTURE,
			COMET_SKY_TEXTURE,
			COMET_CYAN_TEXTURE,
			COMET_BLUE_TEXTURE,
			COMET_MAGENTA_TEXTURE,
			COMET_PINK_TEXTURE
	};

	protected final static float INITIAL_SIZE = 1;

	protected float maxSize;
	protected long start;
	protected long duration;

	public Comet(ResourceLocation texture, float maxSize, long start, long duration)
	{
		super(texture, INITIAL_SIZE);
		this.maxSize = maxSize;
		this.start = start;
		this.duration = duration;

		//TODO Make sure this works. Also, make sure there's more than just the first comet.
		Random cometRandomizer = new Random(start);
		ResourceLocation cometColor = cometColors[cometRandomizer.nextInt(cometColors.length)];
	}

	public Comet(float maxSize, long start, long duration)
	{
		this(COMET_WHITE_TEXTURE, maxSize, start, duration);
	}
	
	protected boolean isVisible(ClientLevel level)
	{
		long gameTime = level.getDayTime();
		return gameTime > start;
	}

	@Override
	protected boolean shouldBlend(ClientLevel level, Camera camera)
	{
		return true;
	}

	@Override
	protected boolean isVisibleDuringDay(ClientLevel level, Camera camera)
	{
		return isVisible(level);
	}

	@Override
	protected boolean shouldRender(ClientLevel level, Camera camera)
	{
		long gameTime = level.getDayTime();
		return gameTime <= (start + duration);
	}
	
	@Override
	protected float getSize(ClientLevel level, float partialTicks)
	{
		long gameTime = level.getDayTime();
		long lifeTime = gameTime - start;
		float cometSize = (float) (maxSize * Math.sin(Math.PI * lifeTime / duration));
		
		float visualSize = isVisible(level) && ((cometSize >= size) || lifeTime > duration / 2) ? cometSize : size;
		
		return distanceSize(visualSize) * 10;
	}
	
	@Override
	protected float getRotation(ClientLevel level, float partialTicks)
	{
		long gameTime = level.getDayTime();
		long lifeTime = gameTime - start;

		//Unlike supernovae, comets shouldn't rotate.
		return gameTime > start ? (float) (Math.PI * start) : rotation;
	}
}
