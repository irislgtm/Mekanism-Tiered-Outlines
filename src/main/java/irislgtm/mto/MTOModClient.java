package irislgtm.mto;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mekanism.api.tier.BaseTier;
import mekanism.client.render.lib.Outlines;
import mekanism.client.render.lib.Outlines.Line;
import mekanism.common.block.attribute.Attribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Mod(value = MTOMod.MOD_ID, dist = Dist.CLIENT)
public class MTOModClient {
    private final Map<BlockState, List<Line>> cachedWireFrames = new HashMap<>();

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
        if (state.isAir()) {
            return;
        }
        BaseTier tier = Attribute.getBaseTier(state.getBlockHolder());
        if (tier == null) {
            return;
        }
        List<Line> lines = cachedWireFrames.get(state);
        if (lines == null) {
            BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            lines = Outlines.extract(bakedModel, state, RandomSource.create(), ModelData.EMPTY, null);
            cachedWireFrames.put(state, lines);
        }
        if (lines.isEmpty()) {
            return;
        }
        int[] rgb = tier.getRgbCode();
        float red = rgb[0] / 255F;
        float green = rgb[1] / 255F;
        float blue = rgb[2] / 255F;
        Vec3 camera = event.getCamera().getPosition();

        PoseStack matrix = event.getPoseStack();
        matrix.pushPose();
        matrix.translate(pos.getX() - camera.x, pos.getY() - camera.y, pos.getZ() - camera.z);
        event.setCanceled(true);
        renderVertexWireFrame(lines, event.getMultiBufferSource().getBuffer(RenderType.lines()), matrix.last().pose(), matrix.last().normal(), red, green, blue, 0.4F);
        matrix.popPose();
    }

    private void renderVertexWireFrame(List<Line> lines, VertexConsumer buffer, Matrix4f pose, Matrix3f poseNormal, float red, float green, float blue, float alpha) {
        Vector4f pos = new Vector4f();
        Vector3f normal = new Vector3f();
        for (Line line : lines) {
            poseNormal.transform(line.nX(), line.nY(), line.nZ(), normal);

            pose.transform(line.x1(), line.y1(), line.z1(), 1F, pos);
            buffer.addVertex(pos.x, pos.y, pos.z)
                  .setColor(red, green, blue, alpha)
                  .setNormal(normal.x, normal.y, normal.z);

            pose.transform(line.x2(), line.y2(), line.z2(), 1F, pos);
            buffer.addVertex(pos.x, pos.y, pos.z)
                  .setColor(red, green, blue, alpha)
                  .setNormal(normal.x, normal.y, normal.z);
        }
    }
}
