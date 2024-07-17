import jakarta.xml.bind.JAXBException;
import org.irian.rapid.RapidFile;

import java.io.File;
import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws JAXBException, IOException {
        var file = new File(args[0]);

        RapidFile rapidFile = new RapidFile(file);
        rapidFile.process();
    }
}