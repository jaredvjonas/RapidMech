package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;

public class DirCmd implements PathLike {

    @XmlAttribute
    public String id;

    @XmlAttribute
    public String path;
}
