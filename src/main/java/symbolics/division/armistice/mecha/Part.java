package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.movement.Euclidean;

public interface Part extends Euclidean {
	/**
	 * Guaranteed to be called before the first tick, and after core has been
	 * associated with a loaded entity. All children must also be initialized
	 * in this method.
	 * <p>
	 * Assume that initialization order is chassis -> armor -> hull -> ordnance.
	 */
	void init(MechaCore core);

	/**
	 * Called on both client and server inside their respective tick methods.
	 */
	void tick();

	default void serverTick() {
		tick();
	}

	default void clientTick(float tickDelta) {
		tick();
	}

	/**
	 * this.absPos = parent.absPos + parent.absRot * parent.relSlotPos(this)
	 *
	 * @return Absolute position in world space
	 */
	default Vector3fc absPos() {
		Vector3f r = relPos().rotate(parent().absRot(), new Vector3f());
		return r.add(parent().absPos());
	}

	/**
	 * absolute rot = parent.absRot * this.relRot
	 *
	 * @return absolute rotation relative to <0, 0, 1> in world space along the shortest arc.
	 */
	default Quaternionf absRot() {
		return parent().absRot().mul(relRot(), new Quaternionf());
	}

	/**
	 * @return position relative to parent in parent's model coordinate space.
	 */
	default Vector3fc relPos() {
		return new Vector3f(0, 0, 0);
	}

	/**
	 * Defaults to no rotation
	 *
	 * @return rotation in model space (aka relative to <0, 0, 1>)
	 */
	default Quaternionf relRot() {
		return new Quaternionf().identity();
	}

	/**
	 * @return parent part, or throw exception if core.
	 */
	Part parent();

	default void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {

	}
}
