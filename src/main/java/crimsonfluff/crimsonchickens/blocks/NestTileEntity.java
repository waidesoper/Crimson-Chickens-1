package crimsonfluff.crimsonchickens.blocks;

import com.google.gson.JsonParseException;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;

public class NestTileEntity extends BlockEntity implements ImplementedInventory, BlockEntityClientSerializable {
    private final DefaultedList<ItemStack> STORED_ITEMS = DefaultedList.ofSize(4, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() { return STORED_ITEMS; }

    public ResourceChickenData chickenData = null;
    public NbtCompound entityCaptured = null;               // needed to restore chicken to animal net
    public String entityDescription = "";                   // needed to restore chicken to animal net
    public Text entityCustomName = null;
    public int eggLayTime;
    public int chickenAge;
    public int chickenGrowth;
    public int chickenGain;
    public int chickenStrength;

    public NestTileEntity(BlockPos blockPos, BlockState blockState) {
        super(initTiles.NEST_BLOCK_TILE, blockPos, blockState);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);

        entityRemove(false);        // reset all fields

        Inventories.readNbt(compound, STORED_ITEMS);

        if (compound.contains("entityCaptured"))
            entitySet(compound.getCompound("entityCaptured"), compound.getString("entityDescription"), false);

//        CrimsonChickens.LOGGER.info("LoadNBT: " + compound);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compound) {
        Inventories.writeNbt(compound, STORED_ITEMS);

        if (this.entityCaptured != null) {
            this.entityCaptured.putInt("EggLayTime", this.eggLayTime);
            this.entityCaptured.putInt("Age", this.chickenAge);
            compound.put("entityCaptured", this.entityCaptured);
        }

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

//        CrimsonChickens.LOGGER.info("SaveNBT: " + compound);

        return super.writeNbt(compound);
    }

// Note: Now called from the Block(Nest) not the TileEntity
    public static void tick(World world, BlockPos pos, BlockState state, NestTileEntity blockEntity) {
        if (world.isClient) return;
        if (blockEntity.entityCaptured == null) return;
        if (blockEntity.chickenData.eggLayTime == 0) return;

        if (blockEntity.chickenAge < 0) {
            blockEntity.chickenAge++;
            //chickenAge = calcNewAge(chickenAge, compound.getInt("strength"));

            if (blockEntity.chickenAge >= 0) {
                world.playSound(null, pos,
                    world.random.nextInt(2) == 0
                        ? SoundEvents.ENTITY_CHICKEN_EGG
                        : blockEntity.chickenData.hasTrait == 1 ? initSounds.DUCK_AMBIENT : SoundEvents.ENTITY_CHICKEN_AMBIENT
                    , SoundCategory.PLAYERS, 1f, 1f);

                ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(initItems.NEST_BLOCK_ITEM)),
                    pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    20, 0.3, 0.2, 0.3, 0);

                blockEntity.entityCaptured.putInt("Age", blockEntity.chickenAge);       // Force Block Update
                blockEntity.sendUpdates();                                            // Force Block Update

                blockEntity.eggLayTime = CrimsonChickens.calcNewEggLayTime(world.random, blockEntity.chickenData, blockEntity.chickenGrowth);
            }
        }

        if (blockEntity.chickenAge >= 0) {
            if (! blockEntity.getStack(0).isEmpty()) {
                blockEntity.eggLayTime--;

                if (blockEntity.eggLayTime == 0) {
                    blockEntity.eggLayTime = CrimsonChickens.calcNewEggLayTime(world.random, blockEntity.chickenData, blockEntity.chickenGrowth);
                    blockEntity.getStack(0).decrement(1);

                    // note: dont allow mods to use this.storedItems as an inventory
                    // so isValidItem returns false, but we need to put items into this.storedItems

                    CrimsonChickens.calcDrops(blockEntity.chickenGain, blockEntity.chickenData, 0)
                        .forEach(blockEntity::addStack);

                    Inventory outputINV = HopperBlockEntity.getInventoryAt(world, pos.down());
                    if (outputINV != null) {
                        for (int a = 1; a < blockEntity.getItems().size(); a++) {       // slots 1 to 3
                            HopperBlockEntity.transfer(blockEntity, outputINV, blockEntity.removeStack(a) , null);
                        }
                    }

                    // if rendering items in Nest update the NestRenderer
//                    if (CrimsonChickens.CONFIGURATION.renderItems.get()) sendUpdates();
                }
            }
        }
    }

//    @Nullable
//    @Override
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
//        CompoundNBT nbt = pkt.getTag();
//        handleUpdateTag(getBlockState(), nbt);
//    }

    public void entityRemove(boolean sendUpdates) {
        this.chickenAge = 0;
        this.chickenGrowth = 0;
        this.chickenGain = 0;
        this.chickenStrength = 0;
        this.eggLayTime = 0;

        this.chickenData = null;
        this.entityCaptured = null;
        this.entityDescription = "";
        this.entityCustomName = null;

        if (sendUpdates) sendUpdates();
    }

    public void entitySet(NbtCompound compound, String desc, boolean sendUpdates) {
        this.entityCaptured = compound.copy();
        this.entityDescription = desc;
        this.chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(this.entityCaptured.getString("id"));
        this.entityCustomName = Text.Serializer.fromJson(this.entityCaptured.getString("CustomName"));

        this.eggLayTime = compound.getInt("EggLayTime");
        this.chickenAge = compound.getInt("Age");
        this.chickenGrowth = compound.getInt("growth");
        this.chickenGain = compound.getInt("gain");
        this.chickenStrength = compound.getInt("strength");

        if (sendUpdates) sendUpdates();
    }

    public void sendUpdates() {
        // this will force the block to update the render (when you add/remove the chicken to/from the nest)
        this.world.updateListeners(pos, getCachedState(), getCachedState(), 0b11);
        this.world.updateNeighbors(pos, getCachedState().getBlock());
        this.markDirty();
    }

    public void entitySetCustomName(NbtCompound compound) {
        if (this.entityCaptured != null) {
            if (compound != null && compound.contains("Name", 8)) {
                try {
                    String name = compound.getString("Name");
                    Text text = Text.Serializer.fromJson(name);

                    if (text != null) {
                        this.entityCaptured.putString("CustomName", name);
                        this.entityCustomName = text;
                        return;
                    }
                } catch (JsonParseException e) {
                    compound.remove("Name");
                }
            }

            this.entityCaptured.remove("CustomName");
            this.entityCustomName = null;
        }
    }

    @Override
    public void fromClientTag(NbtCompound compound) {
        entityRemove(false);        // reset all fields

        Inventories.readNbt(compound, STORED_ITEMS);

        if (compound.contains("entityCaptured"))
            entitySet(compound.getCompound("entityCaptured"), compound.getString("entityDescription"), false);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compound) {
        if (this.entityCaptured != null)
            compound.put("entityCaptured", this.entityCaptured);

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

//        if (CrimsonChickens.CONFIGURATION.renderItems.get())
//            Inventories.writeNbt(compound, STORED_ITEMS);        // TODO: this changed `Inventory` to `Items` (needed for render)

        //CrimsonChickens.LOGGER.info("getUpdateTagNBT: " + compound);

        return compound;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 0) {
            Item item = stack.getItem();
            return item == Items.WHEAT_SEEDS || item == Items.BEETROOT_SEEDS || item == Items.MELON_SEEDS || item == Items.PUMPKIN_SEEDS;
        }

        return false;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot == 0) return ItemStack.EMPTY;

        return ImplementedInventory.super.removeStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int count) {
        if (slot == 0) return ItemStack.EMPTY;

        return ImplementedInventory.super.removeStack(slot, count);
    }
}
