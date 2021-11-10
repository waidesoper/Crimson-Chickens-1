package crimsonfluff.crimsonchickens.blocks;

import com.google.gson.JsonParseException;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.init.initItems;
import crimsonfluff.crimsonchickens.init.initSounds;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Direction;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

public class NestTileEntity extends BlockEntity implements Tickable, ImplementedInventory {
    private LazyOptional<IItemHandler> outputItemHandlerCached = null;

    public final DefaultedList<ItemStack> STORED_ITEMS = DefaultedList.ofSize(4, ItemStack.EMPTY);

    @Override
    public DefaultedList<ItemStack> getItems() { return STORED_ITEMS; }


    public ResourceChickenData chickenData = null;
    public Identifier chickenTexture = null;          // cached for efficient render of chickenEntity
    public NbtCompound entityCaptured = null;               // needed to restore chicken to animal net
    public String entityDescription = "";                   // needed to restore chicken to animal net
    public Text entityCustomName = null;
    public int eggLayTime;
    public int chickenAge;
    public int chickenGrowth;
    public int chickenGain;
    public int chickenStrength;

    public NestTileEntity() {
        super(initTiles.NEST_BLOCK_TILE);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (! this.isRemoved() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> STORED_ITEMS).cast();

        return super.getCapability(cap, side);
    }

    @Override       // store anything here that updates and is needed by the NestRenderer
    public NbtCompound getUpdateTag() {
        NbtCompound compound = super.getUpdateTag();

        if (this.entityCaptured != null)
            compound.put("entityCaptured", this.entityCaptured);

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

        if (CrimsonChickens.CONFIGURATION.renderItems.get())
            Inventories.writeNbt(compound, STORED_ITEMS);        // TODO: this changed `Inventory` to `Items` (needed for render)

        //CrimsonChickens.LOGGER.info("getUpdateTagNBT: " + compound);

        //sendUpdates();

        return compound;
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);

        entityRemove(false);

        Inventories.readNbt(compound, STORED_ITEMS);

        if (compound.contains("entityCaptured"))
            entitySet(compound.getCompound("entityCaptured"), compound.getString("entityDescription"), false);

        //CrimsonChickens.LOGGER.info("LoadNBT: " + compound);
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

        //CrimsonChickens.LOGGER.info("SaveNBT: " + compound);

        return super.writeNbt(compound);
    }

    @Override
    public void tick() {
        if (this.world.isClient) return;
        if (this.entityCaptured == null) return;
        if (this.chickenData.eggLayTime == 0) return;

        if (this.chickenAge < 0) {
            this.chickenAge++;
            //chickenAge = calcNewAge(chickenAge, compound.getInt("strength"));

            if (this.chickenAge >= 0) {
                this.world.playSound(null, pos,
                    this.world.random.nextInt(2) == 0
                        ? SoundEvents.ENTITY_CHICKEN_EGG
                        : this.chickenData.hasTrait == 1 ? initSounds.DUCK_AMBIENT : SoundEvents.ENTITY_CHICKEN_AMBIENT
                    , SoundCategory.PLAYERS, 1f, 1f);

                ((ServerWorld) this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(initItems.NEST_BLOCK_ITEM)),
                    pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    20, 0.3, 0.2, 0.3, 0);

                this.entityCaptured.putInt("Age", this.chickenAge);       // Force Block Update
                sendUpdates();                                                      // Force Block Update

                this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.world.random, this.chickenData, this.chickenGrowth);
            }
        }

        if (this.chickenAge >= 0) {
            if (! this.STORED_ITEMS.get(0).isEmpty()) {
                this.eggLayTime--;

                if (this.eggLayTime == 0) {
                    this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.world.random, this.chickenData, this.chickenGrowth);
                    this.STORED_ITEMS.get(0).decrement(1);

                    // dont allow mods to use this.storedItems as an inventory
                    // so isValidItem returns false
                    // but we need to put items into this.storedItems
                    CrimsonChickens.calcDrops(this.chickenGain, this.chickenData, 0)
                        .forEach(this.STORED_ITEMS::insertItemAnySlot);

                    // try to push items into inventory below, not seeds (slot(0))
                    if (getOutputItemHandlerCached().isPresent()) {
                        for (int slot = 1; slot < this.STORED_ITEMS.size(); slot++) {
                            int finalSlot = slot;
                            ItemStack result = getOutputItemHandlerCached()
                                .map(iItemHandler -> ItemHandlerHelper.insertItemStacked(iItemHandler,
                                    this.STORED_ITEMS.get(finalSlot), false))
                                .orElse(ItemStack.EMPTY);

                            this.STORED_ITEMS.set(slot, result);
                        }
                    }

                    // if rendering items in Nest update the NestRenderer
                    if (CrimsonChickens.CONFIGURATION.renderItems.get()) sendUpdates();
                }
            }
        }
    }

    public void updateCache() {
        BlockEntity tileEntity = world != null ? world.getBlockEntity(pos.down()) : null;
        if (tileEntity != null) {
            LazyOptional<IItemHandler> lazyOptional = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP);

            if (lazyOptional.isPresent()) {
                if (this.outputItemHandlerCached != lazyOptional) {
                    this.outputItemHandlerCached = lazyOptional;
                    outputItemHandlerCached.addListener(lazy -> updateCache());
                }
            }
            else outputItemHandlerCached = LazyOptional.empty();
        }
        else outputItemHandlerCached = LazyOptional.empty();
    }

    private LazyOptional<IItemHandler> getOutputItemHandlerCached() {
        if (outputItemHandlerCached == null) updateCache();
        return outputItemHandlerCached;
    }

    @Override
    public void handleUpdateTag(BlockState state, NbtCompound tag) {
        readNbt(tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        handleUpdateTag(getBlockState(), nbt);
    }

    public void entityRemove(boolean sendUpdates) {
        this.chickenAge = 0;
        this.chickenGrowth = 0;
        this.chickenGain = 0;
        this.chickenStrength = 0;
        this.eggLayTime = 0;

        this.chickenData = null;
//        this.chickenTexture = null;
        this.entityCaptured = null;
        this.entityDescription = "";
        this.entityCustomName = null;

        if (sendUpdates) sendUpdates();
    }

    public void entitySet(NbtCompound compound, String desc, boolean sendUpdates) {
        this.entityCaptured = compound.copy();
        this.entityDescription = desc;
        this.chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(this.entityCaptured.getString("id"));
//        this.chickenTexture = new ResourceLocation("crimsonchickens:textures/entity/" + this.chickenData.name + ".png");
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
        this.world.setBlockState(pos, getCachedState(), 0b11);
        this.world.updateNeighbor(pos, getCachedState().getBlock(), pos);
        this.markDirty();
    }

    public void entitySetCustomName(NbtCompound compound) {
        if (this.entityCaptured != null) {
            if (compound != null && compound.contains("Name", 8)) {
                try {
                    String name = compound.getString("Name");
                    Text itextcomponent = Text.Serializer.fromJson(name);

                    if (itextcomponent != null) {
                        this.entityCaptured.putString("CustomName", name);
                        this.entityCustomName = itextcomponent;
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
}
