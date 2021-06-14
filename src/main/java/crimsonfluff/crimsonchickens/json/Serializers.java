package crimsonfluff.crimsonchickens.json;

import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;

public class Serializers {
    public static Gson initGson() {
        GsonBuilder gson = new GsonBuilder();

        gson.setPrettyPrinting();
        gson.serializeNulls();
        gson.disableHtmlEscaping();
        gson.registerTypeAdapter(DATA.getType(), DATA);

        return gson.create();
    }

    public static final BaseSerializer<ResourceChickenData> DATA = new BaseSerializer<ResourceChickenData>() {
        @Override
        public ResourceChickenData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            ResourceChickenData mt = new ResourceChickenData();

            // NOTE: ALL line entries MUST exist in JSON file, else it won't parse correctly !!!!

            mt.displayName = obj.get("displayName").getAsString();
            mt.eggLayTime = obj.get("eggLayTime").getAsInt();
            mt.canBreed = obj.get("canBreed").getAsBoolean();
            mt.dropItemItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("dropItem").getAsString()));
            try {
                mt.dropItemNBT = new JsonToNBT(new StringReader(obj.get("dropItemNBT").getAsString())).readStruct();
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
            mt.baseHealth = obj.get("baseHealth").getAsDouble();
            mt.baseSpeed = obj.get("baseSpeed").getAsDouble();
            mt.isFireImmune = obj.get("isFireImmune").getAsBoolean();
            mt.conversion = obj.get("conversion").getAsInt();

            return mt;
        }

        @Override
        public JsonElement serialize(ResourceChickenData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("displayName", src.displayName);
            if (src.dropItemItem.getItem() == Items.AIR)
                obj.addProperty("dropItem", "");
            else
                obj.addProperty("dropItem", src.dropItemItem.getRegistryName().toString());
            obj.addProperty("eggLayTime", src.eggLayTime);
            obj.addProperty("canBreed", src.canBreed);
            obj.addProperty("baseHealth", src.baseHealth);
            obj.addProperty("baseSpeed", src.baseSpeed);
            obj.addProperty("isFireImmune", src.isFireImmune);
            obj.addProperty("conversion", src.conversion);

            return obj;
        }

        @Override
        public Type getType() { return ResourceChickenData.class; }
    };
}
