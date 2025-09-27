package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import xianxiacraft.xianxiacraft.XianxiaCraft;
import xianxiacraft.xianxiacraft.handlers.Manuals.*;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;


public class PointHandler implements Listener {

    public PointHandler(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Основной обработчик взаимодействия с предметами (правый клик) для мануалов, использующих предметы в руке
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand(); // Предмет в основной руке
        String manual = getManual(player); // Получение мануала игрока

        // Обработка мануалов, которые используют предметы через правый клик (без потребления)
        switch (manual) {
            case "Ironskin Manual":
                IronSkinManual.ironSkinManualPointIncrement(itemInHand, player); // Железные слитки
                break;
            case "Ice Manual":
                IceManual.iceManualPointIncrement(itemInHand, player); // Лед
                break;
            case "LightningManual":
                LightningManual.lightningManualPointIncrement(itemInHand, player); // Медные слитки
                break;
        }
    }

    // Обработка установки блоков - предотвращение размещения льда для ледяного мануала
    @EventHandler
    public void onPlayerPlaceEvent(BlockPlaceEvent event){

        Player player = event.getPlayer();
        String manual = getManual(player);

        // Запрет размещения льда для игроков с ледяным мануалом (чтобы предотвратить злоупотребление)
        if(manual.equals("Ice Manual") && event.getBlockPlaced().getType() == Material.ICE){
            event.setCancelled(true); // Можно изменить на уменьшение количества предмета вместо отмены
        }
    }

    // Обработка потребления предметов (еда, зелья) для мануалов, использующих механику поедания
    @EventHandler
    public void onPlayerEatEvent(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String manual = getManual(player);

        // Проверка что предмет является съедобным
        if(!item.getType().isEdible()){
            return;
        }

        // Обработка различных мануалов с уникальной механикой потребления
        switch (manual) {
            case "Fatty Manual": // Мануал обжоры: любая еда
                FattyManual.fattyManualPointIncrement(player, item);
                event.setCancelled(true); // Полная отмена стандартного потребления
                break;
            case "Poison Manual": // Ядовитый мануал: специфические ядовитые предметы
                event.setCancelled(PoisonManual.poisonManualPointIncrement(player, item)); // Условная отмена
                break;
            case "Space Manual": // Космический мануал: плоды хоруса
                SpaceManual.spaceManualPointIncrement(player, item);
                break;
            case "Sugar Fiend": // Сахарный маньяк: сладости (только в аду)
                event.setCancelled(SugarFiendManual.sugarFiendManualPointIncrement(player,item)); // Условная отмена
                break;
            case "Fungal Manual": // Грибной мануал: подозрительное рагу
                FungalManual.fungalManualPointIncrement(player,item);
                break;
        }
    }

    // Обработка смерти игрока для мануала феникса (прогресс через смерть/возрождение)
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();

        // Мануал феникса получает очки культивации при смерти (тематика возрождения)
        if(getManual(player).equals("Phoenix Manual")){
            PhoenixManual.phoenixManualPointIncrement(player);
        }
    }
}