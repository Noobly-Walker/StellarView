package net.povstalec.stellarview.api.celestial_objects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.stellarview.client.render.level.misc.StellarCoordinates;

public class CelestialObject
{
	public static final float DEFAULT_DISTANCE = 100.0F;
	public static final float[] FULL_UV = new float[] {0.0F, 0.0F, 1.0F, 1.0F};

	protected ResourceLocation texture;
	protected float distance;
	protected float size;

	protected ResourceLocation haloTexture;
	protected float haloSize;
	protected boolean hasHalo = false;

	protected boolean blends = false;
	//protected float distanceOffset = 0;
	protected float initialTheta = 0;
	protected float initialPhi = 0;
	//protected float alpha = 0;
	//protected float beta = 0;
	//protected float gamma = 0;
	
	public enum Motion
	{
		GEOCENTRIC,
		HELIOCENTRIC
	}
	
	public CelestialObject(ResourceLocation texture, float distance, float size)
	{
		this.texture = texture;
		this.distance = distance;
		this.size = size;
	}
	
	/**
	 * Forces the Celestial Object to blend with the background
	 * @return self
	 */
	public CelestialObject blends()
	{
		this.blends = true;
		return this;
	}
	
	/**
	 * Creates a Halo around the Celestial Object
	 * @param haloTexture Texture used for the Halo
	 * @param haloSize Size of the Halo
	 * @return self
	 */
	public CelestialObject halo(ResourceLocation haloTexture, float haloSize)
	{
		this.haloTexture = haloTexture;
		this.haloSize = haloSize;
		this.hasHalo = true;
		return this;
	}
	
	/*public CelestialObject distanceOffset(float distanceOffset)
	{
		this.distanceOffset = distanceOffset;
		return this;
	}*/
	
	/**
	 * Initial Theta for Spherical Coordinates
	 * @param initialTheta Initial Theta
	 * @return self
	 */
	public CelestialObject initialTheta(float initialTheta)
	{
		this.initialTheta = initialTheta;
		return this;
	}

	/**
	 * Initial Phi for Spherical Coordinates
	 * @param initialPhi Initial Phi
	 * @return self
	 */
	public CelestialObject initialPhi(float initialPhi)
	{
		this.initialPhi = initialPhi;
		return this;
	}
	
	/*public CelestialObject planeAngles(float alpha, float beta, float gamma)
	{
		this.alpha = alpha;
		this.beta = beta;
		this.gamma = gamma;
		return this;
	}*/
	
	//TODO Finish this
	protected double[] helioCentricMovement(double playerDistance, double playerAngle, double time)
	{
		//NOTE: Polar Coordinates 0, 0 mean that it starts off facing +Z
		
		// Player is on an object from which we create a geocentric view of the sky
		//double playerX = playerDistance * Math.cos(playerAngle);
		//double playerY = 0.0;
		//double playerZ = playerDistance * Math.sin(playerAngle);
		
		double theta = this.initialTheta;
		double phi = this.initialPhi;
		double r = this.distance/* + Math.sin(0.5 * theta) * Math.sin(0.5 * theta) * this.distanceOffset*/;
		
		double helioX = StellarCoordinates.cartesianX(r, theta, phi);
		double helioY = StellarCoordinates.cartesianY(r, theta, phi);
		double helioZ = StellarCoordinates.cartesianZ(r, theta, phi);
		
		//double geoX = helioX - playerX;
		//double geoY = helioY - playerY;
		//double geoZ = helioZ - playerZ;
		
		return new double[] {helioX, helioY, helioZ};
	}
	
	/**
	 * 
	 * @param level
	 * @param partialTicks
	 * @param stack
	 * @param bufferbuilder
	 * @param uv
	 * @param playerDistance
	 * @param playerXAngle
	 * @param playerYAngle
	 * @param playerZAngle
	 */
	public void render(ClientLevel level, float partialTicks, PoseStack stack, BufferBuilder bufferbuilder, float[] uv,
			float playerDistance, float playerXAngle, float playerYAngle, float playerZAngle)
	{
		double[] coords = helioCentricMovement(playerDistance, Math.toRadians(playerXAngle), Math.toRadians((float) level.getDayTime() / 24000 / 8));
		
		float theta = (float) StellarCoordinates.sphericalTheta(coords[0], coords[1], coords[2]);
		float phi = (float) StellarCoordinates.sphericalPhi(coords[0], coords[1], coords[2]);
		
		stack.pushPose();
        stack.mulPose(Vector3f.YP.rotationDegrees(playerYAngle));
        stack.mulPose(Vector3f.ZP.rotationDegrees(playerZAngle));
        stack.mulPose(Vector3f.XP.rotationDegrees(playerXAngle));
        Matrix4f lastMatrix = stack.last().pose();
		
		if(uv == null || uv.length < 4)
			uv = FULL_UV;
		
		if(this.hasHalo)
			this.renderHalo(bufferbuilder, lastMatrix, theta, phi, uv);
		this.renderBody(bufferbuilder, lastMatrix, theta, phi, uv);

		RenderSystem.defaultBlendFunc();
        
        stack.popPose();
	}
	
	protected void renderObject(BufferBuilder bufferbuilder, Matrix4f lastMatrix, ResourceLocation texture,
			float size, float theta, float phi, float[] uv)
	{
		float[] corner00 = StellarCoordinates.placeOnSphere(-size, -size, DEFAULT_DISTANCE, theta, phi);
		float[] corner10 = StellarCoordinates.placeOnSphere(size, -size, DEFAULT_DISTANCE, theta, phi);
		float[] corner11 = StellarCoordinates.placeOnSphere(size, size, DEFAULT_DISTANCE, theta, phi);
		float[] corner01 = StellarCoordinates.placeOnSphere(-size, size, DEFAULT_DISTANCE, theta, phi);
		
		RenderSystem.setShaderTexture(0, texture);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(lastMatrix, corner00[0], corner00[1], corner00[2]).uv(uv[0], uv[1]).endVertex();
        bufferbuilder.vertex(lastMatrix, corner10[0], corner10[1], corner10[2]).uv(uv[2], uv[1]).endVertex();
        bufferbuilder.vertex(lastMatrix, corner11[0], corner11[1], corner11[2]).uv(uv[2], uv[3]).endVertex();
        bufferbuilder.vertex(lastMatrix, corner01[0], corner01[1], corner01[2]).uv(uv[0], uv[3]).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
	}
	
	protected void renderHalo(BufferBuilder bufferbuilder, Matrix4f lastMatrix,
			float theta, float phi, float[] uv)
	{
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		renderObject(bufferbuilder, lastMatrix, this.haloTexture, this.haloSize, theta, phi, uv);
	}
	
	protected void renderBody(BufferBuilder bufferbuilder, Matrix4f lastMatrix,
			float theta, float phi, float[] uv)
	{
		if(this.blends)
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		else
			RenderSystem.defaultBlendFunc();
		renderObject(bufferbuilder, lastMatrix, this.texture, this.size, theta, phi, uv);
	}
}
