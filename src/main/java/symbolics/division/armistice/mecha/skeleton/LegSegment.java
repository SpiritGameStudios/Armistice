package symbolics.division.armistice.mecha.skeleton;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface LegSegment {
	LegSegment parent();

	@Nullable
	LegSegment child();

	ResourceLocation id();

	Vec3 position();

	Vec3 direction();
}
