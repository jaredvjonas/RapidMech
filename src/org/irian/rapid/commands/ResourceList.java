package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import java.util.ArrayList;
import java.util.List;

public class ResourceList {

    @XmlAttribute
    public String id;

    @XmlElements({
            @XmlElement(name = "dir", type = DirCmd.class)
    })
    public List<PathLike> pathItems = new ArrayList<>();

    public List<String> getPaths() {
        List<String> result = new ArrayList<>();

        for (var dir : pathItems) {
            if (dir instanceof DirCmd) {
                result.add(((DirCmd) dir).path);
            }
        }

        return result;
    }
}
