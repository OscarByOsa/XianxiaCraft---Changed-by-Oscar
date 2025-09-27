package xianxiacraft.xianxiacraft.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.player.PlayerInteractEvent;
import xianxiacraft.xianxiacraft.XianxiaCraft;

public class ChatUtils {

    // Закомментированная предыдущая версия метода (более простая)
//    public static void handleManualChange(PlayerInteractEvent e) {
//        TextComponent msg = Component.text("Click ")
//                .append(Component.text("accept",NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/xianxiacraft:manaccept")))
//                .append(Component.text(" to cultivate this manual"));
//        XianxiaCraft.getAdventure().sender(e.getPlayer()).sendMessage(msg);
//    }

    // Обработка смены мануала с интерактивным сообщением подтверждения
    public static void handleManualChange(PlayerInteractEvent e) {
        // Создание сложного интерактивного сообщения с подтверждением
        Component msg = Component.text("Вы уверены, что хотите изучить другой мануал? Смена мануала сбрасывает ваш прогресс культивации! ")
                .append(Component.text("Нажмите здесь", NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/manualaccept")) // Кликабельная команда
                        .hoverEvent(Component.text("Нажмите для подтверждения").color(NamedTextColor.RED))) // Подсказка при наведении
                .append(Component.text(" или введите ", NamedTextColor.GOLD))
                .append(Component.text("/manualaccept ", NamedTextColor.RED)
                        .clickEvent(ClickEvent.runCommand("/manualaccept")) // Дублирующая кликабельная команда
                        .hoverEvent(Component.text("Нажмите для подтверждения").color(NamedTextColor.RED)))
                .append(Component.text("для подтверждения.", NamedTextColor.GOLD));
        
        // Отправка сообщения игроку через Adventure API
        XianxiaCraft.getAdventure().sender(e.getPlayer()).sendMessage(msg.color(NamedTextColor.GOLD));
    }

}