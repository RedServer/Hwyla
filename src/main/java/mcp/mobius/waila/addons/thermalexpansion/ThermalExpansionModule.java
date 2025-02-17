package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;

import java.lang.reflect.Method;

@WailaPlugin
public class ThermalExpansionModule implements IWailaPlugin {

    public static Class TileCache = null;
    public static Method TileCache_getStored = null;

    public static Method IBlockInfo_getBlockInfo = null;

    public void register(IWailaRegistrar registrar) {
        boolean printedThermalExpansionNotFound = false;

        try {
            TileCache = Class.forName("cofh.thermalexpansion.block.storage.TileCache");
            TileCache_getStored = TileCache.getDeclaredMethod("getStoredCount");

            registrar.registerHeadProvider(HUDHandlerCache.INSTANCE, TileCache);
            registrar.registerBodyProvider(HUDHandlerCache.INSTANCE, TileCache);
            registrar.registerNBTProvider(HUDHandlerCache.INSTANCE, TileCache);

            registrar.addConfig("Thermal Expansion", "thermalexpansion.cache");

        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                if (!printedThermalExpansionNotFound) {
                    printedThermalExpansionNotFound = true;
                    Waila.LOGGER.info("[Thermal Expansion] Thermal Expansion mod not found.");
                }
            } else {
                Waila.LOGGER.warn("[Thermal Expansion] Error while loading store cache hooks. {}", e);
            }
        }
        if (!printedThermalExpansionNotFound) {
            Waila.LOGGER.info("Thermal Expansion mod found.");
        }
    }

}
