package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BoneChickenRenderer extends MobRenderer<BoneChickenEntity, ChickenModel<BoneChickenEntity>> {
    private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(CrimsonChickens.MOD_ID, "textures/entity/bone_chicken.png");

    public BoneChickenRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ChickenModel<>(), 0.3F);
    }

    @Override
    public ResourceLocation getEntityTexture(BoneChickenEntity entity) {
        return CHICKEN_TEXTURE;
    }
}
