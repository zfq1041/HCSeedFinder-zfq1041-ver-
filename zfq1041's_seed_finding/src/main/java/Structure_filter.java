import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.source.NetherBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.block.Block;
import com.seedfinding.mccore.block.Blocks;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.block.BlockBox;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootContext;
import com.seedfinding.mcfeature.loot.MCLootTables;
import com.seedfinding.mcfeature.loot.enchantment.Enchantment;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.structure.*;
import com.seedfinding.mcfeature.structure.generator.structure.RuinedPortalGenerator;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Structure_filter {

    long seed;
    int x1;
    int z1;
    int max_x_dist;
    int max_z_dist;
    String item_name;
    int count;
    ChunkRand rand = new ChunkRand();
    Structure_filter(long seed) {
    this.seed=seed;
    }

    public boolean ow_rp(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,String Enchantment,int Enchantment_level,boolean out,boolean check,int y,int obby,boolean no_crying) {
        RuinedPortal rp = new RuinedPortal(Dimension.OVERWORLD,MCVersion.v1_16_1);
        // 生成4个村庄
        CPos rp_1 = rp.getInRegion(seed, 0, 0, rand);
        CPos rp_2 = rp.getInRegion(seed, -1, 0, rand);
        CPos rp_3 = rp.getInRegion(seed, -1, -1, rand);
        CPos rp_4 = rp.getInRegion(seed, 0, -1, rand);
        List<CPos> rp_position_list = new ArrayList<>(Arrays.asList(rp_1, rp_2, rp_3, rp_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_rp = new CPos(0, 0);
        for (CPos rp_now : rp_position_list) {
            double dist_now = rp_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_rp = rp_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_rp.getX()*16,closest_rp.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        }
            if(check) {
                OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
                Biome biome = obs.getBiome(closest_rp.getX(), closest_rp.getY(), closest_rp.getZ());
                TerrainGenerator tr = new TerrainGenerator(obs) {
                    @Override
                    public Dimension getDimension() {
                        return null;
                    }

                    @Override
                    public int getWorldHeight() {
                        return 0;
                    }

                    @Override
                    public Block getDefaultBlock() {
                        return null;
                    }

                    @Override
                    public Block getDefaultFluid() {
                        return null;
                    }

                    @Override
                    protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                    }

                    @Override
                    public int getHeightOnGround(int x, int z) {
                        return 0;
                    }

                    @Override
                    public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                        return 0;
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Optional<Block> getBlockAt(int x, int y, int z) {
                        return Optional.empty();
                    }
                };
                if (!rp.canGenerate(closest_rp, tr) || !rp.canSpawn(closest_rp, tr.getBiomeSource())) {
                    return false;
                }
            }
            if(y!=0&&!rp_y(seed,closest_rp,y)){
                return false;
            }
            if(obby!=0) {
                if (obby < need_obby(seed, closest_rp,no_crying) || need_obby(seed, closest_rp,no_crying) == -1) {
                    return false;
                }
            }
                if(!loot_chest_rp(seed,rp.getSalt(),closest_rp,item_name,count,Enchantment,Enchantment_level)){
                    if(count!=0){return false;}
                }
        if (out) {
            System.out.println("RuinedPortal:/tp " + closest_rp.getX()*16 + " " + closest_rp.getY()*16 + " " + closest_rp.getZ()*16);
        }
        return true;
    }
    public boolean ow_vi(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,String bi_name,String zombie,boolean out,boolean check) {
        Village vi = new Village(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos vi_1 = vi.getInRegion(seed, 0, 0, rand);
        CPos vi_2 = vi.getInRegion(seed, -1, 0, rand);
        CPos vi_3 = vi.getInRegion(seed, -1, -1, rand);
        CPos vi_4 = vi.getInRegion(seed, 0, -1, rand);
        List<CPos> vi_position_list = new ArrayList<>(Arrays.asList(vi_1, vi_2, vi_3, vi_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_vi = new CPos(0, 0);
        for (CPos vi_now : vi_position_list) {
            double dist_now = vi_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_vi = vi_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_vi.getX()*16,closest_vi.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        } else {
            if(check) {
                OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
                Biome biome = obs.getBiome(closest_vi.getX() * 16, closest_vi.getY() * 16, closest_vi.getZ() * 16);
                TerrainGenerator tr = new TerrainGenerator(obs) {
                    @Override
                    public Dimension getDimension() {
                        return null;
                    }

                    @Override
                    public int getWorldHeight() {
                        return 0;
                    }

                    @Override
                    public Block getDefaultBlock() {
                        return null;
                    }

                    @Override
                    public Block getDefaultFluid() {
                        return null;
                    }

                    @Override
                    protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                    }

                    @Override
                    public int getHeightOnGround(int x, int z) {
                        return 0;
                    }

                    @Override
                    public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                        return 0;
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Optional<Block> getBlockAt(int x, int y, int z) {
                        return Optional.empty();
                    }
                };
                if (!vi.canGenerate(closest_vi, tr) || !vi.canSpawn(closest_vi, tr.getBiomeSource())) {
                    return false;
                }
            }
                if(!loot_chest_vi(seed,vi.getSalt(),closest_vi,item_name,count)){
                    if(count!=0){
                        return false;
                    }
                }
                if(bi_name!="none") {
                    OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
                    Biome biome = obs.getBiome(closest_vi.getX() * 16, closest_vi.getY() * 16, closest_vi.getZ() * 16);
                    switch (bi_name) {
                        case "plains":
                            if (biome != Biomes.PLAINS) {
                                return false;
                            }
                            break;
                        case "desert":
                            if (biome != Biomes.DESERT) {
                                return false;
                            }
                            break;
                        case "taiga":
                            if (biome != Biomes.TAIGA) {
                                return false;
                            }
                            break;
                        case "savanna":
                            if (biome != Biomes.SAVANNA) {
                                return false;
                            }
                            break;
                        case "snowy":
                            if (biome != Biomes.SNOWY_TUNDRA) {
                                return false;
                            }
                            break;
                    }
                }
                if (vi.isZombieVillage(seed,closest_vi,rand)){
                    if (zombie == "false"){
                        return false;
                    }
                }
                else {
                    if (zombie == "true"){
                        return false;
                    }
                }
            }
        if (out) {
            System.out.println("Village:/tp " + closest_vi.getX()*16 + " " + closest_vi.getY()*16 + " " + closest_vi.getZ()*16);
        }
        return true;
    }
    public boolean ow_dt(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        DesertPyramid dt = new DesertPyramid(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos dt_1 = dt.getInRegion(seed, 0, 0, rand);
        CPos dt_2 = dt.getInRegion(seed, -1, 0, rand);
        CPos dt_3 = dt.getInRegion(seed, -1, -1, rand);
        CPos dt_4 = dt.getInRegion(seed, 0, -1, rand);
        List<CPos> dt_position_list = new ArrayList<>(Arrays.asList(dt_1, dt_2, dt_3, dt_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_dt = new CPos(0, 0);
        for (CPos dt_now : dt_position_list) {
            double dist_now = dt_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_dt = dt_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_dt.getX()*16,closest_dt.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        } else {
            if(check) {
                OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
                Biome biome = obs.getBiome(closest_dt.getX(), closest_dt.getY(), closest_dt.getZ());
                TerrainGenerator tr = new TerrainGenerator(obs) {
                    @Override
                    public Dimension getDimension() {
                        return null;
                    }

                    @Override
                    public int getWorldHeight() {
                        return 0;
                    }

                    @Override
                    public Block getDefaultBlock() {
                        return null;
                    }

                    @Override
                    public Block getDefaultFluid() {
                        return null;
                    }

                    @Override
                    protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                    }

                    @Override
                    public int getHeightOnGround(int x, int z) {
                        return 0;
                    }

                    @Override
                    public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                        return 0;
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Optional<Block> getBlockAt(int x, int y, int z) {
                        return Optional.empty();
                    }
                };
                if (!dt.canGenerate(closest_dt, tr) || !dt.canSpawn(closest_dt, tr.getBiomeSource())) {
                    return false;
                }
            }
                if(!loot_chest_dt(seed,dt.getSalt(),closest_dt,item_name,count)){
                    if(count!=0){return false;}
            }
        }
        if (out) {
            System.out.println("DesertTemple:/tp " + closest_dt.getX()*16 + " " + closest_dt.getY()*16 + " " + closest_dt.getZ()*16);
        }
        return true;
    }
    public boolean ow_ig(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        Igloo ig = new Igloo(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos ig_1 = ig.getInRegion(seed, 0, 0, rand);
        CPos ig_2 = ig.getInRegion(seed, -1, 0, rand);
        CPos ig_3 = ig.getInRegion(seed, -1, -1, rand);
        CPos ig_4 = ig.getInRegion(seed, 0, -1, rand);
        List<CPos> ig_position_list = new ArrayList<>(Arrays.asList(ig_1, ig_2, ig_3, ig_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_ig = new CPos(0, 0);
        for (CPos ig_now : ig_position_list) {
            double dist_now = ig_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_ig = ig_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_ig.getX()*16,closest_ig.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        } else {
            if(check) {
                OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
                Biome biome = obs.getBiome(closest_ig.getX(), closest_ig.getY(), closest_ig.getZ());
                TerrainGenerator tr = new TerrainGenerator(obs) {
                    @Override
                    public Dimension getDimension() {
                        return null;
                    }

                    @Override
                    public int getWorldHeight() {
                        return 0;
                    }

                    @Override
                    public Block getDefaultBlock() {
                        return null;
                    }

                    @Override
                    public Block getDefaultFluid() {
                        return null;
                    }

                    @Override
                    protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                    }

                    @Override
                    public int getHeightOnGround(int x, int z) {
                        return 0;
                    }

                    @Override
                    public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                        return 0;
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z) {
                        return new Block[0];
                    }

                    @Override
                    public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                        return new Block[0];
                    }

                    @Override
                    public Optional<Block> getBlockAt(int x, int y, int z) {
                        return Optional.empty();
                    }
                };
                if (!ig.canGenerate(closest_ig, tr) || !ig.canSpawn(closest_ig, tr.getBiomeSource())) {
                    return false;
                }
            }
                if(!loot_chest_ig(seed,ig.getSalt(),closest_ig,item_name,count)){
                    if(count!=0){return false;}
                }
        }
        if (out) {
            System.out.println("Igloo:/tp " + closest_ig.getX()*16 + " " + closest_ig.getY()*16 + " " + closest_ig.getZ()*16);
        }
        return true;
    }
    public boolean ow_wh(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        SwampHut wh = new SwampHut(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos wh_1 = wh.getInRegion(seed, 0, 0, rand);
        CPos wh_2 = wh.getInRegion(seed, -1, 0, rand);
        CPos wh_3 = wh.getInRegion(seed, -1, -1, rand);
        CPos wh_4 = wh.getInRegion(seed, 0, -1, rand);
        List<CPos> wh_position_list = new ArrayList<>(Arrays.asList(wh_1, wh_2, wh_3, wh_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_wh = new CPos(0, 0);
        for (CPos wh_now : wh_position_list) {
            double dist_now = wh_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_wh = wh_now;
            }
        }
        // filter the position
        if (!dist(x1, z1, closest_wh.getX() * 16, closest_wh.getZ() * 16, max_x_dist, max_z_dist)) {
            return false;
        } else if(check){
            OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
            Biome biome = obs.getBiome(closest_wh.getX(), closest_wh.getY(), closest_wh.getZ());
            TerrainGenerator tr = new TerrainGenerator(obs) {
                @Override
                public Dimension getDimension() {
                    return null;
                }

                @Override
                public int getWorldHeight() {
                    return 0;
                }

                @Override
                public Block getDefaultBlock() {
                    return null;
                }

                @Override
                public Block getDefaultFluid() {
                    return null;
                }

                @Override
                protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                }

                @Override
                public int getHeightOnGround(int x, int z) {
                    return 0;
                }

                @Override
                public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                    return 0;
                }

                @Override
                public Block[] getColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Optional<Block> getBlockAt(int x, int y, int z) {
                    return Optional.empty();
                }
            };
            if (!wh.canGenerate(closest_wh, tr) || !wh.canSpawn(closest_wh, tr.getBiomeSource())) {
                return false;
            }
        }
        if (out) {
            System.out.println("SwampHut:/tp " + closest_wh.getX()*16 + " " + closest_wh.getY()*16 + " " + closest_wh.getZ()*16);
        }
        return true;
    }
    public boolean ow_mo(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        Monument mo = new Monument(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos mo_1 = mo.getInRegion(seed, 0, 0, rand);
        CPos mo_2 = mo.getInRegion(seed, -1, 0, rand);
        CPos mo_3 = mo.getInRegion(seed, -1, -1, rand);
        CPos mo_4 = mo.getInRegion(seed, 0, -1, rand);
        List<CPos> mo_position_list = new ArrayList<>(Arrays.asList(mo_1, mo_2, mo_3, mo_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_mo = new CPos(0, 0);
        for (CPos mo_now : mo_position_list) {
            double dist_now = mo_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_mo = mo_now;
            }
        }
        // filter the position
        if (!dist(x1, z1, closest_mo.getX() * 16, closest_mo.getZ() * 16, max_x_dist, max_z_dist)) {
            return false;
        } else if(check){
            OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
            Biome biome = obs.getBiome(closest_mo.getX(), closest_mo.getY(), closest_mo.getZ());
            TerrainGenerator tr = new TerrainGenerator(obs) {
                @Override
                public Dimension getDimension() {
                    return null;
                }

                @Override
                public int getWorldHeight() {
                    return 0;
                }

                @Override
                public Block getDefaultBlock() {
                    return null;
                }

                @Override
                public Block getDefaultFluid() {
                    return null;
                }

                @Override
                protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                }

                @Override
                public int getHeightOnGround(int x, int z) {
                    return 0;
                }

                @Override
                public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                    return 0;
                }

                @Override
                public Block[] getColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Optional<Block> getBlockAt(int x, int y, int z) {
                    return Optional.empty();
                }
            };
            if (!mo.canGenerate(closest_mo, tr) || !mo.canSpawn(closest_mo, tr.getBiomeSource())) {
                return false;
            }
        }
        if (out) {
            System.out.println("Monument:/tp " + closest_mo.getX()*16 + " " + closest_mo.getY()*16 + " " + closest_mo.getZ()*16);
        }

        return true;
    }
    public boolean ow_jt(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        JunglePyramid jt = new JunglePyramid(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos jt_1 = jt.getInRegion(seed, 0, 0, rand);
        CPos jt_2 = jt.getInRegion(seed, -1, 0, rand);
        CPos jt_3 = jt.getInRegion(seed, -1, -1, rand);
        CPos jt_4 = jt.getInRegion(seed, 0, -1, rand);
        List<CPos> jt_position_list = new ArrayList<>(Arrays.asList(jt_1, jt_2, jt_3, jt_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_jt = new CPos(0, 0);
        for (CPos jt_now : jt_position_list) {
            double dist_now = jt_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_jt = jt_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_jt.getX()*16,closest_jt.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        } else {
            if(check){
            OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
            Biome biome = obs.getBiome(closest_jt.getX(), closest_jt.getY(), closest_jt.getZ());
            TerrainGenerator tr = new TerrainGenerator(obs) {
                @Override
                public Dimension getDimension() {
                    return null;
                }

                @Override
                public int getWorldHeight() {
                    return 0;
                }

                @Override
                public Block getDefaultBlock() {
                    return null;
                }

                @Override
                public Block getDefaultFluid() {
                    return null;
                }

                @Override
                protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                }

                @Override
                public int getHeightOnGround(int x, int z) {
                    return 0;
                }

                @Override
                public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                    return 0;
                }

                @Override
                public Block[] getColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Optional<Block> getBlockAt(int x, int y, int z) {
                    return Optional.empty();
                }
            };
            if (!jt.canGenerate(closest_jt, tr) || !jt.canSpawn(closest_jt, tr.getBiomeSource())) {
                return false;
            }
        }
                if(!loot_chest_jt(seed,jt.getSalt(),closest_jt,item_name,count)){
                    if(count!=0){return false;}
            }
        }
        if (out) {
            System.out.println("JungleTemple:/tp " + closest_jt.getX()*16 + " " + closest_jt.getY()*16 + " " + closest_jt.getZ()*16);
        }
        return true;
    }
    public boolean ow_ma(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        Mansion ma = new Mansion(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos ma_1 = ma.getInRegion(seed, 0, 0, rand);
        CPos ma_2 = ma.getInRegion(seed, -1, 0, rand);
        CPos ma_3 = ma.getInRegion(seed, -1, -1, rand);
        CPos ma_4 = ma.getInRegion(seed, 0, -1, rand);
        List<CPos> ma_position_list = new ArrayList<>(Arrays.asList(ma_1, ma_2, ma_3, ma_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_ma = new CPos(0, 0);
        for (CPos ma_now : ma_position_list) {
            double dist_now = ma_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_ma = ma_now;
            }
        }
        // filter the position
        if (!dist(x1, z1, closest_ma.getX() * 16, closest_ma.getZ() * 16, max_x_dist, max_z_dist)) {
            return false;
        } else if(check){
            OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
            Biome biome = obs.getBiome(closest_ma.getX(), closest_ma.getY(), closest_ma.getZ());
            TerrainGenerator tr = new TerrainGenerator(obs) {
                @Override
                public Dimension getDimension() {
                    return null;
                }

                @Override
                public int getWorldHeight() {
                    return 0;
                }

                @Override
                public Block getDefaultBlock() {
                    return null;
                }

                @Override
                public Block getDefaultFluid() {
                    return null;
                }

                @Override
                protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                }

                @Override
                public int getHeightOnGround(int x, int z) {
                    return 0;
                }

                @Override
                public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                    return 0;
                }

                @Override
                public Block[] getColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Optional<Block> getBlockAt(int x, int y, int z) {
                    return Optional.empty();
                }
            };
            if (!ma.canGenerate(closest_ma, tr) || !ma.canSpawn(closest_ma, tr.getBiomeSource())) {
                return false;
            }
        }
        if (out) {
            System.out.println("Mansion:/tp " + closest_ma.getX()*16 + " " + closest_ma.getY()*16 + " " + closest_ma.getZ()*16);
        }
        return true;
    }
    public boolean ow_sw(int x1, int z1, int max_x_dist, int max_z_dist,String item_name,int count,boolean out,boolean check) {
        Shipwreck sw = new Shipwreck(MCVersion.v1_16_1);
        // 生成4个村庄
        CPos sw_1 = sw.getInRegion(seed, 0, 0, rand);
        CPos sw_2 = sw.getInRegion(seed, -1, 0, rand);
        CPos sw_3 = sw.getInRegion(seed, -1, -1, rand);
        CPos sw_4 = sw.getInRegion(seed, 0, -1, rand);
        List<CPos> sw_position_list = new ArrayList<>(Arrays.asList(sw_1, sw_2, sw_3, sw_4));
        double closest_dist = Integer.MAX_VALUE;
        CPos closest_sw = new CPos(0, 0);
        for (CPos sw_now : sw_position_list) {
            double dist_now = sw_now.toBlockPos().distanceTo(new BPos(x1, 0, z1), DistanceMetric.EUCLIDEAN);
            if (dist_now < closest_dist) {
                closest_dist = dist_now;
                closest_sw = sw_now;
            }
        }
        // filter the position
        if (!dist(x1,z1,closest_sw.getX()*16,closest_sw.getZ()*16,max_x_dist,max_z_dist)) {
            return false;
        } else {
            if(check){
            OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
            Biome biome = obs.getBiome(closest_sw.getX(), closest_sw.getY(), closest_sw.getZ());
            TerrainGenerator tr = new TerrainGenerator(obs) {
                @Override
                public Dimension getDimension() {
                    return null;
                }

                @Override
                public int getWorldHeight() {
                    return 0;
                }

                @Override
                public Block getDefaultBlock() {
                    return null;
                }

                @Override
                public Block getDefaultFluid() {
                    return null;
                }

                @Override
                protected void sampleNoiseColumnOld(double[] buffer, int x, int z, double depth, double scale) {

                }

                @Override
                public int getHeightOnGround(int x, int z) {
                    return 0;
                }

                @Override
                public int getFirstHeightInColumn(int x, int z, Predicate<Block> predicate) {
                    return 0;
                }

                @Override
                public Block[] getColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBiomeColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z) {
                    return new Block[0];
                }

                @Override
                public Block[] getBedrockColumnAt(int x, int z, List<Pair<Supplier<Integer>, BlockBox>> jigsawBoxes, List<BPos> jigsawJunction) {
                    return new Block[0];
                }

                @Override
                public Optional<Block> getBlockAt(int x, int y, int z) {
                    return Optional.empty();
                }
            };
            if (!sw.canGenerate(closest_sw, tr) || !sw.canSpawn(closest_sw, tr.getBiomeSource())) {
                return false;
            }
        }
                if(!loot_chest_sw(seed,sw.getSalt(),closest_sw,item_name,count)){
                    if(count!=0){return false;}
                }
        }
        if (out) {
            System.out.println("Shipwreck:/tp " + closest_sw.getX()*16 + " " + closest_sw.getY()*16 + " " + closest_sw.getZ()*16);
        }
        return true;
    }

    // 筛箱子
    public boolean loot_chest_rp(long seed,int salt,CPos chest, String target, int num,String Enchantment,int Enchantment_level) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, 40005, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.RUINED_PORTAL_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    if (Enchantment == "none") {
                        is_looted = true;
                    }
                    else{
                        Pair<String, Integer> en = itemStack.getItem().getEnchantments().get(0);
                        if (en.getFirst() == Enchantment && en.getSecond() == Enchantment_level) {
                            is_looted = true;
                        }
                    }
                }
            }
        }
            return is_looted;
        }
    public boolean loot_chest_vi(long seed,int salt,CPos chest, String target, int num) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, salt, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.VILLAGE_WEAPONSMITH_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    is_looted = true;
                }
            }
        }
        return is_looted;
    }//不可用
    public boolean loot_chest_dt(long seed,int salt,CPos chest, String target, int num) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, salt, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.DESERT_PYRAMID_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    is_looted = true;
                }
            }
        }
        return is_looted;
    }//不可用
    public boolean loot_chest_ig(long seed,int salt,CPos chest, String target, int num) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, salt, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.IGLOO_CHEST_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    is_looted = true;
                }
            }
        }
        return is_looted;
    }//不可用
    public boolean loot_chest_jt(long seed,int salt,CPos chest, String target, int num) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, salt, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.JUNGLE_TEMPLE_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    is_looted = true;
                }
            }
        }
        return is_looted;
    }//不可用
    public boolean loot_chest_sw(long seed,int salt,CPos chest, String target, int num) {
        if (chest == null) {
            return false;
        }
        // 加载箱子
        rand.setDecoratorSeed(seed, chest.getX() * 16, chest.getZ() * 16, salt, MCVersion.v1_16_1);
        LootContext a1 = new LootContext(rand.nextLong());
        // 得到战利品
        List<ItemStack> ItemList = MCLootTables.SHIPWRECK_TREASURE_CHEST.get().generate(a1);
        boolean is_looted = false;
        for (ItemStack itemStack : ItemList) {
            if (itemStack.getItem().getName().equals(target)) {   // 如果有
                if (itemStack.getCount() >= num) {      // 如果够
                    is_looted = true;
                }
            }
        }
        return is_looted;
    }//不可用

    // 筛距离
    public boolean dist(int x1,int z1,int x2,int z2, int max_x_dist, int max_z_dist ){
        int x_dist = x1 > x2 ? x1-x2 : x2-x1;
        int z_dist = z1 > z2 ? z1-z2 : z2-z1;
        x_dist = x_dist<0?-x_dist:x_dist;
        z_dist = z_dist<0?-z_dist:z_dist;
        if (x_dist<=max_x_dist&&z_dist<=max_z_dist){
            return true;
        }
        else {
            return false;
        }
    }


    //筛可补废门
    public int need_obby(long seed, CPos rp,boolean no_crying){
        RuinedPortalGenerator rpg = new RuinedPortalGenerator(MCVersion.v1_16_1);
        OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
        OverworldTerrainGenerator otg = new OverworldTerrainGenerator(obs);
        rpg.generate(otg, rp);
        List<Pair<Block, BPos>> blocks = rpg.getMinimalPortal();

        int block_count = 0, obi_count = 0;
        for(Pair<Block, BPos> block : blocks){
            block_count ++;
            if(block_count==11){
                return 1;
            }
            if(block.getFirst().equals(Blocks.OBSIDIAN)){
                obi_count ++;
            }
            else{
                if(no_crying){
                    return -1;
                }
            }
        }

        return obi_count>=block_count?(10-obi_count):-1;
    }

    //筛箱子
    public boolean rp_y(long seed, CPos rp,int y){
        RuinedPortalGenerator rpg = new RuinedPortalGenerator(MCVersion.v1_16_1);
        OverworldBiomeSource obs = new OverworldBiomeSource(MCVersion.v1_16_1, seed);
        OverworldTerrainGenerator otg = new OverworldTerrainGenerator(obs);
        rpg.generate(otg, rp);
        List<Pair<Block, BPos>> blocks = rpg.getMinimalPortal();

        for(Pair<Block, BPos> block : blocks){
            if(block.getSecond().getY()>=y){
                return true;
            }
        }
        return false;
    }
}

