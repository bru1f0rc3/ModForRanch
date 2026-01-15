package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Бур (Drill) - Разрушает блоки в области 3x3.
 * 
 * Применяется на: Кирки
 * Максимальный уровень: 2
 * Уровень I: 3x3x1 (9 блоков)
 * Уровень II: 3x3x2 (18 блоков)
 * Редкость: Редкий
 */
public class DrillEnchantment extends Enchantment {
    
    public DrillEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 2;
    }
    
    @Override
    public int getMinCost(int level) {
        return 15 + (level - 1) * 10;
    }
    
    @Override
    public int getMaxCost(int level) {
        return getMinCost(level) + 50;
    }
    
    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }
    
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isTradeable() {
        return false;
    }
    
    @Override
    public boolean isDiscoverable() {
        return false;
    }
    
    @Override
    public boolean isTreasureOnly() {
        return true;
    }
    
    @Override
    protected boolean checkCompatibility(Enchantment other) {
        // Несовместимо с Мега-буром
        if (other instanceof MegaDrillEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }
    
    /**
     * Get the radius for the drill effect based on enchantment level.
     * @param level The enchantment level (1-2)
     * @return The radius in blocks
     */
    public static int getRadius(int level) {
        return 1; // 3x3 area (1 block in each direction)
    }
    
    /**
     * Get the depth for the drill effect based on enchantment level.
     * @param level The enchantment level (1-2)
     * @return The depth in blocks
     */
    public static int getDepth(int level) {
        return level; // Level 1 = 1 depth, Level 2 = 2 depth
    }
}
