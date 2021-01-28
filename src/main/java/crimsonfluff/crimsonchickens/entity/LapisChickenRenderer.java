package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LapisChickenRenderer extends MobRenderer<LapisChickenEntity, ChickenModel<LapisChickenEntity>> {
    private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(CrimsonChickens.MOD_ID, "textures/entity/lapis_chicken.png");

    public LapisChickenRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ChickenModel<>(), 0.3F);
    }

    @Override
    public ResourceLocation getEntityTexture(LapisChickenEntity entity) {
        return CHICKEN_TEXTURE;
    }
}
