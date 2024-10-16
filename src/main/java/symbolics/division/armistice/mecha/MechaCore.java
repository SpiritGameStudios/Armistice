package symbolics.division.armistice.mecha;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import symbolics.division.armistice.mecha.schematic.MechaSchematic;

/**
 * state holder and entity controller
 * <p>
 * parts are not visible to external objects, must provide all interfaces
 * to the rest of the system
 * <p>
 * updates model to reflect state.
 */
public class MechaCore implements Part {
	// codec contains schematic + state data for this specific instance
	// decoding reconstructs itself from the schem THEN applies state
	protected final MechaSchematic schematic;
	protected final ChassisPart chassis;
	protected final HullPart hull;
	private MechaEntity entity = null;

	public MechaCore(MechaSchematic schematic) {
		this.schematic = schematic;
		this.chassis = schematic.chassis().make();
		this.hull = schematic.hull().make();
	}

	public void initCore(MechaEntity e) {
		this.entity = e;
		init(this);
	}

	@Override
	public void init(MechaCore core) {
		chassis.init(core);
	}

	public void tick() {
		// temp: debug pathing -----------
		Player player = level().getNearestPlayer(entity(), 100);
		if (player != null) {
			var eye = player.getEyePosition();
			var limit = eye.add(player.getLookAngle().scale(30));
			BlockHitResult raycast = level().clip(new ClipContext(
				player.getEyePosition(),
				limit,
				ClipContext.Block.OUTLINE,
				ClipContext.Fluid.NONE,
				player
			));
			if (raycast.getType() == HitResult.Type.BLOCK) {
				chassis.setPathingTarget(raycast.getLocation());
			}
		}
		// --------------------------------

		if (entity().level().isClientSide()) {
			float tickDelta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
			chassis.clientTick(tickDelta);
		} else {
			chassis.serverTick();
			entity().setPos(entity.position().add(acceleration().scale(0.1)));
		}

//		entity().lookAt(EntityAnchorArgument.Anchor.EYES, entity().getEyePosition().add(chassis.direction()));
	}

	@Override
	public Part parent() {
		// todo: better than null for now. consider alternatives.
		throw new RuntimeException("MechaCore has no parent part");
	}

	public Vec3 position() {
		return entity.position();
	}

	public Vec3 direction() {
		return chassis.direction();
	}

	public Level level() {
		return entity.level();
	}

	public MechaEntity entity() {
		return entity;
	}

	public Vec3 acceleration() {
		return chassis.movement();
	}

	@Override
	public Vector3fc absPos() {
		return position().toVector3f();
	}

	@Override
	public Quaternionfc absRot() {
		return new Quaternionf().identity();
	}

	public ChassisPart debugGetChassis() {
		// temp: better encapsulation for renderer access. callback?
		return chassis;
	}
}
