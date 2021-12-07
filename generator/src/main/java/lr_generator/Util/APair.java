package lr_generator.Util;

public class APair <F, S>  {
    private F first; 
    private S second;
    
    public APair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    
    public void setFirst(F first) {
        this.first = first;
    }
    
    public void setSecond(S second) {
        this.second = second;
    }
    
    public F getFirst() {
        return first;
    }
    
    public S getSecond() {
        return second;
    }
}
