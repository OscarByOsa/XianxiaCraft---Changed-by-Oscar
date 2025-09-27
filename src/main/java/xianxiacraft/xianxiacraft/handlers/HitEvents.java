package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import xianxiacraft.xianxiacraft.XianxiaCraft;
import xianxiacraft.xianxiacraft.handlers.Manuals.SpaceManual;
import xianxiacraft.xianxiacraft.util.CountNearbyBlocks;
import xianxiacraft.xianxiacraft.util.FreezeEffect;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.subtractQi;
import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.updateScoreboard;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.*;
import static xianxiacraft.xianxiacraft.handlers.Manuals.VampireManual.demonicManualManualPointIncrement;
import static xianxiacraft.xianxiacraft.util.FungalBlockData.*;

public class HitEvents implements Listener {

    private XianxiaCraft plugin;
    FreezeEffect freezeEffect;
    public HitEvents(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
        freezeEffect = new FreezeEffect(plugin);
    }

    // Обработка ударов по блокам с усиленными техниками (только для грибного и железнокожего мануалов)
    @EventHandler
    public void onPlayerHitBlock(PlayerInteractEvent event){

        Player player = (Player) event.getPlayer();

        // ГРИБНОЙ МАНУАЛ: преобразование блоков в грибные варианты в сферической области
        if(getManual(player).equals("Fungal Manual")) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (getPunchBool(player) && getQi(player) >= 20) {
                    subtractQi(player, 20);
                    updateScoreboard(player);

                    int radius = getStage(player); // Радиус эффекта равен стадии игрока

                    final Block clickedBlock = event.getClickedBlock();

                    if (clickedBlock != null) {

                        final World world = clickedBlock.getWorld();
                        int centerX = clickedBlock.getX();
                        int centerY = clickedBlock.getY();
                        int centerZ = clickedBlock.getZ();

                        // Обработка сферической области вокруг удаленного блока
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int y = centerY - radius; y <= centerY + radius; y++) {
                                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                    int i = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y) + (centerZ - z) * (centerZ - z);
                                    if (i <= radius * radius) {
                                        Block targetBlock = world.getBlockAt(x,y,z);
                                        Material targetBlockType = targetBlock.getType();

                                        // Поэтапное преобразование блоков в грибные варианты
                                        if (checkMycelium(targetBlockType)) {
                                            targetBlock.setType(Material.MYCELIUM);
                                        } else if (checkMushroom(targetBlockType)) {
                                            targetBlock.setType(Material.RED_MUSHROOM_BLOCK);
                                        } else if (checkStem(targetBlockType)) {
                                            targetBlock.setType(Material.MUSHROOM_STEM);
                                        } else if (targetBlockType == Material.GRASS || targetBlockType == Material.TALL_GRASS) {
                                            targetBlock.setType(Material.AIR); // Удаление травы
                                        } else if (checkToMushroomFlower(targetBlockType)) {
                                            targetBlock.setType(Material.RED_MUSHROOM); // Преобразование цветов в грибы
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if(getPunchBool(player)){
                    player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для усиления удара.");
                }
            }
        } 
        // ЖЕЛЕЗНОКОЖИЙ МАНУАЛ: разрушение блоков в сферической области
        else if(getManual(player).equals("Ironskin Manual")){
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (getPunchBool(player) && getQi(player) >= 10) {
                    subtractQi(player, 10);
                    updateScoreboard(player);

                    int radius = (int) Math.ceil(getStage(player)/2.0); // Радиус = половина стадии (округление вверх)

                    final Block clickedBlock = event.getClickedBlock();

                    if (clickedBlock != null) {

                        final World world = clickedBlock.getWorld();
                        int centerX = clickedBlock.getX();
                        int centerY = clickedBlock.getY();
                        int centerZ = clickedBlock.getZ();

                        // Обработка сферической области с разрушением блоков
                        for (int x = centerX - radius; x <= centerX + radius; x++) {
                            for (int y = centerY - radius; y <= centerY + radius; y++) {
                                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                    int i = (centerX - x) * (centerX - x) + (centerY - y) * (centerY - y) + (centerZ - z) * (centerZ - z);
                                    if (i <= radius * radius) {
                                        Block targetBlock = world.getBlockAt(x,y,z);
                                        Material targetBlockType = targetBlock.getType();

                                        // Разрушение всех блоков кроме воздуха и бедрокака
                                        if (targetBlockType != Material.AIR && targetBlockType != Material.BEDROCK) {
                                            targetBlock.setType(Material.AIR);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else if(getPunchBool(player)){
                    player.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для усиления удара.");
                }
            }
        }
    }

    // Основной обработчик получения урона (PvP, PvE, environmental damage)
    @EventHandler
    public void onPlayerTakeDamage(EntityDamageEvent event){

        // Обработка урона от сущностей (игроки, мобы, снаряды)
        if(event instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

            // ИГРОК АТАКУЕТ (других игроков или мобов)
            if(entityDamageByEntityEvent.getDamager() instanceof Player) {

                Player attackingPlayer = (Player) entityDamageByEntityEvent.getDamager();
                String attackingPlayerManual = getManual(attackingPlayer);
                int attackingPlayerStage = getStage(attackingPlayer);

                // Базовый расчет урона: стандартный урон + стадия × атака мануала
                double attackDamage = Math.ceil(event.getDamage()) + attackingPlayerStage * getManualAttackPerStage(attackingPlayerManual);

                // ЭФФЕКТЫ УСИЛЕННЫХ УДАРОВ (требуют активированной техники и 10 ци)
                if (getPunchBool(attackingPlayer) && getQi(attackingPlayer) >= 10) {
                    subtractQi(attackingPlayer, 10);
                    updateScoreboard(attackingPlayer);

                    // Уникальные эффекты для каждого мануала
                    switch (attackingPlayerManual) {
                        case "Ironskin Manual":
                            attackDamage += (attackingPlayerStage); // Дополнительный урон
                            break;
                        case "Fatty Manual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Сильный отбрасывающий эффект
                            Vector knockbackDirection = target.getLocation().toVector().subtract(attackingPlayer.getLocation().toVector()).normalize();
                            double knockbackStrength = 1.5 * attackingPlayerStage;
                            double knockbackVertical = 0.4 * attackingPlayerStage;
                            Vector knockbackVelocity = knockbackDirection.multiply(knockbackStrength).setY(knockbackVertical);
                            target.setVelocity(knockbackVelocity);
                            attackDamage += (2 * attackingPlayerStage);
                            break;
                        }
                        case "Ice Manual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Заморозка цели на 15 секунд × стадия
                            target.setFreezeTicks(20 * 15 * getStage(attackingPlayer));
                            attackDamage += attackingPlayerStage;
                            break;
                        }
                        case "Phoenix Manual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Поджигание цели на 7 секунд × стадия
                            target.setFireTicks(20 * 7 * getStage(attackingPlayer));
                            attackDamage += attackingPlayerStage;
                            break;
                        }
                        case "Poison Manual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Эффекты отравления и дезориентации
                            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON,20*5 + 5*getStage(attackingPlayer),getStage(attackingPlayer)));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,20*10 + 20*getStage(attackingPlayer),getStage(attackingPlayer)));
                            break;
                        }
                        case "Space Manual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Случайная телепортация цели в радиусе 15 × стадия
                            target.teleport(SpaceManual.getRandomLocationWithinRadius(target.getLocation(),15*attackingPlayerStage));
                            break;
                        }
                        case "Sugar Fiend": {
                            attackDamage += (2*attackingPlayerStage); // Двойной бонус урона
                            break;
                        }
                        case "Demonic Manual": {
                            // Вампиризм: восстановление здоровья при атаке
                            attackingPlayer.setHealth(attackingPlayer.getHealth()+1);
                            break;
                        }
                        case "LightningManual": {
                            LivingEntity target = (LivingEntity) event.getEntity();
                            // Призыв молнии в цель
                            target.getWorld().strikeLightning(target.getLocation());
                            attackDamage += 2*attackingPlayerStage;
                            break;
                        }
                    }
                } else if(getPunchBool(attackingPlayer)){
                    attackingPlayer.sendMessage(ChatColor.GOLD + "У вас недостаточно ци для усиления удара.");
                }

                // Бонус грибного мануала при нахождении на мицелии
                if(attackingPlayerManual.equals("Fungal Manual")){
                    if(CountNearbyBlocks.countNearbyBlocks(attackingPlayer,Material.MYCELIUM)>=1){
                        attackDamage += 2*attackingPlayerStage;
                    }
                }

                // Если цель НЕ игрок (моб)
                if (!(event.getEntity() instanceof Player)) {
                    event.setDamage(attackDamage);
                    // Вампирский мануал получает очки за убийство гуманоидов
                    if(attackingPlayerManual.equals("Demonic Manual")){
                        demonicManualManualPointIncrement(attackingPlayer,(LivingEntity) event.getEntity());
                    }
                    return;
                }

                // PvP РАСЧЕТ: атака игрока против защиты игрока
                Player defendingPlayer = (Player) event.getEntity();
                String defendingPlayerManual = getManual(defendingPlayer);
                int defendingPlayerStage = getStage(defendingPlayer);

                double defense = defendingPlayerStage * getManualDefensePerStage(defendingPlayerManual);

                // Удвоение защиты при активной ауре
                if(getAuraBool(defendingPlayer)){
                    defense *= 2;
                }

                double resultAttackDamage = attackDamage - defense;

                if (resultAttackDamage > 0) {
                    event.setDamage(resultAttackDamage+1); // Минимальный урон 1
                } else {
                    event.setDamage(1); // Гарантированный минимальный урон
                }

            } 
            // МОБЫ АТАКУЮТ ИГРОКА
            else if((entityDamageByEntityEvent.getDamager() instanceof LivingEntity) && (event.getEntity() instanceof Player)){
                Player defendingPlayer = (Player) event.getEntity();
                String defendingPlayerManual = getManual(defendingPlayer);
                int defendingPlayerStage = getStage(defendingPlayer);

                double defense = defendingPlayerStage * getManualDefensePerStage(defendingPlayerManual);
                double damage = event.getDamage();

                // Удвоение защиты при активной ауре
                if(getAuraBool(defendingPlayer)){
                    defense *= 2;
                }

                double resultDamage = damage - defense;

                if(resultDamage > 0){
                    event.setDamage(resultDamage);
                } else {
                    event.setDamage(0); // Полная защита от мобов
                }
            } 
            // СНАРЯДЫ И ВЗРЫВЫ против игрока
            else if(((entityDamageByEntityEvent.getDamager() instanceof Projectile) || entityDamageByEntityEvent.getDamager() instanceof Explosive) && (event.getEntity() instanceof Player)){
                Player defendingPlayer = (Player) event.getEntity();
                String defendingPlayerManual = getManual(defendingPlayer);
                int defendingPlayerStage = getStage(defendingPlayer);

                double defense = defendingPlayerStage * getManualDefensePerStage(defendingPlayerManual);
                double damage = event.getDamage();

                double resultDamage = damage - defense;

                if(resultDamage > 0){
                    event.setDamage(resultDamage);
                } else {
                    event.setDamage(1); // Минимальный урон от снарядов
                }
            } 
            // МОЛНИЯ против игрока
            else if(entityDamageByEntityEvent.getDamager() instanceof LightningStrike && (event.getEntity() instanceof Player)){
                if(getManual((Player) event.getEntity()).equals("LightningManual")){
                    event.setCancelled(true); // Иммунитет к молнии для мануала молнии
                    return;
                }
            }

        } 
        // ОКРУЖАЮЩИЙ УРОН (падение, огонь, утопление и т.д.)
        else if(event.getEntity() instanceof Player){

            Player defendingPlayer = (Player) event.getEntity();
            String defendingPlayerManual = getManual(defendingPlayer);
            int defendingPlayerStage = getStage(defendingPlayer);

            // ИММУНИТЕТЫ К СПЕЦИФИЧЕСКИМ ТИПАМ УРОНА
            if(defendingPlayerManual.equals("Phoenix Manual") && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.HOT_FLOOR)){
                event.setCancelled(true); // Иммунитет к огню
                return;
            }

            if(defendingPlayerManual.equals("Ice Manual") && (event.getCause() == EntityDamageEvent.DamageCause.FREEZE)){
                event.setCancelled(true); // Иммунитет к заморозке
                return;
            }

            if(defendingPlayerManual.equals("Poison Manual") && event.getCause() == EntityDamageEvent.DamageCause.POISON){
                event.setCancelled(true); // Иммунитет к яду
                return;
            }

            if(defendingPlayerManual.equals("LightningManual") && (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING)){
                event.setCancelled(true); // Иммунитет к молнии
                return;
            }

            // Расчет защиты от environmental damage
            double defense = defendingPlayerStage * getManualDefensePerStage(defendingPlayerManual);
            double damage = event.getDamage();

            double resultDamage = damage - defense;

            if(resultDamage > 0){
                event.setDamage(resultDamage);
            } else {
                event.setDamage(1); // Минимальный environmental урон
            }
        }
    }
}