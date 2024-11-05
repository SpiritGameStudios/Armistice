package symbolics.division.armistice.serialization;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class PartialEntityHitResult extends EntityHitResult {
	private final int id;

	public PartialEntityHitResult(int id, Vec3 location) {
		super(null, location);
		this.id = id;
	}

	public EntityHitResult finish(Level world) {
		return new EntityHitResult(Objects.requireNonNull(world.getEntity(id)), location);
	}
}
