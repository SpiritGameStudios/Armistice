package symbolics.division.armistice.client.render.hud;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;

public record DrawHelper(GuiGraphics guiGraphics) {
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
}
