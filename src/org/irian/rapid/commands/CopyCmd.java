package org.irian.rapid.commands;


import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import org.irian.rapid.defs.ChasisDef;

import java.util.ArrayList;
import java.util.List;

public class CopyCmd {
    @XmlElement(name="chassisdef")
    public ChassisDefCmd chassisDefCmd;

    @XmlElement(name="mechdef")
    public MechDefCmd mechDefCmd;

    @XmlElement(name="mech")
    public List<MechCmd> mechs = new ArrayList<>();

    @XmlElement(name="hardpoints")
    public List<Hardpoints> hardpoints = new ArrayList<>();
}
