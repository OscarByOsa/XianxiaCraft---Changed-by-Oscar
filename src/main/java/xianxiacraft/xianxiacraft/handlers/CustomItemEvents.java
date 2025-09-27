package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManualQiRegen;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getMaxQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.subtractQi;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.getHiddenByTaijiPaintingBool;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.setHiddenByTaijiPaintingBool;

public class CustomItemEvents implements Listener {

    private final XianxiaCraft plugin;
    public CustomItemEvents(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClickWithTool(PlayerInteractEvent event){

        Player player = event.getPlayer();

        // Обработка правого клика ПО БЛОКУ с инструментом в руке
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem() != null){

            // Проверка на Мотыгу Истинной Сущности (True Essence Hoe)
            NamespacedKey key = new NamespacedKey(plugin, "customUtilityTools");
            ItemMeta itemMeta = event.getItem().getItemMeta();
            assert itemMeta != null;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if(container.has(key , PersistentDataType.STRING)) {
                String foundValue = container.get(key, PersistentDataType.STRING);
                assert foundValue != null;
                if(foundValue.contains("superHoe")){

                    Block block = event.getClickedBlock(); // Блок, по которому кликнули

                    assert block != null;
                    int blockX = block.getX();
                    int blockY = block.getY();
                    int blockZ = block.getZ();

                    int radius = 5; // Радиус области обработки - 5 блоков

                    // Обработка области 11x3x11 блоков (радиус 5 по X/Z, 1 блок выше и ниже по Y)
                    for (int x = blockX - radius; x <= blockX + radius; x++) {
                        for (int y = blockY - 1; y <= blockY + 1; y++) { // 1 блок выше и ниже
                            for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                                Block blockToPlow = player.getWorld().getBlockAt(x, y, z);
                                // Преобразование травяных блоков и земли в вспаханную землю
                                if (blockToPlow.getType() == Material.GRASS_BLOCK || blockToPlow.getType() == Material.DIRT) {
                                    blockToPlow.setType(Material.FARMLAND);
                                }
                            }
                        }
                    }
                }
            }
            // Конец обработки Мотыги Истинной Сущности

        // Обработка правого клика (по блоку или в воздухе) с предметом в руке
        } else if((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null) {

            // Проверка на Картину Тайцзи (Taiji Painting)
            NamespacedKey key = new NamespacedKey(plugin, "customUtilityTools");
            ItemMeta itemMeta = event.getItem().getItemMeta();
            assert itemMeta != null;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING)) {
                String foundValue = container.get(key, PersistentDataType.STRING);
                assert foundValue != null;
                if (foundValue.contains("taijiPainting")) {
                    
                    // Механика скрытия/раскрытия с помощью Картины Тайцзи
                    double regenPercent = getManualQiRegen(getManual(player)); // Получение процента регенерации ци мануала игрока

                    // Если игрок уже скрыт - раскрытие
                    if(getHiddenByTaijiPaintingBool(player)){
                        // Показ игрока всем другим онлайн-игрокам
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            if(player1 != player){
                                player1.showPlayer(plugin,player);
                            }
                        }
                        setHiddenByTaijiPaintingBool(player,false);
                        player.sendMessage(ChatColor.GOLD + "Вы больше не скрыты.");
                    } 
                    // Если игрок не скрыт и имеет достаточно ци - скрытие
                    else if(getQi(player) >= (int) Math.ceil(getMaxQi(player)*(regenPercent+0.01))+1){
                        // Стоимость использования: максимальная ци × (регенерация + 1%) + 1
                        subtractQi(player,(int) Math.ceil(getMaxQi(player)*(regenPercent+0.01))+1);
                        setHiddenByTaijiPaintingBool(player,true);
                        // Скрытие игрока от всех других онлайн-игроков
                        for (Player player1 : Bukkit.getOnlinePlayers()) {
                            if(player1 != player){
                                player1.hidePlayer(plugin,player);
                            }
                        }
                        player.sendMessage(ChatColor.GOLD + "Вы были скрыты Картиной Тайцзи.");
                    } 
                    // Если недостаточно ци для активации
                    else {
                        player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для использования Картины Тайцзи.");
                    }
                    event.setCancelled(true); // Отмена стандартного действия предмета
                }
                // Конец обработки Картины Тайцзи
            }
        }
    }
}