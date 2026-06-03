package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Converts a base missile launcher that carries a Gear_Attachment_ArtemisIV into its dedicated
 * Artemis weapon variant (the "missile type" approach; this mod does not support attachments).
 * Only touches weapons actually targeted by an Artemis attachment, and only when the corresponding
 * Artemis weapon def exists, so it cannot strand unrelated launchers.
 */
public class UpgradeArtemis implements TaskCmd {
    // resource-list id (e.g. "mechGear") used to verify the target Artemis weapon def exists.
    @XmlAttribute
    public String apply;
}
