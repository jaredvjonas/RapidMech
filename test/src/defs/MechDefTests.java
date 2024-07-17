package defs;

import org.irian.rapid.defs.MechDef;
import org.irian.rapid.defs.ReaderWriter;
import org.junit.Test;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;

public class MechDefTests {

    @Test
    public void loadJson() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("mechdef_anvil_ANV-3M.json");
        MechDef def = ReaderWriter.readMech(stream);
        assertEquals("chassisdef_anvil_ANV-3M", def.ChassisID);
        assertEquals("mechdef_anvil_ANV-3M", def.Description.Id);
    }

}
