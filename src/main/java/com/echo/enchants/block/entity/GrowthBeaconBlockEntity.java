package com.echo.enchants.block.entity;

import com.echo.enchants.menu.GrowthBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Block Entity для Маяка Роста.
 * Ускоряет рост растений в настраиваемом радиусе.
 * 
 * Система улучшений:
 * - Уровень скорости (0-5): уменьшает интервал между удобрениями
 * - Уровень радиуса (0-5): увеличивает максимальный радиус действия
 */
public class GrowthBeaconBlockEntity extends BlockEntity implements MenuProvider {
    
    public static final int MIN_RADIUS = 1;
    public static final int DEFAULT_RADIUS = 5;
    
    // Система улучшений
    public static final int MAX_UPGRADE_LEVEL = 5;
    
    // Базовые значения (публичные для GUI)
    public static final int BASE_TICK_INTERVAL = 60; // 3 секунды базово
    public static final int BASE_MAX_RADIUS = 5;     // 5 блоков базово
    public static final int BASE_PLANTS_PER_TICK = 2; // 2 растения базово
    
    // Бонусы за уровень (публичные для GUI)
    public static final int TICK_REDUCTION_PER_LEVEL = 8;   // -8 тиков за уровень скорости (мин 20 тиков = 1 сек)
    public static final int RADIUS_BONUS_PER_LEVEL = 2;     // +2 радиуса за уровень
    public static final int PLANTS_BONUS_PER_LEVEL = 1;     // +1 растение за уровень
    
    private int tickCounter = 0;
    private int radius = DEFAULT_RADIUS;
    private boolean isActive = true;
    
    // Уровни улучшений
    private int speedLevel = 0;   // 0-5: скорость удобрения
    private int rangeLevel = 0;   // 0-5: максимальный радиус
    
    public GrowthBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROWTH_BEACON.get(), pos, state);
    }
    
    // ==================== GETTERS & SETTERS ====================
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = Math.max(MIN_RADIUS, Math.min(getMaxRadius(), radius));
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    public void toggleActive() {
        setActive(!isActive);
    }
    
    // ==================== UPGRADE SYSTEM ====================
    
    public int getSpeedLevel() {
        return speedLevel;
    }
    
    public void setSpeedLevel(int level) {
        this.speedLevel = Math.max(0, Math.min(MAX_UPGRADE_LEVEL, level));
        setChanged();
        syncToClient();
    }
    
    public int getRangeLevel() {
        return rangeLevel;
    }
    
    public void setRangeLevel(int level) {
        this.rangeLevel = Math.max(0, Math.min(MAX_UPGRADE_LEVEL, level));
        // Корректируем текущий радиус если он превышает новый максимум
        if (radius > getMaxRadius()) {
            radius = getMaxRadius();
        }
        setChanged();
        syncToClient();
    }
    
    public void upgradeSpeed() {
        if (speedLevel < MAX_UPGRADE_LEVEL) {
            setSpeedLevel(speedLevel + 1);
        }
    }
    
    public void upgradeRange() {
        if (rangeLevel < MAX_UPGRADE_LEVEL) {
            setRangeLevel(rangeLevel + 1);
        }
    }
    
    /**
     * Возвращает текущий интервал тиков (зависит от уровня скорости)
     * Уровень 0: 60 тиков (3 сек), Уровень 5: 20 тиков (1 сек)
     */
    public int getTickInterval() {
        return Math.max(20, BASE_TICK_INTERVAL - (speedLevel * TICK_REDUCTION_PER_LEVEL));
    }
    
    /**
     * Возвращает максимальный радиус (зависит от уровня радиуса)
     * Уровень 0: 5 блоков, Уровень 5: 15 блоков
     */
    public int getMaxRadius() {
        return BASE_MAX_RADIUS + (rangeLevel * RADIUS_BONUS_PER_LEVEL);
    }
    
    /**
     * Возвращает количество растений за тик (зависит от уровня скорости)
     * Уровень 0: 2 растения, Уровень 5: 7 растений
     */
    public int getPlantsPerTick() {
        return BASE_PLANTS_PER_TICK + (speedLevel * PLANTS_BONUS_PER_LEVEL);
    }
    
    /**
     * Возвращает скорость в секундах для отображения
     */
    public float getSpeedInSeconds() {
        return getTickInterval() / 20.0f;
    }
    
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
    
    // ==================== NBT SAVE/LOAD ====================
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Radius", radius);
        tag.putBoolean("Active", isActive);
        tag.putInt("SpeedLevel", speedLevel);
        tag.putInt("RangeLevel", rangeLevel);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Radius")) {
            radius = tag.getInt("Radius");
        }
        if (tag.contains("Active")) {
            isActive = tag.getBoolean("Active");
        }
        if (tag.contains("SpeedLevel")) {
            speedLevel = tag.getInt("SpeedLevel");
        }
        if (tag.contains("RangeLevel")) {
            rangeLevel = tag.getInt("RangeLevel");
        }
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("Radius", radius);
        tag.putBoolean("Active", isActive);
        tag.putInt("SpeedLevel", speedLevel);
        tag.putInt("RangeLevel", rangeLevel);
        return tag;
    }
    
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    // ==================== MENU PROVIDER ====================
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.echo_enchants.growth_beacon");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GrowthBeaconMenu(containerId, playerInventory, this);
    }
    
    // ==================== TICK LOGIC ====================
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, GrowthBeaconBlockEntity blockEntity) {
        if (!blockEntity.isActive) {
            return;
        }
        
        blockEntity.tickCounter++;
        
        if (blockEntity.tickCounter >= blockEntity.getTickInterval()) {
            blockEntity.tickCounter = 0;
            blockEntity.growNearbyPlants((ServerLevel) level, pos);
        }
    }
    
    private void growNearbyPlants(ServerLevel level, BlockPos centerPos) {
        List<BlockPos> growablePlants = new ArrayList<>();
        
        // Find all growable plants in radius
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -2; dy <= 2; dy++) { // Check 2 blocks up and down
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos checkPos = centerPos.offset(dx, dy, dz);
                    BlockState checkState = level.getBlockState(checkPos);
                    Block block = checkState.getBlock();
                    
                    if (block instanceof BonemealableBlock bonemealable) {
                        if (bonemealable.isValidBonemealTarget(level, checkPos, checkState, false)) {
                            growablePlants.add(checkPos);
                        }
                    }
                }
            }
        }
        
        if (growablePlants.isEmpty()) {
            return;
        }
        
        // Grow random plants from the list (количество зависит от уровня)
        int plantsToGrow = Math.min(getPlantsPerTick(), growablePlants.size());
        boolean anyGrown = false;
        
        for (int i = 0; i < plantsToGrow; i++) {
            int randomIndex = level.random.nextInt(growablePlants.size());
            BlockPos plantPos = growablePlants.remove(randomIndex);
            
            BlockState plantState = level.getBlockState(plantPos);
            Block block = plantState.getBlock();
            
            if (block instanceof BonemealableBlock bonemealable) {
                if (bonemealable.isValidBonemealTarget(level, plantPos, plantState, false)) {
                    if (bonemealable.isBonemealSuccess(level, level.random, plantPos, plantState)) {
                        bonemealable.performBonemeal(level, level.random, plantPos, plantState);
                        
                        // Spawn particles at the plant
                        level.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                plantPos.getX() + 0.5,
                                plantPos.getY() + 0.5,
                                plantPos.getZ() + 0.5,
                                5,
                                0.3, 0.3, 0.3,
                                0.0
                        );
                        
                        anyGrown = true;
                    }
                }
            }
        }
        
        // Play subtle sound if any plants grew
        if (anyGrown && level.random.nextInt(3) == 0) {
            level.playSound(null, centerPos, SoundEvents.AMETHYST_BLOCK_CHIME, 
                    SoundSource.BLOCKS, 0.3f, 1.2f + level.random.nextFloat() * 0.3f);
        }
    }
}
