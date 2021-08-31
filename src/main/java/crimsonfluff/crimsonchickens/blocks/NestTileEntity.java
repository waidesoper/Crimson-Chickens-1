package crimsonfluff.crimsonchickens.blocks;

import com.google.gson.JsonParseException;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.MyItemStackHandler;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NestTileEntity extends BlockEntity implements EntityBlock {
    public MyItemStackHandler storedItems = new MyItemStackHandler(5);

    private LazyOptional<IItemHandler> outputItemHandlerCached = null;

    public ResourceChickenData chickenData = null;
    public ResourceLocation chickenTexture = null;          // cached for efficient render of chickenEntity
    public CompoundTag entityCaptured = null;               // needed to restore chicken to animal net
    public String entityDescription = "";                   // needed to restore chicken to animal net
    public Component entityCustomName = null;
    public int eggLayTime;
    public int chickenAge;
    public int chickenGrowth;
    public int chickenGain;
    public int chickenStrength;

    public NestTileEntity(BlockPos pos, BlockState state) {
        super(initTiles.NEST_BLOCK_TILE.get(), pos, state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new NestTileEntity(blockPos, blockState);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (! this.isRemoved() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> storedItems).cast();

        return super.getCapability(cap, side);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compound = super.getUpdateTag();

        if (this.entityCaptured != null)
            compound.put("entityCaptured", this.entityCaptured);

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

        //CrimsonChickens.LOGGER.info("getUpdateTagNBT: " + compound);

        sendUpdates();

        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        entityRemove(false);

        if (compound.contains("Inventory"))
            storedItems.deserializeNBT(compound.getCompound("Inventory"));

        if (compound.contains("entityCaptured"))
            entitySet(compound.getCompound("entityCaptured"), compound.getString("entityDescription"), false);

        //CrimsonChickens.LOGGER.info("LoadNBT: " + compound);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.put("Inventory", this.storedItems.serializeNBT());

        if (this.entityCaptured != null) {
            this.entityCaptured.putInt("EggLayTime", this.eggLayTime);
            this.entityCaptured.putInt("ChickenAge", this.chickenAge);
            compound.put("entityCaptured", this.entityCaptured);
        }

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

        //CrimsonChickens.LOGGER.info("SaveNBT: " + compound);

        return super.save(compound);
    }

//    @Override
    public void tick() {
//        if (this.level.isClientSide) return;          // we only create this Serverside, not sure if thats proper conduct, but it works
        if (this.entityCaptured == null) return;
        if (this.chickenData.eggLayTime == 0) return;

        if (this.chickenAge < 0) {
            this.chickenAge++;
            //chickenAge = calcNewAge(chickenAge, compound.getInt("strength"));

            if (this.chickenAge >= 0)
                this.eggLayTime = 1;   // should trigger code below to start egglay timer
        }

        if (this.chickenAge >= 0) {
            if (! this.storedItems.getStackInSlot(0).isEmpty()) {
                this.eggLayTime--;

                if (this.eggLayTime == 0) {
                    this.eggLayTime = CrimsonChickens.calcNewEggLayTime(this.level.random, this.chickenData, this.chickenGrowth);
                    this.storedItems.getStackInSlot(0).shrink(1);

                    // dont allow mods to use this.storedItems as an inventory
                    // so isValidItem returns false
                    // but we need to put items into this.storedItems
                    CrimsonChickens.calcDrops(this.chickenGain, this.chickenData, 0)
                        .forEach(this.storedItems::insertItemAnySlot);

                    // try to push items into inventory below
                    if (getOutputItemHandlerCached().isPresent()) {
                        for (int slot = 1; slot < this.storedItems.getSlots(); slot++) {
                            int finalSlot = slot;
                            ItemStack result = getOutputItemHandlerCached()
                                .map(iItemHandler -> ItemHandlerHelper.insertItemStacked(iItemHandler,
                                    this.storedItems.getStackInSlot(finalSlot), false))
                                .orElse(ItemStack.EMPTY);

                            this.storedItems.setStackInSlot(slot, result);
                        }
                    }
                }
            }
        }
    }

    public void updateCache() {
        BlockEntity tileEntity = level != null ? level.getBlockEntity(getBlockPos().below()) : null;
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

    @Override       // definitely needed for BlockRender Update
    public void handleUpdateTag(CompoundTag tag) { load(tag); }

    @Nullable
    @Override       // definitely needed for BlockRender Update
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(worldPosition, 1, getUpdateTag());
    }

    @Override       // definitely needed for BlockRender Update
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        handleUpdateTag(nbt);
    }

    public void entityRemove(boolean sendUpdates) {
        this.chickenAge = 0;
        this.chickenGrowth = 0;
        this.chickenGain = 0;
        this.chickenStrength = 0;
        this.eggLayTime = 0;

        this.chickenData = null;
        this.chickenTexture = null;
        this.entityCaptured = null;
        this.entityDescription = "";
        this.entityCustomName = null;

        if (sendUpdates) sendUpdates();
    }

    public void entitySet(CompoundTag compound, String desc, boolean sendUpdates) {
        this.entityCaptured = compound.copy();
        this.entityDescription = desc;
        this.chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(this.entityCaptured.getString("id"));
        this.chickenTexture = new ResourceLocation("crimsonchickens:textures/entity/" + this.chickenData.name + ".png");
        this.entityCustomName = Component.Serializer.fromJson(this.entityCaptured.getString("CustomName"));

        this.eggLayTime = compound.getInt("EggLayTime");
        this.chickenAge = compound.getInt("Age");
        this.chickenGrowth = compound.getInt("growth");
        this.chickenGain = compound.getInt("gain");
        this.chickenStrength = compound.getInt("strength");

        if (sendUpdates) sendUpdates();
    }

    public void sendUpdates() {
        // this will force the block to update the render (when you add/remove the chicken to/from the nest)
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0b11);     //0b11
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        this.setChanged();
    }

    public void entitySetCustomName(CompoundTag compound) {
        if (this.entityCaptured != null) {
            if (compound != null && compound.contains("Name", 8)) {
                try {
                    String name = compound.getString("Name");
                    Component component = Component.Serializer.fromJson(name);

                    if (component != null) {
                        this.entityCaptured.putString("CustomName", name);
                        this.entityCustomName = component;
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
