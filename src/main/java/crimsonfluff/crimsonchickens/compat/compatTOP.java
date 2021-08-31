package crimsonfluff.crimsonchickens.compat;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.ResourceChickenEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.InterModComms;

import java.util.function.Function;

public class compatTOP {
    private compatTOP() {}

    public static void register() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {
        @Override
        public Void apply(ITheOneProbe iTheOneProbe) {
            iTheOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
                @Override
                public String getID() {
                    return CrimsonChickens.MOD_ID + ":default";
                }

                @Override
                public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
                    if (entity instanceof ResourceChickenEntity) {
                        ITOPInfoEntityProvider provider = (ITOPInfoEntityProvider) entity;
                        provider.addProbeEntityInfo(probeMode, iProbeInfo, playerEntity, world, entity, iProbeHitEntityData);
                    }
                }
            });

            iTheOneProbe.registerProvider(new IProbeInfoProvider() {
                @Override
                public ResourceLocation getID() {
                    return new ResourceLocation(CrimsonChickens.MOD_ID + ":default");
                }

                @Override
                public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, BlockState blockState, IProbeHitData iProbeHitData) {
//                    if (blockState.getBlock() == initBlocks.NEST_BLOCK.get()) {
                    if (blockState.getBlock() instanceof ITOPInfoProvider) {
                        ITOPInfoProvider provider = (ITOPInfoProvider) blockState.getBlock();
                        provider.addProbeInfo(probeMode, iProbeInfo, playerEntity, world, blockState, iProbeHitData);
                    }
                }
            });

            return null;
        }
    }
}
