package SourceCode;

public class StochasticRule extends Rule {
    private double probability;

    public StochasticRule(char pre, String succ, double prob) {
        super(pre, succ);
        probability = prob;
    }

    public double getProbability(){
        return probability;
    }

    public void setProbability(double prob){
        probability = prob;
    }

}
