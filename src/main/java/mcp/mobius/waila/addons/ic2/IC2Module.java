package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;

import java.lang.reflect.Field;

@WailaPlugin
public class IC2Module implements IWailaPlugin{

    public static Class TileBaseGenerator = null;
    public static Field TileBaseGenerator_storage = null;
    public static Field TileBaseGenerator_maxStorage = null;
    public static Field TileBaseGenerator_production = null;


    @Override
    public void register(IWailaRegistrar registrar) {

        try {
            TileBaseGenerator = Class.forName("ic2.core.block.base.tile.TileEntityGeneratorBase");
            TileBaseGenerator_storage = TileBaseGenerator.getDeclaredField("storage");
            TileBaseGenerator_maxStorage = TileBaseGenerator.getDeclaredField("maxStorage");
            TileBaseGenerator_production = TileBaseGenerator.getDeclaredField("production");


            registrar.registerBodyProvider(HUDHandlerTEGenerator.INSTANCE, TileBaseGenerator);
            registrar.registerNBTProvider(HUDHandlerTEGenerator.INSTANCE, TileBaseGenerator);

            registrar.addConfig("Industrial Craft 2", "ic2.storage", true);
            registrar.addConfig("Industrial Craft 2", "ic2.outputeu", true);

        } catch (Exception e) {
            if (e instanceof ClassNotFoundException || e instanceof NoSuchFieldException) {
                Waila.LOGGER.info("[Industrial Craft 2] IndustrialCraft 2 mod not found.");
            } else {
                Waila.LOGGER.warn("[Industrial Craft 2] Error while loading generator hooks." + e);
            }
        }
    }
}
