package com.echo.enchants.datagen;

import com.echo.enchants.EchoEnchantsMod;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Генератор моделей для предметов.
 */
public class ModItemModelProvider extends ItemModelProvider {
    
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EchoEnchantsMod.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        // Super fertilizer - generated texture
        withExistingParent("super_fertilizer", "item/generated")
                .texture("layer0", modLoc("item/super_fertilizer"));
    }
}
