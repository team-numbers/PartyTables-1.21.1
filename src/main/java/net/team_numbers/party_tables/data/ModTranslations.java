package net.team_numbers.party_tables.data;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.team_numbers.party_tables.PartyTables;
import net.team_numbers.party_tables.item.ModItems;

public interface ModTranslations {

    void accept(ModLanguageProvider provider);


    ModTranslations EN_US = p -> {
        p.add("itemGroup." + PartyTables.MOD_ID, "Cardist");

        p.addItem(ModItems.CARD, "Card");
    };

    ModTranslations JA_JP = p -> {
        p.add("itemGroup." + PartyTables.MOD_ID, "Cardist");

        p.addItem(ModItems.CARD, "カード");
    };


    default DataProvider.Factory<ModLanguageProvider> asProvider(String locale) {
        return packOutput ->  new ModLanguageProvider(packOutput, locale, this);
    }

    class ModLanguageProvider extends LanguageProvider {
        private final ModTranslations translations;

        public ModLanguageProvider(PackOutput output, String locale, ModTranslations translations) {
            super(output, PartyTables.MOD_ID, locale);
            this.translations = translations;
        }

        @Override
        protected void addTranslations() {
            translations.accept(this);
        }
    }
}
