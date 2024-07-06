package cc.unilock.chromatinei;

import cc.unilock.chromatinei.compat.IMCHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = ChromatiNEI.MOD_ID,
    version = Tags.VERSION,
    name = "ChromatiNEI",
    acceptedMinecraftVersions = "[1.7.10]",
    dependencies = "required-after:ChromatiCraft;" +
        "required-after:NotEnoughItems"
)
public class ChromatiNEI {
    public static final String MOD_ID = "chromatinei";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Hello from ChromatiNEI!");
        IMCHandler.init();
    }
}
