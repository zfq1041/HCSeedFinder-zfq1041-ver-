import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;

public class Main {
    public static void main(String[] args) {

        System.out.println("\n" + "A seed finding program by zfq1041(a0.0.4)\n" + "Special thanks:Harold_HC");
        long seed_start = 1041000000000000000L;       // 从哪开始遍历种子
        long seed_end = 1042000000000000000L;
        long seed_found_count=0;
        long max_seed_found=1;

        System.out.println("Start seed finding!\n");
        for (long seed_now = seed_start; seed_now <= seed_end && seed_found_count<max_seed_found; seed_now++) {
            Structure_filter sf = new Structure_filter(seed_now);
            if (sf.ow_rp(0, 0, 256, 256,
                    "golden_sword", 1,"looting",3,
                    true,true,
                    0,
                    0,false)) {
                System.out.println(seed_now+"\n");
                seed_found_count++;
                /*
                操作指南：
                定义一个Structure_filter类，参数（种子）

                可以筛的结构
                ow_rp:主世界废门
                ow_vi:村庄
                ow.dt:沙漠神殿
                ow.ig:雪屋
                ow.wh:女巫小屋
                ow.mo:海洋神殿
                ow.jt:丛林神殿
                ow.ma:林地府邸
                ow.sw:沉船

                参数（坐标原点x值, 坐标原点z值, 和x1的x轴距离, 和z1的z轴距离,
                筛箱子的物品名,物品数（不筛物品直接给0）,
                是否输出结构坐标,是否检查结构可以正常生成，
                如果是true速度较慢但是种子都是正确的，
                如果是false速度更快但是可能会输出错误的种子，
                其实就是用不用canGenerate和canSpawn）

                目前只有废门可以筛箱子
                x1，z1这两个参数Debug中请用0

                这个类如果筛出符合的种子返回true，否则返回false

                max_seed_found:筛出的种子数限制
                seed_start:递增开始的种子
                seed_end:递增结束的种子
                 */
            }
        }
    }
}