package model;

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

        int noImprovementCounter = 0;
        Individual globalBest = population.getBest();
        for (int gen = 0; gen < maxGenerations; gen++) {
            List<Individual> newIndividuals = new ArrayList<>();

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
                    offspring = new Individual(graph);
                    offspring.getGenes().or(parent1.getGenes());
                    offspring.calculateFitness();
                }

                offspring.mutate(mutationRate);
                offspring.calculateFitness();
                newIndividuals.add(offspring);
            }
            // thay quần thể & sắp xếp
            population.getIndividuals().clear();
            population.getIndividuals().addAll(newIndividuals);
            population.sortByFitness();

            // tìm best hiện tại
            Individual currentBest = population.getBest();
            // nếu có cải thiện so với global best
            if (currentBest.getFitness() > globalBest.getFitness()) {
                globalBest = currentBest;
                noImprovementCounter = 0;
            }else{
                noImprovementCounter++;
            }

            // tính diversity (tỷ lệ cá thể khác nhau)
            double diversity = computeDiversity(population);

            // In ra quần thể và best fitness của thế hệ
            System.out.println("=== Generation " + gen + " ===");
            printPopulation(population);
            System.out.println("Best Fitness so far: " + globalBest.getFitness());
            System.out.printf("Diversity: %.3f, No improvement: %d generations\n", diversity, noImprovementCounter);
            System.out.println("--------------------------------------");
            // Điều kiện dừng sớm
            if (patience > 0 && noImprovementCounter >= patience) break;
            if (diversityThreshold > 0 && diversity < diversityThreshold) break;
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
}
