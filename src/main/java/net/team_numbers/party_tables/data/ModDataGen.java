package net.team_numbers.party_tables.data;

import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class ModDataGen {

    private ModDataGen() {}

    public static void gatherData(GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), ModBlockStateProvider.create(helper));
        generator.addProvider(event.includeClient(), ModItemModelProvider.create(helper));
        generator.addProvider(event.includeClient(), ModTranslations.EN_US.asProvider("en_us"));
        generator.addProvider(event.includeClient(), ModTranslations.JA_JP.asProvider("ja_jp"));
    }

}
