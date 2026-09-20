package org.irian.rapid.commands.tasks;


import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Rewrite one passage of a mechdef's flavour text.
 *
 * TKT-019 left a pile of mechs whose Details still described a weapon they no longer carry, so the
 * store card sold a Thumper on a mech mounting an autocannon. The fix has to live here rather than
 * in the deployed file, because every rebuild recopies Details straight from the RogueTech source.
 *
 * Deliberately a find/replace and not a whole-description setter: the surrounding history is worth
 * keeping, only the sentence naming the gun is wrong. An empty {@code with} deletes the passage.
 */
public class ReplaceDetails implements TaskCmd {
    @XmlAttribute
    public String find;

    @XmlAttribute
    public String with;
}
