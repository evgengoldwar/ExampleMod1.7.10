package com.EvgenWarGold.SpaceCraft.Api.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.EvgenWarGold.SpaceCraft.Api.Block.BlockMetaPair;
import com.EvgenWarGold.SpaceCraft.Api.Perlin.Generator.Gradient;
import com.EvgenWarGold.SpaceCraft.Util.XSTR;

public abstract class ChunkProviderSpaceCraters extends ChunkProviderGenerate {

    private Gradient noiseGen1;
    private Gradient noiseGen2;
    private Gradient noiseGen3;
    private Gradient noiseGen4;
    private Gradient noiseGen5;
    private Gradient noiseGen6;
    private Gradient noiseGen7;
    private double terrainHeightMult;
    private double smallHillHeightMult;
    private double mountainHeightMult;
    private double valleyHeightMult;
    private float craterProbInv;
    private int MID_HEIGHT;
    private List<MapGenBaseMeta> worldGenerators;

    public ChunkProviderSpaceCraters(World world, long seed, boolean mapFeaturesEnabled) {
        super(world, seed, mapFeaturesEnabled);
        this.biomesForGeneration = this.getBiomesForGeneration();
        this.terrainHeightMult = this.getHeightModifier();
        this.smallHillHeightMult = this.getSmallFeatureHeightModifier();
        this.mountainHeightMult = this.getMountainHeightModifier();
        this.valleyHeightMult = this.getValleyHeightModifier();
        this.craterProbInv = 1f / this.getCraterProbability();
        this.MID_HEIGHT = this.getSeaLevel();
        this.rand = new XSTR(seed);
        this.noiseGen1 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen2 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen3 = new Gradient(this.rand.nextLong(), 4, 0.25f);
        this.noiseGen4 = new Gradient(this.rand.nextLong(), 2, 0.25f);
        this.noiseGen5 = new Gradient(this.rand.nextLong(), 1, 0.25f);
        this.noiseGen6 = new Gradient(this.rand.nextLong(), 1, 0.25f);
        this.noiseGen7 = new Gradient(this.rand.nextLong(), 1, 0.25f);
    }

    public void generateTerrain(int chunkX, int chunkZ, Block[] idArray, byte[] metaArray) {
        this.noiseGen1.setFrequency(0.015f);
        this.noiseGen2.setFrequency(0.01f);
        this.noiseGen3.setFrequency(0.01f);
        this.noiseGen4.setFrequency(0.01f);
        this.noiseGen5.setFrequency(0.01f);
        this.noiseGen6.setFrequency(0.001f);
        this.noiseGen7.setFrequency(0.005f);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double baseHeight = this.noiseGen1.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * this.terrainHeightMult;
                double smallHillHeight = this.noiseGen2.getNoise(chunkX * 16 + x, chunkZ * 16 + z)
                    * this.smallHillHeightMult;
                double mountainHeight = Math.abs(this.noiseGen3.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                double valleyHeight = Math.abs(this.noiseGen4.getNoise(chunkX * 16 + x, chunkZ * 16 + z));
                double featureFilter = this.noiseGen5.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * 4.0;
                double largeFilter = this.noiseGen6.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * 8.0;
                double smallFilter = this.noiseGen7.getNoise(chunkX * 16 + x, chunkZ * 16 + z) * 8.0 - 0.5;
                mountainHeight = this.lerp(
                    smallHillHeight,
                    mountainHeight * this.mountainHeightMult,
                    this.fade(this.clamp(mountainHeight * 2.0, 0.0, 1.0)));
                valleyHeight = this.lerp(
                    smallHillHeight,
                    valleyHeight * this.valleyHeightMult - this.valleyHeightMult + 9.0,
                    this.fade(this.clamp((valleyHeight + 2.0) * 4.0, 0.0, 1.0)));
                double yDev = this.lerp(valleyHeight, mountainHeight, this.fade(largeFilter));
                yDev = this.lerp(smallHillHeight, yDev, smallFilter);
                yDev = this.lerp(baseHeight, yDev, featureFilter);
                SCBiomeGenBase biome = (SCBiomeGenBase) this.worldObj
                    .getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
                for (int y = 0; y < 256; ++y) {
                    if (y < this.MID_HEIGHT + yDev) {
                        if (this.enableBiomeGenBaseBlock()) {
                            idArray[this.getIndex(x, y, z)] = biome.stoneBlock;
                            metaArray[this.getIndex(x, y, z)] = biome.stoneMeta;
                        } else {
                            idArray[this.getIndex(x, y, z)] = this.getStoneBlock()
                                .getBlock();
                            metaArray[this.getIndex(x, y, z)] = this.getStoneBlock()
                                .getMetadata();
                        }
                    }
                }
            }
        }
    }

    protected double lerp(double d1, double d2, double t) {
        if (t < 0.0) {
            return d1;
        }
        if (t > 1.0) {
            return d2;
        }
        return d1 + (d2 - d1) * t;
    }

    protected double fade(double n) {
        return n * n * n * (n * (n * 6.0 - 15.0) + 10.0);
    }

    protected double clamp(double x, double min, double max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }

    @Override
    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] arrayOfIDs, byte[] arrayOfMeta,
        BiomeGenBase[] biomes) {
        this.noiseGen4.setFrequency(0.0625f);
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                SCBiomeGenBase biomegenbase = (SCBiomeGenBase) biomes[x + z * 16];
                int noise = (int) (this.noiseGen4.getNoise(chunkX * 16 + x, chunkZ * 16 + z) / 3.0 + 3.0
                    + this.rand.nextDouble() * 0.25);
                int i = -1;
                Block topBlock = this.enableBiomeGenBaseBlock() ? biomegenbase.topBlock
                    : this.getGrassBlock()
                        .getBlock();
                byte topMeta = this.enableBiomeGenBaseBlock() ? biomegenbase.topMeta
                    : this.getGrassBlock()
                        .getMetadata();
                Block fillerBlock = this.enableBiomeGenBaseBlock() ? biomegenbase.fillerBlock
                    : this.getDirtBlock()
                        .getBlock();
                byte fillerMeta = this.enableBiomeGenBaseBlock() ? biomegenbase.fillerMeta
                    : this.getDirtBlock()
                        .getMetadata();
                for (int y = 255; y >= 0; --y) {
                    int index = this.getIndex(x, y, z);
                    if (y <= 0 + this.rand.nextInt(5)) {
                        arrayOfIDs[index] = Blocks.bedrock;
                    } else {
                        Block var14 = arrayOfIDs[index];
                        if (Blocks.air == var14) {
                            i = -1;
                        } else if (var14 == this.getStoneBlock()
                            .getBlock()) {
                                arrayOfMeta[index] = this.getStoneBlock()
                                    .getMetadata();
                                if (i == -1) {
                                    if (noise <= 0) {
                                        topBlock = Blocks.air;
                                        topMeta = 0;
                                        if (this.enableBiomeGenBaseBlock()) {
                                            fillerBlock = biomegenbase.stoneBlock;
                                            fillerMeta = biomegenbase.stoneMeta;
                                        } else {
                                            fillerBlock = this.getStoneBlock()
                                                .getBlock();
                                            fillerMeta = this.getStoneBlock()
                                                .getMetadata();
                                        }
                                    } else if (y >= 36 && y <= 21) {
                                        if (this.enableBiomeGenBaseBlock()) {
                                            topBlock = biomegenbase.topBlock;
                                            topMeta = biomegenbase.topMeta;
                                            topBlock = biomegenbase.fillerBlock;
                                            topMeta = biomegenbase.fillerMeta;
                                        } else {
                                            topBlock = this.getGrassBlock()
                                                .getBlock();
                                            topMeta = this.getGrassBlock()
                                                .getMetadata();
                                            topBlock = this.getDirtBlock()
                                                .getBlock();
                                            topMeta = this.getDirtBlock()
                                                .getMetadata();
                                        }
                                    }
                                    i = noise;
                                    if (y >= 19) {
                                        arrayOfIDs[index] = topBlock;
                                        arrayOfMeta[index] = topMeta;
                                    } else {
                                        arrayOfIDs[index] = fillerBlock;
                                        arrayOfMeta[index] = fillerMeta;
                                    }
                                } else if (i > 0) {
                                    --i;
                                    arrayOfIDs[index] = fillerBlock;
                                    arrayOfMeta[index] = fillerMeta;
                                }
                            }
                    }
                }
            }
        }
    }

    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);
        Block[] ids = new Block[65536];
        byte[] meta = new byte[65536];
        this.generateTerrain(chunkX, chunkZ, ids, meta);
        this.createCraters(chunkX, chunkZ, ids, meta);
        this.replaceBlocksForBiome(
            chunkX,
            chunkZ,
            ids,
            meta,
            this.biomesForGeneration = this.worldObj.getWorldChunkManager()
                .loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16));
        if (this.worldGenerators == null) {
            this.worldGenerators = this.getWorldGenerators();
        }
        for (MapGenBaseMeta generator : this.worldGenerators) {
            generator.generate(this, this.worldObj, chunkX, chunkZ, ids, meta);
        }
        this.onChunkProvide(chunkX, chunkZ, ids, meta);
        Chunk chunk = new Chunk(this.worldObj, ids, meta, chunkX, chunkZ);
        byte[] biomes = chunk.getBiomeArray();
        for (int i = 0; i < biomes.length; ++i) {
            biomes[i] = (byte) this.biomesForGeneration[i].biomeID;
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    public void createCraters(int chunkX, int chunkZ, Block[] chunkArray, byte[] metaArray) {
        this.noiseGen5.setFrequency(0.015f);
        XSTR rng = new XSTR(0);
        for (int cx = chunkX - 2; cx <= chunkX + 2; ++cx) {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; ++cz) {
                for (int x = 0; x < 16; ++x) {
                    for (int z = 0; z < 16; ++z) {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000))
                            < this.noiseGen5.getNoise(cx * 16 + x, cz * 16 + z) * this.craterProbInv) {
                            rng.setSeed(cx * 16 + x + (cz * 16 + z) * 5000);
                            EnumCraterSize cSize = EnumCraterSize.sizeArray[rng
                                .nextInt(EnumCraterSize.sizeArray.length)];
                            int size = rng.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15;
                            this.makeCrater(
                                cx * 16 + x,
                                cz * 16 + z,
                                chunkX * 16,
                                chunkZ * 16,
                                size,
                                chunkArray,
                                metaArray);
                        }
                    }
                }
            }
        }
    }

    public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Block[] chunkArray,
        byte[] metaArray) {
        float sizeInv = 1f / size;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev *= sizeInv;
                    zDev *= sizeInv;
                    double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6.0;
                    yDev = 5.0 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; --y) {
                        if (helper > yDev) {
                            break;
                        }
                        if (chunkArray[this.getIndex(x, y, z)] != null) {
                            chunkArray[this.getIndex(x, y, z)] = Blocks.air;
                            metaArray[this.getIndex(x, y, z)] = 0;
                            ++helper;
                        }
                    }
                }
            }
        }
    }

    private int getIndex(int x, int y, int z) {
        return (x * 16 + z) * 256 + y;
    }

    private double randFromPoint(int x, int z) {
        int n = x + z * 57;
        n ^= n << 13;
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE) * 9.313225746E-10;
    }

    public void decoratePlanet(World world, Random random, int chunkX, int chunkZ) {
        this.getBiomeGenerator()
            .decorate(world, random, chunkX, chunkZ);
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
        BlockFalling.fallInstantly = true;
        int x = chunkX * 16;
        int z = chunkZ * 16;
        this.worldObj.getBiomeGenForCoords(x + 16, z + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long seedX = this.rand.nextLong() + 1L;
        long seedZ = this.rand.nextLong() + 1L;
        this.rand.setSeed(chunkX * seedX + chunkZ * seedZ ^ this.worldObj.getSeed());
        this.decoratePlanet(this.worldObj, this.rand, x, z);
        this.onPopulate(chunkProvider, chunkX, chunkZ);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public abstract String makeString();

    @Override
    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        if (creatureType == EnumCreatureType.monster) {
            List<BiomeGenBase.SpawnListEntry> monsters = new ArrayList<>();
            Collections.addAll(monsters, this.getMonsters());
            return monsters;
        }
        if (creatureType == EnumCreatureType.creature) {
            List<BiomeGenBase.SpawnListEntry> creatures = new ArrayList<>();
            Collections.addAll(creatures, this.getCreatures());
            return creatures;
        }
        if (creatureType == EnumCreatureType.waterCreature) {
            List<BiomeGenBase.SpawnListEntry> watercreatures = new ArrayList<>();
            Collections.addAll(watercreatures, this.getWaterCreatures());
            return watercreatures;
        }
        return null;
    }

    protected abstract BiomeDecoratorSpace getBiomeGenerator();

    protected abstract BiomeGenBase[] getBiomesForGeneration();

    protected abstract int getSeaLevel();

    protected abstract List<MapGenBaseMeta> getWorldGenerators();

    protected abstract BiomeGenBase.SpawnListEntry[] getMonsters();

    protected abstract BiomeGenBase.SpawnListEntry[] getCreatures();

    protected abstract BiomeGenBase.SpawnListEntry[] getWaterCreatures();

    public abstract double getHeightModifier();

    public abstract double getSmallFeatureHeightModifier();

    public abstract double getMountainHeightModifier();

    public abstract double getValleyHeightModifier();

    public abstract int getCraterProbability();

    public abstract void onChunkProvide(int chunkX, int chunkZ, Block[] blocks, byte[] meta);

    public abstract void onPopulate(IChunkProvider chunkProvider, int chunkX, int chunkZ);

    protected abstract BlockMetaPair getGrassBlock();

    protected abstract BlockMetaPair getDirtBlock();

    protected abstract BlockMetaPair getStoneBlock();

    protected abstract boolean enableBiomeGenBaseBlock();
}
