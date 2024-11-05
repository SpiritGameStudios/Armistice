package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import symbolics.division.armistice.Armistice;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@OnlyIn(value = Dist.CLIENT)
public record DrawHelper(GuiGraphics guiGraphics) {
	private static final RandomSource RANDOM = RandomSource.create();

	/**
	 * Each number is 5 x 9
	 * Spritesheet is 55 x 9
	 * Numbers are at 5 * number
	 */
	private static final ResourceLocation NUMBER_FONT = Armistice.id("textures/hud/number.png");

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

		RenderSystem.setShader(GameRenderer::getPositionShader);

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

	// region Lines
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

	@SuppressWarnings("deprecation")
	public void aLine(Vec2 start, Vec2 end, float thickness) {
		if (start.x > end.x) {
			float prevX = end.x;
			float prevY = end.y;

			end = new Vec2(start.x, start.y);
			start = new Vec2(prevX, prevY);
		}

		RenderSystem.setShader(GameRenderer::getPositionShader);

		BufferBuilder bufferBuilder = Tesselator.getInstance().begin(
			VertexFormat.Mode.QUADS,
			DefaultVertexFormat.POSITION
		);

		Matrix4f matrix = guiGraphics.pose().last().pose();

		float dx = end.x - start.x;
		float dy = end.y - start.y;
		float resolution = thickness * (float) Mth.fastInvSqrt(dx * dx + dy * dy);
		float w = thickness / 2;
		for (float t = 0; t <= 1; t += resolution) {
			float xt = start.x + t * dx;
			float yt = start.y + t * dy;
			bufferBuilder.addVertex(matrix, xt - w, yt - w, 0);
			bufferBuilder.addVertex(matrix, xt - w, yt + w, 0);
			bufferBuilder.addVertex(matrix, xt + w, yt + w, 0);
			bufferBuilder.addVertex(matrix, xt + w, yt - w, 0);
		}

		BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
	}
	// endregion

	// region Flicker
	// Yttr FlickeryRenderer was heavily referenced for this
	public void renderFlicker(Consumer<Vec2> render, Vec2 pos, Vector4f color) {
		float alpha = RANDOM.nextInt(15) == 0 ? Math.min(RANDOM.nextFloat(), 0.5F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * color.w;

		RenderSystem.setShaderColor(color.x, color.y, color.z, alpha * 0.05F);

		for (float i = -0.8F; i < 0.8F; i += 0.2F) {
			for (float j = -0.8F; j < 0.8F; j += 0.2F) {
				Vec2 currentPos = pos.add(new Vec2(i, j));
				render.accept(new Vec2(Mth.floor(currentPos.x), Mth.floor(currentPos.y)));
			}
		}

		RenderSystem.setShaderColor(color.x, color.y, color.z, alpha);
		render.accept(pos);

		RenderSystem.setShaderColor(color.x, color.y, color.z, color.w);
	}

	public static void renderHologramFlicker(BiConsumer<Vec3, Vector4f> render, Vec3 pos, Vector4f color) {

		float alpha = RANDOM.nextInt(10) == 0 ? Math.min(RANDOM.nextFloat(), 0.25F) : 1.0F;
		alpha = (0.3F + (alpha * 0.7F)) * color.w;

		render.accept(pos.offsetRandom(RANDOM, 0.0025F), new Vector4f(color.x, color.y, color.z, alpha));
		render.accept(pos.offsetRandom(RANDOM, 0.075F), new Vector4f(color.x, color.y, color.z, alpha));
	}
	// endregion

	public void renderNumber(int number, float x, float y, float size, Vector4f color) {
		IntStream digits = String.valueOf(number).chars()
			.map(Character::getNumericValue)
			.map(digit -> digit == -1 ? 10 : digit);


		AtomicInteger i = new AtomicInteger();
		digits.forEach(digit -> {
			renderFlicker(
				pos -> guiGraphics.blit(
					NUMBER_FONT,
					(int) pos.x, (int) pos.y,
					Mth.floor(5 * size), Mth.floor(9 * size),
					5 * digit, 0,
					5, 9,
					55, 9
				),
				new Vec2(x + (9 * size) * i.get(), y),
				color
			);

			i.getAndIncrement();
		});
	}

	public void renderCenteredNumber(int number, float x, float y, float size, Vector4f color) {
		int count = String.valueOf(number).length();
		float width = 9 * size * count;

		renderNumber(number, x - (width / 2), y, size, color);
	}

	public void renderLeftNumber(int number, float x, float y, float size, Vector4f color) {
		int count = String.valueOf(number).length();
		float width = 9 * size * count;

		renderNumber(number, x - width, y, size, color);
	}
}
