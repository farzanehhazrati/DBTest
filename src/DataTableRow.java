import java.io.Serializable;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Z_Babakhani
 * Date: 10/26/14
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataTableRow implements Serializable {

    private HashMap hs;

    public HashMap getHs() {
        return hs;
    }

    public void setHs(HashMap hs) {
        this.hs = hs;
    }
}