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
import cc.unilock.chromatinei.util.CCUtil;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CastingTableHandler extends TemplateRecipeHandler {
    public class CachedCastingRecipe extends CachedRecipe {
        public final CastingRecipe recipe;
        public final boolean visible;

        private CachedCastingRecipe(CastingRecipe c) {
            recipe = c;
            visible = CCUtil.playerCanSee(c.getOutput());
        }

        @Override
        public ArrayList<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (!visible) return stacks;
            ItemStack[] items = recipe.getArrayForDisplay();
            int dx = 48;
            int dy = recipe instanceof MultiBlockCastingRecipe ? 79 : 34;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    ItemStack is = items[i+j*3];
                    if (is != null) {
                        stacks.add(new PositionedStack(is, dx+18*i, dy+18*j));
                    }
                }
            }
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
                    int px = 66+sx*(tx);
                    int py = 97+sy*(ty);
                    stacks.add(new PositionedStack(is.getCycledItem(), px, py));
                }
            }
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            if (!visible) return null;
            return new PositionedStack(recipe.getOutput(), 66, 4);
        }
    }

    @Override
    public String getRecipeName() {
        return "Casting Table";
    }

    @Override
    public String getGuiTexture() {
        return "/assets/chromatinei/textures/gui/table2.png";
    }

    public String getGuiTexture(int recipe) {
        CachedCastingRecipe c = (CachedCastingRecipe)arecipes.get(recipe);
        CastingRecipe r = c.recipe;
        return switch (r.type) {
            case CRAFTING, TEMPLE -> "/assets/chromatinei/textures/gui/table2.png";
            case MULTIBLOCK -> "/assets/chromatinei/textures/gui/table4.png";
            case PYLON -> "/assets/chromatinei/textures/gui/table5.png";
        };
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public void drawBackground(int recipe) {
        CachedCastingRecipe c = (CachedCastingRecipe)arecipes.get(recipe);
        if (!c.visible) return;
        CastingRecipe r = c.recipe;
        int h = r instanceof PylonCastingRecipe ? 225 : r instanceof MultiBlockCastingRecipe ? 178 : 88;
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture(recipe));
        ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 0, 0, 148, h, ReikaGuiAPI.NEI_DEPTH);
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
            for (CastingRecipe castingRecipe : li) {
                arecipes.add(new CachedCastingRecipe(castingRecipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        ArrayList<CastingRecipe> li = RecipesCastingTable.instance.getAllRecipesUsing(ingredient);
        for (CastingRecipe castingRecipe : li) {
            CachedCastingRecipe c = new CachedCastingRecipe(castingRecipe);
            if (c.visible) {
                arecipes.add(c);
            }
        }
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCastingTable.class;
    }

    @Override
    public void drawExtras(int recipe) {
        CachedCastingRecipe c = (CachedCastingRecipe)arecipes.get(recipe);
        if (!c.visible) {
            GuiDraw.drawString("There is still much to learn...", 0, 0, 0, false);
            return;
        } else {
            // TODO: text widget that takes you to lexicon page
        }
        if (c.recipe instanceof PylonCastingRecipe p) {
            ElementTagCompound tag = p.getRequiredAura();
            for (CrystalElement e : tag.elementSet()) {
                int w = 4;
                int x = 12+e.ordinal()*w*2;
                ReikaGuiAPI.instance.drawRect(x, 188, x+w, 223, e.getJavaColor().darker().darker().getRGB());
            }
        }
    }
}
