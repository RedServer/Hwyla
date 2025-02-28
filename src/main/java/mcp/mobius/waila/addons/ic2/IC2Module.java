package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@WailaPlugin
public class IC2Module implements IWailaPlugin {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, Class<?>> classes = new HashMap<>();
    private static final Map<String, Field> fields = new HashMap<>();
    private static final Map<String, Method> methods = new HashMap<>();


    private IWailaRegistrar registrar;

    @Override
    public void register(IWailaRegistrar registrar) {
        if (!Loader.isModLoaded("ic2")) return;
        this.registrar = registrar;

        registerCrops();
        registerGenerator();
        registerEUStorage();
        registerTransformer();
        registerMachines();
        registerConfigs();

    }

    private void registerCrops() {
        try {
            String basePackageName = "ic2.core.block.crop";
            String baseClassName = "TileEntityCrop";

            registerClass("ic2.core.platform.lang.components.base.LocaleComp");
            registerClass("ic2.api.crops.ICropTile");
            registerClass("ic2.api.crops.CropCard");
            registerClass("ic2.core.block.crop.Ic2Crops");
            registerClass(basePackageName + "." + baseClassName);

            registerMethod("LocaleComp", "getLocalized");

            registerMethod("CropCard", "getGrowthDuration", classes.get("ICropTile"));
            registerMethod("CropCard", "getSeeds", classes.get("ICropTile"));
            registerMethod("CropCard", "getMaxSize");

            registerField("Ic2Crops", "instance");
            registerMethod("Ic2Crops", "getCropName", classes.get("CropCard"));
            registerMethod("Ic2Crops", "getDisplayItem", classes.get("CropCard"));

            registerMethod(baseClassName, "getCrop");
            registerMethod(baseClassName, "getScanLevel");
            registerMethod(baseClassName, "getStorageNutrients");
            registerMethod(baseClassName, "getStorageWater");
            registerMethod(baseClassName, "getStorageWeedEX");
            registerMethod(baseClassName, "getTerrainNutrients");
            registerMethod(baseClassName, "getTerrainHumidity");
            registerMethod(baseClassName, "getTerrainAirQuality");
            registerMethod(baseClassName, "getLightLevel");
            registerMethod(baseClassName, "getCurrentSize");
            registerMethod(baseClassName, "getGrowthPoints");
            registerMethod(baseClassName, "getStatGrowth");
            registerMethod(baseClassName, "getStatGain");
            registerMethod(baseClassName, "getStatResistance");

            this.registrar.registerStackProvider(HUDHandlerCrops.INSTANCE, classes.get(baseClassName));
            this.registrar.registerHeadProvider(HUDHandlerCrops.INSTANCE, classes.get(baseClassName));
            this.registrar.registerBodyProvider(HUDHandlerCrops.INSTANCE, classes.get(baseClassName));
            this.registrar.registerNBTProvider(HUDHandlerCrops.INSTANCE, classes.get(baseClassName));
        } catch (Exception e) {
            LOGGER.error("[IC2] Error while loading Crops hooks.", e);
        }
    }


    private void registerGenerator() {
        String packageName = "ic2.core.block.base.tile";
        String className = "TileEntityFuelGeneratorBase";
        try {
            registerClass(packageName + "." + className);
            registerMethod(className, "getStoredEU");
            registerMethod(className, "getMaxEU");
            registerMethod(className, "getOutput");
            registerMethod(className, "getSourceTier");
            registerMethod(className, "getFuel");
            registerMethod(className, "getMaxFuel");

            this.registrar.registerBodyProvider(HUDHandlerGenerators.INSTANCE, classes.get(className));
            this.registrar.registerNBTProvider(HUDHandlerGenerators.INSTANCE, classes.get(className));
        } catch (Exception e) {
            LOGGER.error("[IC2] Error while loading crops hooks.", e);
        }
    }

    private void registerEUStorage() {
        String packageName = "ic2.core.block.base.tile";
        String className = "TileEntityElectricBlock";
        try {
            registerClass(packageName + "." + className);
            registerMethod(className, "getStored");
            registerMethod(className, "getCapacity");
            registerMethod(className, "getOutput");
            registerMethod(className, "getTier");

            this.registrar.registerBodyProvider(HUDHandlerEUStorages.INSTANCE, classes.get(className));
            this.registrar.registerNBTProvider(HUDHandlerEUStorages.INSTANCE, classes.get(className));

        } catch (Exception e) {
            LOGGER.error("[IC2] Error while loading EU Storage hooks.", e);
        }
    }

    private void registerTransformer() {
        String packageName = "ic2.core.block.base.tile";
        String className = "TileEntityTransformer";
        try {
            registerClass(packageName + "." + className);
            registerField(className, "lowOutput");
            registerField(className, "highOutput");
            registerMethod(className, "getActive");
            registerMethod(className, "getSinkTier");
            registerMethod(className, "getSourceTier");
            this.registrar.registerBodyProvider(HUDHandlerTransformers.INSTANCE, classes.get(className));
            this.registrar.registerNBTProvider(HUDHandlerTransformers.INSTANCE, classes.get(className));
        } catch (Exception e) {
            LOGGER.error("[IC2] Error while loading Transformer hooks.", e);
        }
    }

    private void registerMachines() {
        String packageName = "ic2.core.block.base.tile";
        String className = "TileEntityElecMachine";
        try {
            registerClass(packageName + "." + className);
            registerMethod(className, "getSinkTier");
            registerField(className, "maxInput");
            registerMethod(className, "getMaxEU");
            registerMethod(className, "getStoredEU");
            this.registrar.registerBodyProvider(HUDHandlerMachines.INSTANCE, classes.get(className));
            this.registrar.registerNBTProvider(HUDHandlerMachines.INSTANCE, classes.get(className));
        } catch (Exception e) {
            LOGGER.error("[IC2] Error while loading Machines hooks.", e);
        }

    }

    private void registerConfigs() {
        this.registrar.addConfig("Industrial Craft 2", "ic2.storage", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.percentage", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.inouteu", true);
        this.registrar.addConfig("Industrial Craft 2", "ic2.tier", true);
        this.registrar.addConfig("IC2 Crops", "ic2.crops", true);
        this.registrar.addConfig("IC2 Crops", "ic2.crops.sneakshow", true);
    }


    private void registerClass(String className) throws Exception {
        if (classes.containsKey(className)) return;
        Class<?> clazz = Class.forName(className);
        classes.put(clazz.getSimpleName(), clazz);
    }

    private void registerField(String className, String fieldName) throws Exception {
        if (fields.containsKey(className + "." + fieldName)) return;
        Class<?> clazz = classes.get(className);
        Field field = clazz.getField(fieldName);
        fields.put(clazz.getSimpleName() + "." + fieldName, field);
    }

    private void registerMethod(String className, String methodName, Class<?>... parameterTypes) throws Exception {
        if (methods.containsKey("className" + "." + methodName)) return;
        Class<?> clazz = classes.get(className);
        Method method = clazz.getMethod(methodName, parameterTypes);
        methods.put(clazz.getSimpleName() + "." + methodName, method);
    }

    protected static Object invokeMethod(String methodKey, Object instance, Object... args) throws Exception {
        Method method = methods.get(methodKey);
        if (method == null) {
            throw new IllegalArgumentException("Method " + methodKey + " is not registered.");
        }

        return method.invoke(instance, args);
    }

    protected static Object getField(String fieldKey, Object instance) throws Exception {
        Field field = fields.get(fieldKey);
        if (field == null) {
            throw new IllegalArgumentException("Field " + fieldKey + " is not registered.");
        }

        return field.get(instance);
    }

    protected static Class<?> getClass(String className) {
        Class<?> clazz = classes.get(className);
        if (clazz == null) {
            throw new IllegalArgumentException("Class " + className + " is not registered.");
        }
        return clazz;
    }
}
