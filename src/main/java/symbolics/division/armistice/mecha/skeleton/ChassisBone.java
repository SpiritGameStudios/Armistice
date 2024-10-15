package symbolics.division.armistice.mecha.skeleton;

public interface ChassisBone extends Bone {
    int legCount();

    LegBone leg(int leg); // 0 to n-1

    HullBone hull();
}
