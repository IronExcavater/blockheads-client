package com.theblockheads.clienttweaks.compat;

import java.util.List;
import java.util.function.Supplier;

import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.InternalEnchantments;
import com.tiviacz.travelertoolbelt.init.ModItems;
import com.tiviacz.travelertoolbelt.recipe.ShapedBeltRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

	private static final Identifier UID = Identifier.fromNamespaceAndPath(ClientTweaks.MOD_ID, "jei_plugin");

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		if (FabricLoader.getInstance().isModLoaded("travelertoolbelt")) {
			registration.getCraftingCategory().addExtension(ShapedBeltRecipe.class, new BeltShapedExtension());
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (!FabricLoader.getInstance().isModLoaded("travelertoolbelt")) {
			return;
		}

		registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, List.of(
			beltUpgrade(registration, "copper_belt", Items.COPPER_INGOT, ModItems.BELT, ModItems.COPPER_BELT),
			beltUpgrade(registration, "iron_belt", Items.IRON_INGOT, ModItems.COPPER_BELT, ModItems.IRON_BELT),
			beltUpgrade(registration, "gold_belt", Items.GOLD_INGOT, ModItems.IRON_BELT, ModItems.GOLD_BELT),
			beltUpgrade(registration, "diamond_belt", Items.DIAMOND, ModItems.GOLD_BELT, ModItems.DIAMOND_BELT)
		));
	}

	@Override
	public void registerRuntime(IRuntimeRegistration registration) {
		List<ItemStack> hiddenStacks = registration.getIngredientManager()
			.getAllItemStacks()
			.stream()
			.filter(InternalEnchantments::shouldHide)
			.toList();

		if (!hiddenStacks.isEmpty()) {
			registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hiddenStacks);
		}
	}

	private static RecipeHolder<CraftingRecipe> beltUpgrade(
		IRecipeRegistration registration,
		String path,
		Item material,
		Supplier<Item> base,
		Supplier<Item> result
	) {
		IJeiShapedRecipeBuilder builder = registration.getVanillaRecipeFactory()
			.createShapedRecipeBuilder(CraftingBookCategory.EQUIPMENT, new SlotDisplay.ItemSlotDisplay(result.get()))
			.group("travelertoolbelt.belt_upgrade")
			.define('A', Ingredient.of(material))
			.define('B', Ingredient.of(base.get()))
			.pattern(" A ")
			.pattern("ABA")
			.pattern(" A ");

		ResourceKey<Recipe<?>> id = ResourceKey.create(
			Registries.RECIPE,
			Identifier.fromNamespaceAndPath(ClientTweaks.MOD_ID, "jei/travelertoolbelt/" + path)
		);
		return new RecipeHolder<>(id, builder.build());
	}

	private static final class BeltShapedExtension implements ICraftingCategoryExtension<ShapedBeltRecipe> {
		@Override
		public List<SlotDisplay> getIngredients(RecipeHolder<ShapedBeltRecipe> recipeHolder) {
			return recipeHolder.value().pattern.ingredients().stream()
				.map(Ingredient::optionalIngredientToDisplay)
				.toList();
		}

		@Override
		public int getWidth(RecipeHolder<ShapedBeltRecipe> recipeHolder) {
			return recipeHolder.value().pattern.width();
		}

		@Override
		public int getHeight(RecipeHolder<ShapedBeltRecipe> recipeHolder) {
			return recipeHolder.value().pattern.height();
		}
	}
}
