package cc.unilock.chromatinei.util;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CCUtil {
    public static boolean playerCanSee(ItemStack is) {
        if (is != null && is.getItem() != null) {
            EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
            ChromaResearch r = getChromaResearch(is);
            return r != null && r.playerCanSee(ep) && r.isCrafting() && r.isCraftable() && !r.isVanillaRecipe() && r.playerCanSeeRecipe(is, ep) && r.crafts(is);
        }
        return false;
    }

    public static boolean loadLexiconRecipe(ItemStack is) {
        if (is != null && is.getItem() != null) {
            EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
            ChromaResearch r = getChromaResearch(is);
            if (r != null && r.playerCanSee(ep) && r.isCrafting() && r.isCraftable() && !r.isVanillaRecipe() && r.playerCanSeeRecipe(is, ep) && r.crafts(is)) {
                ep.openGui(ChromatiCraft.instance, r.getCraftingType().ordinal(), null, r.ordinal(), r.getRecipeIndex(is, ep), 1);
                return true;
            } else if (ReikaItemHelper.collectionContainsItemStack(ChromaResearch.APIRECIPES.getItemStacks(), is)) {
                ep.openGui(ChromatiCraft.instance, ChromaResearch.APIRECIPES.getCraftingType().ordinal(), null, ChromaResearch.APIRECIPES.ordinal(), ChromaResearch.APIRECIPES.getRecipeIndex(is, ep), 1);
                return true;
            }
        }
        return false;
    }

    private static ChromaResearch getChromaResearch(ItemStack is) {
        ChromaResearch r = ChromaResearch.getPageFor(is);
        if (is.stackTagCompound != null && is.stackTagCompound.getBoolean("boosted") && r.getMachine() != null && r.getMachine().isRepeater()) {
            r = ChromaResearch.TURBOREPEATER;
        }
        return r;
    }
}
