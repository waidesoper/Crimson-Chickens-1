package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ResourceChickenRenderer extends MobRenderer<ResourceChickenEntity, ChickenModel<ResourceChickenEntity>> {
    private final ResourceLocation CHICKEN_TEXTURE;

    public ResourceChickenRenderer(EntityRendererProvider.Context context, ResourceChickenData chickenData) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);

        CHICKEN_TEXTURE = new ResourceLocation(CrimsonChickens.MOD_ID + ":textures/entity/" + chickenData.name + ".png");
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceChickenEntity entityIn) {
        return CHICKEN_TEXTURE;
    }

    @Override
    protected float getBob(ResourceChickenEntity entityIn, float partialTicks) {
        float f = Mth.lerp(partialTicks, entityIn.oFlap, entityIn.flap);
        float f1 = Mth.lerp(partialTicks, entityIn.oFlapSpeed, entityIn.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}
