import java.util.Random; // Import Random class to generate random numbers

// Chromosome class represents a possible solution in the Genetic Algorithm
public class Chromosome {
    private int[] genes; // Array to store task allocations (genes)
    private int maxTimeLimit; // Maximum allowed time for each core
    public int fitness; // Fitness value of this chromosome

    // Constructor to initialize a chromosome with random genes
    public Chromosome(int numTasks, int maxTimeLimit) {
        this.genes = new int[numTasks]; // Initialize genes array for tasks
        this.maxTimeLimit = maxTimeLimit; // Set maximum time limit for cores
        Random rand = new Random(); // Create Random object for random numbers

        // Randomly assign each task to core 1 or core 2
        for (int i = 0; i < numTasks; i++) {
            genes[i] = rand.nextInt(2); // Each gene is randomly set to 0 or 1
        }

        // Calculate initial fitness based on random allocation
        evaluateFitness();
    }

    // Method to calculate and set the fitness of the chromosome
    public void evaluateFitness() {
        int core1Time = 0; // Initialize total time for core 1
        int core2Time = 0; // Initialize total time for core 2

        // Iterate through genes to determine task allocation
        for (int i = 0; i < genes.length; i++) {
            // If gene is 1, allocate task to core 1
            if (genes[i] == 1) {
                core1Time += TaskData.getTaskExecutionTime(i);
            } else {
                // If gene is 0, allocate task to core 2
                core2Time += TaskData.getTaskExecutionTime(i);
            }
        }

        // If either core exceeds time limit, set high fitness as penalty
        if (core1Time > maxTimeLimit || core2Time > maxTimeLimit) {
            this.fitness = Integer.MAX_VALUE;
        } else {
            // Otherwise, set fitness as the max time between two cores
            this.fitness = Math.max(core1Time, core2Time);
        }
    }

    // Getter method to retrieve genes of the chromosome
    public int[] getGenes() {
        return genes;
    }

    // Getter method to retrieve the fitness of the chromosome
    public int getFitness() {
        return fitness;
    }
}
