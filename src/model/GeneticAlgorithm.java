package model;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.*;

public class GeneticAlgorithm {

    private final Graph graph;
    private final int populationSize;
    private final int maxGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    private final int eliteCount;
    private final int patience;
    private final double diversityThreshold;
    private final Random rand = new Random();


    public GeneticAlgorithm(Graph graph, int populationSize, int maxGenerations, double mutationRate, double crossoverRate
            , int eliteCount, int patience, double diversityThreshold) {
        this.graph = graph;
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.eliteCount = eliteCount;
        this.patience = Math.max(0, patience);
        this.diversityThreshold = Math.max(diversityThreshold, 0.0);
    }

    public Individual run() {
        Population population = new Population(graph, populationSize);
        // Sắp xếp quần thể ban đầu để xác định globalBest
        population.sortByFitness();

        int noImprovementCounter = 0;
        Individual globalBest = population.getBest().cloneIndividual();

        // Mở file ghi log một lần duy nhất với try-with-resources
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter("output.txt"))) {

            for (int gen = 0; gen < maxGenerations; gen++) {
                List<Individual> newIndividuals = new ArrayList<>();

                // Thêm cá thể ưu tú (Elite)
                for(int i = 0; i < eliteCount; i++) {
                    newIndividuals.add(population.getIndividuals().get(i).cloneIndividual());
                }

                while (newIndividuals.size() < populationSize) {
                    Individual parent1 = tournamentSelection(population);
                    Individual parent2 = tournamentSelection(population);
                    Individual offspring;

                    if (Math.random() < crossoverRate) {
                        offspring = Individual.crossover(parent1, parent2);
                    } else {
                        // Nếu không lai tạo, lấy một bản sao của parent1
                        offspring = new Individual(graph);
                        offspring.getGenes().or(parent1.getGenes());
                        offspring.calculateFitness();
                    }

                    offspring.mutate(mutationRate);
                    offspring.calculateFitness();
                    newIndividuals.add(offspring);
                }

                // Thay quần thể & sắp xếp
                population.getIndividuals().clear();
                population.getIndividuals().addAll(newIndividuals);
                population.sortByFitness();

                // tìm best hiện tại
                Individual currentBest = population.getBest();

                // nếu có cải thiện so với global best
                if (currentBest.getFitness() > globalBest.getFitness()) {
                    globalBest = currentBest.cloneIndividual(); // Clone để tránh tham chiếu
                    noImprovementCounter = 0;
                }else{
                    noImprovementCounter++;
                }

                // tính diversity (tỷ lệ cá thể khác nhau)
                double diversity = computeDiversity(population);

                // Ghi log, truyền đối tượng BufferedWriter
                logGeneration(bw, gen, population);

                // Điều kiện dừng sớm
                if (patience > 0 && noImprovementCounter >= patience) {
                    System.out.println("Dừng sớm: Không có cải thiện trong " + patience + " thế hệ.");
                    break;
                }
                if (diversityThreshold > 0 && diversity < diversityThreshold) {
                    System.out.println("Dừng sớm: Độ đa dạng quần thể (" + String.format("%.4f", diversity) + ") thấp hơn ngưỡng (" + diversityThreshold + ")");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình chạy thuật toán hoặc ghi log: " + e.getMessage());
            e.printStackTrace();
        }

        return globalBest;
    }

    private Individual tournamentSelection(Population population) {
        int tournamentSize = 3;
        Individual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Individual ind = population.getIndividuals().get(rand.nextInt(populationSize));
            if (best == null || ind.getFitness() > best.getFitness()) {
                best = ind;
            }
        }
        return best;
    }
    private void printPopulation(Population population) {
        int idx = 0;
        for (Individual ind : population.getIndividuals()) {
            System.out.println("[" + idx + "] " + ind);
            idx++;
        }
    }

    private double computeDiversity(Population pop) {
        Set<String> unique = new HashSet<>();
        for (Individual ind : pop.getIndividuals()) {
            String s = ind.getGenes().toString();
            unique.add(s);
        }
        if (pop.getIndividuals().isEmpty()) return 0.0;
        return (double) unique.size() / (double) pop.getIndividuals().size();
    }

    private void logGeneration(BufferedWriter bw, int generation, Population population) {
        try {
            bw.write("===== GENERATION " + generation + " =====");
            bw.newLine();
            bw.newLine();
            bw.write("Population Details:");
            bw.newLine();
            int index = 0;
            for (Individual ind : population.getIndividuals()) {
//                bw.write("#" + index + " Fitness: " + ind.getFitness() + " Genes: " + (ind.getGenes()));
                bw.write("[" + index + "] " + ind);
                bw.newLine();
                index++;
            }

            bw.write("Best Fitness: " + population.getBest().getFitness());
            bw.newLine();
            bw.write("Population Diversity: " + String.format("%.4f", computeDiversity(population)));
            bw.newLine();

            bw.write("----------------------------------------");
            bw.newLine();

            // Đảm bảo dữ liệu được ghi ngay lập tức
            bw.flush();

            System.out.println("Đã ghi Generation " + generation + " vào file output.txt");

        } catch (Exception e) {
            // Xử lý lỗi ghi log mà không làm dừng thuật toán
            System.err.println("Lỗi ghi log generation " + generation + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
