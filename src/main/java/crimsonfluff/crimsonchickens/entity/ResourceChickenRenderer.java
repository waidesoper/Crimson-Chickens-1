package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class ResourceChickenRenderer extends MobRenderer<ResourceChickenEntity, ChickenModel<ResourceChickenEntity>> {
    private ResourceLocation CHICKEN_TEXTURE;

    public ResourceChickenRenderer(EntityRendererManager renderManagerIn, ResourceChickenData chickenData) {
        super(renderManagerIn, new ChickenModel<>(), 0.3F);

        CHICKEN_TEXTURE = ResourceLocation.tryParse(CrimsonChickens.MOD_ID + ":textures/entity/" + chickenData.name + "_chicken.png");
    }

    @Override
    public ResourceLocation getTextureLocation(ResourceChickenEntity entityIn) {
        return CHICKEN_TEXTURE;
    }

    @Override
    protected float getBob(ResourceChickenEntity entityIn, float p_77044_2_) {
        float f = MathHelper.lerp(p_77044_2_, entityIn.oFlap, entityIn.flap);
        float f1 = MathHelper.lerp(p_77044_2_, entityIn.oFlapSpeed, entityIn.flapSpeed);
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
