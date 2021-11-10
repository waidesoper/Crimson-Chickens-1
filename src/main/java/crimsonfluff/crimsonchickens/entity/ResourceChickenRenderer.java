package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ResourceChickenRenderer extends MobEntityRenderer<ResourceChickenEntity, ChickenEntityModel<ResourceChickenEntity>> {
    private final Identifier CHICKEN_TEXTURE;

    public ResourceChickenRenderer(EntityRenderDispatcher renderManagerIn, ResourceChickenData chickenData) {
        super(renderManagerIn, new ChickenEntityModel<>(), 0.3F);

//        CHICKEN_TEXTURE = new ResourceLocation("crimsonchickens:textures/entity/" + chickenData.name + ".png");
        CHICKEN_TEXTURE = chickenData.chickenTexture;
    }

    @Override
    public Identifier getTexture(ResourceChickenEntity entityIn) {
        return CHICKEN_TEXTURE;
    }

    @Override
    protected float getAnimationProgress(ResourceChickenEntity entityIn, float partialTicks) {
        float f = MathHelper.lerp(partialTicks, entityIn.prevFlapProgress, entityIn.flapProgress);
        float f1 = MathHelper.lerp(partialTicks, entityIn.prevMaxWingDeviation, entityIn.maxWingDeviation);
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
