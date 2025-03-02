package HaroldHC.ssg_filter;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class main_finder {
    public static long get_int(String str){
        if(str.charAt(0)!='-'){
            return Long.parseLong(str);
        }else {
            String first_num = String.valueOf(str.charAt(1));
            String str_int_only = str.replace("-"+first_num, first_num);
            long int_1 = Long.parseLong(str_int_only);
            return -int_1;
        }
    }

    public static void main(String[] args) {
        main_finder finder = new main_finder();
        List<String> text = finder.read_seed_list("D:/12eye_list_1.16.txt");
        finder.main_runner(text);
    }

    public List<String> read_seed_list(String file_dir){
        Path path = Paths.get(file_dir);
        List<String> text = null;
        try {
            text = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public void main_runner(List<String> file_text) {
        long seed_now;
        int index_target = file_text.size(), index_now = 0;

        ChunkRand rand = new ChunkRand();
        HaroldHC.ssg_filter.ow_rp_filter orf = new HaroldHC.ssg_filter.ow_rp_filter();
        HaroldHC.ssg_filter.bastion_filter bf = new HaroldHC.ssg_filter.bastion_filter();

        StringBuilder result_seeds = new StringBuilder();

        while (index_now < index_target) {
            String[] line = file_text.get(index_now).split(" ");
            seed_now = get_int(line[0]);
            int sh_x =(int) get_int(line[1]), sh_z =(int) get_int(line[2]);

            CPos rp = orf.get_closest_rp(seed_now, rand);
            if (!Objects.equals(rp.toBlockPos(), new BPos(4096, 0, 4096))) {
                boolean is_looted_1 = orf.loot_chest(seed_now, rand, rp, "obsidian", 3);
                boolean is_looted_2 = orf.loot_chest(seed_now, rand, rp, "gold_pickaxe", 1);
                boolean is_looted_3 = orf.loot_chest(seed_now, rand, rp, "golden_axe", 1);
                boolean is_looted_4 = orf.loot_chest(seed_now, rand, rp, "flint_and_steel", 1);
                boolean is_looted_5 = orf.loot_chest(seed_now, rand, rp, "fire_charge", 2);
                if(is_looted_1 && is_looted_2 && is_looted_3 && (is_looted_4 || is_looted_5)){
                    boolean res_br = bf.is_in_area(seed_now, rand, sh_x, sh_z);
                    boolean res_biome = bf.check_biome(seed_now, rand, sh_x, sh_z);
                    if(res_br && res_biome){
                        result_seeds.append(seed_now);
                        result_seeds.append("\n");
                        System.out.println(seed_now);
                    }
                }
            }
            index_now++;
        }
        try{
            System.out.println("here");
            result_seeds.append("eee");
            System.out.println(result_seeds.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter("D:/bastion_ssg.txt"));
            writer.write(result_seeds.toString());
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
