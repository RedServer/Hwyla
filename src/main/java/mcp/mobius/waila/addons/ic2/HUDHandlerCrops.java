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

    private static final String DEFAULT_FORMAT = "§f%s: %d§r";
    private static final String TITLE_FORMAT = "§f%s§r";
    private static final String STAT_GROWTH_FORMAT = "§f%s: §2%d/31§r";
    private static final String STAT_GAIN_FORMAT = "§f%s: §6%d/31§r";
    private static final String STAT_RESISTANCE_FORMAT = "§f%s: §3%d/31§r";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        Optional.ofNullable(accessor.getTileEntity()).ifPresent(te -> {
            NBTTagCompound tag = accessor.getNBTData();
            int scanLevel = tag.getInteger("scanLevel");

            if (scanLevel >= 1) {
                addGrowthInfo(currenttip, tag);
            }
            if (scanLevel >= 4) {
                addStatInfo(currenttip, tag);
            }
            addStorageInfo(currenttip, tag);
            addTerrainInfo(currenttip, tag);
        });

        return currenttip;
    }

    private void addGrowthInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(TITLE_FORMAT, LangUtil.translateG("hud.ic2.msg.growthtitile")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.currentsize"), tag.getInteger("currentSize")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.growthpoints"), tag.getInteger("growthPoints")));
    }

    private void addStatInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(TITLE_FORMAT, LangUtil.translateG("hud.ic2.msg.stattitle")));
        currenttip.add(String.format(STAT_GROWTH_FORMAT, LangUtil.translateG("hud.ic2.msg.statgrowth"), tag.getInteger("statGrowth")));
        currenttip.add(String.format(STAT_GAIN_FORMAT, LangUtil.translateG("hud.ic2.msg.statgain"), tag.getInteger("statGain")));
        currenttip.add(String.format(STAT_RESISTANCE_FORMAT, LangUtil.translateG("hud.ic2.msg.statresistance"), tag.getInteger("statResistance")));
    }

    private void addStorageInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(TITLE_FORMAT, LangUtil.translateG("hud.ic2.msg.storagetitle")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.storagenutrients"), tag.getInteger("storageNutrients")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.storagewater"), tag.getInteger("storageWater")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.storageweedex"), tag.getInteger("storageWeedEX")));
    }

    private void addTerrainInfo(List<String> currenttip, NBTTagCompound tag) {
        currenttip.add(String.format(TITLE_FORMAT, LangUtil.translateG("hud.ic2.msg.terrraintitle")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.terrainnutrients"), tag.getInteger("terrainNutrients")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.terrainhumidity"), tag.getInteger("terrainHumidity")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.terrainairquality"), tag.getInteger("terrainAirQuality")));
        currenttip.add(String.format(DEFAULT_FORMAT, LangUtil.translateG("hud.ic2.msg.lightlevel"), tag.getInteger("lightLevel")));
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.crops.isInstance(te)) {
                tag.setInteger("scanLevel", (Integer) IC2Module.cropsScanLevel.invoke(te));
                tag.setInteger("storageNutrients", (Integer) IC2Module.cropsStorageNutrients.invoke(te));
                tag.setInteger("storageWater", (Integer) IC2Module.cropsStorageWater.invoke(te));
                tag.setInteger("storageWeedEX", (Integer) IC2Module.cropsStorageWeedEX.invoke(te));
                tag.setInteger("terrainNutrients", (Integer) IC2Module.cropsTerrainNutrients.invoke(te));
                tag.setInteger("terrainHumidity", (Integer) IC2Module.cropsTerrainHumidity.invoke(te));
                tag.setInteger("terrainAirQuality", (Integer) IC2Module.cropsTerrainAirQuality.invoke(te));
                tag.setInteger("lightLevel", (Integer) IC2Module.cropsLightLevel.invoke(te));
                tag.setInteger("currentSize", (Integer) IC2Module.cropsCurrentSize.invoke(te));
                tag.setInteger("growthPoints", (Integer) IC2Module.cropsGrowthPoints.invoke(te));
                tag.setInteger("statGrowth", (Integer) IC2Module.cropsStatGrowth.invoke(te));
                tag.setInteger("statGain", (Integer) IC2Module.cropsStatGain.invoke(te));
                tag.setInteger("statResistance", (Integer) IC2Module.cropsStatResistance.invoke(te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get crop data", e);
        }
        return tag;
    }
}
