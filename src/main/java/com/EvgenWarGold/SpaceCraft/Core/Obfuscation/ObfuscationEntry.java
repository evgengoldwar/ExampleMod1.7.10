package com.EvgenWarGold.SpaceCraft.Core.Obfuscation;

public class ObfuscationEntry {
    public String name;
    public String obfuscatedName;

    public ObfuscationEntry(String name, String obfuscatedName) {
        this.name = name;
        this.obfuscatedName = obfuscatedName;
    }

    public ObfuscationEntry(String commonName) {
        this(commonName, commonName);
    }
}
