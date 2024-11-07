import java.io.*;
import java.util.*;



class TaskData {
    private static int[] taskExecutionTimes;

    public static void setTaskExecutionTimes(int[] times) {
        taskExecutionTimes = times;
    }

    public static int getTaskExecutionTime(int index) {
        return taskExecutionTimes[index];
    }
}

class TestCase {
    public int numTasks;
    public int maxTimeLimit;
    public int[] taskTimes;

    public TestCase(int numTasks, int maxTimeLimit, int[] taskTimes) {
        this.numTasks = numTasks;
        this.maxTimeLimit = maxTimeLimit;
        this.taskTimes = taskTimes;
    }
}

class GAResult {
    private Chromosome bestChromosome;
    private TestCase testCase;

    public GAResult(Chromosome bestChromosome, TestCase testCase) {
        this.bestChromosome = bestChromosome;
        this.testCase = testCase;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // Best solution (fitness score)
        result.append("Best fitness: ").append(bestChromosome.getFitness()).append("\n");

        // Chromosome representation
        result.append("Chromosome: ");
        for (int gene : bestChromosome.getGenes()) {
            result.append(gene).append(" ");
        }
        result.append("\n");

        // Task assignments to each core and their total time
        int core1Time = 0, core2Time = 0;
        result.append("Core 1 tasks: ");
        for (int i = 0; i < bestChromosome.getGenes().length; i++) {
            if (bestChromosome.getGenes()[i] == 1) {
                result.append(i).append(" ");
                core1Time += testCase.taskTimes[i];
            }
        }
        result.append("\nCore 1 total time: ").append(core1Time).append("\n");

        result.append("Core 2 tasks: ");
        for (int i = 0; i < bestChromosome.getGenes().length; i++) {
            if (bestChromosome.getGenes()[i] == 0) {
                result.append(i).append(" ");
                core2Time += testCase.taskTimes[i];
            }
        }
        result.append("\nCore 2 total time: ").append(core2Time).append("\n");

        return result.toString();
    }
}


class Population {
    public Chromosome[] chromosomes;

    public Population(int populationSize, int numTasks, int maxTimeLimit) {
        chromosomes = new Chromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            chromosomes[i] = new Chromosome(numTasks, maxTimeLimit);
        }
    }

    public Chromosome getBestChromosome() {
        Chromosome best = chromosomes[0];
        for (Chromosome c : chromosomes) {
            if (c.getFitness() < best.getFitness()) {
                best = c;
            }
        }
        return best;
    }

    public Chromosome rouletteWheelSelection() {
        int totalFitness = Arrays.stream(chromosomes).mapToInt(Chromosome::getFitness).sum();
        int randomPoint = new Random().nextInt(totalFitness);
        int runningSum = 0;
        for (Chromosome c : chromosomes) {
            runningSum += c.getFitness();
            if (runningSum >= randomPoint) {
                return c;
            }
        }
        return chromosomes[chromosomes.length - 1];
    }

    public void addChromosome(Chromosome chromosome) {
        Chromosome[] newChromosomes = Arrays.copyOf(chromosomes, chromosomes.length + 1);
        newChromosomes[newChromosomes.length - 1] = chromosome;
        chromosomes = newChromosomes;
    }

    public int size() {
        return chromosomes.length;
    }
}
