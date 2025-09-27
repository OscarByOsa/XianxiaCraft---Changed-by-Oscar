package xianxiacraft.xianxiacraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xianxiacraft.xianxiacraft.QiManagers.TechniqueManager;
import xianxiacraft.xianxiacraft.runnables.RunAura;

import java.util.HashMap;
import java.util.Map;

// Статические импорты менеджеров для удобства
import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getMaxQi;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.setQi;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.getAuraBool;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.qiAuraGlow;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FattyManual.fattyManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FungalManual.fungalManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.SugarFiendManual.sugarFiendQiMove;

// Исполнитель команд для пассивных техник cultivation (культивации)
public class CultPassiveCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;
    // Карта для хранения активных ауры для каждого игрока
    public static Map<Player, RunAura> runAuraMap = new HashMap<Player,RunAura>();

    public CultPassiveCommandExecutor(JavaPlugin plugin){
        this.plugin = plugin;
    }


    // Все команды культивации, дающие пассивные эффекты
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        // Проверка, что отправитель - игрок
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("Для использования этой команды необходимо быть игроком.");
            return true;
        }

        Player sender = (Player) commandSender;



        // Команда: detonate (самоподрыв)
        if(command.getName().equalsIgnoreCase("detonate")){

            // Проверка, что ци заполнена полностью
            if(!(getQi(sender) >= getMaxQi(sender))){
                sender.sendMessage(ChatColor.GOLD + "Для самоподрыва необходимо иметь полный запас ци.");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Вы совершили самоподрыв.");
            boolean fire = false;
            // Если у игрока мануал Феникса, взрыв будет огненным
            if(getManual(sender).equals("Phoenix Manual")){
                fire = true;
            }
            // Если у игрока мануал Молнии, вызывается удар молнии
            if(getManual(sender).equals("LightningManual")){
                sender.getWorld().strikeLightning(sender.getLocation());
            }
            // Создание взрыва с мощностью, зависящей от стадии культивации
            sender.getWorld().createExplosion(sender.getLocation(),getStage(sender),fire);
            // Убийство игрока
            sender.setHealth(0.0);
            // Обнуление ци
            setQi(sender,0);

        }

        // Команда: qipunch (ци-удар)
        if(command.getName().equalsIgnoreCase("qipunch")){
            // Требуется минимум 1 стадия
            if(!(getStage(sender) >= 1)){
                sender.sendMessage(ChatColor.GOLD + "Для использования этой техники необходим культивационный базис Стадии 1 или выше.");
                return true;
            }
            // Получение текущего состояния способности
            boolean currentPunchBool = TechniqueManager.getPunchBool(sender);

            // Переключение состояния
            if(currentPunchBool){
                sender.sendMessage(ChatColor.GOLD + "Ци-Удар: Неактивен");
            } else {
                sender.sendMessage(ChatColor.GOLD + "Ци-Удар: Активен");
            }

            TechniqueManager.setPunchBool(sender,!currentPunchBool);
            return true;
        }

        // Команда: qimine (ци-копание)
        if(command.getName().equalsIgnoreCase("qimine")){
            // Требуется минимум 2 стадия
            if(!(getStage(sender) >= 2)){
                sender.sendMessage(ChatColor.GOLD + "Для использования этой техники необходим культивационный базис Стадии 2 или выше.");
                return true;
            }

            boolean currentMineBool = TechniqueManager.getMineBool(sender);

            if(currentMineBool){
                sender.sendMessage(ChatColor.GOLD + "Ци-Копание: Неактивно");
                // Удаление эффекта скорости копания
                sender.removePotionEffect(PotionEffectType.FAST_DIGGING);
            } else {
                sender.sendMessage(ChatColor.GOLD + "Ци-Копание: Активно");
                // Расчет уровня эффекта в зависимости от стадии (макс. уровень 3)
                int amplifier = (int) (getStage(sender)/2.0)-1;
                if (amplifier > 3){
                    amplifier = 3;
                }
                // Добавление эффекта ускорения копания на неограниченное время
                sender.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE,amplifier,false,false,false));
            }

            TechniqueManager.setMineBool(sender,!currentMineBool);

            return true;
        }

        // Команда: qimove (ци-движение)
        if(command.getName().equalsIgnoreCase("qimove")){
            // Требуется минимум 3 стадия
            if(!(getStage(sender) >= 3)){
                sender.sendMessage(ChatColor.GOLD + "Для использования этой техники необходим культивационный базис Стадии 3 или выше.");
                return true;
            }

            boolean currentMoveBool = TechniqueManager.getMoveBool(sender);

            if(currentMoveBool){
                sender.sendMessage(ChatColor.GOLD + "Ци-Движение: Неактивно");
            } else {
                sender.sendMessage(ChatColor.GOLD + "Ци-Движение: Активно");
            }

            TechniqueManager.setMoveBool(sender,!currentMoveBool);
            String senderManual = getManual(sender);

            // Применение специальных эффектов в зависимости от мануала
            switch (senderManual) {
                case "Sugar Fiend":
                    sugarFiendQiMove(sender, TechniqueManager.getMoveBool(sender));
                    break;
                case "Fatty Manual":
                    fattyManualQiMove(sender, TechniqueManager.getMoveBool(sender));
                    break;
                case "Fungal Manual":
                    fungalManualQiMove(sender,TechniqueManager.getMoveBool(sender));
                    break;
            }
            return true;
        }

        // Команда: qiaura (ци-аура)
        if(command.getName().equalsIgnoreCase("qiaura")){
            // Требуется минимум 6 стадия
            if(!(getStage(sender) >= 6)){
                sender.sendMessage(ChatColor.GOLD + "Для использования этой техники необходим культивационный базис Стадии 6 или выше.");
                return true;
            }

            boolean currentAuraBool = TechniqueManager.getAuraBool(sender);
            // Получение или создание задачи ауры для игрока
            RunAura runAura = runAuraMap.getOrDefault(sender,new RunAura(plugin,sender));

            if(currentAuraBool){
                sender.sendMessage(ChatColor.GOLD + "Ци-Аура: Неактивна");
                runAura.stop(); // Остановка задачи

            } else {
                sender.sendMessage(ChatColor.GOLD + "Ци-Аура: Активна");
                runAuraMap.put(sender,runAura);
                runAura.start(); // Запуск задачи

            }

            TechniqueManager.setAuraBool(sender,!currentAuraBool);
            // Визуальный эффект свечения
            qiAuraGlow(sender,getAuraBool(sender));
            return true;
        }

        // Команда: qifly (ци-полет)
        if(command.getName().equalsIgnoreCase("qifly")){
            // Требуется минимум 7 стадия
            if(!(getStage(sender) >= 7)){
                sender.sendMessage(ChatColor.GOLD + "Для использования этой техники необходим культивационный базис Стадии 7 или выше.");
                return true;
            }

            boolean currentFlyBool = TechniqueManager.getFlyBool(sender);

            if(currentFlyBool){
                sender.sendMessage(ChatColor.GOLD + "Ци-Полет: Неактивен");
                sender.setAllowFlight(false); // Запрет полета
                sender.setFlySpeed(0.1f); // Сброс скорости полета
            } else {
                sender.sendMessage(ChatColor.GOLD + "Ци-Полет: Активен");
                sender.setAllowFlight(true); // Разрешение полета

                // Специальный бонус для мануала Сахарного Маньяка
                if(getManual(sender).equals("Sugar Fiend")){
                    sender.setFlySpeed((float) (getStage(sender)*0.05));
                }

            }

            TechniqueManager.setFlyBool(sender,!currentFlyBool);

            return true;
        }

        return true;
    }
}