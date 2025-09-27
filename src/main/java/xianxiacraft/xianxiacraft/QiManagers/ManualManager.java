package xianxiacraft.xianxiacraft.QiManagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import xianxiacraft.xianxiacraft.XianxiaCraft;
import xianxiacraft.xianxiacraft.handlers.Manuals.*;
import xianxiacraft.xianxiacraft.runnables.RunAura;
import xianxiacraft.xianxiacraft.util.ChatUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.updateScoreboard;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.*;
import static xianxiacraft.xianxiacraft.commands.CultPassiveCommandExecutor.runAuraMap;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FattyManual.fattyManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FungalManual.fungalManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.SugarFiendManual.sugarFiendQiMove;

public class ManualManager implements Listener {

    private final JavaPlugin plugin;

    // Карта для хранения мануалов игроков: UUID игрока -> название мануала
    public static Map<UUID, String> manualsMap = new HashMap<>();

    // Список всех доступных мануалов в игре (ОБЯЗАТЕЛЬНО ОБНОВЛЯТЬ ПРИ ДОБАВЛЕНИИ НОВЫХ)
    public static List<Object> manualList1 = new ArrayList<Object>(Arrays.asList(new IronSkinManual(),new FattyManual(),new IceManual(), new PhoenixManual(), new SpaceManual(), new SugarFiendManual(), new VampireManual(),new PoisonManual(),new FungalManual(),new LightningManual()));

    public ManualManager(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
    }

    // Получение мануала игрока (возвращает "none" если мануал не выбран)
    public static String getManual(Player player){
        return manualsMap.getOrDefault(player.getUniqueId(),"none");
    }

    // Получение бонуса атаки за стадию для конкретного мануала
    public static int getManualAttackPerStage(String manualName){
        for(Object object : manualList1) {
            if (object instanceof Manual) {
                Manual manual = (Manual) object;
                if (!(manual.getManualName().equals("none"))) {
                    if(manual.getManualName().equals(manualName)){
                        return manual.getAttackPerStage();
                    }
                }
            }
        }
        // Значение по умолчанию для игроков без мануала
        return 0;
    }

    // Получение бонуса защиты за стадию для конкретного мануала
    public static int getManualDefensePerStage(String manualName){
        for(Object object : manualList1){
            if(object instanceof Manual){
                Manual manual = (Manual) object;
                if(!(manual.getManualName().equals("none"))){
                    if(manual.getManualName().equals(manualName)){
                        return manual.getDefensePerStage();
                    }
                }
            }
        }
        // Значение по умолчанию для игроков без мануала
        return 0;
    }

    // Получение скорости регенерации ци для конкретного мануала
    public static double getManualQiRegen(String manualName){
        for(Object object : manualList1){
            if(object instanceof Manual){
                Manual manual = (Manual) object;
                if(!(manual.getManualName().equals("none"))){
                    if(manual.getManualName().equals(manualName)){
                        return manual.getQiRegeneration();
                    }
                }
            }
        }
        // Значение по умолчанию для игроков без мануала
        return 0.0;
    }

    // Обработчик смены мануала через взаимодействие с книгой
    @EventHandler
    public void onManualChange(PlayerInteractEvent event) {
        // Игнорирование взаимодействия второй рукой
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        Player p = event.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        // Проверка что предмет в руке - написанная книга
        if (itemInHand.getType() == Material.WRITTEN_BOOK) {
            BookMeta bookMeta = (BookMeta) itemInHand.getItemMeta();

            // Проверка что книга имеет заголовок и автора
            if (bookMeta != null && bookMeta.hasTitle() && bookMeta.hasAuthor()) {
                String bookTitle = bookMeta.getTitle();
                String bookAuthor = bookMeta.getAuthor();

                // Проверка что автор книги - Spellslot (специальный автор для мануалов)
                assert bookAuthor != null;
                if (bookAuthor.equals("Spellslot")) {

                    assert bookTitle != null;
                    // Проверка что книга отличается от текущего мануала игрока
                    if (!(bookTitle.equals(manualsMap.get(p.getUniqueId())))) {
                        // Обработка смены мануала через утилиту чата
                        ChatUtils.handleManualChange(event);
                        event.setCancelled(true); // Отмена стандартного действия книги
                    }
                }
            }
        }
    }

    // Метод принятия нового мануала (вызывается после подтверждения через чат)
    public static void accept(Player p, XianxiaCraft plugin) {
        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        if(itemInHand.getType() == Material.WRITTEN_BOOK){
            BookMeta bookMeta = (BookMeta) itemInHand.getItemMeta();
            String bookAuthor = bookMeta.getAuthor();
            String bookTitle = bookMeta.getTitle();

            // Проверка автора и что мануал отличается от текущего
            if(bookAuthor.equals("Spellslot")  && !(bookTitle.equals(manualsMap.get(p.getUniqueId())))) {

                // СБРОС ВСЕХ АКТИВНЫХ ТЕХНИК при смене мануала:
                
                // Отключение техник удара
                setPunchBool(p, false);
                setMoveBool(p, false);

                // Отключение уникальных эффектов движения
                sugarFiendQiMove(p, false);
                fattyManualQiMove(p, false);
                fungalManualQiMove(p, false);

                // Отключение остальных техник
                setMineBool(p, false);
                setAuraBool(p, false);
                // Остановка задачи ауры если она активна
                runAuraMap.getOrDefault(p,new RunAura(plugin,p)).stop();

                setFlyBool(p, false);
                // Сброс параметров полета
                p.setAllowFlight(false);
                p.setFlySpeed(0.1f);

                // Установка нового мануала и сброс прогресса культивации
                manualsMap.put(p.getUniqueId(), bookTitle);
                PointManager.setPoints(p, 1); // Сброс очков до 1
                QiManager.setQi(p, 0); // Обнуление ци
                p.sendMessage(ChatColor.GOLD + "Мануал культивации изменен на " + bookMeta.getDisplayName() + ".\nПрогресс культивации сброшен.");

                updateScoreboard(p); // Обновление интерфейса
            }
        }
    }

    // Сохранение данных мануалов в YAML файл
    public void saveManualData() {
        File dataFolder = plugin.getDataFolder();
        File dataFile = new File(dataFolder, "player_manual_data.yml");
        try {
            if (!dataFile.exists()) {
                dataFolder.mkdirs();
                dataFile.createNewFile();
            }

            FileWriter writer = new FileWriter(dataFile);
            Yaml yaml = new Yaml();
            yaml.dump(manualsMap, writer); // Сериализация карты в YAML
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка данных мануалов из YAML файла
    public void loadManualData() {
        File dataFile = new File(plugin.getDataFolder(), "player_manual_data.yml");
        try {
            if (dataFile.exists()) {
                FileReader reader = new FileReader(dataFile);
                LoaderOptions loaderOptions = new LoaderOptions();
                loaderOptions.setTagInspector(tag -> true); // Разрешение всех тегов
                Yaml yaml = new Yaml(loaderOptions);
                manualsMap = yaml.loadAs(reader, HashMap.class); // Десериализация из YAML
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}