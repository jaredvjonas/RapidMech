package org.irian.rapid.commands;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.irian.rapid.commands.unsupported.UnsupportedList;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="rapid")
public class RapidCmd {

    @XmlElement(name="dir")
    public List<DirCmd> dirList = new ArrayList<>();

    @XmlElement(name="resource-list")
    public List<ResourceList> resourceLists = new ArrayList<>();

    @XmlElement(name="unsupported-list")
    public UnsupportedList unsupported;

    @XmlElement(name="task-list")
    public List<TaskList> tasks = new ArrayList<>();

    @XmlElement(name="copy")
    public List<CopyCmd> instructions = new ArrayList<>();
}
