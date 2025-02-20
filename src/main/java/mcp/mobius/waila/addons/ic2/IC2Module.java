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

    protected static Class<?>
            TileEntityGeneratorBase,
            TileEntityElectricBlock,
            TileEntityElecMachine,
            TileEntityCrop,
            CropCard,
            ICropTile,
            Ic2Crops;

    protected static Field
            generatorStored,
            generatorCapacity,
            generatorOutput,
            generatorTier,
            eBlockStored,
            eBlockCapacity,
            eBlockOutput,
            eBlockTier,
            eMachineStored,
            eMachineCapacity,
            eMachineInput,
            eMachineTier,
            ic2cropsInstance;

    protected static Method
            ic2cropsDisplayItem,
            cCardMaxSize,
            cCardMaxPoints,
            teCropGetCropCard,
            teCropStorageNutrients,
            teCropStorageWater,
            teCropStorageWeedEX,
            teCropTerrainNutrients,
            teCropTerrainHumidity,
            teCropTerrainAirQuality,
            teCropLightLevel,
            teCropScanLevel,
            teCropCurrentSize,
            teCropGrowthPoints,
            teCropStatGrowth,
            teCropStatGain,
            teCropStatResistance;


    private IWailaRegistrar registrar;

    @Override
    public void register(IWailaRegistrar registrar) {
        if (!Loader.isModLoaded("ic2")) return;
        this.registrar = registrar;

        try {
            registerGenerators();
            registerEUStorages();
            registerMachines();
            registerCrops();
            registerConfigs();
        } catch (Exception e) {
            Waila.LOGGER.warn("[Industrial Craft 2] Error while loading hooks.", e);
        }

    }

    private void registerCrops() throws Exception {
        TileEntityCrop = Class.forName("ic2.core.block.crop.TileEntityCrop");
        CropCard = Class.forName("ic2.api.crops.CropCard");
        ICropTile = Class.forName("ic2.api.crops.ICropTile");
        Ic2Crops = Class.forName("ic2.core.block.crop.Ic2Crops");

        ic2cropsInstance = IC2Module.Ic2Crops.getField("instance");
        ic2cropsDisplayItem = IC2Module.Ic2Crops.getMethod("getDisplayItem", IC2Module.CropCard);

        cCardMaxPoints = CropCard.getMethod("getGrowthDuration", ICropTile);
        cCardMaxSize = CropCard.getMethod("getMaxSize");

        teCropGetCropCard = TileEntityCrop.getMethod("getCrop");
        teCropStorageNutrients = TileEntityCrop.getMethod("getStorageNutrients");
        teCropStorageWater = TileEntityCrop.getMethod("getStorageWater");
        teCropStorageWeedEX = TileEntityCrop.getMethod("getStorageWeedEX");
        teCropTerrainNutrients = TileEntityCrop.getMethod("getTerrainNutrients");
        teCropTerrainHumidity = TileEntityCrop.getMethod("getTerrainHumidity");
        teCropTerrainAirQuality = TileEntityCrop.getMethod("getTerrainAirQuality");
        teCropLightLevel = TileEntityCrop.getMethod("getLightLevel");
        teCropScanLevel = TileEntityCrop.getMethod("getScanLevel");
        teCropCurrentSize = TileEntityCrop.getMethod("getCurrentSize");
        teCropGrowthPoints = TileEntityCrop.getMethod("getGrowthPoints");
        teCropStatGrowth = TileEntityCrop.getMethod("getStatGrowth");
        teCropStatGain = TileEntityCrop.getMethod("getStatGain");
        teCropStatResistance = TileEntityCrop.getMethod("getStatResistance");

        this.registrar.registerStackProvider(HUDHandlerCrops.INSTANCE, TileEntityCrop);
        this.registrar.registerHeadProvider(HUDHandlerCrops.INSTANCE, TileEntityCrop);
        this.registrar.registerBodyProvider(HUDHandlerCrops.INSTANCE, TileEntityCrop);
        this.registrar.registerNBTProvider(HUDHandlerCrops.INSTANCE, TileEntityCrop);
    }

    private void registerMachines() throws Exception{
            TileEntityElecMachine = Class.forName("ic2.core.block.base.tile.TileEntityElecMachine");
            eMachineStored = TileEntityElecMachine.getDeclaredField("energy");
            eMachineCapacity = TileEntityElecMachine.getDeclaredField("maxEnergy");
            eMachineInput = TileEntityElecMachine.getDeclaredField("maxInput");
            eMachineTier = TileEntityElecMachine.getDeclaredField("tier");

            this.registrar.registerBodyProvider(HUDHandlerMachines.INSTANCE, TileEntityElecMachine);
            this.registrar.registerNBTProvider(HUDHandlerMachines.INSTANCE, TileEntityElecMachine);
    }

    private void registerEUStorages() throws Exception{
            TileEntityElectricBlock = Class.forName("ic2.core.block.base.tile.TileEntityElectricBlock");
            eBlockStored = TileEntityElectricBlock.getDeclaredField("energy");
            eBlockCapacity = TileEntityElectricBlock.getDeclaredField("maxEnergy");
            eBlockOutput = TileEntityElectricBlock.getDeclaredField("output");
            eBlockTier = TileEntityElectricBlock.getDeclaredField("tier");

            this.registrar.registerBodyProvider(HUDHandlerMachines.INSTANCE, TileEntityElectricBlock);
            this.registrar.registerNBTProvider(HUDHandlerMachines.INSTANCE, TileEntityElectricBlock);
    }

    private void registerGenerators() throws Exception{
            TileEntityGeneratorBase = Class.forName("ic2.core.block.base.tile.TileEntityGeneratorBase");
            generatorStored = TileEntityGeneratorBase.getDeclaredField("storage");
            generatorCapacity = TileEntityGeneratorBase.getDeclaredField("maxStorage");
            generatorOutput = TileEntityGeneratorBase.getDeclaredField("production");
            generatorTier = TileEntityGeneratorBase.getDeclaredField("tier");

            this.registrar.registerBodyProvider(HUDHandlerMachines.INSTANCE, TileEntityGeneratorBase);
            this.registrar.registerNBTProvider(HUDHandlerMachines.INSTANCE, TileEntityGeneratorBase);
    }

    private void registerConfigs() {
        this.registrar.addConfig("Industrial Craft 2", "ic2.storage", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.percentage", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.inouteu", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.tier", true);
        this.registrar.addConfig("IC2 Crops", "ic2.crops", true);
        this.registrar.addConfig("IC2 Crops", "ic2.crops.sheakshow", true);
    }
}
