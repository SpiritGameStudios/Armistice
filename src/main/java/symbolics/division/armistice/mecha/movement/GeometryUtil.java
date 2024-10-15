package symbolics.division.armistice.mecha.movement;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class GeometryUtil {
    public static Vector3f rotatePitch(Vector3f v, float rad) {
        var axis = new Vector3f(v.x, 0, v.z).cross(v).normalize();
        return new Vector3f(v).rotateAxis(rad, axis.x, axis.y, axis.z);
    }

    // obviously not thread safe
//	private static final Matrix4f M_view = new Matrix4f();
//	private static final Matrix4f M_inv = new Matrix4f();
//	private static final Vector4f transformed = new Vector4f();

    public static Vec3 clampToFrustum(RotationConstraint constraint, Vec3 p, Vec3 p1, Vec3 p2, Vector3f ref) {
        return new Vec3(clampToFrustum(constraint, p.toVector3f(), p1.toVector3f(), p2.toVector3f(), ref));
    }

    /**
     * Clamp p to frustum described by constraint, rooted at p2 and pointing towards (p2-p1).
     * Not thread safe.
     * <p>
     * The canonical use case arises when there is a segment s1 with a segment s2 at the tip,
     * and s2 must be rotated. `constraint` is the allowed rotation of s1 relative to s2.
     * p1 is the base of s1, p2 is the tip of s1/base of s2, and p is the unconstrained (desired)
     * rotation of the tip of s2. After clamping, the result of this operation must be rescaled
     * to ensure s2 retains its correct length.
     *
     * @param constraint
     * @param p
     * @param p1
     * @param p2
     * @return
     */
    public static Vector3f clampToFrustum(RotationConstraint constraint, Vector3f p, Vector3f p1, Vector3f p2, Vector3f ref) {
        var look = p2.sub(p1, new Vector3f());
        var eye = p2;
        var center = eye.add(look, new Vector3f());

        Vector3f up;
        if (Mth.equal(0, look.x) && Mth.equal(0, look.z)) {
            // exactly up
            up = ref.mul(-1, new Vector3f());
        } else {
            // it can still have issues if you
            up = rotatePitch(
                    look, (float) Math.PI / 2
            );
        }

//		var up = rotatePitch(look, (float) Math.PI / 2);
        //rotatePitch(look, (float) Math.PI / 2);
        var M_view = new Matrix4f().setLookAt(
                eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z
        );
//		var M_inv = constraint.frustum().invertPerspectiveView(M_view, new Matrix4f());
        // apply view then perspective transformation
        Vector4f transformed = new Vector4f();
        var M2 = constraint.frustum().mul(M_view, new Matrix4f());
        M2.transform(p.x, p.y, p.z, 1, transformed);
        // clamp then reverse perspective division
        transformed.x = Mth.clamp(transformed.x, -1f, 1f) * transformed.w;
        transformed.y = Mth.clamp(transformed.y, -1f, 1f) * transformed.w;
        M2.invert();
        M2.transform(transformed, transformed);
        return look.set(transformed.x, transformed.y, transformed.z);
    }

//    public static Vec3 constrain2D() {
//        // ensure not behind p2. if so, set to straight ahead from it.
//
//        // ensure inside constrained  area
//    }

    public static Vec2 dir2Rad(Vec3 dir) {
        // yaw (x: positive z, y: positive x), pitch (x: horizontal length, y: positive y)
        return new Vec2((float) Math.atan(dir.x / dir.z), (float) Math.atan(dir.y / Math.sqrt(dir.x * dir.x + dir.z * dir.z)));
    }

    public static double chord(double radians) {
        return 2 * Math.sin(radians / 2);
    }

    public static boolean inRange(Vec3 a, Vec3 b, double tolerance) {
        return a.distanceToSqr(b) <= tolerance * tolerance;
    }

    /**
     * Gives a point on an up-down curve with a given flatness at the top.
     *
     * @param x a value from 0 to 1.
     * @return a value from 0 to 1 on the curve evaluated at x.
     */
    public static float easedCurve(float x) {
        return easedCurve(x, 3.5f);
    }

    public static float easedCurve(float x, float flatness) {
        return (float) (1.0 - Math.pow(Math.abs((x + 1) / 2), 2.5));
    }
}
