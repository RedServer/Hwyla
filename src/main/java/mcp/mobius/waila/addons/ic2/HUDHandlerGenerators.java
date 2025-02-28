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

public class HUDHandlerGenerators implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerGenerators();
    private static final String TEXT_LINE_FORMAT = "%s: §f%d§r ";
    private static final String ENERGY_INOUT_FORMAT = "%s: §f%d§r EU/t";
    private static final String STORAGE_FORMAT = "%s: §f%d/%d§r %s ";
    private static final String PERCENTAGE_FORMAT = "(§f%.1f%%§r) ";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null) return currentTip;
        NBTTagCompound nbtData = accessor.getNBTData();

        int maxEU = nbtData.getInteger("maxEU");
        int storedEU = nbtData.getInteger("storedEU");

        int maxFuel = nbtData.getInteger("maxFuel");
        int storedFuel = nbtData.getInteger("storedFuel");

        if (config.getConfig("ic2.tier")) {
            currentTip.add(String.format(TEXT_LINE_FORMAT, LangUtil.translateG("hud.ic2.msg.tier"), nbtData.getInteger("tier")));
        }


        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.output"), nbtData.getInteger("output")));

        if (maxEU > 0 && config.getConfig("ic2.storageenergy")) {
            addStorageLine(currentTip, config, LangUtil.translateG("hud.ic2.msg.storedenergy"), "EU", storedEU, maxEU);
        }

        if (maxFuel > 0 && config.getConfig("ic2.storagefuel")) {
            addStorageLine(currentTip, config, LangUtil.translateG("hud.ic2.msg.storedfuel"), "", storedFuel, maxFuel);
        }
        return currentTip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.getClass("TileEntityFuelGeneratorBase").isInstance(te)) {
                tag.setInteger("tier", (Integer) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getSourceTier", te));
                tag.setInteger("output", (Integer) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getOutput", te));
                tag.setInteger("maxEU", (Integer) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getMaxEU", te));
                tag.setInteger("storedEU", (Integer) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getStoredEU", te));
                tag.setInteger("maxFuel", Math.round((Float) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getMaxFuel", te)));
                tag.setInteger("storedFuel", Math.round((Float) IC2Module.invokeMethod("TileEntityFuelGeneratorBase.getFuel", te)));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get generator data", e);
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
