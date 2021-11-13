package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class AngryChickenEntity extends ResourceChickenEntity {
    public AngryChickenEntity(EntityType<? extends ResourceChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world, chickenData);
    }

    public static DefaultAttributeContainer.Builder createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1)
            .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0)
            .add(EntityAttributes.GENERIC_MAX_HEALTH, chickenData.baseHealth)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        //this.targetSelector.add(2, new FollowTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new AngryChickenGoal(this, 1.3D, true));
        // TODO
        //        this.goalSelector.add(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.add(6, new AnimalMateGoal(this, 1.0D));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compoundNBT) {
        super.readCustomDataFromNbt(compoundNBT);

        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.dataTracker.get(STRENGTH));
    }
}
