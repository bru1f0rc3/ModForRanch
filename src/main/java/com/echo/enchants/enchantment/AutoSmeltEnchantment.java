package com.echo.enchants.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Автоплавка (Auto Smelt) - Автоматически переплавляет добытые руды.
 * 
 * Применяется на: Кирки
 * Максимальный уровень: 1
 * Редкость: Редкий
 */
public class AutoSmeltEnchantment extends Enchantment {
    
    public AutoSmeltEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMaxLevel() {
        return 1;
    }
    
    @Override
    public int getMinCost(int level) {
        return 20;
    }
    
    @Override
    public int getMaxCost(int level) {
        return 60;
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
        // Несовместимо с Шёлковым касанием (Silk Touch)
        if (other == Enchantments.SILK_TOUCH) {
            return false;
        }
        return super.checkCompatibility(other);
    }
}
