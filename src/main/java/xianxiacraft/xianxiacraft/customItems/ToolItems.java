package xianxiacraft.xianxiacraft.customItems;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolItems {

    public static ItemStack taijiPaintingItem;
    public static ItemStack superHoeItem;

    private static XianxiaCraft plugin;

    public ToolItems(XianxiaCraft plugin){
        ToolItems.plugin = plugin;
    }

    public static void init2(){
        createSuperHoe();
        createTaijiPainting();
    }

    private static void createTaijiPainting(){
        ItemStack item = new ItemStack(Material.PAINTING);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.GOLD + "Картина Тайцзи");

        List<String> lore = new ArrayList<>();
        lore.add("Верховная Картина, которая");
        lore.add("скрывает обладателя от Небес.");
        lore.add("Не размещайте её. Небеса");
        lore.add("могут забрать её обратно.");
        itemMeta.setLore(lore);

        // Добавление скрытого зачарования Удачи
        itemMeta.addEnchant(Enchantment.LUCK,1,false);
        // Скрытие зачарований и атрибутов для чистого вида
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_ATTRIBUTES);

        // Установка пользовательского тега для идентификации
        NamespacedKey key = new NamespacedKey(plugin, "customUtilityTools");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING,"taijiPainting");

        item.setItemMeta(itemMeta);

        taijiPaintingItem = item;
    }

    private static void createSuperHoe(){
        ItemStack item = new ItemStack(Material.WOODEN_HOE);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.GOLD + "Мотыга Истинной Сущности");

        List<String> lore = new ArrayList<>();
        lore.add("Мотыга, содержащая");
        lore.add("каплю истинной сущности.");
        itemMeta.setLore(lore);

        // Мощные зачарования
        itemMeta.addEnchant(Enchantment.MENDING,1,false);      // Починка
        itemMeta.addEnchant(Enchantment.LUCK,10,false);        // Удача X
        itemMeta.addEnchant(Enchantment.DURABILITY,10,false);  // Прочность X
        // Скрытие визуальных эффектов
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_ATTRIBUTES);

        // Пользовательский идентификатор
        NamespacedKey key = new NamespacedKey(plugin, "customUtilityTools");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING,"superHoe");

        item.setItemMeta(itemMeta);

        superHoeItem = item;
    }
}