package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class AngryChickenEntity extends ResourceChickenEntity {
    public AngryChickenEntity(EntityType<? extends ResourceChickenEntity> type, World world, ResourceChickenData chickenData) {
        super(type, world, chickenData);
    }

    public static AttributeModifierMap.MutableAttribute createChickenAttributes(String name) {
        ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(name);

        return createMobAttributes()
            .add(Attributes.ATTACK_DAMAGE, 1)
            .add(Attributes.ATTACK_KNOCKBACK, 1)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0)
            .add(Attributes.MAX_HEALTH, chickenData.baseHealth)
            .add(Attributes.MOVEMENT_SPEED, chickenData.baseSpeed);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(4, new AngryChickenGoal(this, 1.3D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.entityData.get(STRENGTH));
    }
}
