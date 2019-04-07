
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by f_paseban on 11/9/15.
 */
public class LoadPropertiesFile {
    Properties prop;
    LoadPropertiesFile(){
    }
    LoadPropertiesFile(String fileName ){
    prop= new Properties();
    InputStream input = null;
    try {
        input = new FileInputStream(fileName);
        // load a properties file
        prop.load(input);
    } catch (IOException ex) {
        ex.printStackTrace();
    } finally {
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    }

    public Properties getProp() {
        return prop;
    }

    public void setProp(Properties prop) {
        this.prop = prop;
    }
}
