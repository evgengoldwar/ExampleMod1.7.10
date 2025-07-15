package com.EvgenWarGold.SpaceCraft.Api.Block;

import net.minecraft.block.Block;

public class BlockMetaPair {

    private final Block block;
    private final byte metadata;

    public BlockMetaPair(Block block, byte metadata) {
        this.block = block;
        this.metadata = metadata;
    }

    public Block getBlock() {
        return this.block;
    }

    public byte getMetadata() {
        return this.metadata;
    }
}
