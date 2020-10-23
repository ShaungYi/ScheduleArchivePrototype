package scheduleArchive;

import scheduleArchive.Main;

import java.io.IOException;
import java.util.Properties;

public class UrlFetcher {

    String propName = "archiveUrl.properties";


    Properties urlProp = new Properties();

    public String fetchURL(){

        try {
            urlProp.load(getClass().getClassLoader().getResourceAsStream(propName));

        } catch (IOException e) {
            System.out.println("property file: " + propName + ", not found");
        }


        System.out.println(urlProp.getProperty("URL"));
        return  urlProp.getProperty("URL");


    }
}
