package net.povstalec.stellarview.client.render.shader;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class StellarViewVertexFormat
{
	public static final VertexFormatElement ELEMENT_STAR_POS = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
	public static final VertexFormatElement ELEMENT_HEIGHT_WIDTH_SIZE = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
	public static final VertexFormatElement ELEMENT_COLOR = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
	
	// NOTE: The order of elements very much MATTERS!!!
	public static final VertexFormat STAR_POS_COLOR_LY = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("StarPos", ELEMENT_STAR_POS).put("Color", ELEMENT_COLOR).put("HeightWidthSize", ELEMENT_HEIGHT_WIDTH_SIZE).build());
}
