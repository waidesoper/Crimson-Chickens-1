package crimsonfluff.crimsonchickens.client;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ChickenNestedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class NestRenderer extends BlockEntityRenderer<NestTileEntity> {
    private static final ChickenNestedModel<ChickenEntity> chickenModel = new ChickenNestedModel<>();
    private static final MinecraftClient mc = MinecraftClient.getInstance();
//    private final FontRenderer fontRenderer = mc.font;

    public NestRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(NestTileEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.entityCaptured != null) {
            Direction direction = blockEntity.getCachedState().get(Nest.FACING);

            RenderLayer renderType = chickenModel.getLayer(blockEntity.chickenData.chickenTexture);
            VertexConsumer vertexBuilder = vertexConsumers.getBuffer(renderType);

            chickenModel.child = blockEntity.chickenAge < 0;

            matrices.push();
            matrices.translate(0.5f, chickenModel.child ? 1.4f : 1.32f, 0.5f);
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(direction.asRotation()));      // default facing is opposite so no need for +180
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));                   // else they render upside down, because of course they do...
            chickenModel.render(matrices, vertexBuilder, light, overlay, 1, 1, 1, 1);
            vertexBuilder.next();

            matrices.pop();

//        if (CrimsonChickens.CONFIGURATION.renderLabels.get()) {
//            if (tileEntity.entityCustomName != null) {
//                if (this.renderer.camera.getPosition().distanceToSqr(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ()) < 64)
//                    renderLabel(matrixStack, buffer, combinedLight, tileEntity.entityCustomName, 0);
//            }
        }

//        // render storedItems if any in the nest
//        if (CrimsonChickens.CONFIGURATION.renderItems.get()) {
//            if (tileEntity.storedItems.contents().size() != 0) {
//                IntStream.range(0, tileEntity.storedItems.contents().size())
//                    .filter(a -> ! tileEntity.storedItems.getStackInSlot(a).isEmpty())
//                    .forEach(a -> renderItem(tileEntity.storedItems.getStackInSlot(a), getTranslation(a, direction), Vector3f.YP.rotationDegrees(180.0F + direction.toYRot()), matrixStack, buffer, combinedOverlay, combinedLight));
//            }
//        }
    }
}
