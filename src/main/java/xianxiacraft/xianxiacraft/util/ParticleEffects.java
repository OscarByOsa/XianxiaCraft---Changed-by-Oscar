package xianxiacraft.xianxiacraft.util;

import org.bukkit.Particle;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;

public class ParticleEffects {

    /**
     * Создание визуального эффекта частиц для ауры ци игрока
     * Эффект зависит от мануала игрока и создает тематические частицы вокруг игрока
     * 
     * @param player игрок для которого создается эффект частиц
     */
    public static void qiAuraParticleEffect(Player player){
        int stage = getStage(player); // Получение стадии игрока (может использоваться для масштабирования эффекта)
        String manual = getManual(player); // Получение мануала игрока
        Particle particle; // Тип частиц для эффекта

        // Выбор типа частиц в зависимости от мануала игрока
        switch(manual){
            case "Phoenix Manual": // Мануал феникса - огненные частицы
                particle = Particle.FLAME;
                break;
            case "Ice Manual": // Ледяной мануал - снежинки
                particle = Particle.SNOWFLAKE;
                break;
            case "Fatty Manual": // Мануал обжоры - капли меда
                particle = Particle.DRIPPING_HONEY;
                break;
            case "Fungal Manual": // Грибной мануал - обсидиановые слезы
                particle = Particle.DRIPPING_OBSIDIAN_TEAR;
                break;
            case "Ironskin Manual": // Мануал железной кожи - маленькие взрывы
                particle = Particle.EXPLOSION_NORMAL;
                break;
            case "LightningManual": // Мануал молнии - электрические искры
                particle = Particle.ELECTRIC_SPARK;
                break;
            case "Poison Manual": // Ядовитый мануал - скульк заряды
                particle = Particle.CRIT;
                break;
            case "Space Manual": // Космический мануал - дыхание дракона
                particle = Particle.DRAGON_BREATH;
                break;
            case "Sugar Fiend": // Сахарный маньяк - пепел
                particle = Particle.ASH;
                break;
            case "Demonic Manual": // Демонический мануал - скульк души
                particle = Particle.SOUL_FIRE_FLAME;
                break;
            default: // Стандартные частицы по умолчанию
                particle = Particle.DRIP_WATER;
        }

        // Создание эффекта частиц вокруг игрока
        // Параметры: тип частиц, локация, количество частиц, смещение по X/Y/Z
        player.spawnParticle(particle, player.getLocation(), 100, 4, 4, 4);
    }
}