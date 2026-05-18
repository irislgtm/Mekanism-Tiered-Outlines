package irislgtm.mto;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(MTOMod.MOD_ID)
public class MTOMod {
    public static final String MOD_ID = "mekanismtieredoutlines";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MTOMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, MTOConfig.CLIENT_SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (client, parent) -> new MTOConfigScreen(parent));
        LOGGER.info("Loaded {}", MOD_ID);
    }
}
