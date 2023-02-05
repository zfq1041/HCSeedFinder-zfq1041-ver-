import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;

public class Main {
    public static void main(String[] args) {

        System.out.println("\n" + "A seed finding program by zfq1041(a0.0.4)\n" + "Special thanks:Harold_HC");
        long seed_start = 1041000000000000000L;       // ���Ŀ�ʼ��������
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
                ����ָ�ϣ�
                ����һ��Structure_filter�࣬���������ӣ�

                ����ɸ�Ľṹ
                ow_rp:���������
                ow_vi:��ׯ
                ow.dt:ɳĮ���
                ow.ig:ѩ��
                ow.wh:Ů��С��
                ow.mo:�������
                ow.jt:�������
                ow.ma:�ֵظ�ۡ
                ow.sw:����

                ����������ԭ��xֵ, ����ԭ��zֵ, ��x1��x�����, ��z1��z�����,
                ɸ���ӵ���Ʒ��,��Ʒ������ɸ��Ʒֱ�Ӹ�0��,
                �Ƿ�����ṹ����,�Ƿ���ṹ�����������ɣ�
                �����true�ٶȽ����������Ӷ�����ȷ�ģ�
                �����false�ٶȸ��쵫�ǿ��ܻ������������ӣ�
                ��ʵ�����ò���canGenerate��canSpawn��

                Ŀǰֻ�з��ſ���ɸ����
                x1��z1����������Debug������0

                ��������ɸ�����ϵ����ӷ���true�����򷵻�false

                max_seed_found:ɸ��������������
                seed_start:������ʼ������
                seed_end:��������������
                 */
            }
        }
    }
}