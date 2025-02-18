package mcp.mobius.waila.addons.ic2;

import mcp.mobius.waila.Waila;
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

import javax.annotation.Nonnull;
import java.util.List;

public class HUDHandlerTEGenerator implements IWailaDataProvider {

    static final IWailaDataProvider INSTANCE = new HUDHandlerTEGenerator();

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null)
            return currenttip;

        double stored = accessor.getNBTData().getDouble("stored");
        int input = accessor.getNBTData().getInteger("input");
        int output = accessor.getNBTData().getInteger("output");
        long capacity = accessor.getNBTData().getLong("capacity");
        int tier = accessor.getNBTData().getInteger("tier");

        String storedStr = LangUtil.translateG("hud.ic2.msg.stored");
        String inputStr = LangUtil.translateG("hud.ic2.msg.input");
        String outputStr = LangUtil.translateG("hud.ic2.msg.output");
        String tierStr = LangUtil.translateG("hud.ic2.msg.tier");
        String energyLine = "";

        /* EU Storage*/
        if (capacity > 0) {
            if (config.getConfig("ic2.storage"))
                energyLine += String.format(
                        "%s: §f%d§r / §f%d§r EU",
                        storedStr,
                        Math.round(Math.min(stored, capacity)),
                        capacity
                );

            if (config.getConfig("ic2.percentage"))
                energyLine += String.format(" (§f%d%%§r)", Math.round((stored / capacity) * 100));

            ((ITaggedList<String, String>) currenttip).add(energyLine, "IEnergyStorage");
        }

        /* Input/Output EU */
        if (config.getConfig("ic2.inouteu")) {
            if (input > 0)
                currenttip.add(String.format("%s: §f%d §r EU/t", inputStr, input));
            if (output > 0)
                currenttip.add(String.format("%s: §f%d §r EU/t", outputStr, output));
        }

        /* Tier */
        if (config.getConfig("ic2.tier") && tier > 0)
            currenttip.add(String.format("%s: §f%d §r", tierStr, tier));


        return currenttip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        double stored = 0;
        long capacity = 0;
        int input = 0;
        int output = 0;
        int tier = 0;

        try {
            if (IC2Module.generator.isInstance(te)) {
                stored = IC2Module.generatorStored.getDouble(te);
                capacity = IC2Module.generatorCapacity.getLong(te);
                output = IC2Module.generatorOutput.getInt(te);
                tier = IC2Module.generatorTier.getInt(te);

            } else if (IC2Module.eBlock.isInstance(te)) {
                stored = IC2Module.eBlockStored.getDouble(te);
                capacity = IC2Module.eBlockCapacity.getLong(te);
                output = IC2Module.eBlockOutput.getInt(te);
                tier = IC2Module.eBlockTier.getInt(te);

            } else if (IC2Module.eMachine.isInstance(te)) {
                input = IC2Module.eMachineInput.getInt(te);
                tier = IC2Module.eMachineTier.getInt(te);
            }
            
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }

        tag.setDouble("stored", stored);
        tag.setLong("capacity", capacity);
        tag.setInteger("input", input);
        tag.setInteger("output", output);
        tag.setInteger("tier", tier);

        return tag;
    }
}
