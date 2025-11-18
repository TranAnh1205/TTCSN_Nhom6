package model;

import java.util.Scanner;
import java.util.Arrays;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Đọc input từ file
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        try {
            // --- Đọc dữ liệu từ file ---
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;

            // Đọc số đỉnh
            line = reader.readLine();
            if (line == null) {
                System.out.println("File input không có dữ liệu");
                return;
            }
            int n = Integer.parseInt(line.trim());
            if (n < 1) {
                System.out.println("Số đỉnh phải >= 1");
                reader.close();
                return;
            }

            Graph g = new Graph(n);

            // Đọc số cạnh
            line = reader.readLine();
            if (line == null) {
                System.out.println("Thiếu số cạnh trong file");
                reader.close();
                return;
            }
            int m = Integer.parseInt(line.trim());
            if (m <= 0) {
                System.out.println("Số cạnh phải > 0");
                reader.close();
                return;
            }

            // Đọc các cạnh
            System.out.println("Đang đọc các cạnh từ file...");
            for (int i = 0; i < m; i++) {
                line = reader.readLine();
                if (line == null) {
                    System.out.println("Thiếu cạnh thứ " + (i + 1));
                    break;
                }
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length < 2) {
                    System.out.println("Dòng không hợp lệ: " + line + " (bỏ qua)");
                    continue;
                }
                int u = Integer.parseInt(tokens[0]);
                int v = Integer.parseInt(tokens[1]);
                if (u < 0 || u >= n || v < 0 || v >= n) {
                    System.out.println("Chỉ số đỉnh không hợp lệ: " + u + " " + v + " (bỏ qua cạnh này)");
                    continue;
                }
                if (u == v) {
                    System.out.println("Bỏ qua self-loop: " + u + " " + v);
                    continue;
                }
                g.addEdge(u, v);
            }

            // Đọc tham số GA
            int popSize = 20;
            int maxGenerations = 50;
            double mutationRate = 0.05;
            double crossoverRate = 0.7;
            int eliteCount = 2;
            int patience = 0;
            double diversityThreshold = 0.0;

            try {
                line = reader.readLine();
                if (line != null) popSize = Integer.parseInt(line.trim());

                line = reader.readLine();
                if (line != null) maxGenerations = Integer.parseInt(line.trim());

                line = reader.readLine();
                if (line != null) mutationRate = Double.parseDouble(line.trim());

                line = reader.readLine();
                if (line != null) crossoverRate = Double.parseDouble(line.trim());

                line = reader.readLine();
                if (line != null) eliteCount = Integer.parseInt(line.trim());

                line = reader.readLine();
                if (line != null) patience = Integer.parseInt(line.trim());

                line = reader.readLine();
                if (line != null) diversityThreshold = Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Lỗi định dạng tham số GA, sử dụng giá trị mặc định");
            }

            reader.close();

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

            // --- Ghi kết quả ra file ---
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));

            writer.write("==== KẾT QUẢ TỐI ƯU ====\n");
            writer.write("Độ thích nghi: " + best.getFitness() + "\n");

            // Ghi danh sách đỉnh có gene = 1
            writer.write("Đỉnh có trong tập (index): ");
            for (int i = 0; i < best.getGenes().length(); i++) {
                if (best.getGenes().get(i)) {
                    writer.write(i + " ");
                }
            }
            writer.write("\n");

            // Ghi thông tin genes
            writer.write("Genes: ");
            for (int i = 0; i < best.getGenes().length(); i++) {
                writer.write(best.getGenes().get(i) ? "1 " : "0 ");
            }
            writer.write("\n");

            writer.close();

            // --- In kết quả ra console ---
            System.out.println("\n==== KẾT QUẢ TỐI ƯU ====");
            System.out.println("Độ thích nghi: " + best.getFitness());
            System.out.print("Đỉnh có trong tập (index): ");
            for (int i = 0; i < best.getGenes().length(); i++) {
                if (best.getGenes().get(i)) System.out.print(i + " ");
            }
            System.out.println();
            System.out.println("Kết quả đã được ghi vào file: " + outputFile);

        } catch (IOException e) {
            System.out.println("Lỗi đọc/ghi file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}