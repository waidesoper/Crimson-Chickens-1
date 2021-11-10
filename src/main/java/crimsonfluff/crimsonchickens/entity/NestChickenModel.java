package crimsonfluff.crimsonchickens.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;

public class NestChickenModel<T extends Entity> extends AnimalModel<T> {
    public final ModelPart head;
    public final ModelPart body;
    public final ModelPart wing0;
    public final ModelPart wing1;
    public final ModelPart beak;
    public final ModelPart comb;

    public NestChickenModel() {
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(- 2.0F, - 6.0F, - 2.0F, 4.0F, 6.0F, 3.0F, 0.0F);
        this.head.setPivot(0.0F, 15.0F, - 4.0F);
        this.beak = new ModelPart(this, 14, 0);
        this.beak.addCuboid(- 2.0F, - 4.0F, - 4.0F, 4.0F, 2.0F, 2.0F, 0.0F);
        this.beak.setPivot(0.0F, 15.0F, - 4.0F);
        this.comb = new ModelPart(this, 14, 4);
        this.comb.addCuboid(- 1.0F, - 2.0F, - 3.0F, 2.0F, 2.0F, 2.0F, 0.0F);
        this.comb.setPivot(0.0F, 15.0F, - 4.0F);
        this.body = new ModelPart(this, 0, 9);
        this.body.addCuboid(- 3.0F, - 4.0F, - 3.0F, 6.0F, 7.0F, 6.0F, 0.0F);  // slight body height change 8 to 7
        this.body.setPivot(0.0F, 16.0F, 0.0F);
        this.wing0 = new ModelPart(this, 24, 13);
        this.wing0.addCuboid(0.0F, 0.0F, - 3.0F, 1.0F, 4.0F, 6.0F);
        this.wing0.setPivot(- 4.0F, 13.0F, 0.0F);
        this.wing1 = new ModelPart(this, 24, 13);
        this.wing1.addCuboid(- 1.0F, 0.0F, - 3.0F, 1.0F, 4.0F, 6.0F);
        this.wing1.setPivot(4.0F, 13.0F, 0.0F);
    }

    @Override
    public void setAngles(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head, this.beak, this.comb);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.wing0, this.wing1);
    }
}
