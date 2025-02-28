package mcp.mobius.waila.addons.ic2;

import java.util.List;
import javax.annotation.Nonnull;
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

public class HUDHandlerEUStorages implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerEUStorages();

    private static final String TEXT_LINE_FORMAT = "%s: §f%d§r ";
    private static final String ENERGY_INOUT_FORMAT = "%s: §f%d§r EU/t";
    private static final String STORAGE_FORMAT = "%s: §f%d/%d§r %s ";
    private static final String PERCENTAGE_FORMAT = "(§f%.1f%%§r)";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null) return currentTip;

        NBTTagCompound nbtData = accessor.getNBTData();
        int capacity = nbtData.getInteger("capacityEnergy");
        int stored = nbtData.getInteger("storedEnergy");

        if (config.getConfig("ic2.tier")) {
            currentTip.add(String.format(TEXT_LINE_FORMAT, LangUtil.translateG("hud.ic2.msg.tier"), nbtData.getInteger("tier")));
        }

        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.input"), nbtData.getInteger("input")));
        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.output"), nbtData.getInteger("output")));


        if (capacity > 0 && config.getConfig("ic2.storageenergy"))
            addStorageLine(currentTip, config, LangUtil.translateG("hud.ic2.msg.storedenergy"), "EU", stored, capacity);

        return currentTip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.getClass("TileEntityElectricBlock").isInstance(te)) {
                tag.setInteger("tier", (Integer) IC2Module.invokeMethod("TileEntityElectricBlock.getTier", te));
                tag.setInteger("capacityEnergy", (Integer) IC2Module.invokeMethod("TileEntityElectricBlock.getCapacity", te));
                tag.setInteger("storedEnergy", (Integer) IC2Module.invokeMethod("TileEntityElectricBlock.getStored", te));
                tag.setInteger("output", (Integer) IC2Module.invokeMethod("TileEntityElectricBlock.getOutput", te));
                tag.setInteger("input", (Integer) IC2Module.invokeMethod("TileEntityElectricBlock.getOutput", te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get EU storages data", e);
        }
        return tag;
    }


    private void addStorageLine(List<String> currentTip, IWailaConfigHandler config, String title, String units, int stored, int capacity) {
        String capacityLine = "";
        capacityLine += String.format(STORAGE_FORMAT, title, stored, capacity, units);
        if (config.getConfig("ic2.percentage"))
            capacityLine += String.format(PERCENTAGE_FORMAT, ((float)stored / capacity) * 100);

        currentTip.add(capacityLine);
    }
}
