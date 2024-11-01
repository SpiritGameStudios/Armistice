package symbolics.division.armistice.mecha;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class AbstractMechaPart implements Part {
	protected MechaCore core;

	@Override
	public void init(MechaCore core) {
		this.core = core;
	}

	/**
	 * Quick sanity check to avoid NPEs and the like.
	 *
	 * @return whether the part is fully initialized and safe to be ticked.
	 */
	public boolean ready() {
		return core != null;
	}

	@Override
	public void tick() {
		if (!ready()) throw new RuntimeException("Part was ticked before it was ready!");
	}

	@Override
	public void renderDebug(MultiBufferSource bufferSource, PoseStack poseStack) {
		if (!ready()) throw new RuntimeException("Part debug render was called before it was ready!");
	}
}
