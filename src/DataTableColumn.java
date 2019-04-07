/**
 * Created by f_paseban on 8/24/15.
 */
public class DataTableColumn {

    private String header;
    private String property;
    private boolean showInTable;
    private  boolean valid;


    public DataTableColumn(String columStr) {
        this.valid=false;
        String[] rowParameters=columStr.split(":");
        //Column:Type:Header:(visible=true,hidden=false)
        if(rowParameters.length==4){ this.valid=true;
        this.header = rowParameters[2];
        this.property = rowParameters[0];
        if(rowParameters[3].equals("true"))
        this.showInTable= true;
        else
            this.showInTable=false;
        }

    }

     public String getProperty() {
        return property;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public boolean isShowInTable() {
        return showInTable;
    }

    public void setShowInTable(boolean showInTable) {
        this.showInTable = showInTable;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
