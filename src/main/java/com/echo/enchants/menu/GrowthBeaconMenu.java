package com.echo.enchants.menu;

import com.echo.enchants.block.entity.GrowthBeaconBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Menu (Container) для GUI Маяка Роста.
 * Синхронизирует данные между клиентом и сервером.
 */
public class GrowthBeaconMenu extends AbstractContainerMenu {
    
    private final GrowthBeaconBlockEntity blockEntity;
    private final ContainerData data;
    
    // Client-side constructor (from network)
    public GrowthBeaconMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, extraData));
    }
    
    // Server-side constructor
    public GrowthBeaconMenu(int containerId, Inventory playerInventory, GrowthBeaconBlockEntity blockEntity) {
        super(ModMenus.GROWTH_BEACON_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        
        // Create container data for syncing radius, active state, and upgrade levels
        this.data = new SimpleContainerData(4) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GrowthBeaconMenu.this.blockEntity.getRadius();
                    case 1 -> GrowthBeaconMenu.this.blockEntity.isActive() ? 1 : 0;
                    case 2 -> GrowthBeaconMenu.this.blockEntity.getSpeedLevel();
                    case 3 -> GrowthBeaconMenu.this.blockEntity.getRangeLevel();
                    default -> 0;
                };
            }
            
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> GrowthBeaconMenu.this.blockEntity.setRadius(value);
                    case 1 -> GrowthBeaconMenu.this.blockEntity.setActive(value != 0);
                    case 2 -> GrowthBeaconMenu.this.blockEntity.setSpeedLevel(value);
                    case 3 -> GrowthBeaconMenu.this.blockEntity.setRangeLevel(value);
                }
            }
        };
        
        addDataSlots(this.data);
    }
    
    private static GrowthBeaconBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof GrowthBeaconBlockEntity growthBeacon) {
            return growthBeacon;
        }
        throw new IllegalStateException("Block entity is not a GrowthBeaconBlockEntity at " + pos);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // No inventory slots
    }
    
    @Override
    public boolean stillValid(Player player) {
        return blockEntity.getBlockPos().closerToCenterThan(player.position(), 8.0);
    }
    
    // ==================== GETTERS ====================
    
    public GrowthBeaconBlockEntity getBlockEntity() {
        return blockEntity;
    }
    
    public int getRadius() {
        return data.get(0);
    }
    
    public boolean isActive() {
        return data.get(1) != 0;
    }
    
    public int getSpeedLevel() {
        return data.get(2);
    }
    
    public int getRangeLevel() {
        return data.get(3);
    }
    
    public int getMaxRadius() {
        return blockEntity.getMaxRadius();
    }
    
    public BlockPos getBlockPos() {
        return blockEntity.getBlockPos();
    }
}
