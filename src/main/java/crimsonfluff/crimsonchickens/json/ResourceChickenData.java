package crimsonfluff.crimsonchickens.json;

import com.google.gson.JsonArray;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class ResourceChickenData {
    //private static RegistryObject<Item> spawnEggItemRegistryObject;
    public String name;                         // filename minus ".json", used for texture name and key in ChickensRegistry(name)
    public String displayName;
    public int eggLayTime;                      // 6000
    public boolean canBreed;

    public double baseHealth;                   // 4.0D
    public double baseSpeed;                    // 0.3F
    public boolean isFireImmune;
    public int conversion;

    public int eggPrimaryColor;
    public int eggSecondaryColor;

    // 0= nothing, 1= duck, 2= teleport on damage, 3= explode when killed, 4= give thorns damage, 5 = give fire damage
    // 6= glass sounds, 7= ghast sounds, 8= bone sounds
    public int hasTrait = 0;

    public CompoundNBT dropItemNBT = null;      // cache the actual NBT
    public Item dropItemItem = null;            // cache the actual item

    public JsonArray biomesWhitelist = null;
    public JsonArray biomesBlacklist = null;
    public boolean spawnNaturally = false;

    // spawning in the Nether has better chance if its of type Monster? WIP
    public int spawnType = 0;                        // 0=CREATURE, 1=MONSTER
    public int spawnWeight = 15;

    public ResourceChickenData() {}

    public String parentA = "";
    public String parentB = "";


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
