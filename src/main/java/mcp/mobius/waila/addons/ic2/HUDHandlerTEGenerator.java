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
        int output = accessor.getNBTData().getInteger("output");
        long capacity = accessor.getNBTData().getLong("capacity");
        int tier = accessor.getNBTData().getInteger("tier");

        String storedStr = LangUtil.translateG("hud.ic2.msg.stored");
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

        /* Output EU */
        if (config.getConfig("ic2.outputeu") && output > 0)
            currenttip.add(String.format("%s: §f%d §r EU/t", outputStr, output));

        /* Tier */
        if (config.getConfig("ic2.tier") && tier > 0)
            currenttip.add(String.format("%s: §f%d §r", tierStr, tier));


        return currenttip;
    }


    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        double stored = -1;
        int output = -1;
        long capacity = -1;
        int tier = 0;

        try {
            if (IC2Module.generator.isInstance(te)) {
                stored = IC2Module.generatorStored.getDouble(te);
                output = IC2Module.generatorOutput.getInt(te);
                capacity = IC2Module.generatorCapacity.getLong(te);
                tier = IC2Module.generatorTier.getInt(te);
            } else if (IC2Module.eBlock.isInstance(te)) {
                stored = IC2Module.eBlockStored.getDouble(te);
                output = IC2Module.eBlockOutput.getInt(te);
                capacity = IC2Module.eBlockCapacity.getLong(te);
                tier = IC2Module.eBlockTier.getInt(te);
            }
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }
        tag.setDouble("stored", stored);
        tag.setInteger("output", output);
        tag.setLong("capacity", capacity);
        tag.setInteger("tier", tier);

        return tag;
    }
}
