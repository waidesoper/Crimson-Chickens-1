package crimsonfluff.crimsonchickens.json;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class ResourceChickenData {
    public String name;                     // filename minus ".json", used for texture name and key in ChickensRegistry(name)
    public String displayName;
//    public String dropItem;
    public int eggLayTime;
    public boolean canBreed;

    public double baseHealth;       // 4.0D
    public double baseSpeed;        // 0.3F
    public boolean isFireImmune;
    public int conversion;

    public CompoundNBT dropItemNBT = null;  // cache the actual NBT
    public Item dropItemItem = null;        // cache the actual item

    public ResourceChickenData() {}



    private transient RegistryObject<Item> spawnEggItemRegistryObject;
    private transient ResourceLocation entityTypeRegistryID;

    public ResourceLocation getEntityTypeRegistryID() {
        return entityTypeRegistryID;
    }

    public void setEntityTypeRegistryID(ResourceLocation entityTypeRegistryID) {
        this.entityTypeRegistryID = this.entityTypeRegistryID == null ? entityTypeRegistryID : this.entityTypeRegistryID;
    }

    public RegistryObject<Item> getSpawnEggItemRegistryObject() {
        return spawnEggItemRegistryObject;
    }

    public void setSpawnEggItemRegistryObject(RegistryObject<Item> spawnEggItemRegistryObject) {
        this.spawnEggItemRegistryObject = this.spawnEggItemRegistryObject == null ? spawnEggItemRegistryObject : this.spawnEggItemRegistryObject;
    }
}
