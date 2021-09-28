package crimsonfluff.crimsonchickens.json;

import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.JsonToNBT;

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
            //mt.dropItemItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("dropItem").getAsString()));
            mt.dropItemItem = obj.get("dropItem").getAsString();
            String st = obj.get("dropItemNBT").getAsString();
            if (st.length() != 0) {
                try {
                    mt.dropItemNBT = new JsonToNBT(new StringReader(st)).readStruct();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
            mt.baseHealth = obj.get("baseHealth").getAsDouble();
            mt.baseSpeed = obj.get("baseSpeed").getAsDouble();
            mt.isFireImmune = obj.get("isFireImmune").getAsBoolean();
            mt.conversion = obj.get("conversion").getAsInt();
            mt.eggPrimaryColor = obj.get("eggColorForeground").getAsInt();
            mt.eggSecondaryColor = obj.get("eggColorBackground").getAsInt();
            mt.hasTrait = obj.get("hasTrait").getAsInt();

            JsonArray element;
            element = obj.getAsJsonArray("biomesWhitelist");
            if (element != null && element.size() != 0) mt.biomesWhitelist = element;   //.getAsJsonArray();

            element = obj.getAsJsonArray("biomesBlacklist");
            if (element != null && element.size() != 0) mt.biomesBlacklist = element;   //.getAsJsonArray();

            mt.spawnNaturally = obj.get("spawnNaturally").getAsBoolean();
            mt.spawnType = obj.get("spawnType").getAsInt();
            mt.spawnWeight = obj.get("spawnWeight").getAsInt();

            mt.parentA = obj.get("parentA").getAsString();
            mt.parentB = obj.get("parentB").getAsString();
            mt.enabled = obj.get("enabled").getAsBoolean();

            return mt;
        }

        @Override
        public JsonElement serialize(ResourceChickenData src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();

            obj.addProperty("displayName", src.displayName);
            obj.addProperty("dropItem", src.dropItemItem);
            obj.addProperty("dropItemNBT", src.dropItemNBT == null ? "" : src.dropItemNBT.toString());
            obj.addProperty("eggLayTime", src.eggLayTime);
            obj.addProperty("canBreed", src.canBreed);
            obj.addProperty("baseHealth", src.baseHealth);
            obj.addProperty("baseSpeed", src.baseSpeed);
            obj.addProperty("isFireImmune", src.isFireImmune);
            obj.addProperty("conversion", src.conversion);
            obj.addProperty("eggColorForeground", src.eggPrimaryColor);
            obj.addProperty("eggColorBackground", src.eggSecondaryColor);
            obj.addProperty("hasTrait", src.hasTrait);

            if (src.biomesWhitelist != null)
                obj.add("biomesWhitelist", src.biomesWhitelist);

            if (src.biomesBlacklist != null)
                obj.add("biomesBlacklist", src.biomesBlacklist);

            obj.addProperty("spawnNaturally", src.spawnNaturally);
            obj.addProperty("spawnWeight", src.spawnWeight);
            obj.addProperty("spawnType", src.spawnType);

            obj.addProperty("parentA", src.parentA);
            obj.addProperty("parentB", src.parentB);
            obj.addProperty("enabled", src.enabled);

            return obj;
        }

        @Override
        public Type getType() { return ResourceChickenData.class; }
    };
}
