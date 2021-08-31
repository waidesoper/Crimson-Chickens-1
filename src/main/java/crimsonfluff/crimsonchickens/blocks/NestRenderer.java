package crimsonfluff.crimsonchickens.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NestRenderer implements BlockEntityRenderer<NestTileEntity> {
    public NestRenderer(BlockEntityRendererProvider.Context context) {}

    private static final NestChickenModel<ResourceChickenEntity> chickenModel = new NestChickenModel<>(NestChickenModel.createBodyLayer().bakeRoot());
    private static final Minecraft mc = Minecraft.getInstance();
    private final Font fontRenderer = mc.font;

    @Override
    public void render(NestTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (tileEntity.entityCaptured != null) {
            Direction direction = tileEntity.getBlockState().getValue(Nest.FACING);

            RenderType renderType = chickenModel.renderType(tileEntity.chickenTexture);
            VertexConsumer vertexBuilder = buffer.getBuffer(renderType);

            matrixStack.pushPose();
            matrixStack.translate(0.5f, 1.3f, 0.5f);
            matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot()));   // default facing is opposite so no need for +180
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));        // else they render upside down, because of course they do...

//            // render all but the legs/feet
//            // 1, 1, 1, 1 is the colorf(1,1,1,1)
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
            chickenModel.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            chickenModel.beak.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            chickenModel.comb.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
//            }

            chickenModel.body.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            chickenModel.leftWing.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);
            chickenModel.rightWing.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, 1);

            matrixStack.popPose();

            // TODO: Should be 64*64, cos everything is squared
            if (CrimsonChickens.CONFIGURATION.renderLabels.get()) {
                if (tileEntity.entityCustomName != null) {
                    if (mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ()) < 64)
                        renderLabel(matrixStack, buffer, combinedLight, tileEntity.entityCustomName, 0);
                }
            }
        }
    }

    private void renderLabel(PoseStack matrixStack, MultiBufferSource buffer, int lightLevel, Component text, int color) {
        matrixStack.pushPose();
        float scale = 0.02f;
        int opacity = (int) (.4f * 255.0F) << 24;
        float offset = (float) (- fontRenderer.width(text) / 2);
        Matrix4f matrix4f = matrixStack.last().pose();

        matrixStack.translate(0.5f, 1f, 0.5f);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());    // face the camera
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));         // flip vertical

        fontRenderer.drawInBatch(text, offset, 0, color, false, matrix4f, buffer, true, opacity, lightLevel);
        fontRenderer.drawInBatch(text, offset, 0, - 1, false, matrix4f, buffer, false, 0, lightLevel);
        matrixStack.popPose();
    }
}