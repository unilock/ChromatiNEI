/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package cc.unilock.chromatinei.compat.nei.handler;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.PylonCastingRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCastingTable;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CastingTableHandler extends TemplateRecipeHandler {
    public class CastingCrafting extends CachedRecipe {
        public final CastingRecipe recipe;

        private CastingCrafting(CastingRecipe c) {
            recipe = c;
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            ItemStack[] items = recipe.getArrayForDisplay();
            int dx = 53;
            int dy = recipe instanceof MultiBlockCastingRecipe ? 51 : 31;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    ItemStack is = items[i+j*3];
                    if (is != null) {
                        stacks.add(new PositionedStack(is, dx+18*i, dy+18*j));
                    }
                }
            }
            dx = 9;
            dy = 6;
            if (recipe instanceof MultiBlockCastingRecipe) {
                Map<List<Integer>, ItemMatch> map = ((MultiBlockCastingRecipe)recipe).getAuxItems();
                for (List<Integer> key : map.keySet()) {
                    ItemMatch is = map.get(key);
                    int i = key.get(0);
                    int k = key.get(1);
                    int sx = Integer.compare(i, 0);
                    int sy = Integer.compare(k, 0);
                    int tx = Math.abs(i) == 2 ? 38 : 64;
                    int ty = Math.abs(k) == 2 ? 38 : 63;
                    int px = 80+sx*(tx);
                    int py = 75+sy*(ty);
                    stacks.add(new PositionedStack(is.getCycledItem(), px-dx, py-dy));
                }
            }
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            return new PositionedStack(recipe.getOutput(), 180, 6);
        }
    }

    @Override
    public String getRecipeName() {
        return "Casting Table";
    }

    @Override
    public String getGuiTexture() {
        return "/Reika/ChromatiCraft/Textures/GUIs/table2.png";
    }

    public String getGuiTexture(int recipe) {
        CastingCrafting c = (CastingCrafting)arecipes.get(recipe);
        CastingRecipe r = c.recipe;
        return switch (r.type) {
            case CRAFTING, TEMPLE -> "/Reika/ChromatiCraft/Textures/GUIs/table2.png";
            case MULTIBLOCK -> "/Reika/ChromatiCraft/Textures/GUIs/table4.png";
            case PYLON -> "/Reika/ChromatiCraft/Textures/GUIs/table5.png";
            default -> "/Reika/ChromatiCraft/Textures/GUIs/table.png";
        };
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public void drawBackground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        CastingCrafting c = (CastingCrafting)arecipes.get(recipe);
        CastingRecipe r = c.recipe;
        int h = r instanceof PylonCastingRecipe ? 187 : r instanceof MultiBlockCastingRecipe ? 150 : 114;
        ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture(recipe));
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 9, 6, 207, h, ReikaGuiAPI.NEI_DEPTH);
    }

    @Override
    public void drawForeground(int recipe) {
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
        this.drawExtras(recipe);
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result != null) {
            ArrayList<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipesMaking(result);
            for (CastingRecipe castingRecipe : li)
                arecipes.add(new CastingCrafting(castingRecipe));
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        ArrayList<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipesUsing(ingredient);
        for (CastingRecipe castingRecipe : li)
            arecipes.add(new CastingCrafting(castingRecipe));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCastingTable.class;
    }

    @Override
    public void drawExtras(int recipe) {
        CastingCrafting r = (CastingCrafting)arecipes.get(recipe);
        if (r.recipe instanceof PylonCastingRecipe p) {
            ElementTagCompound tag = p.getRequiredAura();
            for (CrystalElement e : tag.elementSet()) {
                int w = 4;
                int x = 174+e.ordinal()%4*w*2;
                int y1 = 29+e.ordinal()/4*40;
                int y = 64+e.ordinal()/4*40;
                ReikaGuiAPI.instance.drawRect(x, y1, x+w, y, e.getJavaColor().darker().darker().getRGB());
            }
        }
		/*
		ReikaGuiAPI.instance.drawTexturedModalRect(6, 17, 176, 44, 11, 43);
		BlastTempRecipe r = ((BlastTempRecipe)arecipes.get(recipe));
		String s = String.format("%dC", r.getRecipeTemperature());
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		ReikaGuiAPI.instance.drawCenteredStringNoShadow(f, s, f.getStringWidth(s)/2-2*(s.length()/5), 61, 0);
		 */
    }
}
