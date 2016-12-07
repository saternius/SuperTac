import java.io.Serializable;

/**
 * Created by jason on 7/1/16.
 */
public class FreqStruct implements Serializable {
    private static final long serialVersionUID = 6569685098267757690L;
    public int freq;
    public int occ;
    public FreqStruct(int freq, int occ){
        this.freq = freq;
        this.occ = occ;
    }
}