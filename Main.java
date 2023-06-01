package SourceCode;

import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;

public class Main {
    BufferedImage origImage;
    int populationSize;
    char[] genai;
    Organism[] population;
    int maxIteration = 10;
    double mutationProb = 0.1;
    int maxN=5;
    int lSystemType; // 0 - deterministic, 1 -stochastic
    double[] startPoint;
    String outputFile;

    public Main(String file, int type, int popSize, String output) {
        lSystemType = type;
        outputFile = output;
        populationSize = popSize;
        try {
            origImage = ImageIO.read(new File(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        population = new Organism[populationSize];

    }
    public double[] calcStartPoint(){
        int white = Color.WHITE.getRGB();
        for (int y = origImage.getHeight()-1; y>=0; y--) {
            for (int x = 0; x < origImage.getWidth(); x++) {
                if (origImage.getRGB(x, y) != white){
                    return  new double[] {(double) x, (double) y};
                }
            }
        }
        return new double[] {origImage.getWidth()/2, origImage.getHeight()};
        
    }

    public void createPopulation(){
        startPoint = calcStartPoint();
        LSystem chrom;
        for(int i = 0; i<populationSize; i++){
            if(lSystemType==0){
                chrom = generateIndivid();
            }else{
                chrom = generateIndividStochastic();
            } 
            Organism tmp = new Organism(chrom);
            population[i] = calcFitness(tmp);
        }
    }

    public LSystem generateIndivid(){
        String axiom = generateAxiom();
        LSystem individ = new LSystem(axiom, lSystemType);
        individ.addRule('F', generateSuccessor());
        individ.addRule('L', generateSuccessor());
        return individ;
    }

    public LSystem generateIndividStochastic(){
        String axiom = generateAxiom();
        LSystem individ = new LSystem(axiom, lSystemType);
        double prob=1;
        while (prob>0){
            double rand = Math.random();
            rand = (rand > prob) ? prob : rand;
            individ.addStochasticRule('F', generateSuccessor(), rand);
            prob-=rand;
        }
        prob=1;
        while (prob>0){
            double rand = Math.random();
            rand = (rand > prob) ? prob : rand;
            individ.addStochasticRule('L', generateSuccessor(), rand);
            prob-=rand;
        }
        return individ;
    }

    public String generateAxiom(){
        String axiom = (Math.random()>0.5) ? "L" : "F";
        // String[] symbols = {"F","L","+","-", ""};
        // for(int i=1; i<3; i++){
        //     double rand = Math.random();
        //     if(rand<=0.3){
        //         axiom+=symbols[0];
        //     } else if (rand<=0.6){
        //         axiom+=symbols[1];
        //     } else if (rand<=0.7){
        //         axiom+=symbols[2];
        //     }else if (rand<=0.8){
        //         axiom+=symbols[3];
        //     }else{
        //         axiom+=symbols[4];
        //     }            
        // }
        // axiom = axiom.replace("-+", "");
        // axiom = axiom.replace("+-", "");
        return axiom;
    }

    public String generateSuccessor(){
        String succ =(Math.random()>0.5) ? "L" : "F";
        String[] symbols = {"F","L","+","-", "", "[","]"};
        int brakedCount=0;
        for(int i=1; i<8; i++){
            double rand = Math.random();
            if(rand<=0.25){
                succ+=symbols[0];
            } else if (rand<=0.5){
                succ+=symbols[1];
            } else if (rand<=0.6){
                succ+=symbols[2];
            }else if (rand<=0.70){
                succ+=symbols[3];
            }else if (rand<=0.8){
                succ+=symbols[4];
            }else if (brakedCount%2==0){
                succ+=symbols[5];
                brakedCount++;
                succ+= (Math.random()>0.5) ? "-" : "+";
                succ+= (Math.random()>0.5) ? "F" : "L";
                i+=2;
            }else{
                succ+=symbols[6];
                brakedCount++;
            }
        }
        if (brakedCount%2==1){
            succ+="]";
        }
        succ = succ.replace("-]", "]");
        succ = succ.replace("+]", "]");
        succ = succ.replace("-+", "");
        succ = succ.replace("+-", "");
        succ = succ.replace("[]", "");
        return succ;
    }

    public Organism calcFitness(Organism org){
        org.setStartPoint(startPoint);
        double maxFitness=0;
        int maxFitnessN=0;
        LSystem chrom = org.getChromasome();
        BufferedImage tmp = new BufferedImage(origImage.getWidth(), origImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        LSystem bestL = chrom;
        for(int i=1; i<=maxN; i++){
            chrom.setBufferedImage(tmp);
            chrom.doIteration();
            BufferedImage tmp2 = chrom.getBufferedImage();
            double fitness = ComparePictures.compareImages(origImage, tmp2);
            if (fitness>maxFitness){
                bestL = chrom;
                maxFitness=fitness;
                maxFitnessN=i;
            }
        }
        // chrom.clear();
        org.setChromasome(bestL);
        org.setFitness(maxFitness);
        org.setIteration(maxFitnessN);
        return org; 
    }

    public Organism selection(){
        return population[new Random().nextInt(populationSize/2)];
    }

    public Organism[] crossingover(Organism ind1, Organism ind2){
        LSystem chrom1 = ind1.getChromasome();
        LSystem chrom2 = ind2.getChromasome();
        LSystem newChrom1 = new LSystem(chrom1.getAxiom(), lSystemType);
        LSystem newChrom2 = new LSystem(chrom2.getAxiom(), lSystemType);
        if (lSystemType == 0){
            newChrom1.addRule(chrom2.getRule(0));
            newChrom1.addRule(chrom1.getRule(1));
            newChrom2.addRule(chrom1.getRule(0));
            newChrom2.addRule(chrom2.getRule(1));
        } else {
            double prob=1;
            int index=0;
            while (prob>0 && chrom1.getRuleCount()>index){
                double rand = Math.random();
                rand = (rand > prob) ? prob : rand;
                StochasticRule newRule = chrom1.getRuleStoch(index);
                newRule.setProbability(rand);
                if(index%2==0){
                    newChrom1.addStochasticRule(newRule);
                }else{
                    newChrom2.addStochasticRule(newRule);
                }
                prob-=rand;
                index++;
            }
            prob=1;
            index=0;
            while (prob>0 && chrom2.getRuleCount()>index){
                double rand = Math.random();
                rand = (rand > prob) ? prob : rand;
                StochasticRule newRule = chrom2.getRuleStoch(index);
                newRule.setProbability(rand);
                if(index%2==0){
                    newChrom1.addStochasticRule(newRule);
                }else{
                    newChrom2.addStochasticRule(newRule);
                }
                prob-=rand;
                index++;
            }
        }
        Organism[] newInd = new Organism[2];
        newInd[0] = new Organism(newChrom1);
        newInd[1] = new Organism(newChrom2);
        return newInd;
    }

    public void orderPopulation(){
        Arrays.sort(population, (a,b) -> Double.compare(b.getFitness(),a.getFitness()));
    }

    public void saveParameters(Organism org){
        try {
            LSystem chrom = org.getChromasome();
            FileWriter fileWriter = new FileWriter(outputFile.replace(".png", ".txt"));
            fileWriter.write("Axiom: " + chrom.getAxiom());
            if(lSystemType==0){
                fileWriter.write("\nF->" + chrom.getSuccessor('F') + " L->" + chrom.getSuccessor('L'));
            } else {
                for(int i=0; i<chrom.getRuleCount(); i++){
                    fileWriter.write("\n"+chrom.getRuleStoch(i).getPredecessor()+ "->" + chrom.getRuleStoch(i).getSuccessor() +" : " + chrom.getRuleStoch(i).getProbability());
                }
            }
            fileWriter.write("\nIteration: " + org.getIteration());
            fileWriter.write("\nFitness: " + org.getFitness());
            fileWriter.write("\nPopulation size: " + populationSize);
            fileWriter.write("\nGeneration count: " + maxIteration);
            fileWriter.write("\nMutation probability: " + mutationProb);
            fileWriter.write("\nL-system type: " + lSystemType);
            fileWriter.write("\nSequence: " + chrom.getSequence());
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        } 

    }

    public void runSimulation(){
        
        createPopulation();
        for(int i=0; i<maxIteration; i++){
            orderPopulation();
            System.out.printf("Generation: %d, Fitness: %.4f%n", i, population[0].getFitness());
            // System.out.println("Generation: " + i + " Fitness: " + population[0].getFitness());
            Organism[] newPop = new Organism[populationSize];
            int split = (int) (populationSize*0.1);
            split = split%2==0 ? split : split+1;
            for (int j=0; j<split; j++){
                newPop[j] = population[j];
                if (Math.random()<mutationProb)
                    newPop[j].mutation();
            }
            for (int j=split; j<populationSize; j+=2){
                Organism[] ind = new Organism[2];
                ind[0] = selection();
                ind[1] = selection();
                ind = crossingover(ind[0], ind[1]);
                if (Math.random()<mutationProb)
                    ind[0].mutation();
                if (Math.random()<mutationProb)
                    ind[1].mutation();    
                newPop[j] = calcFitness(ind[0]);
                newPop[j+1] = calcFitness(ind[1]);
            }
            population=newPop;
        }
        orderPopulation();  
        System.out.printf("Generation: %d, Fitness: %.4f%n", maxIteration, population[0].getFitness());      
        population[0].saveChromasomeToFile(outputFile);
        saveParameters(population[0]);
    } 


    public static void main(String[] args) {
        if (args.length == 4){
            Main app = new Main(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
            app.runSimulation();
        } else {
            System.out.println("Invalid input. Please try again.");
        }
      }


}
