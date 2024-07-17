package org.irian.rapid.commands.unsupported;

import jakarta.xml.bind.annotation.XmlAttribute;

public class UnsupportedItem implements UnsupportedCmd {
    @XmlAttribute
    public String item;
}
