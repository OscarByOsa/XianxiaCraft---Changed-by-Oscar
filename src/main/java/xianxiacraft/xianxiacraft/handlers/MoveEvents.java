package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.subtractQi;
import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.updateScoreboard;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.getAuraBool;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.getMoveBool;
import static xianxiacraft.xianxiacraft.util.FreezeEffect.createIce;
import static xianxiacraft.xianxiacraft.util.FreezeEffect.removeIce;


public class MoveEvents implements Listener {

    XianxiaCraft plugin;

    public MoveEvents(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
    }

    // Обработка ломания блоков - предотвращение фарма блоков ауры
    @EventHandler
    public void onPlayerMine(BlockBreakEvent event){
        Player player = event.getPlayer();
        // Запрет ломания блоков при активном движении для ледяного мануала (чтобы нельзя было фармить лед ауры)
        if((getMoveBool(player) && getManual(player).equals("Ice Manual"))){
            event.setCancelled(true);
        }

        // Закомментированный код для защиты блоков ауры других игроков
//        Block block = event.getBlock();
//        Location blockLocation = block.getLocation();
//        //if block is in any of the originalBlocks player maps that is not the player making this change, dont change it
//        for (Map<Block, BlockState> playerBlocks : originalBlockStates.values()) {
//            boolean blockAffected = playerBlocks.keySet().stream()
//                    .anyMatch(b -> b.getLocation().equals(blockLocation));
//            if (blockAffected) {
//                event.setCancelled(true);
//                return;
//            }
//        }
    }

    // Закомментированный обработчик взрывов для защиты блоков ауры
//    @EventHandler
//    public void onBlockExplode(EntityExplodeEvent event){
//
//        for (Block block : event.blockList()) {
//            Location blockLocation = block.getLocation();
//            //if block is in any of the originalBlocks player maps that is not the player making this change, dont change it
//            for (Map<Block, BlockState> playerBlocks : originalBlockStates.values()) {
//                boolean blockAffected = playerBlocks.keySet().stream()
//                        .anyMatch(b -> b.getLocation().equals(blockLocation));
//                if (blockAffected) {
//                    event.setCancelled(true);
//                    return;
//                }
//            }
//        }
//    }

    // Закомментированные системы хранения оригинальных состояний блоков и флагов очистки
//    private Map<UUID,Map<Block, BlockState>> originalBlockStates = new HashMap<>();
//    private Map<UUID,Boolean> isCleanedMap = new HashMap<>();

    // Обработка движения игрока - активация пассивных эффектов движения
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();

        // Проверка активированной техники движения
        if(getMoveBool(player)){
            String playerManual = getManual(player);

            // ЛЕДЯНОЙ МАНУАЛ: создание временного льда под ногами при движении
            if(playerManual.equals("Ice Manual")){
                if(!player.isSneaking()) { // Эффект не работает при крадущемся движении

                    Location[] locations = new Location[9]; // Область 3x3 блока

                    Location playerLocation = player.getLocation();
                    Location baseLocation = playerLocation.subtract(1, 1, 1); // Центрирование под игроком

                    // Заполнение массива локаций для области 3x3
                    int index = 0;
                    for (int x = 0; x < 3; x++) {
                        for (int z = 0; z < 3; z++) {
                            locations[index] = baseLocation.clone().add(x, 0, z);
                            index++;
                        }
                    }

                    // Создание льда на каждой локации с удалением через 5 секунд (100 тиков)
                    for (Location location : locations) {
                        createIce(location);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> removeIce(location), 100);
                    }
                }
            }
        }

        // Закомментированная сложная система ауры (преобразование блоков вокруг игрока)
        // Код содержал логику для каждого мануала с уникальными блоками и системой восстановления оригинальных состояний
    }

    // Обработка правого клика для активации рывков/телепортаций
    @EventHandler
    public void onRightClick(PlayerInteractEvent event){

        Player player = event.getPlayer();

        // Проверка активированной техники движения
        if(getMoveBool(player)) {
            String playerManual = getManual(player);
            switch (playerManual) {
                case "Space Manual": // ТЕЛЕПОРТАЦИЯ В НАПРАВЛЕНИИ ВЗГЛЯДА
                    if (event.getAction().toString().contains("RIGHT_CLICK")) {
                        if (!(getQi(player) >= 50)) {
                            player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для телепортации.");
                            return;
                        }
                        subtractQi(player, 50);
                        updateScoreboard(player);

                        Vector direction = player.getLocation().getDirection(); // Направление взгляда

                        double teleportDistance = 3 * getStage(player) + 5; // Дистанция зависит от стадии

                        Vector teleportVector = direction.multiply(teleportDistance);
                        player.teleport(player.getLocation().add(teleportVector)); // Мгновенная телепортация
                    }
                    break;
                    
                case "LightningManual":  // МОЛНИЕНОСНЫЙ РЫВОК С ПРОВЕРКОЙ ПРЕПЯТСТВИЙ
                    if (event.getAction().toString().contains("RIGHT_CLICK")) {
                        if (!(getQi(player) >= 20)) {
                            player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для рывка.");
                            return;
                        }
                        subtractQi(player, 20);
                        updateScoreboard(player);

                        Vector direction = player.getLocation().getDirection();
                        direction.setY(0).normalize(); // Обнуление Y чтобы игрок не мог летать

                        double teleportDistance = 3 * getStage(player) + 5;

                        Block blockingBlock = null;

                        // Проверка препятствий на пути телепортации с шагом 0.5 блока
                        for (double distance = 0; distance <= teleportDistance; distance += 0.5) {
                            Vector checkPosition = player.getEyeLocation().toVector().add(direction.clone().multiply(distance));
                            Location checkLocation = checkPosition.toLocation(player.getWorld());
                            Block block = checkLocation.getBlock();
                            if (!block.isPassable()) { // Если блок непроходимый
                                blockingBlock = block;
                                break;
                            }
                        }

                        // Если найден блок-препятствие - телепортация на вершину этого блока
                        if (blockingBlock != null) {
                            Block highestSolidBlock = null;
                            // Поиск самой высокой твердой поверхности над препятствием
                            for (int y = blockingBlock.getY() + 1; y <= player.getWorld().getMaxHeight(); y++) {
                                Block aboveBlock = blockingBlock.getWorld().getBlockAt(blockingBlock.getX(), y, blockingBlock.getZ());
                                if (aboveBlock.getType().isSolid()) {
                                    highestSolidBlock = aboveBlock;
                                } else {
                                    break;
                                }
                            }

                            if (highestSolidBlock != null) {
                                player.teleport(highestSolidBlock.getLocation());
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                            }
                        } else {
                            // Если препятствий нет - обычная телепортация
                            Vector teleportVector = direction.multiply(teleportDistance);
                            player.teleport(player.getLocation().add(teleportVector));
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                        }
                    }
                    break;

                case "Phoenix Manual": // ФЕНИКС: РЫВОК С ПОДЪЕМОМ В ВОЗДУХ
                    if (event.getAction().toString().contains("RIGHT_CLICK")) {
                        if (!(getQi(player) >= 30)) {
                            player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для рывка.");
                            return;
                        }
                        subtractQi(player, 30);
                        updateScoreboard(player);

                        Vector direction = player.getLocation().getDirection();
                        //direction.setY(1).normalize(); // Закомментированный вертикальный компонент

                        double strength = 1 + getStage(player)/3.0; // Сила рывка растет со стадией
                        Vector velocity = direction.multiply(strength);
                        //velocity.setY(1); // Закомментированный подъем

                        player.setVelocity(velocity); // Применение скорости вместо телепортации
                        break;
                    }
                    
                // СТАНДАРТНЫЙ РЫВОК для нескольких мануалов
                case "Poison Manual":
                case "Demonic Manual":
                case "Ironskin Manual":
                case "Sugar Fiend":
                    if (event.getAction().toString().contains("RIGHT_CLICK")) {
                        if (!(getQi(player) >= 20)) {
                            player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для рывка.");
                            return;
                        }
                        subtractQi(player, 20);
                        updateScoreboard(player);

                        Vector direction = player.getLocation().getDirection();
                        direction.setY(0).normalize(); // Горизонтальное движение без полета

                        double strength = 1 + getStage(player)/3.0;
                        Vector velocity = direction.multiply(strength);

                        player.setVelocity(velocity); // Рывок через установку скорости
                        break;
                    }
            } // Конец switch statement
        }
    }

    // Черный список блоков которые нельзя преобразовывать аурой (важные или функциональные блоки)
    public static boolean qiAuraBlacklisted(Block block){
        // Контейнеры и сельскохозяйственные блоки
        if(block.getState() instanceof Container || block.getType() == Material.FARMLAND ){
            return true;
        }
        
        // Специфические типы блоков которые защищены от преобразования
        switch (block.getType()) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case NETHER_WART:
            case SUGAR_CANE:
            case BAMBOO:
            case END_PORTAL:
            case NETHER_PORTAL:
            case END_PORTAL_FRAME:
            case OBSIDIAN:
                return true;
            default:
        }

        // Проверка блока над текущим (защита растений и посевов)
        Block aboveBlock = block.getRelative(BlockFace.UP);
        switch (aboveBlock.getType()) {
            case WHEAT:
            case CARROTS:
            case POTATOES:
            case BEETROOTS:
            case NETHER_WART:
            case SUGAR_CANE:
            case BAMBOO:
            case PUMPKIN_SEEDS:
            case MELON_SEEDS:
            case BEETROOT_SEEDS:
            case STONE:
                return true;
            default:
        }

        // Защита саженцев и травы по названию материала
        String materialAboveName = aboveBlock.getType().name();
        if(materialAboveName.endsWith("SAPLING") || materialAboveName.endsWith("GRASS")){
            return true;
        }

        // Защита кроватей и дверей
        String materialName = block.getType().name();
        if (materialName.endsWith("BED") || materialName.endsWith("DOOR")) {
            return true;
        }

        return false;
    }
}