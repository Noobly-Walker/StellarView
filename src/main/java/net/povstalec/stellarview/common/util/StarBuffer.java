package net.povstalec.stellarview.common.util;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL31C;
import org.lwjgl.opengl.GL40;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public class StarBuffer implements AutoCloseable
{
	private int vertexBufferId;
	   private int indexBufferId;
	   private int arrayObjectId;
	   @Nullable
	   private VertexFormat format;
	   @Nullable
	   private RenderSystem.AutoStorageIndexBuffer sequentialIndices;
	   private VertexFormat.IndexType indexType;
	   private int indexCount;
	   private VertexFormat.Mode mode;

	   public StarBuffer()
	   {
	      RenderSystem.assertOnRenderThread();
	      this.vertexBufferId = GlStateManager._glGenBuffers();
	      this.indexBufferId = GlStateManager._glGenBuffers();
	      this.arrayObjectId = GlStateManager._glGenVertexArrays();
	   }

	   public void upload(BufferBuilder.RenderedBuffer p_231222_) {
	      if (!this.isInvalid()) {
	         RenderSystem.assertOnRenderThread();
	         try {
	            BufferBuilder.DrawState bufferbuilder$drawstate = p_231222_.drawState();
	            this.format = this.uploadVertexBuffer(bufferbuilder$drawstate, p_231222_.vertexBuffer());
	            this.sequentialIndices = this.uploadIndexBuffer(bufferbuilder$drawstate, p_231222_.indexBuffer());
	            this.indexCount = bufferbuilder$drawstate.indexCount();
	            this.indexType = bufferbuilder$drawstate.indexType();
	            this.mode = bufferbuilder$drawstate.mode();
	         } finally {
	            p_231222_.release();
	         }

	      }
	   }

	   private VertexFormat uploadVertexBuffer(BufferBuilder.DrawState p_231219_, ByteBuffer p_231220_) {
	      boolean flag = false;
	      if (!p_231219_.format().equals(this.format)) {
	         if (this.format != null) {
	            this.format.clearBufferState();
	         }

	         GlStateManager._glBindBuffer(34962, this.vertexBufferId);
	         p_231219_.format().setupBufferState();
	         flag = true;
	      }

	      if (!p_231219_.indexOnly()) {
	         if (!flag) {
	            GlStateManager._glBindBuffer(34962, this.vertexBufferId);
	         }

	         RenderSystem.glBufferData(34962, p_231220_, 35044);
	      }

	      return p_231219_.format();
	   }

	   @Nullable
	   private RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(BufferBuilder.DrawState p_231224_, ByteBuffer p_231225_) {
	      if (!p_231224_.sequentialIndex()) {
	         GlStateManager._glBindBuffer(34963, this.indexBufferId);
	         RenderSystem.glBufferData(34963, p_231225_, 35044);
	         return null;
	      } else {
	         RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(p_231224_.mode());
	         if (rendersystem$autostorageindexbuffer != this.sequentialIndices || !rendersystem$autostorageindexbuffer.hasStorage(p_231224_.indexCount())) {
	            rendersystem$autostorageindexbuffer.bind(p_231224_.indexCount());
	         }

	         return rendersystem$autostorageindexbuffer;
	      }
	   }

	   public void bind() {
	      BufferUploader.invalidate();
	      GlStateManager._glBindVertexArray(this.arrayObjectId);
	   }

	   public static void unbind() {
	      BufferUploader.invalidate();
	      GlStateManager._glBindVertexArray(0);
	   }

	   public void draw() {
		   RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType);
		   // Custom
		   //RenderSystem.assertOnRenderThread();
		   //GL31.glDrawElementsInstanced(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType, 0L, this.indexCount);
		  // GL31C.glDrawArraysInstanced(GL40.GL_PATCHES, 0, indexCount, 100);
	   }

	   private VertexFormat.IndexType getIndexType() {
	      RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = this.sequentialIndices;
	      return rendersystem$autostorageindexbuffer != null ? rendersystem$autostorageindexbuffer.type() : this.indexType;
	   }

	   public void drawWithShader(Matrix4f p_254480_, Matrix4f p_254555_, ShaderInstance p_253993_) {
	      if (!RenderSystem.isOnRenderThread()) {
	         RenderSystem.recordRenderCall(() -> {
	            this._drawWithShader(new Matrix4f(p_254480_), new Matrix4f(p_254555_), p_253993_);
	         });
	      } else {
	         this._drawWithShader(p_254480_, p_254555_, p_253993_);
	      }

	   }

	   private void _drawWithShader(Matrix4f p_253705_, Matrix4f p_253737_, ShaderInstance p_166879_) {
	      for(int i = 0; i < 12; ++i) {
	         int j = RenderSystem.getShaderTexture(i);
	         p_166879_.setSampler("Sampler" + i, j);
	      }

	      if (p_166879_.MODEL_VIEW_MATRIX != null) {
	         p_166879_.MODEL_VIEW_MATRIX.set(p_253705_);
	      }

	      if (p_166879_.PROJECTION_MATRIX != null) {
	         p_166879_.PROJECTION_MATRIX.set(p_253737_);
	      }

	      if (p_166879_.INVERSE_VIEW_ROTATION_MATRIX != null) {
	         p_166879_.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
	      }

	      if (p_166879_.COLOR_MODULATOR != null) {
	         p_166879_.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
	      }

	      if (p_166879_.FOG_START != null) {
	         p_166879_.FOG_START.set(RenderSystem.getShaderFogStart());
	      }

	      if (p_166879_.FOG_END != null) {
	         p_166879_.FOG_END.set(RenderSystem.getShaderFogEnd());
	      }

	      if (p_166879_.FOG_COLOR != null) {
	         p_166879_.FOG_COLOR.set(RenderSystem.getShaderFogColor());
	      }

	      if (p_166879_.FOG_SHAPE != null) {
	         p_166879_.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
	      }

	      if (p_166879_.TEXTURE_MATRIX != null) {
	         p_166879_.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
	      }

	      if (p_166879_.GAME_TIME != null) {
	         p_166879_.GAME_TIME.set(RenderSystem.getShaderGameTime());
	      }

	      if (p_166879_.SCREEN_SIZE != null) {
	         Window window = Minecraft.getInstance().getWindow();
	         p_166879_.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
	      }

	      if (p_166879_.LINE_WIDTH != null && (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP)) {
	         p_166879_.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
	      }

	      RenderSystem.setupShaderLights(p_166879_);
	      p_166879_.apply();
	      this.draw();
	      p_166879_.clear();
	   }

	   public void close() {
	      if (this.vertexBufferId >= 0) {
	         RenderSystem.glDeleteBuffers(this.vertexBufferId);
	         this.vertexBufferId = -1;
	      }

	      if (this.indexBufferId >= 0) {
	         RenderSystem.glDeleteBuffers(this.indexBufferId);
	         this.indexBufferId = -1;
	      }

	      if (this.arrayObjectId >= 0) {
	         RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
	         this.arrayObjectId = -1;
	      }

	   }

	   public VertexFormat getFormat() {
	      return this.format;
	   }

	   public boolean isInvalid() {
	      return this.arrayObjectId == -1;
	   }
}
