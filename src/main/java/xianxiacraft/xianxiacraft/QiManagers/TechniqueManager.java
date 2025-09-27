package xianxiacraft.xianxiacraft.QiManagers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TechniqueManager {

    // Карты для хранения состояний техник каждого игрока (UUID -> состояние)
    private static Map<UUID,Boolean> punchBool = new HashMap<>();     // Техника усиленного удара
    private static Map<UUID,Boolean> moveBool = new HashMap<>();      // Техника движения
    private static Map<UUID,Boolean> mineBool = new HashMap<>();      // Техника копания
    private static Map<UUID,Boolean> auraBool = new HashMap<>();      // Техника ауры
    private static Map<UUID,Boolean> flyBool = new HashMap<>();       // Техника полета
    public static Map<UUID,Boolean> hiddenByTaijiPainting = new HashMap<>(); // Скрытие картиной Тайцзи

    // === МЕТОДЫ ДЛЯ КАРТИНЫ ТАЙЦЗИ ===
    
    // Получение состояния скрытия картиной Тайцзи (по умолчанию false)
    public static Boolean getHiddenByTaijiPaintingBool(Player player){
        return hiddenByTaijiPainting.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния скрытия картиной Тайцзи
    public static void setHiddenByTaijiPaintingBool(Player player, boolean bool){
        hiddenByTaijiPainting.put(player.getUniqueId(),bool);
    }

    // === МЕТОДЫ ДЛЯ ТЕХНИКИ УДАРА (QiPunch) ===
    
    // Получение состояния техники удара (по умолчанию false)
    public static Boolean getPunchBool(Player player){
        return punchBool.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния техники удара
    public static void setPunchBool(Player player, boolean bool){
        punchBool.put(player.getUniqueId(),bool);
    }

    // === МЕТОДЫ ДЛЯ ТЕХНИКИ ДВИЖЕНИЯ (QiMove) ===
    
    // Получение состояния техники движения (по умолчанию false)
    public static Boolean getMoveBool(Player player){
        return moveBool.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния техники движения
    public static void setMoveBool(Player player, boolean bool){
        moveBool.put(player.getUniqueId(),bool);
    }

    // === МЕТОДЫ ДЛЯ ТЕХНИКИ КОПАНИЯ (QiMine) ===
    
    // Получение состояния техники копания (по умолчанию false)
    public static Boolean getMineBool(Player player){
        return mineBool.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния техники копания
    public static void setMineBool(Player player, boolean bool){
        mineBool.put(player.getUniqueId(),bool);
    }

    // === МЕТОДЫ ДЛЯ ТЕХНИКИ АУРЫ (QiAura) ===
    
    // Получение состояния техники ауры (по умолчанию false)
    public static Boolean getAuraBool(Player player){
        return auraBool.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния техники ауры
    public static void setAuraBool(Player player, boolean bool){
        auraBool.put(player.getUniqueId(),bool);
    }

    // === МЕТОДЫ ДЛЯ ТЕХНИКИ ПОЛЕТА (QiFly) ===
    
    // Получение состояния техники полета (по умолчанию false)
    public static Boolean getFlyBool(Player player){
        return flyBool.getOrDefault(player.getUniqueId(),false);
    }
    
    // Установка состояния техники полета
    public static void setFlyBool(Player player, boolean bool){
        flyBool.put(player.getUniqueId(),bool);
    }

    // === ВИЗУАЛЬНЫЙ ЭФФЕКТ АУРЫ ===
    
    // Управление свечением игрока при активации/деактивации ауры
    public static void qiAuraGlow(Player player, Boolean bool){
        if(bool){
            player.setGlowing(true); // Включение свечения при активной ауре
        } else{
            player.setGlowing(false); // Выключение свечения при неактивной ауре
        }
    }
}