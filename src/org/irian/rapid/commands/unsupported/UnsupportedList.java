package org.irian.rapid.commands.unsupported;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import java.util.ArrayList;
import java.util.List;

public class UnsupportedList {
    @XmlElements(
            @XmlElement(name="unsupported-inventory", type= UnsupportedItem.class)
    )
    public List<UnsupportedCmd> list = new ArrayList<>();
}
