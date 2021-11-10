package crimsonfluff.crimsonchickens.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class AngryChickenGoal extends MeleeAttackGoal {
    public AngryChickenGoal(PathAwareEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        return super.getSquaredMaxAttackDistance(entity) * 0.8f;
    }
}
