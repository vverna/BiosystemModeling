package SourceCode;

public class Organism {
    private double fitness;    
    private LSystem chromasome; 
    private int iteration;

    public Organism(LSystem chrom) {
        chromasome = chrom;
        fitness = 0;
        iteration=1;
    }

    public void setFitness(double fit){
        fitness=fit;
    }
    public void setIteration(int n){
        iteration = n;
    }
    public void setChromasome(LSystem chrom){
        chromasome = chrom;
    }
    public double getFitness(){
        return fitness;
    }
    public int getIteration(){
        return iteration;
    }
    public LSystem getChromasome(){
        return chromasome;
    }

    public void printChromasome(){
        chromasome.printLSystem();
    }

    public void mutation(){
        try{
            chromasome.mutate();
        } catch(Exception e){
            // Empty
        }
    }

    public void saveChromasomeToFile(String file){
        System.out.println("Iteration: " + iteration);
        chromasome.saveToFile(file);
    }

    public void setStartPoint(double[] coords){
        chromasome.setStartPoint(coords);
    }

    public static void main(String[] args) {
    }

}