package crimsonfluff.crimsonchickens.init;

import com.google.gson.Gson;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.json.Serializers;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import crimsonfluff.crimsonchickens.registry.RegistryHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.filefilter.FileFilterUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class initChickenConfigs {
    private static final Path MOD_ROOT = ModList.get().getModFileById(CrimsonChickens.MOD_ID).getFile().getFilePath();

    public static void loadConfigs() {
        File dir = FMLPaths.CONFIGDIR.get().resolve(CrimsonChickens.MOD_ID).toFile();

        // copy configs from 'data/crimsonchickens'
        // only set up defaults if 'config/crimsonchickens' folder does not exist
        if (! dir.exists()) {
            if (dir.mkdir()) {
                copyDefaultConfigs("/data/crimsonchickens/configs/vanilla", Paths.get(dir.toString(), "vanilla"));
                copyDefaultConfigs("/data/crimsonchickens/configs/modded", Paths.get(dir.toString(), "modded"));
            }
        }

        loadChickenConfigs(Paths.get(dir.toString(),"vanilla"));
        loadChickenConfigs(Paths.get(dir.toString(),"modded"));
    }

    private static void loadChickenConfigs(Path path) {
        File[] files = path.toFile().listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (files == null) return;

        Gson gson = Serializers.initGson();
        for (File file : files) {
            try {
                FileReader reader = new FileReader(file);
                ResourceChickenData chickenData = gson.fromJson(reader, ResourceChickenData.class);
                reader.close();

                if (chickenData.enabled) {
                    String name = file.getName();
                    name = name.substring(0, name.length() - 5);        // remove ".json"

                    chickenData.name = name;
                    ChickenRegistry.getRegistry().registerChicken(name, chickenData);
                    RegistryHandler.registerChicken(name, chickenData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void copyDefaultConfigs(String dataPath, Path targetPath) {
        if (! Files.exists(targetPath)) targetPath.toFile().mkdir();

        if (Files.isRegularFile(MOD_ROOT)) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(MOD_ROOT)) {
                Path path = fileSystem.getPath(dataPath);
//                if (! Files.exists(targetPath)) targetPath.toFile().mkdir();

//                if (Files.exists(path))
                copyFiles(path, targetPath);

            } catch (IOException e) {
                CrimsonChickens.LOGGER.error("Could not load source {}!!", MOD_ROOT);
                e.printStackTrace();
            }
        }
        else if (Files.isDirectory(MOD_ROOT)) {
  //          if (! Files.exists(targetPath)) targetPath.toFile().mkdir();

            copyFiles(Paths.get(MOD_ROOT.toString(), dataPath), targetPath);
        }
    }

    private static void copyFiles(Path sourcePath, Path targetPath) {
        try (Stream<Path> sourceStream = Files.walk(sourcePath)) {
            sourceStream.filter(files -> files.getFileName().toString().endsWith(".json"))
                .forEach(file -> {
                    File targetFile = new File(Paths.get(targetPath.toString(), file.getFileName().toString()).toString());
                    try {
                        Files.copy(file, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    } catch (IOException e) {
                        CrimsonChickens.LOGGER.error("Could not copy file: {}, Target: {}", file, targetPath);
                    }
                });

        } catch (IOException e) {
            CrimsonChickens.LOGGER.error("Could not stream source files: {}", sourcePath);
        }
    }
}
