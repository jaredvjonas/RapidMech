package org.irian.rapid;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.irian.rapid.commands.*;
import org.irian.rapid.commands.tasks.*;
import org.irian.rapid.commands.unsupported.UnsupportedItem;
import org.irian.rapid.defs.ChasisDef;
import org.irian.rapid.defs.MechDef;
import org.irian.rapid.defs.ReaderWriter;
import org.irian.rapid.defs.chassis.Equipment;
import org.irian.rapid.defs.chassis.Hardpoint;
import org.irian.rapid.defs.mech.Inventory;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RapidFile {
    private final File _file;
    private final Map<String, String> variables = new HashMap<>();
    private final Map<String, ResourceList> resourceMap = new HashMap<>();
    private final Map<String, ResourceScanner> resourceCache = new HashMap<>();
    private final Map<String, TaskList> taskMap = new HashMap<>();
    private static final List<String> NonPortableEquipment = new ArrayList<>();

    public RapidFile(File file) {
        this._file = file;
    }

    public void process() throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance( RapidCmd.class );
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        RapidCmd rapidCmd = (RapidCmd) unmarshaller.unmarshal(_file);

        System.out.printf("Processing %s\n", _file.getName());

        for (var dir : rapidCmd.dirList) {
            variables.put(dir.id, dir.path);
        }

        for (var list : rapidCmd.resourceLists) {
            resourceMap.put(list.id, list);
        }

        for (var item : rapidCmd.unsupported.list) {
            if (item instanceof UnsupportedItem) {
                NonPortableEquipment.add(((UnsupportedItem) item).item);
            }
        }

        for (var taskList : rapidCmd.tasks) {
            System.out.printf("Task-List ID=%s\n", taskList.id);
            taskMap.put(taskList.id, taskList);

            for (var task : taskList.taskItems) {
                if (task instanceof RemoveItem removeItem) {
                    System.out.printf("remove-inventory item=%s\n", removeItem.item);
                }
                else if (task instanceof SwapItem swapItem) {
                    System.out.printf("swap-inventory item=%s with=[%s]\n", swapItem.item, swapItem.with);
                }
                else if (task instanceof UpdateLocation updateLocation) {
                    System.out.printf("update-location location=%s property=[%s] value=[%s]\n", updateLocation.name, updateLocation.property, updateLocation.value);
                }
            }
        }

        for (var copy : rapidCmd.instructions) {
            System.out.print("copy...\n");

            System.out.printf("mechdef source=%s dest=%s tasks=%s\n", copy.mechDefCmd.source, copy.mechDefCmd.dest, copy.mechDefCmd.tasks);
            System.out.printf("chassisdef source=%s dest=%s tasks=%s\n", copy.chassisDefCmd.source, copy.chassisDefCmd.dest, copy.chassisDefCmd.tasks);

            var hardpointsMap = new HashMap<String, Hardpoints>();
            for (var list : copy.hardpoints) {
                hardpointsMap.put(list.id, list);
            }

            for (var mechCmd : copy.mechs) {
                System.out.printf("mech model=[%s]\n", mechCmd.model);

                var mech = loadMech(mechCmd, copy.chassisDefCmd, copy.mechDefCmd);
                processTaskList(mechCmd.taskList, mech, false); // process child elements

                TaskList mechTasks = taskMap.get(mechCmd.tasks); // process tasks attribute
                if (mechTasks != null) {
                    processTaskList(mechTasks.taskItems, mech, false);
                }

                System.out.printf("Processing %s\n", mech.chasisDef.Description.Id);
                for (var taskId : parseList(copy.chassisDefCmd.tasks)) {
                    TaskList chassisList = taskMap.get(taskId);
                    processTaskList(chassisList.taskItems, mech, true);
                }

                System.out.printf("Processing %s\n", mech.mechDef.Description.Id);
                for (var taskId : parseList(copy.mechDefCmd.tasks)) {
                    TaskList globalTasks = taskMap.get(taskId); // process tasks referenced by the <mechdef> element
                    processTaskList(globalTasks.taskItems, mech, false);
                }

                Hardpoints hardpoints = hardpointsMap.get(mechCmd.apply);
                if (hardpoints != null) {
                    processTaskList(hardpoints.tasks, mech, false);
                }

                validateInventory(mech);
                saveMech(mech, copy.chassisDefCmd, copy.mechDefCmd);
            }
        }

//        FilenameFilter fileFilter = (dir, name) -> name.startsWith(files.filter);
//        String[] fileList = srcDir.list(fileFilter);
//        if (fileList != null) {
//            for (var fileName : fileList) {
//                processFile(
//                        new File(srcDir, fileName),
//                        new File(targetDir, fileName),
//                        taskList);
//            }
//        }

    }

    private Mech loadMech(MechCmd mechCmd, ChassisDefCmd chassisDefCmd, MechDefCmd mechDefCmd) throws IOException {
        var mechDef = loadMechDef(mechCmd.model, mechDefCmd);
        var chassisDef = loadChassisDef(mechCmd.model, chassisDefCmd);

        return new Mech(mechCmd, mechDef, chassisDef);
    }

    private MechDef loadMechDef(String model, MechDefCmd mechDefCmd) throws IOException {
        String fileName = replaceProperty("mechdef_${model}.json", "model", model);
        File srcDir = buildPath(mechDefCmd.source);
        File inputFile = new File(srcDir, fileName);
        try (var stream = new FileInputStream(inputFile)) {
            return ReaderWriter.readMech(stream);
        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s\n", inputFile);
        } catch (IOException e) {
            System.out.printf("Error reading file: %s\n", inputFile);
        }
        return null;
    }

    private ChasisDef loadChassisDef(String model, ChassisDefCmd chassisDefCmd) throws IOException {
        String fileName = replaceProperty("chassisdef_${model}.json", "model", model);
        File srcDir = buildPath(chassisDefCmd.source);
        File inputFile = new File(srcDir, fileName);
        try (var stream = new FileInputStream(inputFile)) {
            return ReaderWriter.readChassis(stream);
        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s\n", inputFile);
        } catch (IOException e) {
            System.out.printf("Error reading file: %s\n", inputFile);
        }
        return null;
    }

    private void saveMech(Mech mech, ChassisDefCmd chassisDefCmd, MechDefCmd mechDefCmd) throws IOException {
        saveMechDef(mech.mechDef, mechDefCmd);
        saveChassisDef(mech.chasisDef, chassisDefCmd);
    }

    private void saveMechDef(MechDef def, MechDefCmd mechDefCmd) throws IOException {
        String fileName = def.Description.Id + ".json";
        File targetDir = buildPath(mechDefCmd.dest);
        File outputFile = new File(targetDir, fileName);
        try (var outStream = new FileOutputStream(outputFile)) {
            ReaderWriter.writeMech(def, outStream);
        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s\n", outputFile);
        } catch (IOException e) {
            System.out.printf("Error writing file: %s\n", outputFile);
        }
    }

    private void saveChassisDef(ChasisDef def, ChassisDefCmd chassisDefCmd) throws IOException {
        String fileName = def.Description.Id + ".json";
        File targetDir = buildPath(chassisDefCmd.dest);
        File outputFile = new File(targetDir, fileName);
        try (var outStream = new FileOutputStream(outputFile)) {
            ReaderWriter.writeChassis(def, outStream);
        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s\n", outputFile);
        } catch (IOException e) {
            System.out.printf("Error writing file: %s\n", outputFile);
        }
    }

    private void validateInventory(Mech mech) {
        for (var item : mech.mechDef.inventory) {
            if (NonPortableEquipment.contains(item.ComponentDefID)) {
                System.out.printf("WARNING: %s contains incompatible component >> %s [%s]\n", mech.mechDef.Description.Id, item.ComponentDefID, item.ComponentDefType);
            }
        }
    }

    private void processTaskList(List<TaskCmd> tasks, Mech mech, boolean isChassis) {
        for (var task : tasks) {
            processTask(task, mech, isChassis);
        }
    }

    private void processTask(TaskCmd task, Mech mech, boolean isChassis) {
        if (task instanceof AddHardpoint) {
            addHardpoint((AddHardpoint) task, mech.chasisDef);
        }
        else if (task instanceof SetHardpoint) {
            setHardpoint((SetHardpoint) task, mech.chasisDef);
        }
        else if (task instanceof UpdateLocation) {
            updateLocation((UpdateLocation) task, mech.chasisDef);
        }
        else if (task instanceof SwapFixedEquipment) {
            swapFixedEquipment((SwapFixedEquipment) task, mech.chasisDef);
        }
        else if (task instanceof RemoveFixedEquipment) {
            removeFixedEquipment((RemoveFixedEquipment) task, mech.chasisDef);
        }
        else if (task instanceof RemoveQuirk) {
            removeQuirk((RemoveQuirk) task, mech.chasisDef);
        }
        else if (task instanceof SwapHardpoint) {
            swapHardpoint((SwapHardpoint) task, mech.chasisDef);
        }
        else if (task instanceof RemoveItem) {
            removeItem((RemoveItem) task, mech.mechDef);
        }
        else if (task instanceof SwapItem) {
            swapItem((SwapItem) task, mech.mechDef);
        }
        else if (task instanceof RemoveEngine) {
            removeEngine((RemoveEngine) task, mech, isChassis);
        }
        else if (task instanceof RemoveGyro) {
            removeGyro((RemoveGyro) task, mech, isChassis);
        }
        else if (task instanceof RemoveEndoSteel) {
            removeEndoSteel((RemoveEndoSteel) task, mech, isChassis);
        }
        else if (task instanceof RecalcMovement) {
            mech.recalculateMovement();
        }
        else if (task instanceof RecalcTonnage) {
            mech.recalculateTonnage();
        }
        else if (task instanceof RecalcCost) {
            String apply = ((RecalcCost) task).apply;
            var resourceScanner = resourceCache.get(apply);
            if (resourceScanner == null) {
                var resource = resourceMap.get(apply);
                var files = buildResourceList(resource.getPaths());
                resourceScanner = new ResourceScanner(files);
                resourceCache.put(apply, resourceScanner);
            }

            mech.recalculateCost(resourceScanner);
        }
    }

    private void addHardpoint(AddHardpoint task, ChasisDef def) {
        var weaponMounts = task.weaponMounts.split(",");
        for (var location : def.Locations) {
            if (location.Location.equals(task.location)) {
                for (var weaponMount : weaponMounts) {
                    var hardpoint = new Hardpoint();
                    hardpoint.WeaponMountID = weaponMount.trim();
                    hardpoint.Omni = false;
                    location.Hardpoints.add(hardpoint);
                }
            }
        }
    }

    private void setHardpoint(SetHardpoint task, ChasisDef def) {
        var weaponMounts = task.weaponMounts.split(",");
        for (var location : def.Locations) {
            if (location.Location.equals(task.location)) {
                location.Hardpoints.clear(); // clear the list before insertion
                for (var weaponMount : weaponMounts) {
                    var hardpoint = new Hardpoint();
                    hardpoint.WeaponMountID = weaponMount.trim();
                    hardpoint.Omni = false;
                    location.Hardpoints.add(hardpoint);
                }
            }
        }
    }

    private void swapHardpoint(SwapHardpoint task, ChasisDef def) {
        for (var location : def.Locations) {
            for (var hardpoint : location.Hardpoints) {
                if (hardpoint.WeaponMountID.equals(task.weaponMount)) {
                    hardpoint.WeaponMountID = task.with;
                }
            }
        }
    }

    private void swapFixedEquipment(SwapFixedEquipment task, ChasisDef def) {
        for (var equipment : def.FixedEquipment) {
            if (equipment.ComponentDefID.equals(task.item)) {
                equipment.ComponentDefID = task.with;
            }
        }
    }

    private void removeFixedEquipment(RemoveFixedEquipment task, ChasisDef def) {
        def.FixedEquipment.removeIf(item -> item.ComponentDefID.equals(task.item));
    }

    private void removeEngine(RemoveEngine task, Mech mech, boolean isChassis) {
        var engineName = "Gear_EngineCore_" + mech.engineRating;
        if (isChassis) {
            List<Equipment> equipmentList = mech.chasisDef.FixedEquipment;
            equipmentList.removeIf(item -> item.ComponentDefID.equals(engineName));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_Light"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XL"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XL_Clan"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XXL"));
        }
        else {
            List<Inventory> inventory = mech.mechDef.inventory;
            inventory.removeIf(item -> item.ComponentDefID.equals(engineName));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_Light"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XL"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XL_Clan"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Engine_XXL"));
        }
    }

    private void removeGyro(RemoveGyro task, Mech mech, boolean isChassis) {
        if (isChassis) {
            List<Equipment> equipmentList = mech.chasisDef.FixedEquipment;
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_XL"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_HeavyDuty"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_Omnimech"));
        }
        else {
            List<Inventory> inventory = mech.mechDef.inventory;
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_XL"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_HeavyDuty"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Gyro_Omnimech"));
        }
    }

    private void removeEndoSteel(RemoveEndoSteel task, Mech mech, boolean isChassis) {
        if (isChassis) {
            List<Equipment> equipmentList = mech.chasisDef.FixedEquipment;
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Structure_EndoSteel"));
            equipmentList.removeIf(item -> item.ComponentDefID.equals("Gear_Structure_EndoSteel_Clan"));
        }
        else {
            List<Inventory> inventory = mech.mechDef.inventory;
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Structure_EndoSteel"));
            inventory.removeIf(item -> item.ComponentDefID.equals("Gear_Structure_EndoSteel_Clan"));
        }
    }

    private void removeQuirk(RemoveQuirk task, ChasisDef def) {
        def.FixedEquipment.removeIf(item -> item.ComponentDefID.startsWith("Quirk_"));
    }

    private void swapItem(SwapItem task, MechDef def) {
        List<String> attachments = new ArrayList<>();

        for (var item : def.inventory) {
            if (item.ComponentDefID.equals(task.item)) {
                if (task.whenAttachment != null && item.LocalGUID != null) {
                    // find attachment
                    var temp = def.findAttachment(item.LocalGUID);
                    if (temp.ComponentDefID.equals(task.whenAttachment)) {
                        item.LocalGUID = null; // erase the GUID
                        attachments.add(temp.TargetComponentGUID); // save attachment for deletion
                        // and swap items...
                    } else {
                        continue; // skip this task
                    }
                }

                // swap the component
                item.ComponentDefID = task.with;
                if (task.itemType != null) {
                    item.ComponentDefType = task.itemType;
                }
            }
        }

        if (!attachments.isEmpty()) {
            for (var attachment : attachments) {
                def.inventory.removeIf(item -> attachment.equals(item.TargetComponentGUID));
            }
        }
    }

    private void removeItem(RemoveItem task, MechDef def) {
        def.inventory.removeIf(item -> item.ComponentDefID.equals(task.item));
    }

    private void updateLocation(UpdateLocation task, ChasisDef def) {
        try {
            for (var item : def.Locations) {
                if (item.Location.equals(task.name)) {
                    Field field = item.getClass().getField(task.property);
                    Object data = ConvertType(field.getType(), task.value);
                    field.set(item, data);
                }
            }
        } catch (NoSuchFieldException e) {
            System.out.printf("updateLocation::Property Not Found %s\n", task.property);
        } catch (IllegalAccessException e) {
            System.out.printf("updateLocation::IllegalAccessException %s\n", task.property);
        }
    }

    private Object ConvertType(Class<?> type, String value) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        }
        else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        }
        else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        }
        else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        return value;
    }

    private List<File> buildResourceList(List<String> paths) {
        var resourceList = new ArrayList<File>();
        for (var path : paths) {
            try {
                var f = buildPath(path);
                if (f != null) {
                    resourceList.add(f);
                }
            } catch (IOException e) {
                System.out.printf("Error processing directory path %s", path);
            }
        }
        return resourceList;
    }

    /**
     * C:\Program Files (x86)\Steam\steamapps\common\BATTLETECH\mods\RT-Mechs\ClanInvasion3061\chassis
     * C:\Program Files (x86)\Steam\steamapps\common\BATTLETECH\mods\RT-Mechs\ClanInvasion3061\mech
     */
    private File buildPath(String relativePath) throws IOException {
        var path = Path.of(replaceVariable(relativePath));
//        System.out.printf("   path [%s]\n", path);
        File directory = path.toFile().getCanonicalFile();
//        System.out.printf("   directory [%s]\n", directory);
        if (!directory.exists()) {
            System.out.printf("   Directory [%s] does not exist!!!\n", directory);
            return null;
        }
        return directory;
    }

    private String replaceVariable(String s) {
        var updatedString = new StringBuilder(s);
        Pattern pattern = Pattern.compile("\\$\\{[^}]+}");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String varName = s.substring(matcher.start() + 2, matcher.end() - 1);
            updatedString.replace(matcher.start(), matcher.end(), variables.get(varName));
        }
        return updatedString.toString();
    }

    private String replaceProperty(String s, String property, String value) {
        var updatedString = new StringBuilder(s);
        Pattern pattern = Pattern.compile("\\$\\{[^}]+}");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            String varName = s.substring(matcher.start() + 2, matcher.end() - 1);
            if (varName.equals(property)) {
                updatedString.replace(matcher.start(), matcher.end(), value);
            }
        }
        return updatedString.toString();
    }

    private static List<String> parseList(String arguments) {
        String[] split = arguments.split("\\s*,\\s*");
        return List.of(split);
    }
}
