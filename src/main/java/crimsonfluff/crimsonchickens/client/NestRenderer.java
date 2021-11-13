package crimsonfluff.crimsonchickens.client;

import crimsonfluff.crimsonchickens.blocks.Nest;
import crimsonfluff.crimsonchickens.blocks.NestTileEntity;
import crimsonfluff.crimsonchickens.entity.ChickenNestedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class NestRenderer implements BlockEntityRenderer<NestTileEntity> {
    private static final ChickenNestedModel<ChickenEntity> chickenModel = new ChickenNestedModel<>(ChickenNestedModel.getTexturedModelData().createModel());
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    //protected final BlockEntityRenderDispatcher dispatcher;

    public NestRenderer(BlockEntityRendererFactory.Context ctx) {
        //this.dispatcher = dispatcher;
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
            if (blockEntity.entityCustomName != null) {
                if (mc.gameRenderer.getCamera().getBlockPos().isWithinDistance(blockEntity.getPos(), 16))
                    renderLabel(matrices, vertexConsumers, light, blockEntity.entityCustomName);
            }
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

    // EntityRenderer.class
    private void renderLabel(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, Text text) {
//        TextRenderer textRenderer = dispatcher.getTextRenderer();
        TextRenderer textRenderer = mc.textRenderer;

        matrixStack.push();
        float scale = 0.02f;
        int opacity = (int) (mc.options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
        float offset = (float) (- textRenderer.getWidth(text) / 2);
        Matrix4f matrix4f = matrixStack.peek().getModel();

        matrixStack.translate(0.5f, 1f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.multiply(mc.getEntityRenderDispatcher().getRotation());         // face the camera
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180f));     // flip vertical

        textRenderer.draw(text, offset, 0, 553648127, false, matrix4f, vertexConsumers, true, opacity, light);
        textRenderer.draw(text, offset, 0, - 1, false, matrix4f, vertexConsumers, false, 0, light);
        matrixStack.pop();
    }
}
