import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Generate {
    public static void main(String[] args) {
        if (args == null || args.length != 2) {
            System.out.println("Input format java Generate cellnum hostnum");
            return;
        }

        int cell_num = Integer.parseInt(args[0]);
        int host_num = Integer.parseInt(args[1]);
        int cell_num_per_host = (cell_num + host_num - 1) / host_num;
        String file_path = "result.txt";
        String pattern1 = "\t\tpnm.set_gid2node(%d, %d)";
        String pattern2 = "\t\tmap_matrix.x[%d][%d] = %d";

        Random random = new Random();
        //connection relation
        Map<Integer, Integer> map = new TreeMap<>();
        //how many node in a host
        Map<Integer, List<Integer>> count = new HashMap<>();

        for (int i = 0; i < host_num; i++) {
            count.put(i, new LinkedList<Integer>());
        }

        //get a random host for gid
        //repick when the host is full
        for (int i = 0; i < cell_num; i++) {
            while (true) {
                int r = random.nextInt(host_num);
                if (count.get(r).size() < cell_num_per_host) {
                    map.put(i, r);
                    count.get(r).add(i);
                    break;
                }
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file_path)))) {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                writer.write(String.format(pattern1, entry.getKey(), entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, List<Integer>> entry : count.entrySet()) {
                writer.write("\t\tnum_local_ncells.append(" + entry.getValue().size() + ")");
                writer.newLine();
                for (int i = 0; i < entry.getValue().size(); ++i) {
                    writer.write(String.format(pattern2, entry.getKey(), i, entry.getValue().get(i)));
                    writer.newLine();
                }
            }
        } catch (IOException io) {
            System.out.println("Unable to write in the file");
        }

        System.out.println("process finished!!");
    }
}
