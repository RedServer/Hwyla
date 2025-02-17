package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Method;

@WailaPlugin
public class ThermalExpansionModule implements IWailaPlugin {

    /* CoFH Helpers */
    public static Class cofhItemHelper = null;
    public static Method readItemStackFromNBT = null;


    public static Class tileCache = null;
    public static Method tileCacheGetStored = null;


    public void register(IWailaRegistrar registrar) {
        try {
            cofhItemHelper = Class.forName("cofh.core.util.helpers.ItemHelper");
            readItemStackFromNBT = cofhItemHelper.getMethod("readItemStackFromNBT", NBTTagCompound.class);


            tileCache = Class.forName("cofh.thermalexpansion.block.storage.TileCache");
            tileCacheGetStored = tileCache.getDeclaredMethod("getStoredCount");

            registrar.registerHeadProvider(HUDHandlerCache.INSTANCE, tileCache);
            registrar.registerBodyProvider(HUDHandlerCache.INSTANCE, tileCache);
            registrar.registerNBTProvider(HUDHandlerCache.INSTANCE, tileCache);

            registrar.addConfig("Thermal Expansion", "thermalexpansion.cache");
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                Waila.LOGGER.info("[Thermal Expansion] Thermal Expansion mod not found.");

            } else {
                Waila.LOGGER.warn("[Thermal Expansion] Error while loading store cache hooks. {}", e);
            }
        }
    }

}
