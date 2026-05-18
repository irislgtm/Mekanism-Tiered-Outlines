package irislgtm.mto;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = MTOMod.MOD_ID, dist = Dist.CLIENT)
public class MTOModClient {
    public MTOModClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MTOMod.LOGGER.info("Client setup for {}", MTOMod.MOD_ID);
    }
}
