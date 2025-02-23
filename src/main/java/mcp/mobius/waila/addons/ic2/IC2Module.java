package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WailaPlugin
public class IC2Module implements IWailaPlugin {

    protected static Map<String, Class<?>> classes = new HashMap<>();
    protected static Map<String, Field> fields = new HashMap<>();
    protected static Map<String, Method> methods = new HashMap<>();

    protected static Class<?>
            TileEntityGeneratorBase,
            TileEntityElectricBlock,
            TileEntityElecMachine;

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
            eMachineTier;


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

        registerClass("ic2.core.platform.lang.components.base.LocaleComp");
        registerClass("ic2.api.crops.ICropTile");
        registerClass("ic2.api.crops.CropCard");
        registerClass("ic2.core.block.crop.Ic2Crops");
        registerClass("ic2.core.block.crop.TileEntityCrop");

        registerMethod("LocaleComp","getLocalized");

        registerMethod("CropCard", "getGrowthDuration", classes.get("ICropTile"));
        registerMethod("CropCard", "getSeeds", classes.get("ICropTile"));
        registerMethod("CropCard", "getMaxSize");

        registerField("Ic2Crops", "instance");
        registerMethod("Ic2Crops", "getCropName", classes.get("CropCard"));
        registerMethod("Ic2Crops", "getDisplayItem", classes.get("CropCard"));

        registerMethod("TileEntityCrop", "getCrop");
        registerMethod("TileEntityCrop", "getScanLevel");
        registerMethod("TileEntityCrop", "getStorageNutrients");
        registerMethod("TileEntityCrop", "getStorageWater");
        registerMethod("TileEntityCrop", "getStorageWeedEX");
        registerMethod("TileEntityCrop", "getTerrainNutrients");
        registerMethod("TileEntityCrop", "getTerrainHumidity");
        registerMethod("TileEntityCrop", "getTerrainAirQuality");
        registerMethod("TileEntityCrop", "getLightLevel");
        registerMethod("TileEntityCrop", "getCurrentSize");
        registerMethod("TileEntityCrop", "getGrowthPoints");
        registerMethod("TileEntityCrop", "getStatGrowth");
        registerMethod("TileEntityCrop", "getStatGain");
        registerMethod("TileEntityCrop", "getStatResistance");


        this.registrar.registerStackProvider(HUDHandlerCrops.INSTANCE, classes.get("TileEntityCrop"));
        this.registrar.registerHeadProvider(HUDHandlerCrops.INSTANCE, classes.get("TileEntityCrop"));
        this.registrar.registerBodyProvider(HUDHandlerCrops.INSTANCE, classes.get("TileEntityCrop"));
        this.registrar.registerNBTProvider(HUDHandlerCrops.INSTANCE, classes.get("TileEntityCrop"));
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
        this.registrar.addConfig("IC2 Crops", "ic2.crops.sneakshow", true);
    }


    protected void registerClass(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        classes.put(clazz.getSimpleName(), clazz);
    }

    protected void registerField(String className, String fieldName) throws Exception {
        Class<?> clazz = classes.get(className);
        Field field = clazz.getField(fieldName);
        fields.put(clazz.getSimpleName() + "." + fieldName, field);
    }
    protected void registerMethod(String className, String methodName, Class<?>... parameterTypes) throws Exception {
        Class<?> clazz = classes.get(className);
        Method method = clazz.getMethod(methodName, parameterTypes);
        methods.put(clazz.getSimpleName() + "." + methodName, method);
    }
}
