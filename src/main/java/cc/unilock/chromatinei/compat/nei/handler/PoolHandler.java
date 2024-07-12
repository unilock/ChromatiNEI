package cc.unilock.chromatinei.compat.nei.handler;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PoolRecipes;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import cc.unilock.chromatinei.util.CCUtil;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PoolHandler extends TemplateRecipeHandler {
    public class CachedPoolRecipe extends CachedRecipe {
        public final PoolRecipes.PoolRecipe recipe;
        public final boolean visible;

        public CachedPoolRecipe(PoolRecipes.PoolRecipe p) {
            recipe = p;
            visible = p.playerHasProgress(Minecraft.getMinecraft().thePlayer);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<>();
            if (!visible) return stacks;
            stacks.add(new PositionedStack(recipe.getMainInput(), 12, 65));
            int i = 0;
            for (ItemStack is : recipe.getInputs()) {
                List<ItemStack> li = null;
                if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                    li = new ArrayList<>();
                    is.getItem().getSubItems(is.getItem(), is.getItem().getCreativeTab(), li);
                }
                int dx = 50+(i%3)*17;
                int dy = 3+(i/3)*17;
                if (li != null) {
                    stacks.add(new PositionedStack(li, dx, dy, true));
                } else {
                    stacks.add(new PositionedStack(is, dx, dy));
                }
                i++;
            }
            stacks.add(new PositionedStack(ChromaItems.BUCKET.getStackOf(), 122, 65));
            return stacks;
        }

        @Override
        public PositionedStack getResult() {
            if (!visible) return null;
            return new PositionedStack(recipe.getOutput(), 67, 102);
        }

        @Override
        public PositionedStack getOtherStack() {
            return new PositionedStack(ChromaItems.HELP.getStackOf().setStackDisplayName("Click for info"), 0, 0);
        }
    }

    @Override
    public String getRecipeName() {
        return "Alloying";
    }

    @Override
    public String getGuiTexture() {
        return "/assets/chromatinei/textures/gui/pool.png";
    }

    @Override
    public int recipiesPerPage() {
        return 1;
    }

    @Override
    public void drawBackground(int recipe) {
        CachedPoolRecipe c = (CachedPoolRecipe)arecipes.get(recipe);
        if (!c.visible) return;
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getGuiTexture());
        ReikaGuiAPI.instance.drawTexturedModalRectWithDepth(0, 0, 0, 0, 150, 130, ReikaGuiAPI.NEI_DEPTH);
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
            PoolRecipes.PoolRecipe poolRecipe = PoolRecipes.instance.getPoolRecipeByOutput(result);
            if (poolRecipe != null) {
                arecipes.add(new CachedPoolRecipe(poolRecipe));
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        Collection<PoolRecipes.PoolRecipe> li = CCUtil.getAllPoolRecipesUsing(ingredient);
        for (PoolRecipes.PoolRecipe poolRecipe : li) {
            CachedPoolRecipe c = new CachedPoolRecipe(poolRecipe);
            if (c.visible) {
                arecipes.add(c);
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        CachedPoolRecipe c = (CachedPoolRecipe)arecipes.get(recipe);
        if (!c.visible) {
            GuiDraw.drawString("There is still much to learn...", 0, 0, 0, false);
        }
    }

    @Override
    public boolean mouseClicked(GuiRecipe<?> gui, int button, int recipe) {
        CachedPoolRecipe c = (CachedPoolRecipe)arecipes.get(recipe);
        if (c.visible && button == 0 && gui.isMouseOver(c.getOtherStack(), recipe)) {
            CCUtil.loadLexiconRecipe(c.recipe.getOutput());
            return true;
        } else {
            return super.mouseClicked(gui, button, recipe);
        }
    }
}
