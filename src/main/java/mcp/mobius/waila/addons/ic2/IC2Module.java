package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;

import java.lang.reflect.Field;

@WailaPlugin
public class IC2Module implements IWailaPlugin {

    public static Class generator = null;
    public static Field generatorStorage = null;
    public static Field generatorMaxStorage = null;
    public static Field generatorProduction = null;


    @Override
    public void register(IWailaRegistrar registrar) {
        try {
            generator = Class.forName("ic2.core.block.base.tile.TileEntityGeneratorBase");
            generatorStorage = generator.getDeclaredField("storage");
            generatorMaxStorage = generator.getDeclaredField("maxStorage");
            generatorProduction = generator.getDeclaredField("production");


            registrar.registerBodyProvider(HUDHandlerTEGenerator.INSTANCE, generator);
            registrar.registerNBTProvider(HUDHandlerTEGenerator.INSTANCE, generator);

            registrar.addConfig("Industrial Craft 2", "ic2.storage", true);
            registrar.addConfig("Industrial Craft 2", "ic2.outputeu", true);

        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                Waila.LOGGER.info("[Industrial Craft 2] IndustrialCraft 2 mod not found.");
            } else {
                Waila.LOGGER.warn("[Industrial Craft 2] Error while loading generator hooks. {}", e);
            }
        }
    }
}
