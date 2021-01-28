package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BoneChickenEntity extends baseChickenEntity {
    public BoneChickenEntity(EntityType<? extends baseChickenEntity> type, World worldIn) {
        super(type, worldIn);
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void livingTick() {
        super.livingTick();

        if (!this.world.isRemote && this.isAlive() && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
            this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.entityDropItem(Items.BONE_MEAL);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }

    @Override
    public baseChickenEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {

        //baseChickenEntity entity = new baseChickenEntity(entitiesInit.BONE_CHICKEN.get(), p_241840_1_);
        //return entity;

        CrimsonChickens.LOGGER.info("CHICKENS: " + p_241840_2_.getDisplayName().toString());

        return null;   //Entities.BONE_CHICKEN.create(p_241840_1_);
    }
}
