package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class HUDHandlerCrops implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerCrops();

    private static final String FORMAT_PARAMS_DEFAULT = "§f%s: %d/%d§r";
    private static final String FORMAT_PARAMS_COLORED = "%s%s:§r §f%d/%d§r";
    private static final String FORMAT_PARAMS_TITLE = "§e%s§r";

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        NBTTagCompound tag = accessor.getNBTData();
        ItemStack result = accessor.getStack();
        TileEntity te = accessor.getTileEntity();

        try {
            if (!IC2Module.classes.get("TileEntityCrop").isInstance(te)) return result;

            Object cropCard = IC2Module.methods.get("TileEntityCrop.getCrop").invoke(te);

            if (cropCard == null) return result;

            if (tag.getInteger("scanLevel") < 1) {
                result.setStackDisplayName(LangUtil.translateG("hud.ic2.msg.unknown"));
                return result;
            }

            Object instance = IC2Module.fields.get("Ic2Crops.instance").get(null);
            ItemStack displayItem = (ItemStack) IC2Module.methods.get("Ic2Crops.getDisplayItem").invoke(instance, cropCard);
            if (displayItem == null) return result;

            Object LocaleContext = IC2Module.methods.get("Ic2Crops.getCropName").invoke(instance, cropCard);
            displayItem.setStackDisplayName((String) IC2Module.methods.get("LocaleComp.getLocalized").invoke(LocaleContext));
            result = displayItem;
        } catch (Exception e) {
            LOGGER.error("Failed to get crop data", e);
        }
        return result;
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (!config.getConfig("ic2.crops")) return currenttip;

        Optional.ofNullable(accessor.getTileEntity()).ifPresent(te -> {
            NBTTagCompound tag = accessor.getNBTData();
            int scanLevel = tag.getInteger("scanLevel");

            if (!accessor.getPlayer().isSneaking() && config.getConfig("ic2.crops.sneakshow")) {
                currenttip.add(String.format(FORMAT_PARAMS_TITLE, LangUtil.translateG("hud.ic2.msg.sneaktip")));
            } else {
                if (scanLevel < 4) {
                    currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.scanlevel"), scanLevel, 4));
                }
                if (scanLevel >= 1) {
                    addGrowthInfo(currenttip, tag);
                }
                if (scanLevel >= 4) {
                    addStatInfo(currenttip, tag);
                }
                addStorageInfo(currenttip, tag);
                addTerrainInfo(currenttip, tag);
            }
        });

        return currenttip;
    }

    private void addGrowthInfo(List<String> currenttip, NBTTagCompound tag) {

        currenttip.add(String.format(FORMAT_PARAMS_TITLE, LangUtil.translateG("hud.ic2.msg.growthtitile")));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.currentsize"), tag.getInteger("currentSize"), tag.getInteger("maxSize")));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.growthpoints"), tag.getInteger("growthPoints"), tag.getInteger("maxPoints")));
    }

    private void addStatInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(FORMAT_PARAMS_TITLE, LangUtil.translateG("hud.ic2.msg.stattitle")));
        currenttip.add(String.format(FORMAT_PARAMS_COLORED, "§2", LangUtil.translateG("hud.ic2.msg.statgrowth"), tag.getInteger("statGrowth"), 31));
        currenttip.add(String.format(FORMAT_PARAMS_COLORED, "§6", LangUtil.translateG("hud.ic2.msg.statgain"), tag.getInteger("statGain"), 31));
        currenttip.add(String.format(FORMAT_PARAMS_COLORED, "§3", LangUtil.translateG("hud.ic2.msg.statresistance"), tag.getInteger("statResistance"), 31));
    }

    private void addStorageInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(FORMAT_PARAMS_TITLE, LangUtil.translateG("hud.ic2.msg.storagetitle")));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.storagenutrients"), tag.getInteger("storageNutrients"), 300));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.storagewater"), tag.getInteger("storageWater"), 200));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.storageweedex"), tag.getInteger("storageWeedEX"), 150));
    }

    private void addTerrainInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(FORMAT_PARAMS_TITLE, LangUtil.translateG("hud.ic2.msg.terrraintitle")));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.terrainnutrients"), tag.getInteger("terrainNutrients"), 20));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.terrainhumidity"), tag.getInteger("terrainHumidity"), 20));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.terrainairquality"), tag.getInteger("terrainAirQuality"), 10));
        currenttip.add(String.format(FORMAT_PARAMS_DEFAULT, LangUtil.translateG("hud.ic2.msg.lightlevel"), tag.getInteger("lightLevel"), 15));
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.classes.get("TileEntityCrop").isInstance(te)) {
                Object cropCard = IC2Module.methods.get("TileEntityCrop.getCrop").invoke(te);
                if (cropCard != null) {
                    tag.setInteger("maxSize", (Integer) IC2Module.methods.get("CropCard.getMaxSize").invoke(cropCard));
                    tag.setInteger("maxPoints", (Integer) IC2Module.methods.get("CropCard.getGrowthDuration").invoke(cropCard, IC2Module.classes.get("ICropTile").cast(te)));
                }

                tag.setInteger("scanLevel",         (Integer) IC2Module.methods.get("TileEntityCrop.getScanLevel").invoke(te));
                tag.setInteger("storageNutrients",  (Integer) IC2Module.methods.get("TileEntityCrop.getStorageNutrients").invoke(te));
                tag.setInteger("storageWater",      (Integer) IC2Module.methods.get("TileEntityCrop.getStorageWater").invoke(te));
                tag.setInteger("storageWeedEX",     (Integer) IC2Module.methods.get("TileEntityCrop.getStorageWeedEX").invoke(te));
                tag.setInteger("terrainNutrients",  (Integer) IC2Module.methods.get("TileEntityCrop.getTerrainNutrients").invoke(te));
                tag.setInteger("terrainHumidity",   (Integer) IC2Module.methods.get("TileEntityCrop.getTerrainHumidity").invoke(te));
                tag.setInteger("terrainAirQuality", (Integer) IC2Module.methods.get("TileEntityCrop.getTerrainAirQuality").invoke(te));
                tag.setInteger("lightLevel",        (Integer) IC2Module.methods.get("TileEntityCrop.getLightLevel").invoke(te));
                tag.setInteger("currentSize",       (Integer) IC2Module.methods.get("TileEntityCrop.getCurrentSize").invoke(te));
                tag.setInteger("growthPoints",      (Integer) IC2Module.methods.get("TileEntityCrop.getGrowthPoints").invoke(te));
                tag.setInteger("statGrowth",        (Integer) IC2Module.methods.get("TileEntityCrop.getStatGrowth").invoke(te));
                tag.setInteger("statGain",          (Integer) IC2Module.methods.get("TileEntityCrop.getStatGain").invoke(te));
                tag.setInteger("statResistance",    (Integer) IC2Module.methods.get("TileEntityCrop.getStatResistance").invoke(te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get crop data", e);
        }
        return tag;
    }
}
