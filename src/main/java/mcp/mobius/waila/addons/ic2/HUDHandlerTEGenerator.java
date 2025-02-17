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
        double storage = accessor.getNBTData().getDouble("storage");
        int production = accessor.getNBTData().getInteger("production");
        long maxStorage = accessor.getNBTData().getLong("maxStorage");

        String storedStr = LangUtil.translateG("hud.ic2.msg.stored");
        String outputStr = LangUtil.translateG("hud.ic2.msg.output");

        if (accessor.getTileEntity() == null) {
            return currenttip;
        }

        /* EU Storage */
        if (config.getConfig("ic2.storage"))
            if (maxStorage > 0) {
                ((ITaggedList<String, String>) currenttip)
                        .add(String.format(
                                "%s §f%d§r / §f%d§r EU",
                                storedStr,
                                Math.round(Math.min(storage, maxStorage)),
                                maxStorage
                        ), "IEnergyStorage");
            }

        if (config.getConfig("ic2.outputeu")) {
            currenttip.add(String.format("%s §f%d §r EU/t", outputStr, production));
        }

        return currenttip;
    }


    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        double storage = -1;
        int production = -1;
        long maxStorage = -1;

        try {
            if (IC2Module.generator.isInstance(te)) {
                storage = IC2Module.generatorStorage.getDouble(te);
                production = IC2Module.generatorProduction.getInt(te);
                maxStorage = IC2Module.generatorMaxStorage.getLong(te);
            }
        } catch (java.lang.Exception e) {
            throw new RuntimeException(e);
        }
        tag.setDouble("storage", storage);
        tag.setInteger("production", production);
        tag.setLong("maxStorage", maxStorage);

        return tag;
    }
}
