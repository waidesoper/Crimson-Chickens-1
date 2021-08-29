package crimsonfluff.crimsonchickens.blocks;

import com.google.gson.JsonParseException;
import crimsonfluff.crimsonchickens.CrimsonChickens;
import crimsonfluff.crimsonchickens.MyItemStackHandler;
import crimsonfluff.crimsonchickens.init.initTiles;
import crimsonfluff.crimsonchickens.json.ResourceChickenData;
import crimsonfluff.crimsonchickens.registry.ChickenRegistry;
import mcp.mobius.waila.api.IDataAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NestTileEntity extends TileEntity implements ITickableTileEntity {
    public final MyItemStackHandler storedItems = new MyItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) {
                Item item = stack.getItem();
                return item == Items.WHEAT_SEEDS || item == Items.BEETROOT_SEEDS || item == Items.MELON_SEEDS || item == Items.PUMPKIN_SEEDS;
            }

            return false;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0) return ItemStack.EMPTY;

            return super.extractItem(slot, amount, simulate);
        }
    };

    private LazyOptional<IItemHandler> outputItemHandlerCached = null;

    public ResourceChickenData chickenData = null;
    public ResourceLocation chickenTexture = null;          // cached for efficient render of chickenEntity
    public CompoundNBT entityCaptured = null;               // needed to restore chicken to animal net
    public String entityDescription = "";                   // needed to restore chicken to animal net
    public ITextComponent entityCustomName = null;
    public int eggLayTime;
    public int chickenAge;
    public int chickenGrowth;
    public int chickenGain;
    public int chickenStrength;

    public NestTileEntity() {
        super(initTiles.NEST_BLOCK_TILE.get());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (! this.isRemoved() && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> storedItems).cast();

        return super.getCapability(cap, side);
    }

    @Override
    public CompoundNBT getUpdateTag() {
//        this.chickenCompound = this.getTileData().getCompound("entityCaptured");
//        this.chickenData = ChickenRegistry.getRegistry().getChickenDataFromParent(chickenCompound.getString("id"));

        CompoundNBT compound = super.getUpdateTag();
        //save(nbt);

        if (this.entityCaptured != null)
            compound.put("entityCaptured", this.entityCaptured);

        if (! this.entityDescription.isEmpty())
            compound.putString("entityDescription", this.entityDescription);

        //CrimsonChickens.LOGGER.info("getUpdateTagNBT: " + compound);

        sendUpdates();

        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);

        entityRemove(false);

        if (compound.contains("Inventory"))
            storedItems.deserializeNBT(compound.getCompound("Inventory"));

        if (compound.contains("entityCaptured"))
            entitySet(compound.getCompound("entityCaptured"), compound.getString("entityDescription"), false);

        //CrimsonChickens.LOGGER.info("LoadNBT: " + compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
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

    @Override
    public void tick() {
        if (this.level.isClientSide) return;
        if (this.entityCaptured == null) return;
        if (this.chickenData == null) return;       // this should never happen!?

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
        TileEntity tileEntity = level != null ? level.getBlockEntity(getBlockPos().below()) : null;
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


    // TODO: not updating correctly
    public void addWailaEntityInfo(List<ITextComponent> tooltip, IDataAccessor accessor) {
        NestTileEntity te = (NestTileEntity) accessor.getTileEntity();
        if (te == null) return;

        if (te.entityCaptured != null) {
            tooltip.add(new StringTextComponent(te.entityDescription));

            if (te.entityCaptured.contains("CustomName", 8))
                tooltip.add(ITextComponent.Serializer.fromJson(te.entityCaptured.getString("CustomName")));

            if (te.entityCaptured.getBoolean("analyzed")) {
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.growth", te.chickenGrowth));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.gain", te.chickenGain));
                tooltip.add(new TranslationTextComponent("tip.crimsonchickens.strength", te.chickenStrength));
            }

//            int secs;
//            if (te.chickenAge < 0) {
//                secs = -te.chickenAge / 20;
//                tooltip.add(new StringTextComponent("Growing Time: " + String.format("%02d:%02d", secs / 60, secs %
//                60)));
//
//            } else {
//                if (te.storedItems.getStackInSlot(0).isEmpty())
//                    tooltip.add(new StringTextComponent("Requires seeds"));
//
//                  else {
//                    secs = te.chickenEggLayTime / 20;
//                    tooltip.add(new StringTextComponent("Next Drop: " + String.format("%02d:%02d", secs / 60, secs
//                    % 60)));
//                }
//            }

        }
//        else {
//            tooltip.add(new StringTextComponent("Empty"));
//        }
        //iProbeInfo.text(new StringTextComponent(te.getTileData().toString()));
    }


    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        load(state, tag);
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
        this.chickenTexture = null;
        this.entityCaptured = null;
        this.entityDescription = "";
        this.entityCustomName = null;

        if (sendUpdates) sendUpdates();
    }

    public void entitySet(CompoundNBT compound, String desc, boolean sendUpdates) {
        this.entityCaptured = compound.copy();
        this.entityDescription = desc;
        this.chickenData = ChickenRegistry.getRegistry().getChickenDataFromID(this.entityCaptured.getString("id"));
        this.chickenTexture = new ResourceLocation("crimsonchickens:textures/entity/chicken/" + this.chickenData.name + ".png");
        this.entityCustomName = ITextComponent.Serializer.fromJson(this.entityCaptured.getString("CustomName"));

        this.eggLayTime = compound.getInt("EggLayTime");
        this.chickenAge = compound.getInt("Age");
        this.chickenGrowth = compound.getInt("growth");
        this.chickenGain = compound.getInt("gain");
        this.chickenStrength = compound.getInt("strength");

        if (sendUpdates) sendUpdates();
    }

    public void sendUpdates() {
        // this will force the block to update the render (when you add/remove the chicken to/from the nest)
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0b11);
        this.level.updateNeighborsAt(worldPosition, this.getBlockState().getBlock());
        this.setChanged();
    }

    public void entitySetCustomName(CompoundNBT compound) {
        if (this.entityCaptured != null) {
            if (compound != null && compound.contains("Name", 8)) {
                try {
                    String name = compound.getString("Name");
                    ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(name);

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
