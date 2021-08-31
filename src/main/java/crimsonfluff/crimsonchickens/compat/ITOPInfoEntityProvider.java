package crimsonfluff.crimsonchickens.compat;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ITOPInfoEntityProvider {
    void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, Entity entity, IProbeHitEntityData data);
}
