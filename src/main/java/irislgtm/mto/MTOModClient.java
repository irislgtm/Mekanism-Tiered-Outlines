package irislgtm.mto;

import mekanism.api.tier.BaseTier;
import mekanism.common.block.attribute.Attribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = MTOMod.MOD_ID, dist = Dist.CLIENT)
public class MTOModClient {
    public MTOModClient(IEventBus modEventBus) {
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.addListener(this::onBlockHighlight);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MTOMod.LOGGER.info("Client setup for {}", MTOMod.MOD_ID);
    }

    private void onBlockHighlight(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        BlockHitResult target = event.getTarget();
        if (target.getType() != Type.BLOCK) {
            return;
        }
        Level level = player.level();
        BlockPos pos = target.getBlockPos();
        BlockState state = level.getBlockState(pos);
        BaseTier tier = Attribute.getBaseTier(state.getBlockHolder());
        if (tier == null) {
            return;
        }
        VoxelShape shape = state.getShape(level, pos, CollisionContext.of(player));
        if (shape.isEmpty()) {
            return;
        }
        int[] rgb = tier.getRgbCode();
        float red = rgb[0] / 255F;
        float green = rgb[1] / 255F;
        float blue = rgb[2] / 255F;
        Vec3 camera = event.getCamera().getPosition();
        event.setCanceled(true);
        LevelRenderer.renderShape(event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), shape,
              pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z, red, green, blue, 0.4F);
    }
}
