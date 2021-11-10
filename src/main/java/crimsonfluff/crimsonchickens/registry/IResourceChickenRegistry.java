package crimsonfluff.crimsonchickens.registry;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;

import java.util.Map;

public interface IResourceChickenRegistry {
    /**
     * Returns a ChickenData object for the given chicken.
     *
     * @param name Chicken for which ChickenData is requested.
     * @return Returns a ChickenData object for the given chicken.
     */
    ResourceChickenData getChickenData(String name);

    /**
     * Returns an unmodifiable copy of the Chicken Registry.
     * This is useful for iterating over all chickens without worry of changing data
     *
     * @return Returns unmodifiable copy of chicken registry.
     */
    Map<String, ResourceChickenData> getChickens();
}
