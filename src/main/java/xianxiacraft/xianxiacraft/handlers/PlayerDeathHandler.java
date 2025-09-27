package xianxiacraft.xianxiacraft.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.addPoints;
import static xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1.updateScoreboard;
import static xianxiacraft.xianxiacraft.util.ManualUtils.getCultivationModifier;

public class PlayerDeathHandler implements Listener {

    XianxiaCraft plugin;

    public PlayerDeathHandler(XianxiaCraft plugin){
        Bukkit.getPluginManager().registerEvents(this,plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){

        // Проверка что смерть вызвана другим игроком (не окружающей средой или мобом)
        if(event.getEntity().getKiller() != null){

            final Player defendingPlayer = event.getEntity(); // Умерший игрок
            final Player attackingPlayer = event.getEntity().getKiller(); // Убийца

            // Проверка что убийца использует демонический мануал (вампирский)
            assert attackingPlayer != null;
            if(getManual(attackingPlayer).equals("Demonic Manual")){

                // РАСЧЕТ КРАЖИ ОЧКОВ КУЛЬТИВАЦИИ
                int points1 = getPoints(defendingPlayer); // Текущие очки умершего игрока
                int stage1 = getStage(defendingPlayer); // Текущая стадия умершего игрока

                // variable1 = количество очков необходимое для достижения текущей стадии (от стадии 0 до stage1-1)
                // Формула: 20 × 2^(стадия) - 20
                int variable1 = (int) (20 * Math.pow(2, (stage1))) - 20;
                
                // leeched = количество очков которые будут украдены (все очки сверх необходимого минимума для текущей стадии)
                // points1 - variable1 - 1 = очки накопленные на текущей стадии минус 1 (оставляем игроку 1 очко)
                int leeched = (points1 - variable1 - 1);

                // ПЕРЕРАСПРЕДЕЛЕНИЕ ОЧКОВ:
                // У мертвого игрока отнимаются украденные очки
                setPoints(defendingPlayer, points1 - leeched);
                // Убийце добавляются украденные очки
                addPoints(attackingPlayer, leeched);
                
                // ОБНОВЛЕНИЕ ИНТЕРФЕЙСА:
                updateScoreboard(defendingPlayer);
                updateScoreboard(attackingPlayer);
            }
        }
    }
}