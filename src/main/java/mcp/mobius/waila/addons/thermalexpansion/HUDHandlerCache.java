package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.annotation.Nonnull;

public class HUDHandlerCache implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerCache();

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        if (!config.getConfig("thermalexpansion.cache")) return currenttip;
        try {
            ItemStack storedItem = null;
            TileEntity te = accessor.getTileEntity();
            NBTTagCompound tag = accessor.getNBTData();
            if (accessor.getNBTData().hasKey("Item"))
                storedItem = (ItemStack) ThermalExpansionModule.invokeMethod("ItemHelper.readItemStackFromNBT", te, tag.getCompoundTag("Item"));


            String name = currenttip.get(0);
            String color = "";
            if (name.startsWith("§")) color = name.substring(0, 2);

            if (storedItem != null) {
                name += String.format(color + " < %s >", storedItem.getDisplayName());
            } else name += " " + "EMPTY";

            currenttip.set(0, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return currenttip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        if (!config.getConfig("thermalexpansion.cache")) return currenttip;

        try {
            String storedStr = LangUtil.translateG("hud.te.msg.stored");
            String capacityStr = LangUtil.translateG("hud.te.msg.capacity");

            TileEntity te = accessor.getTileEntity();
            NBTTagCompound tag = accessor.getNBTData();

            ItemStack storedItem = null;
            if (tag.hasKey("Item")) {
                storedItem = (ItemStack) ThermalExpansionModule.invokeMethod("ItemHelper.readItemStackFromNBT", te, tag.getCompoundTag("Item"));
            }

            int stored = 0;
            int maxStored = 0;
            if (tag.hasKey("Stored")) stored = tag.getInteger("Stored");
            if (tag.hasKey("MaxStored")) maxStored = tag.getInteger("MaxStored");

            if (storedItem != null) {
                currenttip.add(storedStr + ": " + stored);
            } else currenttip.add(capacityStr + ": " + maxStored);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currenttip;
    }


    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te != null) te.writeToNBT(tag);

        try {
            tag.setInteger("Stored", (Integer) ThermalExpansionModule.invokeMethod("TileCache.getStoredCount",te));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tag;
    }


}
