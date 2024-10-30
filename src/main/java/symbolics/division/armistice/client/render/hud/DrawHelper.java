package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

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

		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
			VertexFormat.Mode.QUADS,
			DefaultVertexFormat.POSITION
		);

		Matrix4f matrix = guiGraphics.pose().last().pose();

		float dx = x2 - x1;
		float dy = y2 - y1;
		float resolution = thickness * (float) Mth.fastInvSqrt(dx * dx + dy * dy);
		float w = thickness / 2;
		for (float t = 0; t <= 1; t += resolution) {
			float xt = x1 + t * dx;
			float yt = y1 + t * dy;
			bufferBuilder.addVertex(matrix, xt - w, yt - w, 0);
			bufferBuilder.addVertex(matrix, xt - w, yt + w, 0);
			bufferBuilder.addVertex(matrix, xt + w, yt + w, 0);
			bufferBuilder.addVertex(matrix, xt + w, yt - w, 0);
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
