package SourceCode;

import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.awt.BasicStroke;
import javax.imageio.ImageIO;

public class LSystem {
    private String axiom;
    private List<Rule> rules;
    private List<StochasticRule> rulesStoch;
    private List<ParametricRule> rulesParam;
    private List<Character> sequence;
    private HashMap<Integer, Double> widths;
    private BufferedImage bufferedImage;
    private double delta = 35;
    private double[] startPoint ={0,0};
    private int[] startPosition = {0,0};
    private double initLength = 500;
    private double lenghtCoof = 0.5;
    private double initWidth = 10.0;
    private double widthCoof = 1.2;
    private int type;
    private int iteration;

    public LSystem(String a, int t){
        axiom = a;
        type = t; // 0 - deterministic, 1 - stochastic, 2 - parametric;
        iteration = 0;
        sequence = new ArrayList<Character>();
        widths = new HashMap<Integer, Double>();
        switch (t){
            case 0:
                rules= new ArrayList<Rule>();
                break;
            case 1:
                rulesStoch = new ArrayList<StochasticRule>();
                break;
            case 2:
                rulesParam = new ArrayList<ParametricRule>();
                break;
            default:
                rules= new ArrayList<Rule>();
                break;
        }
        char[] axiomArray = axiom.toCharArray();
        int index=0;
        for (char c : axiomArray){
            sequence.add(c);
            if(c=='F' || c=='L')
               widths.put(index, initWidth);
            index++;
        }
    }

    public void setBufferedImage(BufferedImage buffImage){
        Graphics2D graphics = buffImage.createGraphics();
        graphics.setPaint(Color.white);
        graphics.fillRect (0, 0, buffImage.getWidth(), buffImage.getHeight());
        graphics.dispose();
        bufferedImage=buffImage;
        // startPosition[0] = bufferedImage.getWidth()/2;
        // startPosition[1] = bufferedImage.getHeight();
    }

    public BufferedImage getBufferedImage(){
        drawLSystem();
        return bufferedImage;
    }

    public int getType(){
        return type;
    }

    public String getSequence(){
        String joinedSeq = "";
        for (Character element : sequence)
            joinedSeq+=element;
        return joinedSeq;
    }

    public void setSequence(String seq){
        sequence = new ArrayList<Character>();
        char[] seqArray = seq.toCharArray();
        for (char c : seqArray){
            sequence.add(c);
        }
    }

    public int getRuleCount(){
        if (type == 0){
            return rules.size();
        } else {
            return rulesStoch.size();
        }
    }

    public Rule getRule(int i){
        return rules.get(i);
    }

    public StochasticRule getRuleStoch(int i){
        return rulesStoch.get(i);
    }

    public String getAxiom(){
        return axiom;
    }

    public void addRule(char pre, String succ){
        Rule rule = new Rule(pre, succ);
        rules.add(rule);
    }

    public void addRule(Rule rule){
        rules.add(rule);
    }

    public void addStochasticRule(char pre, String succ, double prob){
        StochasticRule rule = new StochasticRule(pre, succ, prob);
        rulesStoch.add(rule);
    }

    public void addStochasticRule(StochasticRule rule){
        rulesStoch.add(rule);
    }

    public void addParametricRule(char pre, String succ, String param){
        ParametricRule rule = new ParametricRule(pre, succ, param);
        rulesParam.add(rule);
    }

    public void doIteration(){
        String newSeq="";
        String joinedSeq="";
        for (Character element : sequence)
            joinedSeq+=element;
        switch(type){
            case 0:
                newSeq=applyRules(joinedSeq);
                break;
            case 1:
                newSeq=applyStochasticRules(joinedSeq);
                break;
            case 2:
                newSeq=applyParametricRules(joinedSeq);
                break;
            default:
                break;
        }
        List<Character> newSeqList = new ArrayList<Character>();
        char[] arr = newSeq.toCharArray();
        for (char c : arr)
            newSeqList.add(c);
        sequence=newSeqList;
        iteration++;
    }

    public void drawLSystem(){
        drawSeq(startPoint, 90, initLength*Math.pow(lenghtCoof,iteration));
    }

    public String getSuccessor(char pre){
        for (Rule element : rules){
            if (element.getPredecessor() == pre)
                return element.getSuccessor();
        }
        return String.valueOf(pre);
    }
    private String applyRules(String seq){
        String newSeq = "";
        String[] strArray = seq.split("(?!^)");
        HashMap<Integer, Double> newWidths = new HashMap<Integer, Double>();
        int index=0;
        String tmp ="";
        for(String s : strArray){
            tmp = getSuccessor(s.charAt(0));
            if(s.charAt(0)=='F' || s.charAt(0)=='L' ){
                double width = widths.containsKey(index) ?  widths.get(index) : initWidth;
                newWidths.put(newSeq.length(),width*widthCoof);
                for(int i=1; i<tmp.length(); i++){
                    if (tmp.charAt(i)=='F' || tmp.charAt(i)=='L'){
                        newWidths.put(i+newSeq.length(),width);
                    }
                }
            }else{
                for(int i=0; i<tmp.length(); i++)
                    if (tmp.charAt(i)=='F' || tmp.charAt(i)=='L')
                        newWidths.put(i+newSeq.length(),initWidth);
            }
            newSeq+= tmp;
            index++;
        }
        widths=newWidths;
        return newSeq;
    }

    private String getSuccessorStochastic(char pre){
        List<StochasticRule> ruleList = new ArrayList<StochasticRule>();
        for (StochasticRule element : rulesStoch){
            if (element.getPredecessor() == pre)
                ruleList.add(element);
        }
        double rand = Math.random();
        for (StochasticRule element : ruleList){
            if (element.getProbability() >= rand)
                return element.getSuccessor();
            else
                rand -= element.getProbability();
        }
        return String.valueOf(pre);
    }

    private String applyStochasticRules(String seq){
        String newSeq = "";
        String[] strArray = seq.split("(?!^)");
        HashMap<Integer, Double> newWidths = new HashMap<Integer, Double>();
        String tmp="";
        int index=0;
        for(String s : strArray){
            tmp = getSuccessorStochastic(s.charAt(0));
            if(s.charAt(0)=='F' || s.charAt(0)=='L'){
                double width = widths.containsKey(index) ?  widths.get(index) : initWidth;
                newWidths.put(newSeq.length(),width*widthCoof);
                for(int i=1; i<tmp.length(); i++)
                    if (tmp.charAt(i)=='F' || tmp.charAt(i)=='L')
                        newWidths.put(i+newSeq.length(),width);
            }else{
                for(int i=0; i<tmp.length(); i++)
                    if (tmp.charAt(i)=='F')
                        newWidths.put(i+newSeq.length(),initWidth);
            }
            newSeq+= tmp;
            index++;
        }
        widths=newWidths;
        return newSeq;
    }

    private String applyParametricRules(String seq){
        // TBA
        return seq;
    }

    private void drawLine(double x1, double y1, double x2, double y2, double width){
        Graphics g = bufferedImage.getGraphics();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke((float) width));
        g2.setColor(Color.BLACK);
        g2.drawLine((int) x1+startPosition[0], (int) y1+startPosition[1], (int) x2+startPosition[0], (int) y2+startPosition[1]);
        g.dispose();
        g2.dispose();
    }

    private double[] calcPoint(double[] p, double angle, double dist) {
        double[] newP = new double[2];
        newP[0] = p[0]+dist*Math.sin(Math.toRadians(angle+90));
        newP[1] = p[1]+dist*Math.cos(Math.toRadians(angle+90));
        return newP;
      }
    
    private double[] doR(double[] p, double angle, double dist, double width){
        double[] newP = calcPoint(p, angle, dist);
        drawLine(p[0], p[1], newP[0], newP[1], width);
        return newP;
      }
    
    private double[] doL(double[] p, double angle, double curr_lenght){
        return calcPoint(p, angle, curr_lenght);
      }
    private double doP(double angle){
        return angle+delta;
      }
    private double doM(double angle){
        return angle-delta;
      }
    
    private double do180(double angle){
        return angle+180;
      }

    private void drawSeq(double[] curr_point, double curr_angle, double curr_length) {
        String joinedSeq="";
        for (Character element : sequence)
            joinedSeq+=element;
        String[] strArray = joinedSeq.toString().split("(?!^)");
        Deque<double[]> pointDeque = new ArrayDeque<double[]>();  
        Deque<Double> angleDeque = new ArrayDeque<Double>();
        int index=0;
        double width = initWidth;
        for(String s: strArray){
            switch(s) {
                case "F":
                    width = widths.containsKey(index) ?  widths.get(index) : initWidth;
                    curr_point = doR(curr_point, curr_angle, curr_length, width);
                    break;
                case "L":
                    // curr_point = doL(curr_point, curr_angle, curr_length);
                    width = widths.containsKey(index) ?  widths.get(index) : initWidth;
                    curr_point = doR(curr_point, curr_angle, curr_length, width);
                    break;
                case "+":
                    curr_angle=doP(curr_angle);
                    break;
                case "-":
                    curr_angle=doM(curr_angle);
                    break;  
                case "|":
                    curr_angle=do180(curr_angle);
                    break; 
                case "[":
                    pointDeque.addLast(curr_point);
                    angleDeque.addLast(curr_angle);
                    break;
                case "]":
                    curr_point=pointDeque.removeLast();
                    curr_angle=angleDeque.removeLast();
                    break; 
                default:
                    break;
                }
            index++;
        }
      }

    public void saveToFile(String fileName){
        printLSystem();
        try {
            File outputfile = new File(fileName);
            ImageIO.write(bufferedImage, "png", outputfile);
        } catch (IOException e) {
            System.out.println(e);
        } 
      }

    public void doIterationsAndSave(int n, String fileName){
        clear();
        for(int i=1; i<=n; i++){
            doIteration();
        }
        printLSystem();
        drawLSystem();
        saveToFile(fileName);
      }

      public BufferedImage doIterationsAndReturn(int n){
        clear();
        for(int i=0; i<n; i++){
            doIteration();
        }
        drawLSystem();
        return bufferedImage;
      }

      public void clear(){
        iteration = 0;
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(Color.white);
        graphics.fillRect (0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.dispose();

        sequence = new ArrayList<Character>();
        widths = new HashMap<Integer, Double>();
        char[] axiomArray = axiom.toCharArray();
        int index=0;
        for (char c : axiomArray){
            sequence.add(c);
            if(c=='F' || c=='L')
               widths.put(index, initWidth);
            index++;
        }
      }

      public void printLSystem(){
        // String joinedSeq="";
        // for (Character element : sequence)
        //     joinedSeq+=element;
        if(type==0){
            System.out.println("Axiom: " + axiom + " F->" + getSuccessor('F') + " L->" + getSuccessor('L'));
        }
        else {
            System.out.println("Axiom: " + axiom);
            for(StochasticRule rule : rulesStoch){
                System.out.printf("%s->%s : %.4f%n",rule.getPredecessor(), rule.getSuccessor(), rule.getProbability());
                // System.out.println(rule.getPredecessor()+ "->" + rule.getSuccessor() + " : " + rule.getProbability());
            }
        }
        // System.out.println(joinedSeq);
      }

      public void mutate(){
          if(type == 0){
            int rand1 = new Random().nextInt(rules.size());
            int rand2 = new Random().nextInt(rules.size());
            String succ1 = rules.get(rand1).getSuccessor();
            String succ2 = rules.get(rand2).getSuccessor();
            int minLenght = (succ1.length()>succ2.length()) ? succ2.length() : succ1.length();
            int rand3 = new Random().nextInt(minLenght);
            int rand4 = new Random().nextInt(minLenght);
            rand3 = (rand3==0) ? 1 : rand3;
            rand4 = (rand4==0) ? 1 : rand4;
            if (rand1==rand2 && rand3<rand4 && succ2.charAt(rand4)!='[' && succ2.charAt(rand4)!=']' && succ1.charAt(rand3)!='[' && succ1.charAt(rand3)!=']'){
                String newSucc = succ1.substring(0, rand3)+succ2.charAt(rand4)+succ1.substring(rand3+1, rand4)+succ1.charAt(rand3)+succ2.substring(rand4+1);
                rules.get(rand1).setSuccessor(newSucc);
            } else if (rand1==rand2 && rand3>rand4 && succ2.charAt(rand3)!='[' && succ2.charAt(rand3)!=']' && succ1.charAt(rand4)!='[' && succ1.charAt(rand4)!=']'){
                String newSucc = succ1.substring(0, rand4)+succ2.charAt(rand3)+succ1.substring(rand4+1, rand3)+succ1.charAt(rand4)+succ2.substring(rand3+1);
                rules.get(rand1).setSuccessor(newSucc);
            } else if(rand1!=rand2 && succ2.charAt(rand4)!='[' && succ2.charAt(rand4)!=']' && succ1.charAt(rand3)!='[' && succ1.charAt(rand3)!=']'){
                String newSucc1 = succ1.substring(0, rand3)+succ2.charAt(rand4)+succ1.substring(rand3+1);
                String newSucc2 = succ2.substring(0, rand4)+succ1.charAt(rand3)+succ2.substring(rand4+1);
                newSucc1 = newSucc1.replace("-+", "");
                newSucc1 = newSucc1.replace("+-", "");
                newSucc2 = newSucc2.replace("-+", "");
                newSucc2 = newSucc2.replace("+-", "");
                rules.get(rand1).setSuccessor(newSucc1);
                rules.get(rand1).setSuccessor(newSucc2);
            }
        } else {
            int rand1 = new Random().nextInt(rulesStoch.size());
            int rand2 = new Random().nextInt(rulesStoch.size());
            String succ1 = rulesStoch.get(rand1).getSuccessor();
            String succ2 = rulesStoch.get(rand2).getSuccessor();
            int rand3 = new Random().nextInt(succ1.length());
            int rand4 = new Random().nextInt(succ2.length());
            if (rand1==rand2 && rand3<rand4 && succ2.charAt(rand4)!='[' && succ2.charAt(rand4)!=']' && succ1.charAt(rand3)!='[' && succ1.charAt(rand3)!=']'){
                String newSucc = succ1.substring(0, rand3)+succ2.charAt(rand4)+succ1.substring(rand3+1, rand4)+succ1.charAt(rand3)+succ2.substring(rand4+1);
                rulesStoch.get(rand1).setSuccessor(newSucc);
            } else if (rand1==rand2 && rand3>rand4 && succ2.charAt(rand3)!='[' && succ2.charAt(rand3)!=']' && succ1.charAt(rand4)!='[' && succ1.charAt(rand4)!=']'){
                String newSucc = succ1.substring(0, rand4)+succ2.charAt(rand3)+succ1.substring(rand4+1, rand3)+succ1.charAt(rand4)+succ2.substring(rand3+1);
                rulesStoch.get(rand1).setSuccessor(newSucc);
            } else if(rand1!=rand2 && succ2.charAt(rand4)!='[' && succ2.charAt(rand4)!=']' && succ1.charAt(rand3)!='[' && succ1.charAt(rand3)!=']'){
                String newSucc1 = succ1.substring(0, rand3)+succ2.charAt(rand4)+succ1.substring(rand3+1);
                String newSucc2 = succ2.substring(0, rand4)+succ1.charAt(rand3)+succ2.substring(rand4+1);
                newSucc1 = newSucc1.replace("-+", "");
                newSucc1 = newSucc1.replace("+-", "");
                newSucc2 = newSucc2.replace("-+", "");
                newSucc2 = newSucc2.replace("+-", "");
                rulesStoch.get(rand1).setSuccessor(newSucc1);
                rulesStoch.get(rand1).setSuccessor(newSucc2);
            }
        }
      }

      public void setStartPoint(double[] coords){
        startPoint = coords;
      }

      public static void main(String[] args) {
        // LSystem newL = new LSystem("F", 0);
        // newL.addRule('F', "F+F--F+F");
        // newL.addRule('L', "L");
        // newL.addRule('-', "-");
        // newL.addRule('+', "+");
        // BufferedImage bi = new BufferedImage(1280, 800, BufferedImage.TYPE_INT_RGB);
        // newL.setBufferedImage(bi); 
        // newL.doIterationsAndSave(3, "image.png");

        LSystem newL = new LSystem("F", 1);
        newL.addStochasticRule('F', "F+F--F+F", 0.5);
        newL.addStochasticRule('F', "F-F++F-F", 0.5);
        BufferedImage bi = new BufferedImage(1280, 800, BufferedImage.TYPE_INT_RGB);
        newL.setBufferedImage(bi); 
        newL.setStartPoint(new double[] {640,800});
        newL.doIterationsAndSave(3, "image.png");
      }
}
