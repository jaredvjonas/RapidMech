package org.irian.rapid.commands.tasks;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.irian.rapid.commands.TaskCmd;

/**
 * Re-points a chassis at a different 3D model (TKT-142). Some RT source chassis name a dedicated
 * model (e.g. "daikyu", "ionclint") that no installed mod ships, so the game logs
 * "ManifestEntry is null" for the prefab and hardpointdatadef and the mech has no model or weapon
 * mounts. Matches the source PrefabBase (case-insensitive) and rewrites PrefabBase,
 * HardpointDataDefID and PrefabIdentifier together. No-op on chassis using any other model.
 */
public class SwapPrefab implements TaskCmd {
    /** Source PrefabBase to replace, e.g. "daikyu". */
    @XmlAttribute
    public String base;

    /** Installed PrefabBase to use instead, e.g. "cataphract". */
    @XmlAttribute
    public String with;

    /**
     * PrefabIdentifier for the replacement. Resource ids are case-sensitive: vanilla models are
     * "chrPrfMech_{x}Base-001", CAB models "chrprfmech_{x}base-001". Defaults to the
     * lowercase CAB form when omitted.
     */
    @XmlAttribute
    public String identifier;
}
