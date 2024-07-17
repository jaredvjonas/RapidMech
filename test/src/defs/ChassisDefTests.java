package defs;

import org.irian.rapid.defs.ChasisDef;
import org.irian.rapid.defs.ReaderWriter;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class ChassisDefTests {

    @Test
    public void loadJson() {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("chassisdef_anvil_ANV-3M.json");
        ChasisDef def = ReaderWriter.readChassis(stream);
        assertEquals("chassisdef_anvil_ANV-3M", def.Description.Id);
        assertEquals("chrprfmech_anvilbase-001", def.PrefabIdentifier);
        assertEquals("hardpointdatadef_anvil", def.HardpointDataDefID);
    }
}
