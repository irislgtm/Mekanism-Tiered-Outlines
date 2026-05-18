package irislgtm.mto;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(MTOMod.MOD_ID)
public class MTOMod {
    public static final String MOD_ID = "mekanismtieredoutlines";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MTOMod() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MTOConfig.CLIENT_SPEC);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> MTOModClient::new);
        LOGGER.info("Loaded {}", MOD_ID);
    }
}
