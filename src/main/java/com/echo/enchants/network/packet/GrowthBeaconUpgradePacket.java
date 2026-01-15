package com.echo.enchants.network.packet;

import com.echo.enchants.block.entity.GrowthBeaconBlockEntity;
import com.echo.enchants.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet для улучшения Growth Beacon.
 * Требует Супер Удобрение из инвентаря игрока.
 */
public class GrowthBeaconUpgradePacket {
    
    // Стоимость улучшений (Супер Удобрение)
    public static final int SPEED_UPGRADE_COST = 3;  // 3 супер удобрения за уровень скорости
    public static final int RANGE_UPGRADE_COST = 5;  // 5 супер удобрений за уровень радиуса
    
    public enum UpgradeType {
        SPEED,
        RANGE
    }
    
    private final BlockPos pos;
    private final UpgradeType upgradeType;
    
    public GrowthBeaconUpgradePacket(BlockPos pos, UpgradeType upgradeType) {
        this.pos = pos;
        this.upgradeType = upgradeType;
    }
    
    public GrowthBeaconUpgradePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.upgradeType = buf.readEnum(UpgradeType.class);
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(upgradeType);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            
            // Проверка дистанции
            if (!pos.closerToCenterThan(player.position(), 10.0)) return;
            
            BlockEntity be = player.level().getBlockEntity(pos);
            if (!(be instanceof GrowthBeaconBlockEntity growthBeacon)) return;
            
            int cost = upgradeType == UpgradeType.SPEED ? SPEED_UPGRADE_COST : RANGE_UPGRADE_COST;
            int currentLevel = upgradeType == UpgradeType.SPEED ? 
                    growthBeacon.getSpeedLevel() : growthBeacon.getRangeLevel();
            
            // Проверка максимального уровня
            if (currentLevel >= GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL) {
                player.displayClientMessage(
                        Component.literal("§cМаксимальный уровень улучшения!"), true);
                return;
            }
            
            // Считаем сколько супер удобрений есть в инвентаре
            int totalFertilizer = countItemInInventory(player, ModItems.SUPER_FERTILIZER.get());
            
            if (totalFertilizer < cost) {
                player.displayClientMessage(
                        Component.literal("§cНедостаточно Супер Удобрения! Нужно: " + cost + ", есть: " + totalFertilizer), true);
                return;
            }
            
            // Забираем супер удобрения из инвентаря
            removeItemFromInventory(player, ModItems.SUPER_FERTILIZER.get(), cost);
            
            // Применяем улучшение
            if (upgradeType == UpgradeType.SPEED) {
                growthBeacon.setSpeedLevel(currentLevel + 1);
                player.displayClientMessage(
                        Component.literal("§aСкорость улучшена до уровня " + (currentLevel + 1) + "!"), true);
            } else {
                growthBeacon.setRangeLevel(currentLevel + 1);
                player.displayClientMessage(
                        Component.literal("§aМакс. радиус улучшен до уровня " + (currentLevel + 1) + "!"), true);
            }
            
            // Звук улучшения
            player.level().playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, 
                    SoundSource.BLOCKS, 1.0f, 1.2f);
        });
        return true;
    }
    
    /**
     * Подсчитывает количество предмета во всём инвентаре игрока
     */
    private int countItemInInventory(ServerPlayer player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }
    
    /**
     * Удаляет указанное количество предмета из инвентаря игрока
     */
    private void removeItemFromInventory(ServerPlayer player, net.minecraft.world.item.Item item, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
                
                if (stack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
