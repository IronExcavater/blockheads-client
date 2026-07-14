package com.theblockheads.clienttweaks.compat;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.theblockheads.clienttweaks.ClientTweaks;
import com.theblockheads.clienttweaks.ClientTweaksConstants;
import com.theblockheads.clienttweaks.InternalEnchantments;
import com.tiviacz.travelertoolbelt.init.ModItems;
import com.tiviacz.travelertoolbelt.recipe.ShapedBeltRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRuntimeRegistration;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.equipment.Equippable;

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
	public void registerExtraIngredients(IExtraIngredientRegistration registration) {
		if (FabricLoader.getInstance().isModLoaded("mr_villager_vanity")) {
			registration.addExtraItemStacks(villagerVanityRecipes().stream()
				.map(VillagerVanityRecipe::result)
				.toList());
			ClientTweaksConstants.LOGGER.info("Registered {} Villager Vanity JEI ingredient variants", villagerVanityRecipes().size());
		}
	}

	@Override
	public void registerIngredientAliases(IIngredientAliasRegistration registration) {
		if (FabricLoader.getInstance().isModLoaded("mr_villager_vanity")) {
			for (VillagerVanityRecipe recipe : villagerVanityRecipes()) {
				registration.addAlias(recipe.result(), recipe.displayName());
			}
		}
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		if (FabricLoader.getInstance().isModLoaded("travelertoolbelt")) {
			registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, List.of(
				beltUpgrade(registration, "copper_belt", Items.COPPER_INGOT, ModItems.BELT, ModItems.COPPER_BELT),
				beltUpgrade(registration, "iron_belt", Items.IRON_INGOT, ModItems.COPPER_BELT, ModItems.IRON_BELT),
				beltUpgrade(registration, "gold_belt", Items.GOLD_INGOT, ModItems.IRON_BELT, ModItems.GOLD_BELT),
				beltUpgrade(registration, "diamond_belt", Items.DIAMOND, ModItems.GOLD_BELT, ModItems.DIAMOND_BELT)
			));
		}

		if (FabricLoader.getInstance().isModLoaded("mr_villager_vanity")) {
			List<RecipeHolder<CraftingRecipe>> recipes = villagerVanityRecipes().stream()
				.map(recipe -> villagerVanity(registration, recipe))
				.toList();
			registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, recipes);
			ClientTweaksConstants.LOGGER.info("Registered {} Villager Vanity JEI crafting recipes", recipes.size());
		}
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

	private static RecipeHolder<CraftingRecipe> villagerVanity(
		IRecipeRegistration registration,
		VillagerVanityRecipe recipe
	) {
		IJeiShapedRecipeBuilder builder = registration.getVanillaRecipeFactory()
			.createShapedRecipeBuilder(
				CraftingBookCategory.EQUIPMENT,
				new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(recipe.result()))
			)
			.group("villager_vanity.headwear");

		recipe.ingredients().forEach((key, item) -> builder.define(key, Ingredient.of(item)));
		for (String row : recipe.pattern()) {
			builder.pattern(row);
		}

		ResourceKey<Recipe<?>> id = ResourceKey.create(
			Registries.RECIPE,
			Identifier.fromNamespaceAndPath(ClientTweaks.MOD_ID, "jei/villager_vanity/" + recipe.path())
		);
		return new RecipeHolder<>(id, builder.build());
	}

	private static List<VillagerVanityRecipe> villagerVanityRecipes() {
		return List.of(
			villagerVanityRecipe("armorer_mask", "Armorer's Mask", "armorer_mask", Map.of(
				'G', Items.LEATHER,
				'I', Items.IRON_INGOT,
				'W', Items.IRON_NUGGET
			), "GIG", "IWI"),
			villagerVanityRecipe("butcher_headband", "Butcher's Headband", "butcher_headband", Map.of(
				'G', Items.LEATHER
			), "G G", "GGG"),
			villagerVanityRecipe("cartographer_monocle", "Cartographer's Monocle", "cartographer_monocle", Map.of(
				'G', Items.GLASS_PANE,
				'I', Items.GOLD_NUGGET
			), " II", "IGI", " I "),
			villagerVanityRecipe("desert_cap", "Dusty Cap", "desert_cap", Map.of(
				'G', Items.ORANGE_CARPET,
				'I', Items.WHITE_WOOL
			), " I ", "GGG"),
			villagerVanityRecipe("farmer_hat", "Farmer's Hat", "farmer_hat", Map.of(
				'G', Items.LEATHER,
				'I', Items.WHEAT
			), " G ", "III"),
			villagerVanityRecipe("fisherman_hat", "Fisherman's Hat", "fisherman_hat", Map.of(
				'G', Items.WHEAT
			), "GGG", "G G"),
			villagerVanityRecipe("fletcher_hat", "Fletcher's Cap", "fletcher_hat", Map.of(
				'G', Items.LEATHER,
				'I', Items.FEATHER
			), "GGI", "G G"),
			villagerVanityRecipe("librarian_book", "Librarian's Book and Glasses", "librarian_book", Map.of(
				'G', Items.BOOK,
				'I', Items.GLASS_PANE
			), "G ", "II"),
			villagerVanityRecipe("savanna_headband", "Leafy Headband", "savanna_headband", Map.of(
				'G', Items.VINE
			), " GG", "G G", "GG "),
			villagerVanityRecipe("shepherd_hat", "Shepherd's Cap", "shepherd_hat", Map.of(
				'G', Items.BROWN_WOOL,
				'W', Items.BROWN_CARPET
			), "GGG", "W W"),
			villagerVanityRecipe("snow_cap", "Snowy Cap", "snow_cap", Map.of(
				'G', Items.WHITE_CARPET,
				'W', Items.CYAN_WOOL
			), " G ", "W W"),
			villagerVanityRecipe("swamp_lilypad", "Wet Lilypad", "swamp_lilypad", Map.of(
				'G', Items.LEATHER,
				'W', Items.LILY_PAD
			), "W", "G"),
			villagerVanityRecipe("weaponsmith_eyepatch", "Weaponsmith's Eyepatch", "weaponsmith_eye_patch", Map.of(
				'G', Items.BLACK_WOOL,
				'I', Items.STRING
			), " I ", "I I", " IG")
		);
	}

	private static VillagerVanityRecipe villagerVanityRecipe(
		String path,
		String displayName,
		String modelPath,
		Map<Character, Item> ingredients,
		String... pattern
	) {
		ItemStack result = new ItemStack(Items.POISONOUS_POTATO);
		result.set(DataComponents.ITEM_NAME, Component.literal(displayName));
		result.set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath("villager_vanity", modelPath));
		result.set(DataComponents.MAX_STACK_SIZE, 1);
		result.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build());
		result.remove(DataComponents.CONSUMABLE);
		result.remove(DataComponents.FOOD);
		return new VillagerVanityRecipe(path, displayName, ingredients, List.of(pattern), result);
	}

	private record VillagerVanityRecipe(String path, String displayName, Map<Character, Item> ingredients, List<String> pattern, ItemStack result) {
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
