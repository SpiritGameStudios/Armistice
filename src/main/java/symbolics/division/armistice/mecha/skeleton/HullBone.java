package symbolics.division.armistice.mecha.skeleton;

public interface HullBone extends Bone {
    int ordnanceCount();

    Bone ordnance(int leg);
}
