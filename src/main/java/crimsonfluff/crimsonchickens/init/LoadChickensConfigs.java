package crimsonfluff.crimsonchickens.init;

import com.google.gson.Gson;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.json.Serializers;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraft.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.*;

public class LoadChickensConfigs {
    public static void loadConfigs() {
        File dir = FMLPaths.CONFIGDIR.get().resolve(CrimsonChickens.MOD_ID).toFile();
        Gson gson = Serializers.initGson();

        if (! dir.exists()) {
            if (dir.mkdir()) {
                File filename = new File(dir.getAbsolutePath() + "/coal.json");

                ResourceChickenData chickenData = new ResourceChickenData();
                chickenData.displayName = "Coal Chicken";
                chickenData.dropItemItem = Items.COAL;
                chickenData.canBreed = true;
                chickenData.baseHealth = 4D;
                chickenData.baseSpeed = 0.25D;
                chickenData.isFireImmune = false;
                chickenData.conversion = 1000;
                chickenData.eggLayTime = 6000;

                try (FileWriter file = new FileWriter(filename)) {
                    file.write(gson.toJson(chickenData));

//            } catch (FileAlreadyExistsException ignored) {
                } catch (IOException e) {
                    CrimsonChickens.LOGGER.error("Failed to create default coal.json");
                }
            }
        }

        File[] files = dir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (files == null) return;

        for (File file : files) {
            try {
                FileReader reader = new FileReader(file);
                ResourceChickenData chickenData = gson.fromJson(reader, ResourceChickenData.class);
                reader.close();

                String name = file.getName();
                name = name.substring(0, name.length() - 5);        // remove ".json"

                chickenData.name = name;
                ChickenRegistry.getRegistry().registerChicken(name, chickenData);
                RegistryHandler.registerChicken(name, chickenData);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
