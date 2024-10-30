package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.function.Consumer;

public record DrawHelper(GuiGraphics guiGraphics) {
	private static final RandomSource RANDOM = RandomSource.create();

	public void fill(float minX, float minY, float maxX, float maxY) {
		if (maxX > minX) {
			float prevMinX = minX;
			minX = maxX;
			maxX = prevMinX;
		}

		if (maxY > minY) {
			float prevMinY = minY;
			minY = maxY;
			maxY = prevMinY;
		}

		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
			VertexFormat.Mode.QUADS,
			DefaultVertexFormat.POSITION
		);

		Matrix4f matrix = guiGraphics.pose().last().pose();

		bufferBuilder.addVertex(matrix, minX, minY, 0);
		bufferBuilder.addVertex(matrix, minX, maxY, 0);
		bufferBuilder.addVertex(matrix, maxX, maxY, 0);
		bufferBuilder.addVertex(matrix, maxX, minY, 0);

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}

	public void line(Vec2 start, Vec2 end) {
		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
			VertexFormat.Mode.DEBUG_LINE_STRIP,
			DefaultVertexFormat.POSITION
		);

		Matrix4f matrix = guiGraphics.pose().last().pose();

		bufferBuilder.addVertex(matrix, start.x, start.y, 0);
		bufferBuilder.addVertex(matrix, end.x, end.y, 0);

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}

	public void hLine(float minX, float maxX, float y, float thickness) {
		if (minX > maxX) {
			float prevMinX = minX;
			minX = maxX;
			maxX = prevMinX;
		}

		fill(minX, y, maxX, y + thickness);
	}

	public void vLine(float x, float minY, float maxY, float thickness) {
		if (minY > maxY) {
			float prevMinY = minY;
			minY = maxY;
			maxY = prevMinY;
		}

		fill(x, minY, x + thickness, maxY);
	}

	public void aLine(float x1, float y1, float x2, float y2, float thickness) {
		if (x1 > x2) {
			float t = x2;
			x2 = x1;
			x1 = t;
			t = y2;
			y2 = y1;
			y1 = t;
		}

		float slope = (y2 - y1) / (x2 - x1);
		float reciprocal = -1 / slope;

		float dx = 1f / ((float) Mth.fastInvSqrt(thickness * thickness / (1 + reciprocal * reciprocal)) * 2);
		float dy = reciprocal * dx;


		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
			VertexFormat.Mode.TRIANGLE_STRIP,
			DefaultVertexFormat.POSITION
		);

		Matrix4f matrix = guiGraphics.pose().last().pose();

//		bufferBuilder.addVertex(matrix, x1 - thickness, y1 - thickness, 0);
//		bufferBuilder.addVertex(matrix, x1 - thickness, y1 + thickness, 0);
//		bufferBuilder.addVertex(matrix, x1 + thickness, y1 + thickness, 0);
//		bufferBuilder.addVertex(matrix, x1 + thickness, y1 - thickness, 0);
		Vector2f[] vs = new Vector2f[4];
		if (slope > 0) {
			vs[0] = new Vector2f(x1 - dx, y1 + dy);
			vs[1] = new Vector2f(x1 + dx, y1 - dy);
			vs[2] = new Vector2f(x2 - dx, y2 + dy);
			vs[3] = new Vector2f(x2 + dx, y2 - dy);
		} else {
			vs[0] = new Vector2f(x1 - dx, y1 - dy);
			vs[1] = new Vector2f(x1 + dx, y1 + dy);
			vs[2] = new Vector2f(x2 + dx, y2 + dy);
			vs[3] = new Vector2f(x2 - dx, y2 - dy);
		}

		for (Vector2f v : vs) {
			bufferBuilder.addVertex(matrix, v.x, v.y, 0);
		}

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}

	// Yttr FlickeryRenderer was heavily referenced for this
	public void renderFlicker(Consumer<Vec2> render, Vec2 pos) {
		float[] currentColor = RenderSystem.getShaderColor();

		float alpha = RANDOM.nextInt(15) == 0 ? Math.min(RANDOM.nextFloat(), 0.5F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * currentColor[3];

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], alpha * 0.05F);

		for (float i = -0.8F; i < 0.8F; i += 0.2F) {
			for (float j = -0.8F; j < 0.8F; j += 0.2F) {
				Vec2 currentPos = pos.add(new Vec2(i, j));
				render.accept(new Vec2(Mth.floor(currentPos.x), Mth.floor(currentPos.y)));
			}
		}

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], alpha);
		render.accept(pos);

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], 1);
	}

	public static void renderFlicker(Consumer<Vec3> render, Vec3 pos) {
		float[] currentColor = RenderSystem.getShaderColor();

		float alpha = RANDOM.nextInt(10) == 0 ? Math.min(RANDOM.nextFloat(), 0.25F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * currentColor[3];

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], alpha);
		render.accept(pos.offsetRandom(RANDOM, 0.0025F));
		render.accept(pos.offsetRandom(RANDOM, 0.075F));

		RenderSystem.setShaderColor(currentColor[0], currentColor[1], currentColor[2], 1);
	}
}
