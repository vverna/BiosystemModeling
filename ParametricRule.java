package SourceCode;

public class ParametricRule extends Rule {
    private String parameter;

    public ParametricRule(char pre, String succ, String param) {
        super(pre, succ);
        parameter = param;
    }
  
    public String getParameter(){
        return parameter;
    }
  
    public void setParameter(String param){
        parameter = param;
    }
    
}
