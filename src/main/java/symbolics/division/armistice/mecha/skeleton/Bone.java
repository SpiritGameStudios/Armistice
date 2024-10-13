package symbolics.division.armistice.mecha.skeleton;

import org.jetbrains.annotations.Nullable;

public interface Bone {
	/**
	 * @return This bone's parent. Null if this bone is the root.
	 */
	@Nullable
	Bone parent();
}
