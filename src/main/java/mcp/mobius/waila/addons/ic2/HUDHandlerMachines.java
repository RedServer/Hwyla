package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.api.ITaggedList;
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

public class HUDHandlerMachines implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerMachines();

    private static final String ENERGY_STORAGE_FORMAT = "%s: §f%d§r / §f%d§r EU";
    private static final String PERCENTAGE_FORMAT = " (§f%d%%§r)";
    private static final String INPUT_OUTPUT_FORMAT = "%s: §f%d §r EU/t";
    private static final String TIER_FORMAT = "%s: §f%d §r";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null) {
            return currenttip;
        }

        NBTTagCompound nbtData = accessor.getNBTData();
        double stored = nbtData.getDouble("stored");
        int input = nbtData.getInteger("input");
        int output = nbtData.getInteger("output");
        long capacity = nbtData.getLong("capacity");
        int tier = nbtData.getInteger("tier");

        addEnergyStorageInfo(currenttip, config, stored, capacity);
        addInputOutputInfo(currenttip, config, input, output);
        addTierInfo(currenttip, config, tier);

        return currenttip;
    }

    private void addEnergyStorageInfo(List<String> currenttip, IWailaConfigHandler config, double stored, long capacity) {
        if (capacity > 0) {
            StringBuilder energyLine = new StringBuilder();
            String storedStr = LangUtil.translateG("hud.ic2.msg.stored");

            if (config.getConfig("ic2.storage")) {
                energyLine.append(String.format(ENERGY_STORAGE_FORMAT, storedStr, Math.round(Math.min(stored, capacity)), capacity));
            }

            if (config.getConfig("ic2.percentage")) {
                energyLine.append(String.format(PERCENTAGE_FORMAT, Math.round((stored / capacity) * 100)));
            }

            ((ITaggedList<String, String>) currenttip).add(energyLine.toString(), "IEnergyStorage");
        }
    }

    private void addInputOutputInfo(List<String> currenttip, IWailaConfigHandler config, int input, int output) {
        String inputStr = LangUtil.translateG("hud.ic2.msg.input");
        String outputStr = LangUtil.translateG("hud.ic2.msg.output");

        if (config.getConfig("ic2.inouteu")) {
            if (input > 0) {
                currenttip.add(String.format(INPUT_OUTPUT_FORMAT, inputStr, input));
            }
            if (output > 0) {
                currenttip.add(String.format(INPUT_OUTPUT_FORMAT, outputStr, output));
            }
        }
    }

    private void addTierInfo(List<String> currenttip, IWailaConfigHandler config, int tier) {
        String tierStr = LangUtil.translateG("hud.ic2.msg.tier");
        if (config.getConfig("ic2.tier") && tier > 0) {
            currenttip.add(String.format(TIER_FORMAT, tierStr, tier));
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.TileEntityGeneratorBase.isInstance(te)) {

                tag.setDouble("stored", IC2Module.generatorStored.getDouble(te));
                tag.setLong("capacity", IC2Module.generatorCapacity.getLong(te));
                tag.setInteger("input", 0);
                tag.setInteger("output", IC2Module.generatorOutput.getInt(te));
                tag.setInteger("tier", IC2Module.generatorTier.getInt(te));
            } else if (IC2Module.TileEntityElectricBlock.isInstance(te)) {
                tag.setDouble("stored", IC2Module.eBlockStored.getDouble(te));
                tag.setLong("capacity", IC2Module.eBlockCapacity.getLong(te));
                tag.setInteger("input", 0);
                tag.setInteger("output", IC2Module.eBlockOutput.getInt(te));
                tag.setInteger("tier", IC2Module.eBlockTier.getInt(te));
            } else if (IC2Module.TileEntityElecMachine.isInstance(te)) {
                tag.setDouble("stored", 0);
                tag.setLong("capacity", 0);
                tag.setInteger("input", IC2Module.eMachineInput.getInt(te));
                tag.setInteger("output", 0);
                tag.setInteger("tier", IC2Module.eMachineTier.getInt(te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get generator data", e);
        }
        return tag;
    }
}
