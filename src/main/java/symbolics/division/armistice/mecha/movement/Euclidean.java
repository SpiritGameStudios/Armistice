package symbolics.division.armistice.mecha.movement;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

public interface Euclidean {
	Vector3fc absPos();

	Quaternionf absRot();

	Vector3fc relPos();

	Quaternionf relRot();

	default void transformAbsolute(PoseStack poseStack) {
		Vector3fc p = absPos();
		poseStack.translate(p.x(), p.y(), p.z());
		poseStack.mulPose(absRot());
	}

	default void transformRelative(PoseStack poseStack) {
		Vector3fc p = relPos();
		poseStack.translate(p.x(), p.y(), p.z());
		poseStack.mulPose(relRot());
	}
}
