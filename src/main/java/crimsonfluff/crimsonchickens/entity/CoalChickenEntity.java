package crimsonfluff.crimsonchickens.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CoalChickenEntity extends baseChickenEntity {
    public CoalChickenEntity(EntityType<? extends baseChickenEntity> type, World worldIn) {
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
            this.entityDropItem(Items.COAL);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }
}
