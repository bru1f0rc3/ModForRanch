package com.echo.enchants.util;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.enchantment.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for creating enchanted books with custom display.
 */
public class EnchantedBookUtil {
    
    private static final Random random = new Random();
    
    /**
     * Create an enchanted book with custom styling and detailed description.
     * 
     * @param enchantment The enchantment to apply
     * @param level The level of the enchantment
     * @return A styled enchanted book ItemStack
     */
    public static ItemStack createEnchantedBook(Enchantment enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        
        // Add the enchantment to the book
        EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(enchantment, level));
        
        // Create custom display name with color
        String enchantName = getEnchantmentDisplayName(enchantment, level);
        book.setHoverName(Component.literal(enchantName));
        
        // Add detailed lore
        CompoundTag displayTag = book.getOrCreateTagElement("display");
        ListTag loreList = new ListTag();
        
        // Get detailed description for this enchantment
        List<String> description = getEnchantmentDescription(enchantment, level);
        for (String line : description) {
            loreList.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(line))));
        }
        
        displayTag.put("Lore", loreList);
        
        // Add custom NBT tag to identify as EchoEnchants book
        CompoundTag echoTag = book.getOrCreateTagElement("EchoEnchants");
        echoTag.putBoolean("IsEchoBook", true);
        ResourceLocation enchantKey = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        echoTag.putString("EnchantmentId", enchantKey != null ? enchantKey.toString() : "unknown");
        echoTag.putInt("Level", level);
        
        return book;
    }
    
    /**
     * Get the display name with formatting for an enchantment.
     */
    private static String getEnchantmentDisplayName(Enchantment enchantment, int level) {
        ResourceLocation key = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (key == null) return "§b§lUnknown Enchantment";
        
        String id = key.getPath();
        String levelStr = toRoman(level);
        
        return switch (id) {
            case "magnetism" -> "§b§l✦ Магнетизм " + levelStr + " §7(Magnetism)";
            case "experienced" -> "§a§l✦ Опытный " + levelStr + " §7(Experienced)";
            case "unbreakable" -> "§6§l✦ Неразрушимость " + levelStr + " §7(Unbreakable)";
            case "drill" -> "§c§l✦ Бур " + levelStr + " §7(Drill)";
            case "mega_drill" -> "§4§l✦ Мега-Бур " + levelStr + " §7(Mega Drill)";
            case "auto_smelt" -> "§6§l✦ Автоплавка " + levelStr + " §7(Auto Smelt)";
            case "farmer" -> "§a§l✦ Фермер " + levelStr + " §7(Farmer)";
            case "seeding" -> "§2§l✦ Посев " + levelStr + " §7(Seeding)";
            case "delicate" -> "§d§l✦ Деликатный " + levelStr + " §7(Delicate)";
            default -> "§b§l✦ " + enchantment.getFullname(level).getString();
        };
    }
    
    /**
     * Get detailed description lines for an enchantment.
     */
    private static List<String> getEnchantmentDescription(Enchantment enchantment, int level) {
        List<String> lore = new ArrayList<>();
        ResourceLocation key = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
        if (key == null) return lore;
        
        String id = key.getPath();
        
        // Empty line for spacing
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        
        switch (id) {
            case "magnetism" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Автоматически притягивает все");
                lore.add("§7выпавшие предметы в радиусе §e8 блоков");
                lore.add("§7прямо в ваш инвентарь.");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Кирки, Лопаты, Топоры, Мотыги");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(1));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Радиус притяжения: §e8 блоков");
                lore.add("§a► §7Мгновенный сбор лута");
            }
            case "experienced" -> {
                int bonus = level * 30;
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Увеличивает количество опыта,");
                lore.add("§7получаемого при добыче руд и");
                lore.add("§7убийстве мобов.");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Кирки, Мечи, Топоры");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(3));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Бонус опыта: §a+" + bonus + "%");
                lore.add("§a► §7Множитель: §ex" + String.format("%.1f", 1 + level * 0.3));
            }
            case "unbreakable" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Защищает инструмент от полной");
                lore.add("§7поломки. При достижении §c10%");
                lore.add("§7прочности инструмент перестаёт");
                lore.add("§7работать, но §НЕ ЛОМАЕТСЯ§7!");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Любой инструмент с прочностью");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(1));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Минимальная прочность: §c10%");
                lore.add("§a► §7Инструмент §6НЕ СЛОМАЕТСЯ");
            }
            case "drill" -> {
                String area = "3×3";
                int blocks = level == 1 ? 9 : 18;
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Разрушает блоки в области §e" + area);
                lore.add("§7на уровне игрока одним ударом.");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Только Кирки");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(2));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Область добычи: §e" + area);
                lore.add("§a► §7Глубина: §e" + level + " блок(ов)");
                lore.add("");
                lore.add("§c⚠ §7Несовместимо с Мега-Бур!");
            }
            case "mega_drill" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Разрушает ОГРОМНУЮ область §e5×5");
                lore.add("§7на уровне игрока! Идеально");
                lore.add("§7для быстрой добычи шахт.");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Только Кирки");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(1));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Область добычи: §e5×5");
                lore.add("§a► §7Глубина: §e2 блока");
                lore.add("");
                lore.add("§c⚠ §7Несовместимо с обычным Бур!");
                lore.add("§4§lОЧЕНЬ РЕДКОЕ ЗАЧАРОВАНИЕ!");
            }
            case "auto_smelt" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Автоматически переплавляет добытые");
                lore.add("§7руды в слитки! Железная руда даёт");
                lore.add("§7железный слиток, золотая - золотой.");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Только Кирки");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(1));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lПЕРЕПЛАВЛЯЕТ:");
                lore.add("§a► §7Железная руда → §fЖелезный слиток");
                lore.add("§a► §7Золотая руда → §6Золотой слиток");
                lore.add("§a► §7Медная руда → §6Медный слиток");
                lore.add("");
                lore.add("§c⚠ §7Несовместимо с Шёлковым касанием!");
            }
            case "farmer" -> {
                int bonus = level * 20;
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Увеличивает количество урожая");
                lore.add("§7при сборе культур. Больше пшеницы,");
                lore.add("§7картофеля, моркови и других!");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Мотыги");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(4));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Бонус урожая: §a+" + bonus + "%");
                lore.add("§a► §7Множитель: §ex" + String.format("%.1f", 1 + level * 0.2));
            }
            case "seeding" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Автоматически пересаживает культуры");
                lore.add("§7после сбора урожая. Забирает §e1 семя");
                lore.add("§7из дропа для посадки!");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Мотыги");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(4));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Автопосев из дропа");
                lore.add("§a► §7Не тратит семена из инвентаря!");
            }
            case "delicate" -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Защищает §cНЕВЫРОСШИЕ §7растения от");
                lore.add("§7случайного разрушения. Вы не сможете");
                lore.add("§7сломать незрелые культуры!");
                lore.add("");
                lore.add("§f§lПРИМЕНЯЕТСЯ:");
                lore.add("§7• Мотыги");
                lore.add("");
                lore.add("§f§lМАКС. УРОВЕНЬ: §e" + toRoman(3));
                lore.add("§f§lТЕКУЩИЙ: §a" + toRoman(level));
                lore.add("");
                lore.add("§f§lЭФФЕКТ:");
                lore.add("§a► §7Ур.I: Предупреждение о незрелых");
                lore.add("§a► §7Ур.II: Отмена разрушения незрелых");
                lore.add("§a► §7Ур.III: + Подсветка зрелых");
            }
            default -> {
                lore.add("§f§lОПИСАНИЕ:");
                lore.add("§7Неизвестное зачарование");
            }
        }
        
        lore.add("");
        lore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        lore.add("§7Источник: §cВраждебные мобы");
        lore.add("§d§oEchoEnchants Mod");
        
        return lore;
    }
    
    /**
     * Convert number to Roman numeral.
     */
    private static String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(num);
        };
    }
    
    /**
     * Select a random enchantment from all registered EchoEnchants enchantments.
     */
    public static Enchantment selectRandomEnchantment() {
        var enchantments = ModEnchantments.getAllEnchantments();
        int index = random.nextInt(enchantments.size());
        RegistryObject<Enchantment> selected = enchantments.get(index);
        return selected.get();
    }
    
    /**
     * Get a random level for the given enchantment (1 to max level).
     */
    public static int getRandomLevel(Enchantment enchantment) {
        int maxLevel = enchantment.getMaxLevel();
        if (maxLevel <= 1) {
            return 1;
        }
        return random.nextInt(maxLevel) + 1;
    }
    
    /**
     * Check if an ItemStack is an EchoEnchants book.
     */
    public static boolean isEchoEnchantBook(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }
        return tag.contains("EchoEnchants") && tag.getCompound("EchoEnchants").getBoolean("IsEchoBook");
    }
}
