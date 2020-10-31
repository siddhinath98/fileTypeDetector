package com.example.filedemo.analyser;
import java.io.File;
import org.apache.tika.Tika;

public class analyse {
    public static String analyses(String fileDestination) throws Exception {

        File file = new File(fileDestination);//

        //Instantiating tika facade class
        Tika tika = new Tika();

        //detecting the file type using detect method
        String filetype = tika.detect(file);

        return filetype;
    }
}

