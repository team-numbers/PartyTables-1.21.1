package net.team_numbers.party_tables.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.team_numbers.party_tables.PartyTables;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {

    public static DataProvider.Factory<ModBlockStateProvider> create(ExistingFileHelper exFileHelper) {
        return packOutput ->  new ModBlockStateProvider(packOutput, exFileHelper);
    }

    private ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, PartyTables.MOD_ID, exFileHelper);
    }

    private ResourceLocation texture(String name) {
        return modLoc("block/" + name);
    }

    private ResourceLocation key(Supplier<? extends Block> block) {
        return BuiltInRegistries.BLOCK.getKey(block.get());
    }

    private String name(Supplier<? extends Block> block) {
        return this.key(block).getPath();
    }

    private <T extends Block, U extends DeferredBlock<T>> void simpleBlockWithItem(
        U holder, BiFunction<String, U, ? extends ModelFile> modelFunc
    ) {
        simpleBlockWithItem(holder.get(), modelFunc.apply(holder.getId().getPath(), holder));
    }

    @Override
    protected void registerStatesAndModels() {
    }
}
