package irislgtm.mto;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MTOMod.MOD_ID)
public class MTOMod {
    public static final String MOD_ID = "mekanismtieredoutlines";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MTOMod(IEventBus modEventBus) {
        LOGGER.info("Loaded {}", MOD_ID);
    }
}
