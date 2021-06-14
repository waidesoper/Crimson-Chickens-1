package crimsonfluff.crimsonchickens.registry;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;

public interface IResourceChickenRegistry {
    /**
     * Returns a ChickenData object for the given chicken.
     *
     * @param name Chicken for which ChickenData is requested.
     * @return Returns a ChickenData object for the given chicken.
     */
    ResourceChickenData getChickenData(String name);
}
