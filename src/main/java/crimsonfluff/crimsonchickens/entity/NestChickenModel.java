package crimsonfluff.crimsonchickens.entity;

import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class NestChickenModel<T extends LivingEntity> extends ChickenModel<T> {
    public final ModelRenderer head;
    public final ModelRenderer body;
    public final ModelRenderer wing0;
    public final ModelRenderer wing1;
    public final ModelRenderer beak;
    public final ModelRenderer comb;

    public NestChickenModel() {
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, 0.0F);
        this.head.setPos(0.0F, 15.0F, -4.0F);
        this.beak = new ModelRenderer(this, 14, 0);
        this.beak.addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, 0.0F);
        this.beak.setPos(0.0F, 15.0F, -4.0F);
        this.comb = new ModelRenderer(this, 14, 4);
        this.comb.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, 0.0F);
        this.comb.setPos(0.0F, 15.0F, -4.0F);
        this.body = new ModelRenderer(this, 0, 9);
        this.body.addBox(-3.0F, -4.0F, -3.0F, 6.0F, 7.0F, 6.0F, 0.0F);  // slight body height change 8 to 7
        this.body.setPos(0.0F, 16.0F, 0.0F);
        this.wing0 = new ModelRenderer(this, 24, 13);
        this.wing0.addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
        this.wing0.setPos(-4.0F, 13.0F, 0.0F);
        this.wing1 = new ModelRenderer(this, 24, 13);
        this.wing1.addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F);
        this.wing1.setPos(4.0F, 13.0F, 0.0F);
    }
}
