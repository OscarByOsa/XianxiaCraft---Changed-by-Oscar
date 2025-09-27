package xianxiacraft.xianxiacraft.QiManagers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QiManager implements Listener {

    // Карта для хранения текущего количества ци игроков: UUID -> количество ци
    private static Map<UUID,Integer> qiMap = new HashMap<>();
    private final JavaPlugin plugin;

    public QiManager(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    // Получение текущего количества ци игрока (возвращает 0 если запись отсутствует)
    public static int getQi(Player player){
        return qiMap.getOrDefault(player.getUniqueId(),0);
    }

    // Добавление ци игроку (увеличение текущего значения)
    public static void addQi(Player player, int qi){
        qiMap.put(player.getUniqueId(), qiMap.getOrDefault(player.getUniqueId(),0) + qi);
    }

    // Установка точного количества ци игроку (перезапись значения)
    public static void setQi(Player player, int qi){
        qiMap.put(player.getUniqueId(), qi);
    }

    // Вычитание ци у игрока (уменьшение текущего значения)
    public static void subtractQi(Player player, int qi){
        qiMap.put(player.getUniqueId(), qiMap.getOrDefault(player.getUniqueId(),0) - qi);
    }

    // Сохранение данных ци в YAML файл
    public void saveQiData() {
        File dataFolder = plugin.getDataFolder();
        File dataFile = new File(dataFolder, "qi_data.yml");
        try {
            // Создание файла если он не существует
            if (!dataFile.exists()) {
                dataFolder.mkdirs();
                dataFile.createNewFile();
            }

            // Запись данных в файл
            FileWriter writer = new FileWriter(dataFile);
            Yaml yaml = new Yaml();
            yaml.dump(qiMap, writer); // Сериализация карты ци в YAML
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка данных ци из YAML файла
    public void loadQiData() {
        File dataFile = new File(plugin.getDataFolder(), "qi_data.yml");
        try {
            if (dataFile.exists()) {
                FileReader reader = new FileReader(dataFile);
                LoaderOptions loaderOptions = new LoaderOptions();
                loaderOptions.setTagInspector(tag -> true); // Разрешение всех YAML тегов
                Yaml yaml = new Yaml(loaderOptions);
                qiMap = yaml.loadAs(reader, HashMap.class); // Десериализация из YAML в карту
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}