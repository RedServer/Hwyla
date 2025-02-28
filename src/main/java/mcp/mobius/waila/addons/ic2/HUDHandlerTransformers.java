package mcp.mobius.waila.addons.ic2;

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

public class HUDHandlerTransformers implements IWailaDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    static final IWailaDataProvider INSTANCE = new HUDHandlerTransformers();

    private static final String ENERGY_INOUT_FORMAT = "%s: §f%d §r EU/t";
    private static final String TIER_INOUT_FORMAT = "%s: §f%d -> %d §r";
    private static final String TEXT_LINE_FORMAT = "%s: §f%s §r";

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() == null) return currentTip;

        NBTTagCompound nbtData = accessor.getNBTData();

        int tierIn = nbtData.getInteger("tierIn");
        int tierOut = nbtData.getInteger("tierOut");

        if (nbtData.hasKey("isActive"))
            currentTip.add(String.format(TEXT_LINE_FORMAT, LangUtil.translateG("hud.ic2.msg.mode"), nbtData.getBoolean("isActive") ? LangUtil.translateG("hud.ic2.msg.modestepup") : LangUtil.translateG("hud.ic2.msg.modestepdown")));

        if (tierIn > 0 || tierOut > 0) {
            currentTip.add(String.format(TIER_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.tier"), tierIn, tierOut));
        }

        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.input"), nbtData.getInteger("input")));
        currentTip.add(String.format(ENERGY_INOUT_FORMAT, LangUtil.translateG("hud.ic2.msg.output"), nbtData.getInteger("output")));


        return currentTip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        try {
            if (IC2Module.getClass("TileEntityTransformer").isInstance(te)) {
                boolean isActive = (Boolean) IC2Module.invokeMethod("TileEntityTransformer.getActive", te);
                tag.setBoolean("isActive", isActive);

                tag.setInteger("input", isActive
                        ? (Integer) IC2Module.getField("TileEntityTransformer.lowOutput", te)
                        : (Integer) IC2Module.getField("TileEntityTransformer.highOutput", te));

                tag.setInteger("output", isActive
                        ? (Integer) IC2Module.getField("TileEntityTransformer.highOutput", te)
                        : (Integer) IC2Module.getField("TileEntityTransformer.lowOutput", te));

                tag.setInteger("tierIn", (Integer) IC2Module.invokeMethod("TileEntityTransformer.getSinkTier", te));
                tag.setInteger("tierOut", (Integer) IC2Module.invokeMethod("TileEntityTransformer.getSourceTier", te));
            }
        } catch (Exception e) {
            LOGGER.error("Failed to get transformer data", e);
        }
        return tag;
    }
}
