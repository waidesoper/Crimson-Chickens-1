package crimsonfluff.crimsonchickens.registry;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChickenRegistry implements IResourceChickenRegistry {
//    private static final Map<ResourceLocation, RandomCollection<ResourceChickenData>> spawnableBiomes = new HashMap<>();

    private static final ChickenRegistry INSTANCE = new ChickenRegistry();

    private final Map<String, ResourceChickenData> chickenInfo = new LinkedHashMap<>();


    /**
     * Return the instance of this class. This is useful for calling methods to the mod from a static or threaded context.
     *
     * @return Instance of this class
     */
    public static ChickenRegistry getRegistry() {return INSTANCE;}


    /**
     * Returns a ChickenData object for the given chicken.
     *
     * @param name Chicken for which ChickenData is requested.
     * @return Returns a ChickenData object for the given chicken.
     */
    public ResourceChickenData getChickenData(String name) {return chickenInfo.get(name);}

    public ResourceChickenData getChickenDataFromID(String name) {
        for (Map.Entry<String, ResourceChickenData> entry : chickenInfo.entrySet()) {
            String s = entry.getKey();
            ResourceChickenData ci = entry.getValue();
            if (ci.getEntityTypeRegistryID().toString().equals(name)) return chickenInfo.get(s);
        }

        return null; //chickenInfo.get("");
    }


    @Override
    public Map<String, ResourceChickenData> getChickens() {
        return Collections.unmodifiableMap(chickenInfo);
    }


    /**
     * Registers the supplied Chicken and associated data to the mod.
     * If the chicken already exists in the registry the method will return false.
     *
     * @param name        Name of the chicken being registered.
     * @param chickenData ChickenData of the chicken being registered
     * @return true       existing key is overwritten, new key is added in the registry.
     */
    public boolean registerChicken(String name, ResourceChickenData chickenData) {
        chickenInfo.put(name, chickenData);
        return true;
    }
}
