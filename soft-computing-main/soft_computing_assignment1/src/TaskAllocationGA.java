import java.io.*; // Import classes for input and output operations
import java.util.*; // Import utility classes such as List, Scanner, Random, etc.

// Main class to perform task allocation using Genetic Algorithm
public class TaskAllocationGA {
    // Define constants for the genetic algorithm
    private static final int POPULATION_SIZE = 100; // Number of chromosomes in the population
    private static final int MAX_GENERATIONS = 1000; // Maximum number of generations for GA
    private static final double CROSSOVER_RATE = 0.8; // Probability of crossover
    private static final double MUTATION_RATE = 0.05; // Probability of mutation
    private static final double PENALTY = Double.MAX_VALUE; // High penalty for invalid solutions

    // Main method to execute the program
    public static void main(String[] args) throws IOException {
        // Parse the input file to create test cases
        List<TestCase> testCases = parseInputFile("input.txt");

        // Iterate over each test case
        for (int i = 0; i < testCases.size(); i++) {
            // Get the current test case
            TestCase testCase = testCases.get(i);
            // Run the genetic algorithm on the current test case
            GAResult result = runGA(testCase);
            // Print the test case number and the result
            System.out.println("Test Case " + (i + 1) + ":");
            System.out.println(result);
        }
    }

    // Method to run the Genetic Algorithm on a test case
    private static GAResult runGA(TestCase testCase) {
        // Set global task execution times for this test case
        TaskData.setTaskExecutionTimes(testCase.taskTimes);

        // Initialize the population of chromosomes
        Population population = new Population(POPULATION_SIZE, testCase.numTasks, testCase.maxTimeLimit);
        Chromosome bestChromosome = null; // Track the best chromosome found so far

        // Loop through each generation up to MAX_GENERATIONS
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate the fitness of each chromosome in the population
            for (Chromosome chromosome : population.chromosomes) {
                evaluateFitness(chromosome, testCase);
            }

            // Find the best chromosome in the current population
            Chromosome currentBest = population.getBestChromosome();
            // Update the bestChromosome if currentBest is better
            if (bestChromosome == null || currentBest.fitness < bestChromosome.fitness) {
                bestChromosome = currentBest;
            }

            // Create a new population for the next generation
            Population newPopulation = new Population(POPULATION_SIZE, testCase.numTasks, testCase.maxTimeLimit);

            // Generate new chromosomes using crossover and mutation until the population is full
            while (newPopulation.size() < POPULATION_SIZE) {
                // Select two parent chromosomes for crossover
                Chromosome parent1 = population.rouletteWheelSelection();
                Chromosome parent2 = population.rouletteWheelSelection();

                // Create two offspring chromosomes
                Chromosome offspring1 = new Chromosome(testCase.numTasks, testCase.maxTimeLimit);
                Chromosome offspring2 = new Chromosome(testCase.numTasks, testCase.maxTimeLimit);

                // Perform crossover based on the crossover rate
                if (Math.random() < CROSSOVER_RATE) {
                    // Choose a random crossover point between 1 and numTasks - 1
                    int crossoverPoint = new Random().nextInt(testCase.numTasks - 1) + 1;
                    // Perform one-point crossover on parents to create offspring
                    onePointCrossover(parent1, parent2, offspring1, offspring2, crossoverPoint);
                } else {
                    // If no crossover, offspring are exact copies of parents
                    offspring1 = parent1;
                    offspring2 = parent2;
                }

                // Apply mutation to both offspring chromosomes
                mutate(offspring1);
                mutate(offspring2);

                // Add offspring to the new population
                newPopulation.addChromosome(offspring1);
                newPopulation.addChromosome(offspring2);
            }

            // Ensure the best chromosome survives into the next generation
            newPopulation.addChromosome(bestChromosome);
            // Replace the old population with the new one
            population = newPopulation;
        }

        // Return the result with the best chromosome found and test case data
        return new GAResult(bestChromosome, testCase);
    }

    // Method to evaluate fitness of a chromosome based on task allocation
    private static void evaluateFitness(Chromosome chromosome, TestCase testCase) {
        // Initialize total time for core 1 and core 2
        int core1Time = 0;
        int core2Time = 0;

        // Iterate over each gene in the chromosome to assign tasks to cores
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            if (chromosome.getGenes()[i] == 1) {
                // Add task time to core1 if gene value is 1
                core1Time += testCase.taskTimes[i];
            } else {
                // Add task time to core2 if gene value is 0
                core2Time += testCase.taskTimes[i];
            }
        }

        // Set fitness to max core time if within limits, otherwise assign penalty
        if (core1Time <= testCase.maxTimeLimit && core2Time <= testCase.maxTimeLimit) {
            chromosome.fitness = Math.max(core1Time, core2Time);
        } else {
            chromosome.fitness = Integer.MAX_VALUE;
        }
    }

    // Method to perform one-point crossover between two parents
    private static void onePointCrossover(Chromosome parent1, Chromosome parent2, Chromosome offspring1, Chromosome offspring2, double Pc) {
        // Create a Random object for randomness
        Random rand = new Random();

        // Generate a random number for crossover decision
        double rc = rand.nextDouble();
        // Select a random crossover point between 1 and length - 1
        int Xc = rand.nextInt(parent1.getGenes().length - 1) + 1;

        // If crossover probability is met, perform crossover at Xc
        if (rc <= Pc) {
            // Copy genes from parents to offspring based on crossover point
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
            // If no crossover, offspring are copies of the parents
            for (int i = 0; i < parent1.getGenes().length; i++) {
                offspring1.getGenes()[i] = parent1.getGenes()[i];
                offspring2.getGenes()[i] = parent2.getGenes()[i];
            }
        }
    }

    // Method to apply mutation on a chromosome
    private static void mutate(Chromosome chromosome) {
        // Iterate over each gene in the chromosome
        for (int i = 0; i < chromosome.getGenes().length; i++) {
            // Flip the gene based on mutation probability
            if (Math.random() < MUTATION_RATE) {
                chromosome.getGenes()[i] = 1 - chromosome.getGenes()[i];
            }
        }
    }

    // Method to parse input file and create test cases
    private static List<TestCase> parseInputFile(String filename) throws IOException {
        // Initialize list to hold test cases
        List<TestCase> testCases = new ArrayList<>();
        // Create a file object for the input file
        File file = new File(filename);
        // Create a scanner to read the file
        Scanner scanner = new Scanner(file);

        // First line in the file is the number of test cases
        String noOfTestCasesString = scanner.nextLine();
        int noOfTestCases = Integer.valueOf(noOfTestCasesString);

        // Loop to read each test case
        while (scanner.hasNextLine()) {
            // Read max time limit for cores
            String maxTimeLimitString = scanner.nextLine();
            int maxTimeLimit = Integer.parseInt(maxTimeLimitString);

            // Read number of tasks
            String noOfTasksString = scanner.nextLine();
            int noOfTasks = Integer.parseInt(noOfTasksString);

            // Initialize array for task execution times
            int[] taskTimes = new int[noOfTasks];
            // Read each task execution time
            for (int i = 0; i < noOfTasks; i++) {
                String taskExecTimeString = scanner.nextLine();
                int taskExecTime = Integer.parseInt(taskExecTimeString);
                taskTimes[i] = taskExecTime;
            }

            // Add new test case to the list
            testCases.add(new TestCase(noOfTasks, maxTimeLimit, taskTimes));
        }

        // Return the list of test cases
        return testCases;
    }
}
