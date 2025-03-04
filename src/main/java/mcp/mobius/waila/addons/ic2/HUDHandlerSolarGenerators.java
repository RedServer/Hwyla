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

public class HUDHandlerSolarGenerators implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerSolarGenerators();
    private static final String TEXT_LINE_FORMAT = "%s: §f%d§r ";
    private static final String ENERGY_INOUT_FORMAT = "%s: §f%d§r EU/t";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null) return currentTip;
        NBTTagCompound nbtData = accessor.getNBTData();


        if (config.getConfig("ic2.tier")) {
            currentTip.add(String.format(TEXT_LINE_FORMAT, LangUtil.translateG("hud.ic2.msg.tier"), nbtData.getInteger("tier")));
        }

        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.output"), nbtData.getInteger("output")));

        return currentTip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.getClass("TileEntitySolarPanel").isInstance(te)) {
                tag.setInteger("tier", (Integer) IC2Module.invokeMethod("TileEntitySolarPanel.getSourceTier", te));
                tag.setInteger("output", (Integer) IC2Module.invokeMethod("TileEntitySolarPanel.getOutput", te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get generator data", e);
        }
        return tag;
    }
}
