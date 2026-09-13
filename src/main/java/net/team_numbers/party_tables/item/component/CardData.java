package net.team_numbers.party_tables.item.component;

import com.google.common.collect.ImmutableList;

public record CardData(String type, ImmutableList<CardSlot> slots) {
}
