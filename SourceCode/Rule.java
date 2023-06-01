package SourceCode;

public class Rule {
    private char predecessor;
    private String successor;
    
    public Rule(char pre, String succ){
        predecessor = pre;
        successor = succ;
    }

    public char getPredecessor(){
        return predecessor;
    }

    public String getSuccessor(){
        return successor;
    }

    public void setPredecessor(char pre){
        predecessor = pre;
    }

    public void setSuccessor(String succ){
        successor = succ;
    }
}


