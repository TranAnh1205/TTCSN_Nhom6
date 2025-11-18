package model;

import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            // --- Nhập số đỉnh ---
            System.out.print("Nhập số đỉnh của đồ thị (n >= 1): ");
            int n = sc.nextInt();
            if (n < 1) {
                System.out.println("Số đỉnh phải >= 1");
                return;
            }

            Graph g = new Graph(n);

            // --- Nhập số cạnh ---
            System.out.print("Nhập số cạnh (m > 0): ");
            int m = sc.nextInt();
            if (m <= 0) {
                System.out.println("Số cạnh phải > 0");
                return;
            }

            System.out.println("Nhập các cạnh theo dạng: u v (0 <= u,v < " + n + ")");
            for (int i = 0; i < m; i++) {
                int u = sc.nextInt();
                int v = sc.nextInt();
                if (u < 0 || u >= n || v < 0 || v >= n) {
                    System.out.println("Chỉ số đỉnh không hợp lệ: " + u + " " + v + " (bỏ qua cạnh này)");
                    continue;
                }
                if (u == v) {
                    // nếu bạn không muốn self-loop, bỏ qua
                    System.out.println("Bỏ qua self-loop: " + u + " " + v);
                    continue;
                }
                g.addEdge(u, v);
            }

            // Nhập tham số GA
            System.out.print("Nhập kích thước quần thể (population size, ví dụ 20): ");
            int popSize = sc.nextInt();
            if (popSize < 2) {
                System.out.println("population size phải >= 2");
                return;
            }

            System.out.print("Nhập số thế hệ tối đa (max generations, ví dụ 50): ");
            int maxGenerations = sc.nextInt();
            if (maxGenerations < 1) {
                System.out.println("generations phải >= 1");
                return;
            }

            System.out.print("Nhập tỉ lệ đột biến (mutation rate, ví dụ 0.05): ");
            double mutationRate = sc.nextDouble();
            System.out.print("Nhập tỉ lệ lai ghép (crossover rate, ví dụ 0.7): ");
            double crossoverRate = sc.nextDouble();
            System.out.print("Nhập số lượng cá thể ưu tú giữ lại mỗi thế hệ (elite count, ví dụ 2): ");
            int eliteCount = sc.nextInt();

            // Nhập các tham số dừng sớm
            System.out.print("Nhập số thế hệ không cải thiện trước khi dừng (patience, 0 nếu không dùng): ");
            int patience = sc.nextInt();

            System.out.print("Nhập ngưỡng đa dạng quần thể (0.0 nếu không dùng, 0 < diversity <= 1): ");
            double diversityThreshold = sc.nextDouble();

            // --- Khởi tạo và chạy GA ---
            GeneticAlgorithm ga = new GeneticAlgorithm(
                    g,
                    popSize,
                    maxGenerations,
                    mutationRate,
                    crossoverRate,
                    eliteCount,
                    patience,
                    diversityThreshold
            );

            System.out.println("\nBắt đầu chạy Genetic Algorithm...");
            Individual best = ga.run();

            // --- In kết quả ---
            System.out.println("\n==== KẾT QUẢ TỐI ƯU ====");
             try {
                 System.out.println("Genes: " + Arrays.toString(best.getGenes().stream().mapToObj(String::valueOf).toArray(String[]::new)));
                 // In danh sách đỉnh có gene = 1 (nếu đây là biểu diễn clique)
                 System.out.print("Đỉnh có trong tập (index): ");
                 for (int i = 0; i < best.getGenes().length(); i++) {
                     if (best.getGenes().get(i)) System.out.print(i + " ");
                 }
                 System.out.println();
             } catch (Throwable t) {
                 // nếu không có trường genes, bỏ qua
             }
        } finally {
            sc.close();
        }
    }
}
