package crimsonfluff.crimsonchickens.blocks;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.NestChickenModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.IntStream;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NestRenderer extends TileEntityRenderer<NestTileEntity> {
    public NestRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    private static final NestChickenModel<ChickenEntity> chickenModel = new NestChickenModel<>();
    private static final Minecraft mc = Minecraft.getInstance();
    private final FontRenderer fontRenderer = mc.font;

    @Override
    public void render(NestTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        if (tileEntity.entityCaptured != null) {
            Direction direction = tileEntity.getBlockState().getValue(Nest.FACING);

            RenderType renderType = chickenModel.renderType(tileEntity.chickenTexture);
            IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);

            chickenModel.young = tileEntity.chickenAge < 0;

            matrixStack.pushPose();
            //matrixStack.translate(0.5f, 1.3f, 0.5f);
            matrixStack.translate(0.5f, chickenModel.young ? 1.4f : 1.32f, 0.5f);
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));   // default facing is opposite so no need for +180
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));      // else they render upside down, because of course they do...

            chickenModel.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            vertexBuilder.endVertex();

            // render all but the legs/feet
            // 1, 1, 1, 1 is the colorf(1,1,1,1)
//            if (false) {
//                matrixStack.pushPose();
//                matrixStack.scale(2f, 2f, 2f);
//                matrixStack.translate(0f, - 0.5f, 0f);
//                chickenModel.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//                chickenModel.beak.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//                chickenModel.comb.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//                matrixStack.popPose();
//            }
//            else {
//            chickenModel.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//            chickenModel.beak.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//            chickenModel.comb.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
////            }
//            chickenModel.body.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//            chickenModel.wing0.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//            chickenModel.wing1.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            //mc.getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0, 0, matrixStack, buffer, combinedLight);       // draw the entire chicken model

            matrixStack.popPose();

            if (CrimsonChickens.CONFIGURATION.renderLabels.get()) {
                if (tileEntity.entityCustomName != null) {
                    if (this.renderer.camera.getPosition().distanceToSqr(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ()) < 64)
                        renderLabel(matrixStack, buffer, combinedLight, tileEntity.entityCustomName, 0);
                }
            }

            // render storeItems if any in the nest
            if (CrimsonChickens.CONFIGURATION.renderItems.get()) {
                if (tileEntity.storedItems.contents().size() != 0) {
                    IntStream.range(0, tileEntity.storedItems.contents().size()).filter(a -> ! tileEntity.storedItems.getStackInSlot(a).isEmpty()).forEach(a -> renderItem(tileEntity.storedItems.getStackInSlot(a), getTranslation(a), Vector3f.YP.rotationDegrees(180.0F + direction.toYRot()), matrixStack, buffer, combinedOverlay, combinedLight));
                }
            }
        }
    }

    private void renderLabel(MatrixStack matrixStack, IRenderTypeBuffer buffer, int lightLevel, ITextComponent text, int color) {
        matrixStack.pushPose();
        float scale = 0.02f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (- fontRenderer.width(text) / 2);
        Matrix4f matrix4f = matrixStack.last().pose();

        matrixStack.translate(0.5f, 1f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());    // face the camera
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));       // flip vertical

        fontRenderer.drawInBatch(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.drawInBatch(text, offset, 0, - 1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.popPose();
    }

    private double[] getTranslation(int index) {
        double a = 0.15;

        switch (index) {
            case 0:
                return new double[] {0.75, a, 0.25};
            case 1:
                return new double[] {0.75, a, 0.75};
            case 2:
                return new double[] {0.25, a, 0.75};
            default:
                return new double[] {0.25, a, 0.25};
        }
    }

    private void renderItem(ItemStack stack, double[] translation, Quaternion rotation, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedOverlay, int lightLevel) {
        matrixStack.pushPose();
        matrixStack.translate(translation[0], translation[1], translation[2]);
        matrixStack.mulPose(rotation);
        matrixStack.scale(0.7f, 0.7f, 0.7f);
        IBakedModel ibakedmodel = mc.getItemRenderer().getModel(stack, null, null);
        mc.getItemRenderer().render(stack, ItemCameraTransforms.TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
        matrixStack.popPose();
    }
}
