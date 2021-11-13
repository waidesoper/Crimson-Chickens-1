package crimsonfluff.crimsonchickens.items;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ChickenSpawnEggItem extends SpawnEggItem {
    private final ResourceChickenData chickenData;

    public ChickenSpawnEggItem(EntityType<? extends MobEntity> entityType, int i, int j, Settings settings, ResourceChickenData chickenData) {
        super(entityType, i, j, settings);

        this.chickenData = chickenData;
    }

    @Override
    public Text getName() { return new LiteralText(chickenData.displayName); }

    @Override
    public String getTranslationKey() { return chickenData.displayName; }       // so we dont need 'item.crimsonchickens.duck_chicken_spawn_egg' entries in 'lang/en-us.json'
}
