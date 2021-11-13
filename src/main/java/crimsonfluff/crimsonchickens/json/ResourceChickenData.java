package crimsonfluff.crimsonchickens.json;

import com.google.gson.JsonArray;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

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
    // 6= glass sounds, 7= ghast sounds, 8= bone sounds, 9= poison/radiation sounds
    public int hasTrait = 0;

    public NbtCompound dropItemNBT = null;      // cache the actual NBT
    public String dropItemItem = "";            // cant cache because FMLCommonSetUp is too late to register chickens
                                                // and modded items may not exist yet

    public JsonArray biomesWhitelist = null;
    public JsonArray biomesBlacklist = null;
    public boolean spawnNaturally = false;

    // spawning in the Nether has better chance if its of type Monster !
    public SpawnGroup spawnType;                        // 0=MONSTER, 1=CREATURE    - corrected from Forge version - config changes
    public int spawnWeight = 15;

    public ResourceChickenData() {}

    public String parentA = "";
    public String parentB = "";

    public boolean enabled = false;

    public Identifier chickenTexture = null;

    private transient Identifier entityTypeRegistryID;

    public Identifier getEntityTypeRegistryID() {
        return entityTypeRegistryID;
    }

    public void setEntityTypeRegistryID(Identifier entityTypeRegistryID) {
        this.entityTypeRegistryID = this.entityTypeRegistryID == null ? entityTypeRegistryID : this.entityTypeRegistryID;
    }


    // TODO: SpawnEggs
    public Item spawnEggItem;
//    public RegistryObject<Item> getSpawnEggItemRegistryObject() {
//        return spawnEggItemRegistryObject;
//    }
//    public void setSpawnEggItemRegistryObject(RegistryObject<Item> spawnEggItemRegistryObject) {
//        this.spawnEggItemRegistryObject = this.spawnEggItemRegistryObject == null ? spawnEggItemRegistryObject : this.spawnEggItemRegistryObject;
//    }
}
