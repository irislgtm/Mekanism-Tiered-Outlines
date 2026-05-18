package irislgtm.mto;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mekanism.api.tier.BaseTier;
import mekanism.common.block.attribute.Attribute;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MTOModClient {
    private final Map<BlockState, List<LineData>> cachedWireFrames = new HashMap<>();

    public MTOModClient() {
        MinecraftForge.EVENT_BUS.addListener(this::onBlockHighlight);
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
        BaseTier tier = Attribute.getBaseTier(state.getBlock());
        if (tier == null) {
            return;
        }
        List<LineData> lines = cachedWireFrames.get(state);
        if (lines == null) {
            BakedModel bakedModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            lines = extractLines(bakedModel, state);
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
        renderVertexWireFrame(lines, event.getMultiBufferSource().getBuffer(RenderType.lines()), matrix.last().pose(), matrix.last().normal(), red, green, blue, MTOConfig.OUTLINE_OPACITY.get().floatValue());
        matrix.popPose();
    }

    private List<LineData> extractLines(BakedModel model, BlockState state) {
        List<LineData> lines = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        RandomSource random = RandomSource.create(0L);
        List<BakedQuad> quads = new ArrayList<>();
        for (Direction side : Direction.values()) {
            quads.addAll(model.getQuads(state, side, random, ModelData.EMPTY, null));
        }
        quads.addAll(model.getQuads(state, null, random, ModelData.EMPTY, null));
        for (BakedQuad quad : quads) {
            int[] data = quad.getVertices();
            int stride = data.length / 4;
            float[] x = new float[4];
            float[] y = new float[4];
            float[] z = new float[4];
            for (int i = 0; i < 4; i++) {
                int base = i * stride;
                x[i] = Float.intBitsToFloat(data[base]);
                y[i] = Float.intBitsToFloat(data[base + 1]);
                z[i] = Float.intBitsToFloat(data[base + 2]);
            }
            addEdge(lines, seen, x[0], y[0], z[0], x[1], y[1], z[1]);
            addEdge(lines, seen, x[1], y[1], z[1], x[2], y[2], z[2]);
            addEdge(lines, seen, x[2], y[2], z[2], x[3], y[3], z[3]);
            addEdge(lines, seen, x[3], y[3], z[3], x[0], y[0], z[0]);
        }
        return lines;
    }

    private void addEdge(List<LineData> lines, Set<String> seen, float x1, float y1, float z1, float x2, float y2, float z2) {
        int ax = Float.floatToIntBits(x1);
        int ay = Float.floatToIntBits(y1);
        int az = Float.floatToIntBits(z1);
        int bx = Float.floatToIntBits(x2);
        int by = Float.floatToIntBits(y2);
        int bz = Float.floatToIntBits(z2);
        String keyA = ax + "," + ay + "," + az;
        String keyB = bx + "," + by + "," + bz;
        String key = keyA.compareTo(keyB) <= 0 ? keyA + "|" + keyB : keyB + "|" + keyA;
        if (!seen.add(key)) {
            return;
        }
        float nx = x2 - x1;
        float ny = y2 - y1;
        float nz = z2 - z1;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        lines.add(new LineData(x1, y1, z1, x2, y2, z2, nx, ny, nz));
    }

    private void renderVertexWireFrame(List<LineData> lines, VertexConsumer buffer, Matrix4f pose, Matrix3f poseNormal, float red, float green, float blue, float alpha) {
        Vector4f pos = new Vector4f();
        Vector3f normal = new Vector3f();
        for (LineData line : lines) {
            poseNormal.transform(line.nX, line.nY, line.nZ, normal);

            pose.transform(line.x1, line.y1, line.z1, 1F, pos);
            buffer.vertex(pos.x, pos.y, pos.z)
                .color(red, green, blue, alpha)
                .normal(normal.x, normal.y, normal.z)
                .endVertex();

            pose.transform(line.x2, line.y2, line.z2, 1F, pos);
            buffer.vertex(pos.x, pos.y, pos.z)
                .color(red, green, blue, alpha)
                .normal(normal.x, normal.y, normal.z)
                .endVertex();
        }
    }

    private static class LineData {
        private final float x1;
        private final float y1;
        private final float z1;
        private final float x2;
        private final float y2;
        private final float z2;
        private final float nX;
        private final float nY;
        private final float nZ;

        private LineData(float x1, float y1, float z1, float x2, float y2, float z2, float nX, float nY, float nZ) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.nX = nX;
            this.nY = nY;
            this.nZ = nZ;
        }
    }
}
