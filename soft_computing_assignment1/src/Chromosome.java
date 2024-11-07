import java.util.Random;

public class Chromosome {
    private int[] genes;
    private int maxTimeLimit;
    public int fitness;

    public Chromosome(int numTasks, int maxTimeLimit) {
        this.genes = new int[numTasks];
        this.maxTimeLimit = maxTimeLimit;
        Random rand = new Random();
        for (int i = 0; i < numTasks; i++) {
            genes[i] = rand.nextInt(2);
        }
        evaluateFitness();
    }

    public Chromosome(int[] genes, int maxTimeLimit) {
        this.genes = genes;
        this.maxTimeLimit = maxTimeLimit;
        evaluateFitness();
    }

    public void evaluateFitness() {
        int core1Time = 0;
        int core2Time = 0;

        for (int i = 0; i < genes.length; i++) {
            if (genes[i] == 1) {
                core1Time += TaskData.getTaskExecutionTime(i);
            } else {
                core2Time += TaskData.getTaskExecutionTime(i);
            }
        }

        if (core1Time > maxTimeLimit || core2Time > maxTimeLimit) {
            this.fitness = Integer.MAX_VALUE;
        } else {
            this.fitness = Math.max(core1Time, core2Time);
        }
    }

    public void mutate() {
        Random rand = new Random();
        int mutationPoint = rand.nextInt(genes.length);
        genes[mutationPoint] = (genes[mutationPoint] == 0) ? 1 : 0;
        evaluateFitness();
    }

    public Chromosome crossover(Chromosome other) {
        Random rand = new Random();
        int crossoverPoint = rand.nextInt(genes.length);
        int[] newGenes = new int[genes.length];
        System.arraycopy(this.genes, 0, newGenes, 0, crossoverPoint);
        System.arraycopy(other.genes, crossoverPoint, newGenes, crossoverPoint, genes.length - crossoverPoint);
        return new Chromosome(newGenes, this.maxTimeLimit);
    }

    public int[] getGenes() {
        return genes;
    }

    public int getFitness() {
        return fitness;
    }

    public void printChromosome() {
        System.out.print("Chromosome: ");
        for (int gene : genes) {
            System.out.print(gene + " ");
        }
        System.out.println("\nFitness: " + fitness);
    }
}