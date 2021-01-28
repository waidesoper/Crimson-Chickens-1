package crimsonfluff.crimsonchickens.entity;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IronChickenRenderer extends MobRenderer<IronChickenEntity, ChickenModel<IronChickenEntity>> {
    private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(CrimsonChickens.MOD_ID, "textures/entity/iron_chicken.png");

    public IronChickenRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new ChickenModel<>(), 0.3F);
    }

    @Override
    public ResourceLocation getEntityTexture(IronChickenEntity entity) {
        return CHICKEN_TEXTURE;
    }
}
