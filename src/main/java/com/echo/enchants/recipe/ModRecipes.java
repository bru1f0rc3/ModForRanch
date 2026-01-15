package com.echo.enchants.recipe;

import com.echo.enchants.EchoEnchantsMod;
import com.echo.enchants.item.ModItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

/**
 * Recipe definitions for EchoEnchants items.
 */
@Mod.EventBusSubscriber(modid = EchoEnchantsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes {
    
    // Recipes are now defined in JSON files in data/echo_enchants/recipes/
    // This class can be used for programmatic recipe registration if needed
}
