package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@WailaPlugin
public class IC2Module implements IWailaPlugin {

    public static Class generator = null;
    public static Field generatorStored = null;
    public static Field generatorCapacity = null;
    public static Field generatorOutput = null;
    public static Field generatorTier = null;

    public static Class eBlock = null;
    public static Field eBlockStored = null;
    public static Field eBlockCapacity = null;
    public static Field eBlockOutput = null;
    public static Field eBlockTier = null;

    public static Class eMachine = null;
    public static Field eMachineStored = null;
    public static Field eMachineCapacity = null;
    public static Field eMachineInput = null;
    public static Field eMachineTier = null;

    public static Class crops = null;
    public static Method cropsStorageNutrients = null;
    public static Method cropsStorageWater = null;
    public static Method cropsStorageWeedEX = null;
    public static Method cropsTerrainNutrients = null;
    public static Method cropsTerrainHumidity = null;
    public static Method cropsTerrainAirQuality = null;
    public static Method cropsLightLevel = null;
    public static Method cropsScanLevel = null;
    public static Method cropsCurrentSize = null;
    public static Method cropsGrowthPoints = null;
    public static Method cropsStatGrowth = null;
    public static Method cropsStatGain = null;
    public static Method cropsStatResistance = null;

    @Override
    public void register(IWailaRegistrar registrar) {
        if (!Loader.isModLoaded("ic2")) return;
        try {
            /* Generators */
            generator = Class.forName("ic2.core.block.base.tile.TileEntityGeneratorBase");
            generatorStored = generator.getDeclaredField("storage");
            generatorCapacity = generator.getDeclaredField("maxStorage");
            generatorOutput = generator.getDeclaredField("production");
            generatorTier = generator.getDeclaredField("tier");

            registrar.registerBodyProvider(HUDHandlerTEGenerator.INSTANCE, generator);
            registrar.registerNBTProvider(HUDHandlerTEGenerator.INSTANCE, generator);

            /* EU Storages */
            eBlock = Class.forName("ic2.core.block.base.tile.TileEntityElectricBlock");
            eBlockStored = eBlock.getDeclaredField("energy");
            eBlockCapacity = eBlock.getDeclaredField("maxEnergy");
            eBlockOutput = eBlock.getDeclaredField("output");
            eBlockTier = eBlock.getDeclaredField("tier");

            registrar.registerBodyProvider(HUDHandlerTEGenerator.INSTANCE, eBlock);
            registrar.registerNBTProvider(HUDHandlerTEGenerator.INSTANCE, eBlock);

            /* Machines */
            eMachine = Class.forName("ic2.core.block.base.tile.TileEntityElecMachine");
            // TODO: Вырезать или оставить
            // stored и capacity понадобятся в будущем... Или нет
            eMachineStored = eMachine.getDeclaredField("energy");
            eMachineCapacity = eMachine.getDeclaredField("maxEnergy");
            eMachineInput = eMachine.getDeclaredField("maxInput");
            eMachineTier = eMachine.getDeclaredField("tier");

            registrar.registerBodyProvider(HUDHandlerTEGenerator.INSTANCE, eMachine);
            registrar.registerNBTProvider(HUDHandlerTEGenerator.INSTANCE, eMachine);

            /* Crops */
            crops = Class.forName("ic2.core.block.crop.TileEntityCrop");

            cropsStorageNutrients = crops.getDeclaredMethod("getStorageNutrients");
            cropsStorageWater = crops.getDeclaredMethod("getStorageWater");
            cropsStorageWeedEX = crops.getDeclaredMethod("getStorageWeedEX");
            cropsTerrainNutrients = crops.getDeclaredMethod("getTerrainNutrients");
            cropsTerrainHumidity = crops.getDeclaredMethod("getTerrainHumidity");
            cropsTerrainAirQuality = crops.getDeclaredMethod("getTerrainAirQuality");
            cropsLightLevel = crops.getDeclaredMethod("getLightLevel");
            cropsScanLevel = crops.getDeclaredMethod("getScanLevel");
            cropsCurrentSize = crops.getDeclaredMethod("getCurrentSize");
            cropsGrowthPoints = crops.getDeclaredMethod("getGrowthPoints");
            cropsStatGrowth = crops.getDeclaredMethod("getStatGrowth");
            cropsStatGain = crops.getDeclaredMethod("getStatGain");
            cropsStatResistance = crops.getDeclaredMethod("getStatResistance");

            registrar.registerBodyProvider(HUDHandlerCrops.INSTANCE, crops);
            registrar.registerNBTProvider(HUDHandlerCrops.INSTANCE, crops);

            /* Config */
            registrar.addConfig("Industrial Craft 2", "ic2.storage", true);
            registrar.addConfig("Industrial Craft 2", "ic2.percentage", true);
            registrar.addConfig("Industrial Craft 2", "ic2.inouteu", true);
            registrar.addConfig("Industrial Craft 2", "ic2.tier", true);
            registrar.addConfig("Industrial Craft 2", "ic2.crops", true);

        } catch (Exception e) {
            Waila.LOGGER.warn("[Industrial Craft 2] Error while loading generator hooks.", e);
        }
    }
}
