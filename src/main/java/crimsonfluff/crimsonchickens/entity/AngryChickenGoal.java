package crimsonfluff.crimsonchickens.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class AngryChickenGoal extends MeleeAttackGoal {
    public AngryChickenGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }

    @Override
    protected double getAttackReachSqr(LivingEntity entity) {
        return super.getAttackReachSqr(entity) * 0.8f;
    }
}
