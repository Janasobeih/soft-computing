import java.io.*;
import java.util.*;

public class TaskAllocationGA {
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 1000;
    private static final double CROSSOVER_RATE = 0.8;
    private static final double MUTATION_RATE = 0.05;
    private static final double PENALTY = Double.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        List<TestCase> testCases = parseInputFile("input.txt");

        for (int i = 0; i < testCases.size(); i++) {
            TestCase testCase = testCases.get(i);
            GAResult result = runGA(testCase);
            System.out.println("Test Case " + (i + 1) + ":");
            System.out.println(result);
        }
    }

    private static GAResult runGA(TestCase testCase) {
        // Initialize TaskData with task times for the current test case
        TaskData.setTaskExecutionTimes(testCase.taskTimes); // This ensures TaskData is populated

        Population population = new Population(POPULATION_SIZE, testCase.numTasks, testCase.maxTimeLimit);
        Chromosome bestChromosome = null;

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            for (Chromosome chromosome : population.chromosomes) {
                evaluateFitness(chromosome, testCase);
            }

            Chromosome currentBest = population.getBestChromosome();
            if (bestChromosome == null || currentBest.fitness < bestChromosome.fitness) {
                bestChromosome = currentBest;
            }

            Population newPopulation = new Population(POPULATION_SIZE, testCase.numTasks, testCase.maxTimeLimit);

            while (newPopulation.size() < POPULATION_SIZE) {
                Chromosome parent1 = population.rouletteWheelSelection();
                Chromosome parent2 = population.rouletteWheelSelection();
                Chromosome offspring1 = new Chromosome(testCase.numTasks, testCase.maxTimeLimit);
                Chromosome offspring2 = new Chromosome(testCase.numTasks, testCase.maxTimeLimit);
                if (Math.random() < CROSSOVER_RATE) {
                    int crossoverPoint = new Random().nextInt(testCase.numTasks - 1) + 1;
                    onePointCrossover(parent1, parent2, offspring1, offspring2, crossoverPoint);
                } else {
                    offspring1 = parent1;
                    offspring2 = parent2;
                }

                mutate(offspring1);
                mutate(offspring2);

                newPopulation.addChromosome(offspring1);
                newPopulation.addChromosome(offspring2);
            }

            newPopulation.addChromosome(bestChromosome);
            population = newPopulation;
        }

        return new GAResult(bestChromosome, testCase);
    }

    private static void evaluateFitness(Chromosome chromosome, TestCase testCase) {
        int core1Time = 0;
        int core2Time = 0;

        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (chromosome.getGenes()[i] == 1) {
                core1Time += testCase.taskTimes[i];
            } else {
                core2Time += testCase.taskTimes[i];
            }
        }

        if (core1Time <= testCase.maxTimeLimit && core2Time <= testCase.maxTimeLimit) {
            chromosome.fitness = Math.max(core1Time, core2Time);
        } else {
            chromosome.fitness = Integer.MAX_VALUE;
        }
    }

    private static void onePointCrossover(Chromosome parent1, Chromosome parent2, Chromosome offspring1, Chromosome offspring2, double Pc) {
        Random rand = new Random();

        double rc = rand.nextDouble();
        int Xc = rand.nextInt(parent1.getGenes().length - 1) + 1;

        if (rc <= Pc) {
            for (int i = 0; i < parent1.getGenes().length; i++) {
                if (i < Xc) {
                    offspring1.getGenes()[i] = parent1.getGenes()[i];
                    offspring2.getGenes()[i] = parent2.getGenes()[i];
                } else {
                    offspring1.getGenes()[i] = parent2.getGenes()[i];
                    offspring2.getGenes()[i] = parent1.getGenes()[i];
                }
            }
        } else {
            for (int i = 0; i < parent1.getGenes().length; i++) {
                offspring1.getGenes()[i] = parent1.getGenes()[i];
                offspring2.getGenes()[i] = parent2.getGenes()[i];
            }
        }
    }


    private static void mutate(Chromosome chromosome) {
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (Math.random() < MUTATION_RATE) {
                chromosome.getGenes()[i] = 1 - chromosome.getGenes()[i];
            }
        }
    }

    private static List<TestCase> parseInputFile(String filename) throws IOException {
        List<TestCase> testCases = new ArrayList<>();
        File file = new File("input.txt");
        Scanner scanner = new Scanner(file);
        String noOfTestCasesString = scanner.nextLine();
        int noOfTestCases = Integer.valueOf(noOfTestCasesString);
        while (scanner.hasNextLine()) {
            String maxTimeLimitString = scanner.nextLine();
            int maxTimeLimit = Integer.parseInt(maxTimeLimitString);
            String noOfTasksString = scanner.nextLine();
            int noOfTasks = Integer.parseInt(noOfTasksString);
            int[] taskTimes = new int[noOfTasks];
            for (int i = 0; i < noOfTasks; i++) {
                String taskExecTimeString = scanner.nextLine();
                int taskExecTime = Integer.parseInt(taskExecTimeString);
                taskTimes[i] = taskExecTime;
            }
            testCases.add(new TestCase(noOfTasks, maxTimeLimit, taskTimes));
        }

        return testCases;
    }
}
