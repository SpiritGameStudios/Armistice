package symbolics.division.armistice.math;

import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public record OrdnanceFireInfo(Vector3fc pos, Vector3fc direction, Quaternionfc rotation, HitResult target) {
}
