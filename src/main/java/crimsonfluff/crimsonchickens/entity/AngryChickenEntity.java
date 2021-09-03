package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AngryChickenEntity extends ResourceChickenEntity {
    public AngryChickenEntity(EntityType<? extends ResourceChickenEntity> type, Level world, ResourceChickenData chickenData) {
        super(type, world, chickenData);
    }

    public static AttributeSupplier.Builder createChickenAttributes(String name) {
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
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new AngryChickenGoal(this, 1.3D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new BreedGoal(this, 1.0D));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.entityData.get(STRENGTH));
    }
}
