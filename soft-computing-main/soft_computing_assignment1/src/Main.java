import java.io.*; // Import classes for file handling
import java.util.*; // Import utility classes such as ArrayList, Arrays, and Random

// TaskData class to manage task execution times
class TaskData {
    private static int[] taskExecutionTimes; // Static array to store task execution times

    // Method to set task execution times
    public static void setTaskExecutionTimes(int[] times) {
        taskExecutionTimes = times;
    }

    // Method to retrieve the execution time of a specific task
    public static int getTaskExecutionTime(int index) {
        return taskExecutionTimes[index];
    }
}

// TestCase class to store details for each test case
class TestCase {
    public int numTasks; // Number of tasks
    public int maxTimeLimit; // Maximum time allowed for each core
    public int[] taskTimes; // Array to store execution time for each task

    // Constructor to initialize a test case with its parameters
    public TestCase(int numTasks, int maxTimeLimit, int[] taskTimes) {
        this.numTasks = numTasks; // Assign number of tasks
        this.maxTimeLimit = maxTimeLimit; // Assign maximum time limit
        this.taskTimes = taskTimes; // Assign task execution times
    }
}

// GAResult class to store and format the results of the Genetic Algorithm
class GAResult {
    private Chromosome bestChromosome; // Best solution (chromosome) found by the GA
    private TestCase testCase; // Test case details related to this result

    // Constructor to initialize GAResult with the best chromosome and test case
    public GAResult(Chromosome bestChromosome, TestCase testCase) {
        this.bestChromosome = bestChromosome;
        this.testCase = testCase;
    }

    // Override toString method to format the result output
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // Add the fitness score of the best solution
        result.append("Best fitness: ").append(bestChromosome.getFitness()).append("\n");

        // Add the chromosome representation (task allocation to cores)
        result.append("Chromosome: ");
        for (int gene : bestChromosome.getGenes()) {
            result.append(gene).append(" ");
        }
        result.append("\n");

        // Display tasks assigned to core 1 and their total time
        int core1Time = 0, core2Time = 0;
        result.append("Core 1 tasks: ");
        for (int i = 0; i < bestChromosome.getGenes().length; i++) {
            if (bestChromosome.getGenes()[i] == 1) { // If gene is 1, task is assigned to core 1
                result.append(i).append(" ");
                core1Time += testCase.taskTimes[i];
            }
        }
        result.append("\nCore 1 total time: ").append(core1Time).append("\n");

        // Display tasks assigned to core 2 and their total time
        result.append("Core 2 tasks: ");
        for (int i = 0; i < bestChromosome.getGenes().length; i++) {
            if (bestChromosome.getGenes()[i] == 0) { // If gene is 0, task is assigned to core 2
                result.append(i).append(" ");
                core2Time += testCase.taskTimes[i];
            }
        }
        result.append("\nCore 2 total time: ").append(core2Time).append("\n");

        return result.toString(); // Return the formatted result as a string
    }
}

// Population class to represent a collection of chromosomes in the Genetic Algorithm
class Population {
    public Chromosome[] chromosomes; // Array of chromosomes in the population

    // Constructor to initialize the population with a given size and task parameters
    public Population(int populationSize, int numTasks, int maxTimeLimit) {
        chromosomes = new Chromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            chromosomes[i] = new Chromosome(numTasks, maxTimeLimit); // Create new chromosome for each slot
        }
    }

    // Method to get the chromosome with the best fitness score
    public Chromosome getBestChromosome() {
        Chromosome best = chromosomes[0]; // Assume first chromosome is the best
        for (Chromosome c : chromosomes) {
            if (c.getFitness() < best.getFitness()) { // Find chromosome with the lowest fitness score
                best = c;
            }
        }
        return best;
    }

    // Method for selecting a chromosome using roulette wheel selection
    public Chromosome rouletteWheelSelection() {
        int totalFitness = Arrays.stream(chromosomes).mapToInt(Chromosome::getFitness).sum(); // Calculate total fitness
        int randomPoint = new Random().nextInt(totalFitness); // Randomly pick a selection point
        int runningSum = 0;

        // Traverse chromosomes to find the selected chromosome based on the random point
        for (Chromosome c : chromosomes) {
            runningSum += c.getFitness();
            if (runningSum >= randomPoint) { // Select chromosome when running sum exceeds random point
                return c;
            }
        }
        return chromosomes[chromosomes.length - 1]; // Return last chromosome as fallback
    }

    // Method to add a new chromosome to the population
    public void addChromosome(Chromosome chromosome) {
        Chromosome[] newChromosomes = Arrays.copyOf(chromosomes, chromosomes.length + 1); // Increase array size by 1
        newChromosomes[newChromosomes.length - 1] = chromosome; // Add the new chromosome at the end
        chromosomes = newChromosomes; // Replace old array with the new array
    }

    // Method to get the current size of the population
    public int size() {
        return chromosomes.length;
    }
}
