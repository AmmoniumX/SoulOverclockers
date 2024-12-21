package com.ammonium.souloverclockers.recipe;

import com.ammonium.souloverclockers.SoulOverclockers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class NBTCraftingRecipe extends ShapedRecipe {
    public NBTCraftingRecipe(ResourceLocation id, String group, int recipeWidth, int recipeHeight, NonNullList<Ingredient> ingredients, ItemStack result) {
        super(id, group, recipeWidth, recipeHeight, ingredients, result);
    }

    @Override
    public ItemStack assemble(@NotNull CraftingContainer inv) {
        return super.assemble(inv);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SoulOverclockers.NBT_CRAFTING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<NBTCraftingRecipe> {
        // Directly delegate to ShapedRecipe's serializer for JSON.
        @Override
        public NBTCraftingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Standard ShapedRecipe deserialization
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
            // Deserialize NBT from JSON
            CompoundTag nbt = null;
            if (json.has("result") && json.get("result").isJsonObject()) {
                JsonObject result = json.getAsJsonObject("result");
                if (result.has("nbt")) {
                    try {
                        JsonElement element = result.get("nbt");
                        String nbtData = element.getAsString();
                        if (!StringUtils.isBlank(nbtData)) {
                            nbt = TagParser.parseTag(nbtData);
                        }
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error parsing NBT for recipe: " + recipeId, e);
                    }
                }
            }
            ItemStack output = recipe.getResultItem();
            if (nbt != null) {
                output.setTag(nbt);
            }
            return new NBTCraftingRecipe(recipe.getId(), recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), output);
        }

        @Override
        public NBTCraftingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
            // Read whether there is NBT data
            boolean hasNbt = buffer.readBoolean();
            ItemStack output = Objects.requireNonNull(recipe).getResultItem();
            if (hasNbt) {
                // Read the NBT data from the buffer
                CompoundTag nbt = buffer.readNbt();
                if (nbt != null) {
                    output.setTag(nbt);
                }
            }
            return new NBTCraftingRecipe(recipe.getId(), recipe.getGroup(),
                    recipe.getRecipeWidth(), recipe.getRecipeHeight(),
                    recipe.getIngredients(), output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, NBTCraftingRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
            ItemStack result = recipe.getResultItem();
            CompoundTag nbt = result.getTag();
            // Write whether there is NBT data
            buffer.writeBoolean(nbt != null);
            if (nbt != null) {
                // Write the NBT data to the buffer
                buffer.writeNbt(nbt);
            }
        }
    }
}

