package com.EvgenWarGold.SpaceCraft.Core.Obfuscation;

public class MethodObfuscationEntry extends ObfuscationEntry {
    public String methodDesc;

    public MethodObfuscationEntry(String name, String obfuscatedName, String methodDesc) {
        super(name, obfuscatedName);
        this.methodDesc = methodDesc;
    }

    public MethodObfuscationEntry(String commonName, String methodDesc) {
        this(commonName, commonName, methodDesc);
    }
}
