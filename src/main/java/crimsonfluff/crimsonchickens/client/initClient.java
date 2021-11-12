package crimsonfluff.crimsonchickens.client;

import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.entity.ResourceChickenRenderer;
import crimsonfluff.crimsonchickens.init.initBlocks;
import crimsonfluff.crimsonchickens.init.initRegistry;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

public class initClient implements ClientModInitializer {

    public static final Identifier DUCK_EGG_SPAWN_PACKET = new Identifier(CrimsonChickens.MOD_ID, "duck_egg_spawn_packet");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(initRegistry.DUCK_EGG, (dispatcher, context) -> new FlyingItemEntityRenderer(dispatcher, context.getItemRenderer()));
        BlockRenderLayerMap.INSTANCE.putBlock(initBlocks.NEST_BLOCK, RenderLayer.getCutout());
        BlockEntityRendererRegistry.INSTANCE.register(initTiles.NEST_BLOCK_TILE, NestRenderer::new);

        initRegistry.MOD_CHICKENS.forEach((s, resourceChicken) -> EntityRendererRegistry.INSTANCE.register(resourceChicken, (dispatcher, manager) -> {
            ResourceChickenData chickenData = ChickenRegistry.getRegistry().getChickenData(s);
            return new ResourceChickenRenderer(dispatcher, chickenData);
        }));

        receiveEntityPacket();
    }

    public void receiveEntityPacket() {
        ClientSidePacketRegistry.INSTANCE.register(DUCK_EGG_SPAWN_PACKET, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = DuckEggProjectileSpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = DuckEggProjectileSpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = DuckEggProjectileSpawnPacket.PacketBufUtil.readAngle(byteBuf);
            ClientWorld world = MinecraftClient.getInstance().world;

            ctx.getTaskQueue().execute(() -> {
                if (world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world !");

                Entity entity = et.create(world);
                if (entity == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\" !");

                entity.updateTrackedPosition(pos);
                entity.setPos(pos.x, pos.y, pos.z);
                entity.pitch = pitch;
                entity.yaw = yaw;
                entity.setEntityId(entityId);
                entity.setUuid(uuid);

                world.addEntity(entityId, entity);
            });
        });
    }
}
