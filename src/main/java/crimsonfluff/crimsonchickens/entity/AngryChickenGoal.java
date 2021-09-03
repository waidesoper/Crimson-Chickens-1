package crimsonfluff.crimsonchickens.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class AngryChickenGoal extends MeleeAttackGoal {
    public AngryChickenGoal(PathfinderMob creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity entity) {
        return super.getAttackReachSqr(entity) * 0.8f;
    }
}
