package cc.unilock.chromatinei.compat.nei;

import cc.unilock.chromatinei.Tags;
import cc.unilock.chromatinei.compat.nei.handler.CastingTableHandler;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {
    private static final CastingTableHandler castingTable = new CastingTableHandler();

    @Override
    public void loadConfig() {
        API.registerRecipeHandler(castingTable);
        API.registerUsageHandler(castingTable);
    }

    @Override
    public String getName() {
        return "ChromatiNEI";
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
