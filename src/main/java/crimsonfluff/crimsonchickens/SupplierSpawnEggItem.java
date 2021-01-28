package crimsonfluff.crimsonchickens;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.RegistryObject;

// Work-around: Items are created before Entities, but need entity to make the spawn egg
// this routine by-passes that by passing a supplier instead of the actual entity

public class SupplierSpawnEggItem extends SpawnEggItem {
    private RegistryObject<?> supplier;

    public SupplierSpawnEggItem(EntityType<?> typeIn, RegistryObject<?> supplierIn, int primaryColorIn, int secondaryColorIn, Properties builder)
    {
        super(typeIn, primaryColorIn, secondaryColorIn, builder);
        supplier = supplierIn;
    }

    @Override
    public EntityType<?> getType(CompoundNBT p_208076_1_)
    {
        return (EntityType<?>) supplier.get();
    }
}