package net.team_numbers.party_tables.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.item.ModItems;

import java.util.function.Supplier;

public class ModItemModelProvider extends ItemModelProvider {

    public static DataProvider.Factory<ModItemModelProvider> create(ExistingFileHelper existingFileHelper) {
        return packOutput -> new ModItemModelProvider(packOutput, existingFileHelper);
    }

    private ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PartyTables.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.CARD);
    }

    protected ItemModelBuilder parentOf(Supplier<? extends Item> holder, ModelFile parent) {
        return getBuilder(BuiltInRegistries.ITEM.getKey(holder.get()).toString()).parent(parent);
    }

    protected ItemModelBuilder basicItem(Supplier<? extends Item> holder) {
        return basicItem(holder.get());
    }

    protected ModelFile uncheck(String path) {
        return new ModelFile.UncheckedModelFile(path);
    }

    protected ModelFile existing(ResourceLocation location) {
        return new ModelFile.ExistingModelFile(location, this.existingFileHelper);
    }

    protected ModelFile existingMod(String path) {
        return existing(modLoc(path));
    }
}
