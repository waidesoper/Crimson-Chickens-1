package crimsonfluff.crimsonchickens.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ChickenRenderer extends MobRenderer<ChickenEntity, ChickenModel<ChickenEntity>> {
    //private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(CrimsonChickens.MOD_ID, "textures/entity/bone_chicken.png");
    private static ResourceLocation TEXTURE;

    public ChickenRenderer(EntityRendererManager renderManagerIn, ResourceLocation texture) {
        super(renderManagerIn, new ChickenModel<>(), 0.3F);
        this.TEXTURE = texture;
    }

    @Override
    public ResourceLocation getEntityTexture(ChickenEntity entity) {
        return TEXTURE;
    }
}
