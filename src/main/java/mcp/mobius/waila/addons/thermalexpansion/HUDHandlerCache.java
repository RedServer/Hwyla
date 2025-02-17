package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.annotation.Nonnull;

public class HUDHandlerCache implements IWailaDataProvider {
    static final IWailaDataProvider INSTANCE = new HUDHandlerCache();

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {

        if (!config.getConfig("thermalexpansion.cache")) return currenttip;
        try {
            ItemStack storedItem = null;
            if (accessor.getNBTData().hasKey("Item"))
                storedItem = readItemStack(accessor.getNBTData().getCompoundTag("Item"));


            String name = currenttip.get(0);
            String color = "";
            if (name.startsWith("\u00a7")) color = name.substring(0, 2);

            if (storedItem != null) {;
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

        NBTTagCompound tag = accessor.getNBTData();
        ItemStack storedItem = null;
        if (tag.hasKey("Item")) storedItem = readItemStack(tag.getCompoundTag("Item"));

        int stored = 0;
        int maxStored = 0;
        if (tag.hasKey("Stored")) stored = tag.getInteger("Stored");
        if (tag.hasKey("MaxStored")) maxStored = tag.getInteger("MaxStored");

        if (storedItem != null) {
            currenttip.add("Stored: " + stored);
//            currenttip.add("Stored: " + stored + "/" + maxStored); //TODO: maxStored
        } else currenttip.add("Capacity: " + maxStored);

        return currenttip;
    }


    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (te != null) te.writeToNBT(tag);
        try {
            tag.setInteger("MaxStored", 0); //TODO: maxStored
            tag.setInteger("Stored", (Integer) ThermalExpansionModule.TileCache_getStored.invoke(te));
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return tag;
    }

    public ItemStack readItemStack(NBTTagCompound tag) {
        ItemStack is = new ItemStack(Item.getByNameOrId(tag.getString("id")));
        // TODO: WIP
//        is.splitStack(tag.getInteger("Count"));
//        is.setItemDamage(Math.max(0, tag.getShort("Damage")));
//        if (tag.hasKey("tag", 10)) {
//            is.setTagCompound(tag.getCompoundTag("tag"));
//        }
        return is;
    }

}
