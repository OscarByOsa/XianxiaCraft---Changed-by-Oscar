package xianxiacraft.xianxiacraft.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ManualItems {

    // Статические переменные для всех мануалов и учебника
    public static ItemStack fattyManualItem;
    public static ItemStack iceManualItem;
    public static ItemStack ironSkinManualItem;
    public static ItemStack phoenixManualItem;
    public static ItemStack poisonManualItem;
    public static ItemStack spaceManualItem;
    public static ItemStack sugarFiendManualItem;
    public static ItemStack demonicManualItem;
    public static ItemStack lightningManualItem;
    public static ItemStack fungalManualItem;
    public static ItemStack tutorialBookItem;

    // Инициализация всех предметов
    public static void init(){
        createFattyManualItem();
        createIceManualItem();
        createIronSkinManualItem();
        createPhoenixManualItem();
        createPoisonManualItem();
        createSpaceManualItem();
        createSugarFiendManualItem();
        createDemonicManualItem();
        createLightningManualItem();
        createFungalManualItem();
        createTutorialBookItem();
    }

    // Создание учебной книги для новых игроков
    private static void createTutorialBookItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Tutorial Book");
        bookMeta.setAuthor("Anonymous");
        bookMeta.setDisplayName("A Beginner's Guide to Cultivation");
        bookMeta.addPage("Добро пожаловать в XianxiaCraft от Spellslot (Daniel)!\n" +
                "\n" +
                "Чтобы начать, найдите и используйте мануал культивации! Они имеют шанс выпадения с определенных мобов:\n" +
                "Свиньи, Пиглины, Ифриты, Грибные коровы, Жители, Железные големы, Пещерные пауки, Эндермены, Скитальцы, Криперы.");
        bookMeta.addPage("После получения мануала просто следуйте ему!\n" +
                "\n" +
                "Шанс выпадения мануала с моба в настоящее время 2%.\n" +
                "\n" +
                "Это тестовая версия для понимания баланса мануалов.");
        bookMeta.addPage("Проект все еще в разработке. Пожалуйста, сообщайте об ошибках danialdamanual в дискорде.");

        item.setItemMeta(bookMeta);
        tutorialBookItem = item;
    }

    // Создание грибного мануала
    private static void createFungalManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Fungal Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Хроники Мицелия");
        bookMeta.addPage("Истинный Гриб покоряет все.\n" +
                "\n" +
                "Потребляйте подозрительное рагу чтобы использовать грибные силы.\n" +
                "\n" +
                "Для прорыва вы должны находиться рядом с блоками мицелия.\n" +
                "\n" +
                "Вы наносите двойной урон рядом с мицелием.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "Вы можете выпускать ци в землю вокруг, преобразуя ее в мицелий. Радиус преобразования растет с прогрессом стадии.\n" +
                "\n" +
                "Используйте /qipunch для переключения техники. Работает только на блоках.");
        bookMeta.addPage("Больше техник будет добавлено позже");

        item.setItemMeta(bookMeta);
        fungalManualItem = item;
    }

    // Создание мануала молнии
    private static void createLightningManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("LightningManual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Наследие Императора Молний");
        bookMeta.addPage("Сейчас вы ниже Небес, но однажды вы СТАНЕТЕ Небесами.\n" +
                "\n" +
                "Поглощайте сущность медных слитков для повышения культивации.\n" +
                "\n" +
                "Для прорыва будьте рядом с медными блоками. Требование удваивается каждый стадию.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "Вы можете вкладывать ци в атаки, призывая молнию с небес на врагов.\n" +
                "\n" +
                "Переключайте силу с помощью /qipunch.\n" +
                "\n" +
                "Стоимость: 10 ци за атаку.");
        bookMeta.addPage("Больше техник будет добавлено позже");

        item.setItemMeta(bookMeta);
        lightningManualItem = item;
    }

    // Создание демонического мануала
    private static void createDemonicManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Demonic Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Техника Потока Инь Сердца");
        bookMeta.addPage("Поглощайте жизненную силу жителей и игроков чтобы достичь бессмертия.\n" +
                "\n" +
                "Жители дают мизерную сумму, но игроки дают огромное количество энергии.");
        bookMeta.addPage("Эта удивительная техника не имеет требований прорыва. Скорость прогресса ограничена только вашей милостью.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "Вы научились поглощать жизненную силу врагов в бою. Когда /healqi активирован, вы получаете полсердца за каждый удар. Кража жизненной силы также увеличивает ваш урон.");

        item.setItemMeta(bookMeta);
        demonicManualItem = item;
    }

    // Создание мануала сахарного маньяка
    private static void createSugarFiendManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Sugar Fiend");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Восхождение Сахарного Маньяка");
        bookMeta.addPage("Восхождение Сахарного Маньяка превращает вас в Сахарного Маньяка.\n" +
                "\n" +
                "Вы можете культивировать только в Нижнем мире.\n" +
                "\n" +
                "Культивируйте поедая сладкую еду как печенье и тыквенный пирог (не торт).");
        bookMeta.addPage("Для прорыва вы должны быть рядом с установленными тортами в Нижнем мире. Количество требуемых тортов удваивается каждый стадию.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что можете вкладывать ци в атаки, увеличивая урон.\n" +
                "\n" +
                "Введите /qipunch для переключения техники. Требует 10 ци за атаку.");
        bookMeta.addPage("Стадия 3:\n\nНа этой стадии вы можете вкладывать ци в движения.\n\nВведите /qimove для переключения техники. Требует 4 ци в секунду.");

        item.setItemMeta(bookMeta);
        sugarFiendManualItem = item;
    }

    // Создание космического мануала
    private static void createSpaceManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Space Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Искусство Лотоса Пустоты");
        bookMeta.addPage("В эфирном полотне мира ищите плоды хоруса и шалкербоксы. Ешьте плоды, слушайте их шепот.");
        bookMeta.addPage("Обнимите коробки, почувствуйте их резонанс. Гармонизируйте сущность, преодолейте границы. Вознеситесь с целью, баланс во всем. Танец начинается, тайны раскрываются.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "Слушай, юный ученик, ибо я передам тебе эзотерическое знание вложения ци в твои кулаки. Техника, что раскроет безграничный потенциал в тебе. Готовься отправиться в это путешествие просветления: /qipunch");
        bookMeta.addPage("Стадия 3:\n\nНа этой стадии вы можете использовать ци для телепортации на короткие расстояния.\n\nВведите /qimove для переключения техники. Правый клик с любым предметом в руке для телепортации.");

        item.setItemMeta(bookMeta);
        spaceManualItem = item;
    }

    // Создание ядовитого мануала
    private static void createPoisonManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Poison Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Руководство Юцинь Сюаньи по Токсичности");
        bookMeta.addPage("Следуйте по стопам Сюаньи и станьте самым токсичным младшим!\n" +
                "\n" +
                "Ешьте ядовитую еду как паучьи глаза и рыбу-фугу для культивации. Зелья не считаются.\n" +
                "\n" +
                "Яд больше не вредит вам. Вы получаете огромную защиту под действием яда.");
        bookMeta.addPage("Для прорыва на следующую стадию вы должны культивировать в биоме болота.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что можете вкладывать ци в атаки, отравляя врагов. Токсичность яда увеличивается за стадию.\n" +
                "\n" +
                "Введите /qipunch для переключения техники. Требует 10 ци за атаку.");

        item.setItemMeta(bookMeta);
        poisonManualItem = item;
    }

    // Создание мануала феникса
    private static void createPhoenixManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Phoenix Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Техника Воскрешения Феникса");
        bookMeta.addPage("Три слова:\n" +
                "Умри чтобы культивировать.\n" +
                "\n" +
                "Феникс возрождается сильнее после каждой смерти, знание их опыта помогает им в новой жизни.\n" +
                "\n" +
                "Умирайте с уровнями опыта для культивации.");
        bookMeta.addPage("Огонь больше не вредит вам. Под действием огня большинство вещей не может ранить вас.\n\nДля прорыва умрите с 10×стадия уровнями опыта.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что можете вкладывать ци в атаки, поджигая врагов. Длина горения увеличивается с вашей стадией.\n" +
                "\n" +
                "Введите /qipunch для переключения техники. Требует 10 ци за атаку.");

        item.setItemMeta(bookMeta);
        phoenixManualItem = item;
    }

    // Создание мануала железной кожи
    private static void createIronSkinManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Ironskin Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Железный Путь Несокрушимой Жизненной Силы");
        bookMeta.addPage("СТАНЬТЕ НЕСОКРУШИМЫМ! Это путь этой техники. Как самая первая техника культивации в существовании, она очевидно лучшая!\n" +
                "\n" +
                "Ешьте железные слитки для культивации.\n" +
                "\n" +
                "Железные фермы к бессмертию!");
        bookMeta.addPage("Для прорыва на следующую стадию вы должны быть рядом с железными блоками. Требование удваивается каждый стадию.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что можете вкладывать ци в атаки, делая ВАШИ КУЛАКИ ТВЕРЖЕ!\n" +
                "\n" +
                "Введите /qipunch для переключения техники. Требует 10 ци за атаку.");
        bookMeta.addPage("Стадия 3:\n\nНа этой стадии вы можете вкладывать ци в движения.\n\nВведите /qimove для переключения техники.");

        item.setItemMeta(bookMeta);
        ironSkinManualItem = item;
    }

    // Создание ледяного мануала
    private static void createIceManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Ice Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Небесный Ледяной Путь");
        bookMeta.addPage("Следуйте Небесному Ледяному Пути, очищая ледяную сущность в ваш Даньтянь. Это можно делать через поедание льда, хотя его получение может быть сложным...\n" +
                "\n" +
                "Это искусство сделает вас иммунным к холоду.");
        bookMeta.addPage("Для прорыва на следующую стадию вы должны быть рядом с синими ледяными блоками. Требование удваивается каждый стадию.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что можете вкладывать ци в атаки, замораживая врагов. Длительность увеличивается с вашей стадией.\n" +
                "\n" +
                "Введите /qipunch для переключения техники. Требует 10 ци за атаку.");

        item.setItemMeta(bookMeta);
        iceManualItem = item;
    }

    // Создание мануала обжоры
    private static void createFattyManualItem(){
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) item.getItemMeta();

        assert bookMeta != null;
        bookMeta.setTitle("Fatty Manual");
        bookMeta.setAuthor("Spellslot");
        bookMeta.setDisplayName("Верховное Писание Обжорства");
        bookMeta.addPage("Используя этот метод, вы получите силу, соперничающую с небесами, просто ПОЕДАЯ ЕДУ.\n" +
                "\n" +
                "Это искусство позволит вам есть почти любую еду без негативных эффектов. Знайте только что ваша сытость редко будет удовлетворена...");
        bookMeta.addPage("Для прорыва на следующую стадию вы должны позволить себе голодать пока ваше тело не начнет пожирать себя изнутри. Только тогда вы можете есть и преодолеть барьер.");
        bookMeta.addPage("Стадия 1:\n" +
                "\n" +
                "На этой стадии вы обнаружите что способны вкладывать ци в атаки, отправляя врагов в полет.\n" +
                "\n" +
                "Для переключения техники введите /qipunch.");
        bookMeta.addPage("Стадия 3:\n\nНа этой стадии вы можете вкладывать ци в тело и не получать отталкивание.\n\nВведите /qimove для переключения техники. Требует 4 ци в секунду.");

        item.setItemMeta(bookMeta);
        fattyManualItem = item;
    }
}