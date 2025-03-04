package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.core.BaseModule;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

@WailaPlugin
public class ThermalExpansionModule extends BaseModule implements IWailaPlugin {


    public void register(IWailaRegistrar registrar) {
        if (!Loader.isModLoaded("thermalexpansion") || !Loader.isModLoaded("cofhcore")) return;
        try {
            registerClass("cofh.core.util.helpers.ItemHelper");
            registerClass("cofh.thermalexpansion.block.storage.TileCache");
            registerMethod("ItemHelper", "readItemStackFromNBT", NBTTagCompound.class);
            registerMethod("TileCache", "getStoredCount");


            registrar.registerHeadProvider(HUDHandlerCache.INSTANCE, getClass("TileCache"));
            registrar.registerBodyProvider(HUDHandlerCache.INSTANCE, getClass("TileCache"));
            registrar.registerNBTProvider(HUDHandlerCache.INSTANCE, getClass("TileCache"));

            registrar.addConfig("Thermal Expansion", "thermalexpansion.cache");
        } catch (Exception e) {
            Waila.LOGGER.warn("[Thermal Expansion] Error while loading store cache hooks.", e);
        }
    }
}
