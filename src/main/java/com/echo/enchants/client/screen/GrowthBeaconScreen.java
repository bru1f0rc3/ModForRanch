package com.echo.enchants.client.screen;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.block.entity.GrowthBeaconBlockEntity;
import com.echo.enchants.menu.GrowthBeaconMenu;
import com.echo.enchants.network.ModNetworking;
import com.echo.enchants.network.packet.GrowthBeaconUpdatePacket;
import com.echo.enchants.network.packet.GrowthBeaconUpgradePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Кастомный GUI экран для Маяка Роста.
 * Позволяет настраивать радиус действия, включать/выключать блок,
 * и управлять улучшениями скорости и радиуса.
 * 
 * Улучшения требуют Супер Удобрение из инвентаря игрока.
 */
public class GrowthBeaconScreen extends AbstractContainerScreen<GrowthBeaconMenu> {
    
    // GUI размеры (увеличены для улучшений)
    private static final int GUI_WIDTH = 220;
    private static final int GUI_HEIGHT = 200;
    
    // Цвета
    private static final int COLOR_BG_OUTER = 0xFF1a472a;      // Тёмно-зелёный внешний
    private static final int COLOR_BG_INNER = 0xFF2d5a3f;      // Зелёный внутренний
    private static final int COLOR_BG_PANEL = 0xFF3d7a5f;      // Светло-зелёный панель
    private static final int COLOR_BORDER = 0xFF0d2818;        // Очень тёмный бордер
    private static final int COLOR_BORDER_LIGHT = 0xFF4a9970;  // Светлый бордер
    private static final int COLOR_TEXT_TITLE = 0x3ADF00;      // Ярко-зелёный заголовок
    private static final int COLOR_TEXT_WHITE = 0xFFFFFF;      // Белый текст
    private static final int COLOR_TEXT_GRAY = 0xAAAAAA;       // Серый текст
    private static final int COLOR_TEXT_GOLD = 0xFFD700;       // Золотой текст
    private static final int COLOR_ACTIVE = 0xFF00FF00;        // Активный (зелёный)
    private static final int COLOR_INACTIVE = 0xFFFF0000;      // Неактивный (красный)
    private static final int COLOR_UPGRADE_FULL = 0xFF00FFAA;  // Заполненный апгрейд
    private static final int COLOR_UPGRADE_EMPTY = 0xFF333333; // Пустой апгрейд
    
    private int currentRadius;
    private boolean currentActive;
    private int currentSpeedLevel;
    private int currentRangeLevel;
    
    // Кнопки
    private Button decreaseButton;
    private Button increaseButton;
    private Button toggleButton;
    private Button speedUpgradeButton;
    private Button rangeUpgradeButton;
    
    public GrowthBeaconScreen(GrowthBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.currentRadius = menu.getRadius();
        this.currentActive = menu.isActive();
        this.currentSpeedLevel = menu.getSpeedLevel();
        this.currentRangeLevel = menu.getRangeLevel();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Центрируем GUI
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        // Убираем стандартные лейблы
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        
        int radiusButtonY = topPos + 55;
        
        // Кнопка уменьшения радиуса [-]
        this.decreaseButton = Button.builder(Component.literal("◄"), button -> {
            if (currentRadius > GrowthBeaconBlockEntity.MIN_RADIUS) {
                currentRadius--;
                sendUpdateToServer();
            }
        }).bounds(leftPos + 45, radiusButtonY, 30, 20).build();
        
        // Кнопка увеличения радиуса [+]
        this.increaseButton = Button.builder(Component.literal("►"), button -> {
            int maxRadius = GrowthBeaconBlockEntity.BASE_MAX_RADIUS + 
                           (currentRangeLevel * GrowthBeaconBlockEntity.RADIUS_BONUS_PER_LEVEL);
            if (currentRadius < maxRadius) {
                currentRadius++;
                sendUpdateToServer();
            }
        }).bounds(leftPos + 145, radiusButtonY, 30, 20).build();
        
        // Кнопка включения/выключения
        this.toggleButton = Button.builder(
                getToggleButtonText(), 
                button -> {
                    currentActive = !currentActive;
                    button.setMessage(getToggleButtonText());
                    sendUpdateToServer();
                }
        ).bounds(leftPos + 60, topPos + 170, 100, 20).build();
        
        // === Кнопки улучшений ===
        int upgradeY = topPos + 100;
        
        // Кнопка улучшения скорости (требует Супер Удобрение)
        this.speedUpgradeButton = Button.builder(Component.literal("+"), button -> {
            if (currentSpeedLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL) {
                // Отправляем пакет улучшения на сервер - он сам заберёт удобрение из инвентаря
                ModNetworking.sendToServer(new GrowthBeaconUpgradePacket(
                        menu.getBlockPos(), GrowthBeaconUpgradePacket.UpgradeType.SPEED));
            }
        }).bounds(leftPos + 185, upgradeY, 20, 16).build();
        
        // Кнопка улучшения радиуса (требует Супер Удобрение)
        this.rangeUpgradeButton = Button.builder(Component.literal("+"), button -> {
            if (currentRangeLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL) {
                // Отправляем пакет улучшения на сервер - он сам заберёт удобрение из инвентаря
                ModNetworking.sendToServer(new GrowthBeaconUpgradePacket(
                        menu.getBlockPos(), GrowthBeaconUpgradePacket.UpgradeType.RANGE));
            }
        }).bounds(leftPos + 185, upgradeY + 30, 20, 16).build();
        
        this.addRenderableWidget(decreaseButton);
        this.addRenderableWidget(increaseButton);
        this.addRenderableWidget(toggleButton);
        this.addRenderableWidget(speedUpgradeButton);
        this.addRenderableWidget(rangeUpgradeButton);
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        // Блокируем кнопки если достигнут максимум
        speedUpgradeButton.active = currentSpeedLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL;
        rangeUpgradeButton.active = currentRangeLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL;
        
        int maxRadius = GrowthBeaconBlockEntity.BASE_MAX_RADIUS + 
                       (currentRangeLevel * GrowthBeaconBlockEntity.RADIUS_BONUS_PER_LEVEL);
        increaseButton.active = currentRadius < maxRadius;
        decreaseButton.active = currentRadius > GrowthBeaconBlockEntity.MIN_RADIUS;
    }
    
    private Component getToggleButtonText() {
        return currentActive ? 
                Component.literal("§a✓ ВКЛЮЧЕН") : 
                Component.literal("§c✗ ВЫКЛЮЧЕН");
    }
    
    private void sendUpdateToServer() {
        // Отправляем только радиус и статус активности
        // Улучшения отправляются отдельным пакетом GrowthBeaconUpgradePacket
        ModNetworking.sendToServer(new GrowthBeaconUpdatePacket(
                menu.getBlockPos(), currentRadius, currentActive, currentSpeedLevel, currentRangeLevel));
        updateButtonStates();
    }
    
    @Override
    protected void containerTick() {
        super.containerTick();
        // Синхронизация с сервером
        if (currentRadius != menu.getRadius()) {
            currentRadius = menu.getRadius();
        }
        if (currentActive != menu.isActive()) {
            currentActive = menu.isActive();
            toggleButton.setMessage(getToggleButtonText());
        }
        if (currentSpeedLevel != menu.getSpeedLevel()) {
            currentSpeedLevel = menu.getSpeedLevel();
        }
        if (currentRangeLevel != menu.getRangeLevel()) {
            currentRangeLevel = menu.getRangeLevel();
        }
        updateButtonStates();
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Рисуем кастомный фон GUI
        
        // Внешняя рамка (тёмная)
        guiGraphics.fill(leftPos - 3, topPos - 3, leftPos + imageWidth + 3, topPos + imageHeight + 3, COLOR_BORDER);
        
        // Внешний фон
        guiGraphics.fill(leftPos - 2, topPos - 2, leftPos + imageWidth + 2, topPos + imageHeight + 2, COLOR_BG_OUTER);
        
        // Внутренняя рамка (светлая)
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, COLOR_BORDER_LIGHT);
        
        // Внутренний фон
        guiGraphics.fill(leftPos + 2, topPos + 2, leftPos + imageWidth - 2, topPos + imageHeight - 2, COLOR_BG_INNER);
        
        // Панель заголовка
        guiGraphics.fill(leftPos + 5, topPos + 5, leftPos + imageWidth - 5, topPos + 28, COLOR_BG_PANEL);
        guiGraphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + 27, COLOR_BG_OUTER);
        
        // Панель радиуса
        guiGraphics.fill(leftPos + 10, topPos + 35, leftPos + imageWidth - 10, topPos + 85, COLOR_BG_PANEL);
        guiGraphics.fill(leftPos + 12, topPos + 37, leftPos + imageWidth - 12, topPos + 83, 0xFF1f3d2a);
        
        // Панель улучшений
        guiGraphics.fill(leftPos + 10, topPos + 92, leftPos + imageWidth - 10, topPos + 160, COLOR_BG_PANEL);
        guiGraphics.fill(leftPos + 12, topPos + 94, leftPos + imageWidth - 12, topPos + 158, 0xFF1f3d2a);
        
        // Декоративные уголки
        drawCornerDecoration(guiGraphics, leftPos + 8, topPos + 8);
        drawCornerDecoration(guiGraphics, leftPos + imageWidth - 12, topPos + 8);
        drawCornerDecoration(guiGraphics, leftPos + 8, topPos + imageHeight - 12);
        drawCornerDecoration(guiGraphics, leftPos + imageWidth - 12, topPos + imageHeight - 12);
    }
    
    private void drawCornerDecoration(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x, y, x + 4, y + 1, COLOR_BORDER_LIGHT);
        guiGraphics.fill(x, y, x + 1, y + 4, COLOR_BORDER_LIGHT);
    }
    
    private void drawUpgradeBar(GuiGraphics guiGraphics, int x, int y, int level, int maxLevel) {
        int barWidth = 100;
        int barHeight = 8;
        int segmentWidth = barWidth / maxLevel;
        
        // Фон полоски
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, COLOR_UPGRADE_EMPTY);
        
        // Заполненные сегменты
        for (int i = 0; i < level; i++) {
            int segX = x + i * segmentWidth;
            guiGraphics.fill(segX + 1, y + 1, segX + segmentWidth - 1, y + barHeight - 1, COLOR_UPGRADE_FULL);
        }
        
        // Разделители
        for (int i = 1; i < maxLevel; i++) {
            int lineX = x + i * segmentWidth;
            guiGraphics.fill(lineX, y, lineX + 1, y + barHeight, COLOR_BG_OUTER);
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Затемнение фона
        this.renderBackground(guiGraphics);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // ========== ЗАГОЛОВОК ==========
        String title = "✦ МАЯК РОСТА ✦";
        int titleWidth = this.font.width(title);
        guiGraphics.drawString(this.font, title, 
                leftPos + (imageWidth - titleWidth) / 2, 
                topPos + 12, 
                COLOR_TEXT_TITLE, true);
        
        // ========== ИНФОРМАЦИЯ О РАДИУСЕ ==========
        String radiusLabel = "Радиус действия:";
        int labelWidth = this.font.width(radiusLabel);
        guiGraphics.drawString(this.font, radiusLabel, 
                leftPos + (imageWidth - labelWidth) / 2, 
                topPos + 42, 
                COLOR_TEXT_WHITE, false);
        
        // Значение радиуса (большое число по центру)
        String radiusValue = String.valueOf(currentRadius);
        int valueWidth = this.font.width(radiusValue);
        guiGraphics.drawString(this.font, radiusValue, 
                leftPos + (imageWidth - valueWidth) / 2, 
                topPos + 58, 
                COLOR_TEXT_TITLE, true);
        
        // Информация о площади
        int area = (currentRadius * 2 + 1);
        int maxRadius = GrowthBeaconBlockEntity.BASE_MAX_RADIUS + 
                       (currentRangeLevel * GrowthBeaconBlockEntity.RADIUS_BONUS_PER_LEVEL);
        String areaText = "Площадь: " + area + "×" + area + " (макс: " + maxRadius + ")";
        int areaWidth = this.font.width(areaText);
        guiGraphics.drawString(this.font, areaText, 
                leftPos + (imageWidth - areaWidth) / 2, 
                topPos + 73, 
                COLOR_TEXT_GRAY, false);
        
        // ========== УЛУЧШЕНИЯ ==========
        String upgradesTitle = "⚡ УЛУЧШЕНИЯ ⚡";
        int upgradeTitleWidth = this.font.width(upgradesTitle);
        guiGraphics.drawString(this.font, upgradesTitle, 
                leftPos + (imageWidth - upgradeTitleWidth) / 2, 
                topPos + 97, 
                COLOR_TEXT_GOLD, true);
        
        // Скорость
        int upgradeY = topPos + 112;
        guiGraphics.drawString(this.font, "Скорость:", leftPos + 18, upgradeY, COLOR_TEXT_WHITE, false);
        drawUpgradeBar(guiGraphics, leftPos + 80, upgradeY, currentSpeedLevel, GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL);
        
        // Показываем интервал в секундах и стоимость
        int tickInterval = Math.max(20, 60 - (currentSpeedLevel * 8));
        float seconds = tickInterval / 20.0f;
        String speedInfo = String.format("%.1f сек", seconds);
        guiGraphics.drawString(this.font, speedInfo, leftPos + 18, upgradeY + 10, COLOR_TEXT_GRAY, false);
        
        // Стоимость улучшения скорости
        if (currentSpeedLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL) {
            String speedCost = "§e" + GrowthBeaconUpgradePacket.SPEED_UPGRADE_COST + " СУ";
            guiGraphics.drawString(this.font, speedCost, leftPos + 80, upgradeY + 10, COLOR_TEXT_GRAY, false);
        } else {
            guiGraphics.drawString(this.font, "§aMAX", leftPos + 80, upgradeY + 10, COLOR_TEXT_GRAY, false);
        }
        
        // Радиус (улучшение)
        upgradeY += 30;
        guiGraphics.drawString(this.font, "Макс. радиус:", leftPos + 18, upgradeY, COLOR_TEXT_WHITE, false);
        drawUpgradeBar(guiGraphics, leftPos + 80, upgradeY, currentRangeLevel, GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL);
        
        String rangeInfo = "+" + (currentRangeLevel * 2) + " блоков";
        guiGraphics.drawString(this.font, rangeInfo, leftPos + 18, upgradeY + 10, COLOR_TEXT_GRAY, false);
        
        // Стоимость улучшения радиуса
        if (currentRangeLevel < GrowthBeaconBlockEntity.MAX_UPGRADE_LEVEL) {
            String rangeCost = "§e" + GrowthBeaconUpgradePacket.RANGE_UPGRADE_COST + " СУ";
            guiGraphics.drawString(this.font, rangeCost, leftPos + 80, upgradeY + 10, COLOR_TEXT_GRAY, false);
        } else {
            guiGraphics.drawString(this.font, "§aMAX", leftPos + 80, upgradeY + 10, COLOR_TEXT_GRAY, false);
        }
        
        // ========== СТАТУС ==========
        String statusLabel = "Статус:";
        guiGraphics.drawString(this.font, statusLabel, 
                leftPos + 18, 
                topPos + 175, 
                COLOR_TEXT_WHITE, false);
        
        // Индикатор статуса (цветной квадрат)
        int indicatorColor = currentActive ? COLOR_ACTIVE : COLOR_INACTIVE;
        guiGraphics.fill(leftPos + 18, topPos + 187, leftPos + 26, topPos + 195, indicatorColor);
        
        // Tooltip при наведении
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Не рендерим стандартные лейблы
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
