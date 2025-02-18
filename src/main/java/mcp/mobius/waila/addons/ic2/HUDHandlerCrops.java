package mcp.mobius.waila.addons.ic2;


import mcp.mobius.waila.Waila;
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

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerCrops implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerCrops();

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null)
            return currenttip;
        NBTTagCompound tag = (NBTTagCompound) accessor.getNBTData();

        int scanLevel = tag.getInteger("scanLevel");
        String defaultFormat = "§f%s: %d§r";
        if (scanLevel >= 1) {
            currenttip.add(LangUtil.translateG("hud.ic2.msg.growthtitile"));
            currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.currentsize"), tag.getInteger("currentSize")));
            currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.growthpoints"), tag.getInteger("growthPoints")));
        }
        if (scanLevel >= 4) {
            currenttip.add(LangUtil.translateG("hud.ic2.msg.stattitle"));
            currenttip.add(String.format("§f%s: §2%d/31§r", LangUtil.translateG("hud.ic2.msg.statgrowth"), tag.getInteger("statGrowth")));
            currenttip.add(String.format("§f%s: §6%d/31§r", LangUtil.translateG("hud.ic2.msg.statgain"), tag.getInteger("statGain")));
            currenttip.add(String.format("§f%s: §3%d/31§r", LangUtil.translateG("hud.ic2.msg.statresistance"), tag.getInteger("statResistance")));
        }
        currenttip.add(LangUtil.translateG("hud.ic2.msg.storagetitle"));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.storagenutrients"), tag.getInteger("storageNutrients")));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.storagewater"), tag.getInteger("storageWater")));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.storageweedex"), tag.getInteger("storageWeedEX")));

        currenttip.add(LangUtil.translateG("hud.ic2.msg.terrraintitle"));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.terrainnutrients"), tag.getInteger("terrainNutrients")));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.terrainhumidity"), tag.getInteger("terrainHumidity")));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.terrainairquality"), tag.getInteger("terrainAirQuality")));
        currenttip.add(String.format(defaultFormat, LangUtil.translateG("hud.ic2.msg.lightlevel"), tag.getInteger("lightLevel")));


        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {

        // TODO: Переместить в объект? Ассоциативный массив?
        // TODO: Оптимизировать типы
        int scanLevel = 0;
        int storageNutrients = 0;
        int storageWater = 0;
        int storageWeedEX = 0;

        int terrainNutrients = 0;
        int terrainHumidity = 0;
        int terrainAirQuality = 0;
        int lightLevel = 0;

        int currentSize = -1;
        int growthPoints = -1;
        int statGrowth = -1;
        int statGain = -1;
        int statResistance = -1;

        try {
            if (IC2Module.crops.isInstance(te)) {
                scanLevel = (Integer) IC2Module.cropsScanLevel.invoke(te);
                storageNutrients = (Integer) IC2Module.cropsStorageNutrients.invoke(te);
                storageWater = (Integer) IC2Module.cropsStorageWater.invoke(te);
                storageWeedEX = (Integer) IC2Module.cropsStorageWeedEX.invoke(te);
                terrainNutrients = (Integer) IC2Module.cropsTerrainNutrients.invoke(te);
                terrainHumidity = (Integer) IC2Module.cropsTerrainHumidity.invoke(te);
                terrainAirQuality = (Integer) IC2Module.cropsTerrainAirQuality.invoke(te);
                lightLevel = (Integer) IC2Module.cropsLightLevel.invoke(te);
                if (scanLevel >= 1) {
                    currentSize = (Integer) IC2Module.cropsCurrentSize.invoke(te);
                    growthPoints = (Integer) IC2Module.cropsGrowthPoints.invoke(te);
                }
                if (scanLevel >= 4) {
                    statGrowth = (Integer) IC2Module.cropsStatGrowth.invoke(te);
                    statGain = (Integer) IC2Module.cropsStatGain.invoke(te);
                    statResistance = (Integer) IC2Module.cropsStatResistance.invoke(te);
                }


            }
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }

        tag.setInteger("scanLevel", scanLevel);
        tag.setInteger("storageNutrients", storageNutrients);
        tag.setInteger("storageWater", storageWater);
        tag.setInteger("storageWeedEX", storageWeedEX);
        tag.setInteger("terrainNutrients", terrainNutrients);
        tag.setInteger("terrainHumidity", terrainHumidity);
        tag.setInteger("terrainAirQuality", terrainAirQuality);
        tag.setInteger("lightLevel", lightLevel);
        tag.setInteger("currentSize", currentSize);
        tag.setInteger("growthPoints", growthPoints);
        tag.setInteger("statGrowth", statGrowth);
        tag.setInteger("statGain", statGain);
        tag.setInteger("statResistance", statResistance);

        return tag;
    }

}
