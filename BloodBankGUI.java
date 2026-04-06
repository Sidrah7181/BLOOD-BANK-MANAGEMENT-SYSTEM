import java.io.*;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

// ****************************** INTERFACES *******************************************
interface Storable {
    String toStorageString();
}

interface Loggable {
    void logAction(String action);
}

// ****************************** SIMPLE LOG *******************************************
class SimpleLog implements Storable {
    private String message;
    public SimpleLog(String message) { this.message = message; }
    @Override
    public String toStorageString() { return message; }
}

// ****************************** LOGS *******************************************
class Logs {
    public static void save(Storable obj, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(obj.toStorageString() + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error in Logs.save(): " + e.getMessage());
        }
    }
}

// ****************************** PERSONBASE *******************************************
abstract class Personbase {
    private String name;
    private int age;
    private String cnic;
    private String address;
    private String contact;
    
    abstract String getinfo();

    Personbase() {
        name = " ";
        age = 1;
        cnic = "00000-00000-000";
        address = " ";
        contact = "00000000000";
    }
    
    Personbase(String name, int age, String cnic, String address, String contact) {
        this.name = name;
        this.age = age;
        this.cnic = cnic;
        this.address = address;
        this.contact = contact;
    }

    void setName(String name) { this.name = name; }
    void setage(int age) { this.age = age; }
    void setCnic(String cnic) { this.cnic = cnic; }
    void setaddress(String address) { this.address = address; }
    void setContact(String contact) { this.contact = contact; }
    String getName() { return name; }
    String getAddress() { return address; }
    String getcnic() { return cnic; }
    String getcontact() { return contact; }
    int getage() { return age; }
}

// ****************************** DONOR *******************************************
class Donor extends Personbase {
    private String healthStatus;
    private boolean consent;

    Donor() {
        healthStatus = " ";
        consent = false;
    }
    
    public Donor(String name, int age, String cnic, String address, String contact,
                 String healthStatus, boolean consent) {
        super(name, age, cnic, address, contact);
        this.healthStatus = healthStatus;
        this.consent = consent;
    }

    void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    void setconsent(boolean consent) { this.consent = consent; }
    String getHealthStatus() { return healthStatus; }
    boolean getconsent() { return consent; }
    
    boolean checkEligibility() {
        return getHealthStatus().equalsIgnoreCase("Healthy") && getconsent();
    }
    
    public BloodUnit donateBlood(String bloodGroup, String componentType, int donationAmount) {
        if (checkEligibility()) {
            BloodUnit unit = new BloodUnit(bloodGroup, componentType, this, donationAmount);
            return unit;
        } else {
            System.out.println("Donor not eligible to donate!");
            return null;
        }
    }
    
    @Override
    public String getinfo() {
        return "DONOR INFORMATION\n" +
                "Name: " + getName() + "\n" +
                "Age: " + getage() + "\n" +
                "Contact: " + getcontact() + "\n" +
                "Address: " + getAddress() + "\n" +
                "CNIC: " + getcnic() + "\n" +
                "Status: " + getHealthStatus() + "\n" +
                "Consent: " + getconsent() + "\n" +
                "Eligibility: " + checkEligibility();
    }
}

// ****************************** REQUESTOR *******************************************
class Requestor extends Personbase implements Storable {
    private ArrayList<Request> requests = new ArrayList<>();
    
    public Requestor() { super(); }
    
    public Requestor(String name, int age, String cnic, String address, String contact) {
        super(name, age, cnic, address, contact);
    }
    
    @Override
    public String getinfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("REQUESTER INFORMATION\n")
          .append("Name: ").append(getName()).append("\n")
          .append("Age: ").append(getage()).append("\n")
          .append("Contact: ").append(getcontact()).append("\n")
          .append("Address: ").append(getAddress()).append("\n")
          .append("CNIC: ").append(getcnic()).append("\n")
          .append("Requests:\n");
        for (Request r : requests) {
            sb.append(r.getRequestInfo()).append("\n");
        }
        return sb.toString();
    }
    
    public Request makeRequest(String bloodType, int unitsRequested) {
        Request request = new Request(this, bloodType, unitsRequested);
        requests.add(request);
        return request;
    }
    
    @Override
    public String toStorageString() {
        StringBuilder sb = new StringBuilder();
        for (Request r : requests) {
            sb.append(getName()).append(",")
              .append(getage()).append(",")
              .append(getcontact()).append(",")
              .append(getAddress()).append(",")
              .append(getcnic()).append(",")
              .append(r.getBloodType()).append(",")
              .append(r.getUnitsRequested()).append(",")
              .append(r.getStatus()).append("\n");
        }
        return sb.toString();
    }
    
    public void updateRequestStatus(Request request, String newStatus) {
        if (requests.contains(request)) {
            request.setStatus(newStatus);
        } else {
            System.out.println("Request not found for this requester!");
        }
    }
    
    public void removeRequest(Request request) {
        requests.remove(request);
    }
    
    public ArrayList<Request> getRequests() { return requests; }
}

// ****************************** REQUEST *******************************************
class Request {
    private Requestor requestor;
    private String bloodType;
    private int unitsRequested;
    private String status;
    
    public Request(Requestor requestor, String bloodType, int unitsRequested) {
        this.requestor = requestor;
        this.bloodType = bloodType;
        this.unitsRequested = unitsRequested;
        this.status = "Pending";
    }
    
    public Requestor getRequestor() { return requestor; }
    public String getBloodType() { return bloodType; }
    public int getUnitsRequested() { return unitsRequested; }
    public String getStatus() { return status; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setUnitsRequested(int unitsRequested) { this.unitsRequested = unitsRequested; }
    public void setStatus(String status) { this.status = status; }
    
    public String getRequestInfo() {
        return "BLOOD REQUEST INFORMATION\n" +
                "Requester: " + requestor.getName() + "\n" +
                "Blood Type: " + bloodType + "\n" +
                "Units Requested: " + unitsRequested + "\n" +
                "Status: " + status;
    }
    
    public String getShortInfo() {
        return requestor.getName() + " | " + bloodType + " | " + unitsRequested + " units | " + status;
    }
}

// ****************************** BLOOD UNIT *******************************************
class BloodUnit implements Storable {
    private String unitID;
    private String bloodType;
    private LocalDate donationDate;
    private LocalDate expiryDate;
    private Donor donor;
    private int donationAmount;
    
    public BloodUnit(String bloodType, String componentType, Donor donor, int donationAmount) {
        this.unitID = "U" + System.currentTimeMillis();
        this.bloodType = bloodType;
        this.donationDate = LocalDate.now();
        this.donor = donor;
        this.expiryDate = donationDate.plusDays(42);
        this.donationAmount = donationAmount;
    }
    
    public String getUnitID() { return unitID; }
    public String getBloodType() { return bloodType; }
    public LocalDate getDonationDate() { return donationDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public Donor getDonor() { return donor; }
    public int getDonationAmount() { return donationAmount; }
    
    public void setUnitID(String unitID) { this.unitID = unitID; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setDonationAmount(int donationAmount) { this.donationAmount = donationAmount; }
    
    @Override
    public String toStorageString() {
        return unitID + "," + bloodType + "," + donationDate + "," + expiryDate + "," +
               donor.getName() + "," + donor.getcnic() + "," + donationAmount;
    }
    
    @Override
    public String toString() {
        return "BloodUnit [" +
                "UnitID=" + unitID +
                ", BloodType=" + bloodType +
                ", Amount=" + donationAmount + "ml" +
                ", DonationDate=" + donationDate +
                ", ExpiryDate=" + expiryDate +
                ", Donor=" + donor.getName() + " (" + donor.getcnic() + ")" +
                "]";
    }
}

// ****************************** BLOOD INVENTORY *******************************************
class BloodInventory {
    private ArrayList<BloodUnit> units = new ArrayList<>();
    
    // ---------------- ADD UNIT ----------------
    public void addUnit(BloodUnit unit) {
        try {
            if (unit == null) {
                System.out.println("Error: Cannot add null blood unit");
                return;
            }
            units.add(unit);
            System.out.println(" Blood Unit Added: " + unit.getUnitID() + " (" + unit.getDonationAmount() + "ml)");
        } catch (Exception e) {
            System.out.println("Error adding blood unit: " + e.getMessage());
        }
    }
    
    // ---------------- REMOVE UNIT ----------------
    public boolean removeUnit(String unitID) {
        try {
            if (unitID == null || unitID.trim().isEmpty()) {
                System.out.println("Error: Invalid unit ID");
                return false;
            }
            return units.removeIf(u -> u != null && u.getUnitID() != null && u.getUnitID().equals(unitID));
        } catch (Exception e) {
            System.out.println("Error removing unit: " + e.getMessage());
            return false;
        }
    }
    
    // ---------------- SEARCH BY BLOOD TYPE ----------------
    public ArrayList<BloodUnit> searchByType(String type) {
        ArrayList<BloodUnit> result = new ArrayList<>();
        try {
            if (type == null) {
                return result;
            }
            for (BloodUnit u : units) {
                if (u != null && u.getBloodType() != null && u.getBloodType().equalsIgnoreCase(type)) {
                    result.add(u);
                }
            }
            result.sort(Comparator.comparing(BloodUnit::getExpiryDate));
        } catch (Exception e) {
            System.out.println("Error searching by blood type: " + e.getMessage());
        }
        return result;
    }
    
    // ---------------- GET COMPATIBLE BLOOD (MEDICAL RULES) ----------------
    public ArrayList<BloodUnit> getCompatible(String recipientType) {
        ArrayList<BloodUnit> compatible = new ArrayList<>();
        try {
            if (recipientType == null) {
                return compatible;
            }
            String[] canReceive = getCanReceive(recipientType);
            
            for (BloodUnit u : units) {
                if (u != null && u.getBloodType() != null) {
                    for (String t : canReceive) {
                        if (u.getBloodType().equalsIgnoreCase(t)) {
                            compatible.add(u);
                            break;
                        }
                    }
                }
            }
            compatible.sort(Comparator.comparing(BloodUnit::getExpiryDate));
        } catch (Exception e) {
            System.out.println("Error finding compatible blood: " + e.getMessage());
        }
        return compatible;
    }
    
    // ---------------- GET COMPATIBLE WITH MESSAGE ----------------
    public ArrayList<BloodUnit> getCompatibleWithMessage(String requestedType) {
        try {
            if (requestedType == null) {
                return new ArrayList<>();
            }
            ArrayList<BloodUnit> exactMatch = searchByType(requestedType);
            
            if (!exactMatch.isEmpty()) {
                return exactMatch;
            }
            
            ArrayList<BloodUnit> compatible = getCompatible(requestedType);
            if (!compatible.isEmpty() && compatible.get(0) != null) {
                System.out.println("\nWe didn't have " + requestedType + ", so we're giving you " + 
                                 compatible.get(0).getBloodType() + " instead (compatible type).");
            }
            
            return compatible;
        } catch (Exception e) {
            System.out.println("Error getting compatible blood with message: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private String[] getCanReceive(String type) {
        try {
            if (type == null) {
                return new String[]{};
            }
            return switch (type.toUpperCase()) {
                case "A+"  -> new String[]{"A+", "A-", "O+", "O-"};
                case "A-"  -> new String[]{"A-", "O-"};
                case "B+"  -> new String[]{"B+", "B-", "O+", "O-"};
                case "B-"  -> new String[]{"B-", "O-"};
                case "AB+" -> new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
                case "AB-" -> new String[]{"A-", "B-", "AB-", "O-"};
                case "O+"  -> new String[]{"O+", "O-"};
                case "O-"  -> new String[]{"O-"};
                default    -> new String[]{};
            };
        } catch (Exception e) {
            return new String[]{};
        }
    }
    
    // ---------------- REMOVE EXPIRED UNITS ----------------
    public void removeExpired() {
        try {
            units.removeIf(u -> u != null && u.getExpiryDate() != null && 
                               u.getExpiryDate().isBefore(LocalDate.now()));
        } catch (Exception e) {
            System.out.println("Error removing expired units: " + e.getMessage());
        }
    }
    
    // ---------------- SAVE TO FILE ----------------
    public void saveToFile(String fileName) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (BloodUnit u : units) {
                if (u != null) {
                    pw.println(u.toStorageString());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found: " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    
    // ---------------- LOAD FROM FILE ----------------
    public void loadFromFile(String fileName) {
        units.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] arr = line.split(",");
                    if (arr.length < 7) continue;
                    
                    String unitID = arr[0];
                    String type = arr[1];
                    LocalDate donationDate = LocalDate.parse(arr[2]);
                    LocalDate expiryDate = LocalDate.parse(arr[3]);
                    String donorName = arr[4];
                    String donorCnic = arr[5];
                    int donationAmount = Integer.parseInt(arr[6]);
                    
                    Donor d = new Donor(donorName, 0, donorCnic, "", "00000000000", "Healthy", true);
                    BloodUnit u = new BloodUnit(type, "Whole", d, donationAmount);
                    u.setUnitID(unitID);
                    u.setDonationDate(donationDate);
                    u.setExpiryDate(expiryDate);
                    units.add(u);
                } catch (Exception e) {
                    // Skip invalid lines
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, that's OK
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
    
    // ---------------- SHOW INVENTORY ----------------
    public void showInventory() {
        try {
            removeExpired();
            System.out.println("\nCURRENT INVENTORY");
            if (units.isEmpty()) {
                System.out.println("No blood units in inventory.");
            } else {
                int totalAmount = 0;
                for (BloodUnit u : units) {
                    if (u != null) {
                        System.out.println(u);
                        totalAmount += u.getDonationAmount();
                    }
                }
                System.out.println("Total blood available: " + totalAmount + "ml");
            }
        } catch (Exception e) {
            System.out.println("Error showing inventory: " + e.getMessage());
        }
    }
    
    public ArrayList<BloodUnit> getUnits() {
        return new ArrayList<>(units);
    }
}

// ****************************** TESTING *******************************************
class Testing {
    private Random rand = new Random();
    
    public boolean testBloodUnit(BloodUnit unit) {
        try {
            if (unit == null) {
                System.out.println("Error: Cannot test null blood unit");
                return false;
            }
            System.out.println("Testing: Checking unit " + unit.getUnitID());
            boolean safe = rand.nextInt(100) < 85;
            if (safe) {
                System.out.println(" Blood unit is safe.");
            } else {
                System.out.println(" Blood unit failed safety test!");
            }
            Logs.save(new SimpleLog("Testing: Unit " + unit.getUnitID() + " safe? " + safe), "testing.txt");
            return safe;
        } catch (Exception e) {
            System.out.println("Error testing blood unit: " + e.getMessage());
            return false;
        }
    }
    
    public void screenInfections(BloodUnit unit) {
        try {
            if (unit == null) {
                System.out.println("Error: Cannot screen null blood unit");
                return;
            }
            System.out.println("Screening unit " + unit.getUnitID() + " for infections...");
            Logs.save(new SimpleLog("Screened unit " + unit.getUnitID()), "testing.txt");
        } catch (Exception e) {
            System.out.println("Error screening infections: " + e.getMessage());
        }
    }
}

// ****************************** HEMOVIGILANCE *******************************************
class Hemovigilance {
    private ArrayList<String> events = new ArrayList<>();
    private Random rand = new Random();
    private String[] reactionTypes = {
        "Mild fever",
        "Chills",
        "Allergic reaction",
        "Hypotension",
        "Nausea",
        "Delayed hemolytic reaction"
    };
    
    public void monitorTransfusion(BloodUnit unit) {
        try {
            if (unit == null) {
                System.out.println("Error: Cannot monitor transfusion for null blood unit");
                return;
            }
            System.out.println("Hemovigilance: Monitoring transfusion for unit " + unit.getUnitID() + "...");
            int chance = rand.nextInt(100);
            if (chance < 30) {
                String reaction = reactionTypes[rand.nextInt(reactionTypes.length)];
                String log = "Adverse Reaction | Unit: " + unit.getUnitID() + " | Type: " + reaction;
                events.add(log);
                Logs.save(new SimpleLog(log), "hemovigilance.txt");
                System.out.println(" Adverse Reaction Detected: " + reaction);
                System.out.println("Calling an ambulance!");
            } else {
                String log = "No adverse reaction | Unit: " + unit.getUnitID();
                events.add(log);
                Logs.save(new SimpleLog(log), "hemovigilance.txt");
                System.out.println(" No adverse reaction.");
            }
        } catch (Exception e) {
            System.out.println("Error monitoring transfusion: " + e.getMessage());
        }
    }
    
    public void showAllEvents() {
        try {
            System.out.println("\nHemovigilance Report");
            if (events.isEmpty()) {
                System.out.println("No events recorded.");
            } else {
                for (String e : events) {
                    if (e != null) {
                        System.out.println(e);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error showing events: " + e.getMessage());
        }
    }
}

// ****************************** ADMIN *******************************************
class Admin {
    private BloodInventory inventory;
    private Testing testing;
    private Hemovigilance hemovigilance;
    private ArrayList<Donor> donors;
    private ArrayList<Requestor> requestors;
    
    public Admin() {
        try {
            this.inventory = new BloodInventory();
            this.testing = new Testing();
            this.hemovigilance = new Hemovigilance();
            this.donors = new ArrayList<>();
            this.requestors = new ArrayList<>();
            
            inventory.loadFromFile("blood_units.txt");
            loadRequestorsFromFile();
        } catch (Exception e) {
            System.out.println("Error initializing Admin: " + e.getMessage());
            this.inventory = new BloodInventory();
            this.testing = new Testing();
            this.hemovigilance = new Hemovigilance();
            this.donors = new ArrayList<>();
            this.requestors = new ArrayList<>();
        }
    }
    
    public void addDonor(Donor donor, int donationAmount) {
        try {
            if (donor == null) {
                System.out.println("Error: Cannot add null donor");
                return;
            }
            donors.add(donor);
            Logs.save(new SimpleLog("Admin: Donor added " + donor.getName()), "admin.txt");
            System.out.println("Donor added: " + donor.getName());
            
            if (donor.checkEligibility()) {
                System.out.println("Auto-screening eligible donor...");
                autoRegisterDonor(donor, donationAmount);
            }
        } catch (Exception e) {
            System.out.println("Error adding donor: " + e.getMessage());
        }
    }
    
    //here
        private void autoRegisterDonor(Donor d, int donationAmount) {
        try {
            Scanner tempScanner = new Scanner(System.in);
            
            String bloodType = "";
            while (true) {
                System.out.print("Enter donor's blood type (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
                String input = tempScanner.nextLine().trim().toUpperCase();
                if (input.equals("A+") || input.equals("A-") || input.equals("B+") || input.equals("B-") || 
                    input.equals("AB+") || input.equals("AB-") || input.equals("O+") || input.equals("O-")) {
                    bloodType = input;
                    break;
                }
                System.out.println("Invalid blood type! Must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-");
            }
            
            System.out.print("Enter component type (Whole/Platelets/Plasma): ");
            String component = tempScanner.nextLine();
            
            if (donationAmount < 350 || donationAmount > 500) {
                System.out.println(" Donation amount " + donationAmount + "ml is not safe!");
                System.out.println("Safe range is 350-500ml. Registration cancelled.");
                donors.remove(d);
                return;
            }
            
            BloodUnit unit = d.donateBlood(bloodType, component, donationAmount);
            if (unit != null) {
                testing.screenInfections(unit);
                if (testing.testBloodUnit(unit)) {
                    inventory.addUnit(unit);
                    inventory.saveToFile("blood_units.txt");
                    Logs.save(new SimpleLog("Auto-registered: Blood unit " + unit.toString() + " added to inventory"), "admin.txt");
                    System.out.println(" Donor auto-registered and blood added to inventory!");
                } else {
                    System.out.println(" Blood unit failed testing! Donor registration cancelled.");
                    donors.remove(d);
                }
            } else {
                System.out.println(" Donor not eligible. Registration cancelled.");
                donors.remove(d);
            }
        } catch (Exception e) {
            System.out.println("Error in auto-registration: " + e.getMessage());
        }
    }
    
    public void addRequestor(Requestor r) {
        try {
            if (r == null) {
                System.out.println("Error: Cannot add null requestor");
                return;
            }
            boolean exists = false;
            for (Requestor existing : requestors) {
                if (existing != null && existing.getcnic() != null && existing.getcnic().equals(r.getcnic())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                requestors.add(r);
                Logs.save(new SimpleLog("Admin: Requestor added " + r.getName()), "admin.txt");
            }
        } catch (Exception e) {
            System.out.println("Error adding requestor: " + e.getMessage());
        }
    }
    
    public void issueBlood(Request request) {
        try {
            if (request == null) {
                System.out.println("Error: Cannot issue blood for null request");
                return;
            }
            ArrayList<BloodUnit> compatible = inventory.getCompatibleWithMessage(request.getBloodType());
            int neededAmount = request.getUnitsRequested() * 450;
            int availableAmount = 0;
            
            for (BloodUnit unit : compatible) {
                if (unit != null) {
                    availableAmount += unit.getDonationAmount();
                }
            }
            
            if (availableAmount >= neededAmount) {
                System.out.println("Issuing " + neededAmount + "ml (" + request.getUnitsRequested() + " units) to " + request.getRequestor().getName());
                
                int remainingNeeded = neededAmount;
                ArrayList<BloodUnit> unitsToRemove = new ArrayList<>();
                
                for (BloodUnit unit : compatible) {
                    if (remainingNeeded <= 0) break;
                    if (unit != null) {
                        unitsToRemove.add(unit);
                        remainingNeeded -= unit.getDonationAmount();
                        hemovigilance.monitorTransfusion(unit);
                    }
                }
                
                for (BloodUnit unit : unitsToRemove) {
                    inventory.removeUnit(unit.getUnitID());
                }
                
                request.setStatus("Completed");
                request.getRequestor().removeRequest(request);
                saveRequestorsToFile();
                inventory.saveToFile("blood_units.txt");
                
                Logs.save(new SimpleLog("Admin: Issued " + neededAmount + "ml to " + request.getRequestor().getName()), "admin.txt");
                System.out.println(" Blood issued successfully!");
            } else {
                System.out.println(" Not enough compatible blood units available!");
                System.out.println("Available: " + availableAmount + "ml, Needed: " + neededAmount + "ml");
            }
        } catch (Exception e) {
            System.out.println("Error issuing blood: " + e.getMessage());
        }
    }
    
    public ArrayList<Request> getPendingRequests() {
        ArrayList<Request> pending = new ArrayList<>();
        try {
            for (Requestor r : requestors) {
                if (r != null && r.getRequests() != null) {
                    for (Request req : r.getRequests()) {
                        if (req != null && "Pending".equals(req.getStatus())) {
                            pending.add(req);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error getting pending requests: " + e.getMessage());
        }
        return pending;
    }
    
    private void saveRequestorsToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("requester.csv"))) {
            for (Requestor r : requestors) {
                if (r != null) {
                    pw.print(r.toStorageString());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Cannot save requestors - file not found");
        } catch (IOException e) {
            System.out.println("Error saving requestors: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error saving requestors: " + e.getMessage());
        }
    }
    
    private void loadRequestorsFromFile() {
        requestors.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("requester.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] arr = line.split(",");
                    if (arr.length < 8) continue;
                    
                    String name = arr[0];
                    int age = Integer.parseInt(arr[1]);
                    String contact = arr[2];
                    String address = arr[3];
                    String cnic = arr[4];
                    String bloodType = arr[5];
                    int units = Integer.parseInt(arr[6]);
                    String status = arr[7];
                    
                    Requestor r = findRequestorByCNIC(cnic);
                    if (r == null) {
                        r = new Requestor(name, age, cnic, address, contact);
                        requestors.add(r);
                    }
                    
                    if (!"Completed".equals(status)) {
                        Request req = new Request(r, bloodType, units);
                        req.setStatus(status);
                        r.getRequests().add(req);
                    }
                } catch (Exception e) {
                    // Skip invalid lines
                }
            }
        } catch (FileNotFoundException e) {
            //if file doesnt exist yet
        } catch (IOException e) {
            System.out.println("Error reading requestor file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error loading requestors: " + e.getMessage());
        }
    }
    
    private Requestor findRequestorByCNIC(String cnic) {
        try {
            if (cnic == null) return null;
            for (Requestor r : requestors) {
                if (r != null && r.getcnic() != null && r.getcnic().equals(cnic)) {
                    return r;
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding requestor by CNIC: " + e.getMessage());
        }
        return null;
    }
    
    public void showDonors() {
        try {
            System.out.println("\nDonor List");
            if (donors.isEmpty()) {
                System.out.println("No donors registered.");
            } else {
                for (Donor d : donors) {
                    if (d != null) {
                        System.out.println(d.getinfo());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error showing donors: " + e.getMessage());
        }
    }
    
    public void showRequestors() {
        try {
            System.out.println("\nRequestor List");
            if (requestors.isEmpty()) {
                System.out.println("No requestors registered.");
            } else {
                for (Requestor r : requestors) {
                    if (r != null) {
                        System.out.println(r.getinfo());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error showing requestors: " + e.getMessage());
        }
    }
    
    public BloodInventory getInventory() { 
        if (inventory == null) {
            inventory = new BloodInventory();
        }
        return inventory; 
    }
    
    public Hemovigilance getHemovigilance() { 
        if (hemovigilance == null) {
            hemovigilance = new Hemovigilance();
        }
        return hemovigilance; 
    }
    
    public ArrayList<Donor> getDonors() { 
        if (donors == null) {
            donors = new ArrayList<>();
        }
        return donors; 
    }
    
    public ArrayList<Requestor> getRequestors() { 
        if (requestors == null) {
            requestors = new ArrayList<>();
        }
        return requestors; 
    }
}


// ***************************************MAIN***************************************************


public class BloodBankGUI {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Admin admin;
    
    ImageIcon icon = new ImageIcon("icon.jpeg");
    // Colors for theme
    private final Color THEME_RED = new Color(178, 34, 34); // Firebrick red
    private final Color THEME_LIGHT_GRAY = new Color(245, 245, 245);
    private final Color THEME_DARK_GRAY = new Color(50, 50, 50);
    // USED COLORS (Hardcoded ):
   // Color(70, 130, 180)    - #4682B4 - Steel Blue 
   // Color(46, 139, 87)     - #2E8B57 - Sea Green
   // Color(128, 0, 128)     - #800080 - Purple

    
    // Donor form fields
    private JTextField donorNameField, donorAgeField, donorCnicField;
    private JTextField donorAddressField, donorContactField;
    private JComboBox<String> donorHealthCombo, donorBloodTypeCombo;
    private JComboBox<String> donorComponentCombo;
    private JCheckBox donorConsentCheck;
    private JTextField donorAmountField;
    
    // Request form fields
    private JTextField requestCnicField;
    private JPanel requestCardPanel;
    private CardLayout requestCardLayout;
    private JTextField newNameField, newAgeField, newAddressField, newContactField;
    private JComboBox<String> requestBloodTypeCombo;
    private JTextField requestUnitsField;
    
    // Admin login fields
    private JTextField adminUserField;
    private JPasswordField adminPassField;
    
    // Admin dashboard tables
    private JTable donorsTable, requestorsTable, pendingTable, inventoryTable;
    private DefaultTableModel donorsModel, requestorsModel, pendingModel, inventoryModel;
    private JTextArea hemovigilanceArea;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BloodBankGUI());
    }
    
    public BloodBankGUI() {
        try {
            admin = new Admin();
            createGUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize Blood Bank System: " + e.getMessage(), 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    
    private void createGUI() {
        mainFrame = new JFrame("Blood Bank Management System");
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        try {
            ImageIcon icon = new ImageIcon("icon.jpeg");
            mainFrame.setIconImage(icon.getImage());
        } catch (SecurityException e) {
            System.out.println("Warning: Security restriction on icon loading: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Icon file not found or invalid: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Window icon not loaded: " + e.getMessage());
        }

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        createWelcomePanel();
        createDonorPanel();
        createRequestPanel();
        createAdminLoginPanel();
        createAdminDashboardPanel();
        
        mainFrame.add(mainPanel);
        mainFrame.setSize(900, 650);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
    
    private void createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(THEME_RED);
        headerPanel.setPreferredSize(new Dimension(900, 120));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("BLOOD BANK MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Saving Lives Through Blood Donation", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);
        
        JPanel centerHeader = new JPanel(new GridLayout(2, 1));
        centerHeader.setOpaque(false);
        centerHeader.add(titleLabel);
        centerHeader.add(subtitleLabel);
        
        headerPanel.add(centerHeader, BorderLayout.CENTER);
        
        // Center buttons
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(50, 150, 50, 150));
        
        JButton donorButton = createMenuButton("Register as Donor", THEME_RED);
        donorButton.addActionListener(e -> cardLayout.show(mainPanel, "Donor"));
        
        JButton requestButton = createMenuButton("Make Blood Request", new Color(70, 130, 180));
        requestButton.addActionListener(e -> {
            requestCnicField.setText("");
            requestCardLayout.show(requestCardPanel, "CNIC");
            cardLayout.show(mainPanel, "Request");
        });
        
        JButton adminButton = createMenuButton("Admin Login", new Color(46, 139, 87));
        adminButton.addActionListener(e -> {
            adminUserField.setText("");
            adminPassField.setText("");
            cardLayout.show(mainPanel, "AdminLogin");
        });
        
        JButton exitButton = createMenuButton("Exit", THEME_DARK_GRAY);
        exitButton.addActionListener(e -> saveAndExit());
        
        centerPanel.add(donorButton);
        centerPanel.add(requestButton);
        centerPanel.add(adminButton);
        centerPanel.add(exitButton);
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(THEME_LIGHT_GRAY);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left side label
        JLabel leftLabel = new JLabel("© 2025 Blood Bank | Emergency: 1122");
        leftLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Right side label
        JLabel rightLabel = new JLabel("Academic Project By: Aneeqa Nadeem & Sidra Mehak ");
        rightLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add labels to footer
        footerPanel.add(leftLabel, BorderLayout.WEST);
        footerPanel.add(rightLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "Welcome");
    }
    
    private void createDonorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_RED);
        headerPanel.setPreferredSize(new Dimension(900, 70));
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        
        JLabel titleLabel = new JLabel("DONOR REGISTRATION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Initialize fields
        donorNameField = new JTextField();
        donorAgeField = new JTextField();
        donorCnicField = new JTextField();
        donorAddressField = new JTextField();
        donorContactField = new JTextField();
        
        String[] healthOptions = {"Healthy", "Unhealthy"};
        donorHealthCombo = new JComboBox<>(healthOptions);
        
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        donorBloodTypeCombo = new JComboBox<>(bloodTypes);
        
        String[] components = {"Whole", "Platelets", "Plasma"};
        donorComponentCombo = new JComboBox<>(components);
        
        donorConsentCheck = new JCheckBox("I give consent for blood donation");
        donorConsentCheck.setBackground(Color.WHITE);
        
        donorAmountField = new JTextField("450");
        
        // Add form fields
        formPanel.add(createFormLabel("Full Name:"));
        formPanel.add(donorNameField);
        
        formPanel.add(createFormLabel("Age (17-70):"));
        formPanel.add(donorAgeField);
        
        formPanel.add(createFormLabel("CNIC (XXXXX-XXXXXXX-X):"));
        formPanel.add(donorCnicField);
        
        formPanel.add(createFormLabel("Address:"));
        formPanel.add(donorAddressField);
        
        formPanel.add(createFormLabel("Contact (11 digits):"));
        formPanel.add(donorContactField);
        
        formPanel.add(createFormLabel("Health Status:"));
        formPanel.add(donorHealthCombo);
        
        formPanel.add(createFormLabel("Blood Type:"));
        formPanel.add(donorBloodTypeCombo);
        
        formPanel.add(createFormLabel("Component Type:"));
        formPanel.add(donorComponentCombo);
        
        formPanel.add(createFormLabel("Donation Amount (350-500ml):"));
        formPanel.add(donorAmountField);

        // Add consent checkbox
        formPanel.add(createFormLabel("Consent:"));
        formPanel.add(donorConsentCheck);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JButton submitButton = createMenuButton("Register Donor", THEME_RED);
        submitButton.addActionListener(e -> registerDonor());
        
        JButton clearButton = createMenuButton("Clear Form", THEME_DARK_GRAY);
        clearButton.addActionListener(e -> clearDonorForm());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(clearButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(panel, "Donor");
    }
    
    private void createRequestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(900, 70));
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        
        JLabel titleLabel = new JLabel("BLOOD REQUEST", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Card panel for request flow
        requestCardPanel = new JPanel();
        requestCardLayout = new CardLayout();
        requestCardPanel.setLayout(requestCardLayout);
        requestCardPanel.setBackground(Color.WHITE);
        
        // Panel 1: CNIC Entry
        JPanel cnicPanel = new JPanel(new BorderLayout());
        cnicPanel.setBackground(Color.WHITE);
        cnicPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JPanel cnicCenter = new JPanel(new GridLayout(3, 1, 10, 20));
        cnicCenter.setBackground(Color.WHITE);
        
        JLabel cnicLabel = new JLabel("Enter your CNIC to continue:", SwingConstants.CENTER);
        cnicLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        requestCnicField = new JTextField();
        requestCnicField.setMaximumSize(new Dimension(30, 30));
        requestCnicField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton checkButton = createMenuButton("Check CNIC", new Color(70, 130, 180));
        checkButton.addActionListener(e -> checkRequestorCNIC());
        
        cnicCenter.add(cnicLabel);
        cnicCenter.add(requestCnicField);
        cnicCenter.add(checkButton);
        
        cnicPanel.add(cnicCenter, BorderLayout.CENTER);
        
        // Panel 2: New Requestor Form
        JPanel newRequestorPanel = new JPanel(new BorderLayout());
        newRequestorPanel.setBackground(Color.WHITE);
        
        JPanel newFormPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        newFormPanel.setBackground(Color.WHITE);
        newFormPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        newNameField = new JTextField();
        newAgeField = new JTextField();
        newAddressField = new JTextField();
        newContactField = new JTextField();
        
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        requestBloodTypeCombo = new JComboBox<>(bloodTypes);
        
        requestUnitsField = new JTextField("1");
        
        newFormPanel.add(createFormLabel("Full Name:"));
        newFormPanel.add(newNameField);
        
        newFormPanel.add(createFormLabel("Age (1-120):"));
        newFormPanel.add(newAgeField);
        
        newFormPanel.add(createFormLabel("Address:"));
        newFormPanel.add(newAddressField);
        
        newFormPanel.add(createFormLabel("Contact (11 digits):"));
        newFormPanel.add(newContactField);
        
        newFormPanel.add(createFormLabel("Blood Type Needed:"));
        newFormPanel.add(requestBloodTypeCombo);
        
        newFormPanel.add(createFormLabel("Units Needed:"));
        newFormPanel.add(requestUnitsField);
        
        JPanel newButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        newButtonPanel.setBackground(Color.WHITE);
        newButtonPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JButton newSubmitButton = createMenuButton("Submit Request", new Color(70, 130, 180));
        newSubmitButton.addActionListener(e -> submitNewRequest());
        
        newButtonPanel.add(newSubmitButton);
        
        newRequestorPanel.add(newFormPanel, BorderLayout.CENTER);
        newRequestorPanel.add(newButtonPanel, BorderLayout.SOUTH);
        
        // Panel 3: Existing Requestor
        JPanel existingPanel = new JPanel(new BorderLayout());
        existingPanel.setBackground(Color.WHITE);
        
        JPanel existingFormPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        existingFormPanel.setBackground(Color.WHITE);
        existingFormPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JLabel existingLabel = new JLabel("Welcome back!", SwingConstants.CENTER);
        existingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JComboBox<String> existingBloodCombo = new JComboBox<>(bloodTypes);
        JTextField existingUnitsField = new JTextField("1");
        
        existingFormPanel.add(createFormLabel("Blood Type Needed:"));
        existingFormPanel.add(existingBloodCombo);
        
        existingFormPanel.add(createFormLabel("Units Needed:"));
        existingFormPanel.add(existingUnitsField);
        
        existingFormPanel.add(new JLabel()); // Empty cell
        JButton existingSubmitButton = createMenuButton("Submit Request", new Color(70, 130, 180));
        existingSubmitButton.addActionListener(e -> submitExistingRequest(existingBloodCombo, existingUnitsField));
        existingFormPanel.add(existingSubmitButton);
        
        existingPanel.add(existingLabel, BorderLayout.NORTH);
        existingPanel.add(existingFormPanel, BorderLayout.CENTER);
        
        requestCardPanel.add(cnicPanel, "CNIC");
        requestCardPanel.add(newRequestorPanel, "New");
        requestCardPanel.add(existingPanel, "Existing");
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(requestCardPanel, BorderLayout.CENTER);
        
        mainPanel.add(panel, "Request");

        JLabel typeLabel = new JLabel("Requestor Type:");
        String[] requestorTypes = {"Normal", "NGO"};
        JComboBox<String> requestorTypeCombo = new JComboBox<>(requestorTypes);

        // Add to newFormPanel
        newFormPanel.add(typeLabel);
        newFormPanel.add(requestorTypeCombo);
    }
    
    private void createAdminLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 139, 87));
        headerPanel.setPreferredSize(new Dimension(900, 70));
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
        
        JLabel titleLabel = new JLabel("ADMIN LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Login form
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(100, 200, 100, 200));
        
        adminUserField = new JTextField();
        adminPassField = new JPasswordField();
        
        loginPanel.add(createFormLabel("Username:"));
        loginPanel.add(adminUserField);
        
        loginPanel.add(createFormLabel("Password:"));
        loginPanel.add(adminPassField);
        
        loginPanel.add(new JLabel()); // Empty cell
        JButton loginButton = createMenuButton("Login", new Color(46, 139, 87));
        loginButton.addActionListener(e -> performAdminLogin());
        loginPanel.add(loginButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(loginPanel, BorderLayout.CENTER);
        
        mainPanel.add(panel, "AdminLogin");
    }
    
    private void createAdminDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_RED);
        headerPanel.setPreferredSize(new Dimension(900, 70));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "Welcome");
        });
        
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(logoutButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Sidebar buttons
        JPanel sidebarPanel = new JPanel(new GridLayout(6, 1, 0, 5));
        sidebarPanel.setBackground(THEME_LIGHT_GRAY);
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        sidebarPanel.setPreferredSize(new Dimension(150, 0));
        
        String[] buttonLabels = {"View Donors", "View Requestors", "Pending Requests", 
                                 "View Inventory", "Hemovigilance Report", "Back to Menu"};
        
        for (String label : buttonLabels) {
            JButton button = new JButton("<html><center>" + label + "</center></html>");
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(Color.WHITE);
            button.setBorder(new CompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(10, 5, 10, 5)
            ));
            
            if (label.equals("Back to Menu")) {
                button.addActionListener(e -> cardLayout.show(mainPanel, "Welcome"));
            } else if (label.equals("View Donors")) {
                button.addActionListener(e -> showDonorsView());
            } else if (label.equals("View Requestors")) {
                button.addActionListener(e -> showRequestorsView());
            } else if (label.equals("Pending Requests")) {
                button.addActionListener(e -> showPendingRequestsView());
            } else if (label.equals("View Inventory")) {
                button.addActionListener(e -> showInventoryView());
            } else if (label.equals("Hemovigilance Report")) {
                button.addActionListener(e -> showHemovigilanceView());
            }
            
            sidebarPanel.add(button);
        }
        
        // Main content area 
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(sidebarPanel, BorderLayout.WEST);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(panel, "AdminDashboard");
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        return button;
    }
    
    private void registerDonor() {
        // Check all fields are filled
        if (donorNameField.getText().trim().isEmpty() ||
            donorAgeField.getText().trim().isEmpty() ||
            donorCnicField.getText().trim().isEmpty() ||
            donorAddressField.getText().trim().isEmpty() ||
            donorContactField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(mainFrame, 
                "All fields must be filled!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String name = donorNameField.getText().trim();
        if (name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) {
            JOptionPane.showMessageDialog(mainFrame, "Invalid name! Only letters and spaces allowed.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String ageStr = donorAgeField.getText().trim();
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 17) {
                throw new IllegalArgumentException("Age must be at least 17 years for blood donation.");
            }
            if (age > 70) {
                throw new IllegalArgumentException("Age must be 70 years or below for blood donation.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Age must be a valid number (e.g., 25).", 
                "Invalid Age Format", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Invalid Age Range", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String cnic = donorCnicField.getText().trim();
        if (!cnic.matches("^\\d{5}-\\d{7}-\\d$")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid CNIC format! Must be: XXXXX-XXXXXXX-X", 
                "Invalid CNIC", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String address = donorAddressField.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Address cannot be empty.", 
                "Address Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String contact = donorContactField.getText().trim();
        if (!contact.matches("^\\d{11}$")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Contact number must be exactly 11 digits.", 
                "Invalid Contact", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (contact.equals("00000000000")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Contact number cannot be all zeros!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!donorConsentCheck.isSelected()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Consent must be given for blood donation.", 
                "Consent Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String amountStr = donorAmountField.getText().trim();
        int donationAmount;
        try {
            donationAmount = Integer.parseInt(amountStr);
            if (donationAmount < 350) {
                throw new IllegalArgumentException("Donation amount must be at least 350ml.");
            }
            if (donationAmount > 500) {
                throw new IllegalArgumentException("Donation amount cannot exceed 500ml.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Donation amount must be a valid number.", 
                "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Invalid Donation Amount", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create donor and register
        try {
            String health = (String) donorHealthCombo.getSelectedItem();
            if (health == null) {
                throw new IllegalStateException("Health status not selected.");
            }
            
            boolean consent = donorConsentCheck.isSelected();
            
            String bloodType = (String) donorBloodTypeCombo.getSelectedItem();
            if (bloodType == null) {
                throw new IllegalStateException("Blood type not selected.");
            }
            
            String component = (String) donorComponentCombo.getSelectedItem();
            if (component == null) {
                throw new IllegalStateException("Component type not selected.");
            }
            
            Donor donor = new Donor(name, age, cnic, address, contact, health, consent);
            
            // Check if donor is eligible
            if (donor.checkEligibility()) {
                // Create blood unit
                BloodUnit unit = donor.donateBlood(bloodType, component, donationAmount);
                if (unit != null) {
                    // Test the blood unit
                    Testing testing = new Testing();
                    testing.screenInfections(unit);
                    
                    if (testing.testBloodUnit(unit)) {
                        // Add to inventory
                        admin.getInventory().addUnit(unit);
                        
                        admin.getInventory().saveToFile("blood_units.txt");
                        
                        // Add donor to admin's list
                        admin.getDonors().add(donor);
                        
                        JOptionPane.showMessageDialog(mainFrame, 
                            " Donor registered successfully!\nBlood unit added to inventory.\n" +
                            "Unit ID: " + unit.getUnitID() + "\n" +
                            "Blood Type: " + unit.getBloodType() + "\n" +
                            "Amount: " + unit.getDonationAmount() + "ml",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        
                        clearDonorForm();
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, 
                            " Blood unit failed safety testing!\nDonor registration cancelled.", 
                            "Testing Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, 
                        " Donor not eligible for donation.", 
                        "Eligibility Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Donor not eligible, just add to list
                admin.getDonors().add(donor);
                JOptionPane.showMessageDialog(mainFrame, 
                    "Donor registered but not eligible for donation.\n" +
                    "Reason: Health status or consent issue.",
                    "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                clearDonorForm();
            }
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid donor data: " + e.getMessage(), 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Missing required information: " + e.getMessage(), 
                "Missing Data", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "System error: Missing required data.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Unexpected error registering donor: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearDonorForm() {
        donorNameField.setText("");
        donorAgeField.setText("");
        donorCnicField.setText("");
        donorAddressField.setText("");
        donorContactField.setText("");
        donorHealthCombo.setSelectedIndex(0);
        donorBloodTypeCombo.setSelectedIndex(0);
        donorComponentCombo.setSelectedIndex(0);
        donorConsentCheck.setSelected(false);
        donorAmountField.setText("450");
    }
    
    private void checkRequestorCNIC() {
        String cnic = requestCnicField.getText().trim();
        
        if (!cnic.matches("^\\d{5}-\\d{7}-\\d$")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid CNIC format! Must be: XXXXX-XXXXXXX-X", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if requestor exists
            boolean exists = false;
            for (Requestor r : admin.getRequestors()) {
                if (r.getcnic() != null && r.getcnic().equals(cnic)) {
                    exists = true;
                    break;
                }
            }
            
            if (exists) {
                requestCardLayout.show(requestCardPanel, "Existing");
            } else {
                // Clear new requestor form
                newNameField.setText("");
                newAgeField.setText("");
                newAddressField.setText("");
                newContactField.setText("");
                requestBloodTypeCombo.setSelectedIndex(0);
                requestUnitsField.setText("1");
                requestCardLayout.show(requestCardPanel, "New");
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Requestors list is not available.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error checking CNIC: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void submitNewRequest() {
        // Validate new requestor form
        String name = newNameField.getText().trim();
        if (name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid name! Only letters and spaces allowed.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String ageStr = newAgeField.getText().trim();
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 1) {
                throw new IllegalArgumentException("Age must be at least 1 year.");
            }
            if (age > 120) {
                throw new IllegalArgumentException("Invalid input.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Age must be a valid number.", 
                "Invalid Age", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Invalid Age Range", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String address = newAddressField.getText().trim();
        if (address.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Address cannot be empty.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String contact = newContactField.getText().trim();
        if (!contact.matches("^\\d{11}$")) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Contact must be exactly 11 digits.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String unitsStr = requestUnitsField.getText().trim();
        int units;
        try {
            units = Integer.parseInt(unitsStr);
            if (units <= 0) {
                throw new IllegalArgumentException("Units must be a positive number.");
            }
            if (units > 10) {
                throw new IllegalArgumentException("Cannot request more than 10 units at once.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Units must be a valid number.", 
                "Invalid Units", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Invalid Unit Count", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String cnic = requestCnicField.getText().trim();
        String bloodType = (String) requestBloodTypeCombo.getSelectedItem();
        if (bloodType == null) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Please select a blood type.", 
                "Missing Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create requestor and submit request
        PrintWriter pw = null;
        try {
            Requestor requestor = new Requestor(name, age, cnic, address, contact);
            admin.addRequestor(requestor);
            requestor.makeRequest(bloodType, units);
            
            // Save to file
            try {
                pw = new PrintWriter(new FileWriter("requester.csv", true));
                pw.print(requestor.toStorageString());
                pw.flush();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error: Request file not found.\n" + e.getMessage(), 
                    "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error: Could not save request data.\n" + e.getMessage(), 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Exception e) {
                        System.err.println("Error closing PrintWriter: " + e.getMessage());
                    }
                }
            }
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Blood request submitted successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Return to welcome screen
            cardLayout.show(mainPanel, "Welcome");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid requestor data: " + e.getMessage(), 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "System error: Missing required data.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error submitting request: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void submitExistingRequest(JComboBox<String> bloodCombo, JTextField unitsField) {
        String cnic = requestCnicField.getText().trim();
        String bloodType = (String) bloodCombo.getSelectedItem();
        if (bloodType == null) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Please select a blood type.", 
                "Missing Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String unitsStr = unitsField.getText().trim();
        int units;
        try {
            units = Integer.parseInt(unitsStr);
            if (units <= 0) {
                throw new IllegalArgumentException("Units must be a positive number.");
            }
            if (units > 10) {
                throw new IllegalArgumentException("Cannot request more than 10 units at once.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Units must be a valid number.", 
                "Invalid Units", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Invalid Unit Count", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find the requestor
        Requestor requestor = null;
        try {
            for (Requestor r : admin.getRequestors()) {
                if (r.getcnic() != null && r.getcnic().equals(cnic)) {
                    requestor = r;
                    break;
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Requestors list is not available.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (requestor == null) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Requestor not found. Please register as a new requestor.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PrintWriter pw = null;
        try {
            requestor.makeRequest(bloodType, units);
            
            // Save to file
            try {
                pw = new PrintWriter(new FileWriter("requester.csv", true));
                pw.print(requestor.toStorageString());
                pw.flush();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error: Request file not found.\n" + e.getMessage(), 
                    "File Error", JOptionPane.ERROR_MESSAGE);
                return;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error: Could not save request data.\n" + e.getMessage(), 
                    "Save Error", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                if (pw != null) {
                    try {
                        pw.close();
                    } catch (Exception e) {
                        System.err.println("Error closing PrintWriter: " + e.getMessage());
                    }
                }
            }
            
            JOptionPane.showMessageDialog(mainFrame, 
                "Blood request submitted successfully for " + requestor.getName() + "!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Return to welcome screen
            cardLayout.show(mainPanel, "Welcome");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid request data: " + e.getMessage(), 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "System error: Missing requestor data.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error submitting request: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performAdminLogin() {
        String username = adminUserField.getText().trim();
        String password = new String(adminPassField.getPassword());
        
        if (username.equals("admin") && password.equals("12345")) {
            cardLayout.show(mainPanel, "AdminDashboard");
        } else {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid username or password!", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showDonorsView() {
        try {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(4);
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2);
            contentPanel.removeAll();
            
            JLabel titleLabel = new JLabel("REGISTERED DONORS", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            String[] columns = {"Name", "Age", "CNIC", "Contact", "Health", "Consent", "Eligible"};
            donorsModel = new DefaultTableModel(columns, 0);
            
            donorsTable = new JTable(donorsModel);
            donorsTable.setFont(new Font("Arial", Font.PLAIN, 12));
            donorsTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(donorsTable);
            
            JButton refreshButton = createMenuButton("Refresh", THEME_RED);
            refreshButton.addActionListener(e -> loadDonorsData());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(refreshButton);
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            loadDonorsData();
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Admin dashboard panel not found.", 
                "Panel Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Invalid panel configuration.", 
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: GUI components not properly initialized.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showRequestorsView() {
        try {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(4);
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2);
            contentPanel.removeAll();
            
            JLabel titleLabel = new JLabel("REGISTERED REQUESTORS", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            String[] columns = {"Name", "Age", "CNIC", "Contact", "Address"};
            requestorsModel = new DefaultTableModel(columns, 0);
            
            requestorsTable = new JTable(requestorsModel);
            requestorsTable.setFont(new Font("Arial", Font.PLAIN, 12));
            requestorsTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(requestorsTable);
            
            JButton refreshButton = createMenuButton("Refresh", new Color(70, 130, 180));
            refreshButton.addActionListener(e -> loadRequestorsData());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(refreshButton);
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            loadRequestorsData();
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Admin dashboard panel not found.", 
                "Panel Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Invalid panel configuration.", 
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: GUI components not properly initialized.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showPendingRequestsView() {
        try {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(4);
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2);
            contentPanel.removeAll();
            
            JLabel titleLabel = new JLabel("PENDING BLOOD REQUESTS", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            String[] columns = {"Requester", "Blood Type", "Units", "Status", "Action"};
            pendingModel = new DefaultTableModel(columns, 0) {
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };
            
            pendingTable = new JTable(pendingModel);
            pendingTable.setFont(new Font("Arial", Font.PLAIN, 12));
            pendingTable.setRowHeight(30);
            
            // Add button column
            pendingTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
            pendingTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
            
            JScrollPane scrollPane = new JScrollPane(pendingTable);
            
            JButton refreshButton = createMenuButton("Refresh", new Color(46, 139, 87));
            refreshButton.addActionListener(e -> loadPendingRequestsData());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(refreshButton);
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            loadPendingRequestsData();
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Admin dashboard panel not found.", 
                "Panel Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Invalid panel configuration.", 
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: GUI components not properly initialized.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showInventoryView() {
        try {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(4);
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2);
            contentPanel.removeAll();
            
            JLabel titleLabel = new JLabel("BLOOD INVENTORY", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            String[] columns = {"Unit ID", "Blood Type", "Amount (ml)", "Donation Date", "Expiry Date", "Donor", "Status"};
            inventoryModel = new DefaultTableModel(columns, 0);
            
            inventoryTable = new JTable(inventoryModel);
            inventoryTable.setFont(new Font("Arial", Font.PLAIN, 12));
            inventoryTable.setRowHeight(25);
            
            JScrollPane scrollPane = new JScrollPane(inventoryTable);
            
            JButton refreshButton = createMenuButton("Refresh", THEME_RED);
            refreshButton.addActionListener(e -> loadInventoryData());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(refreshButton);
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            loadInventoryData();
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Admin dashboard panel not found.", 
                "Panel Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Invalid panel configuration.", 
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: GUI components not properly initialized.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showHemovigilanceView() {
        try {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(4);
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2);
            contentPanel.removeAll();
            
            JLabel titleLabel = new JLabel("HEMOVIGILANCE REPORT", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            hemovigilanceArea = new JTextArea();
            hemovigilanceArea.setFont(new Font("Arial", Font.PLAIN, 12));
            hemovigilanceArea.setEditable(false);
            hemovigilanceArea.setLineWrap(true);
            hemovigilanceArea.setWrapStyleWord(true);
            
            JScrollPane scrollPane = new JScrollPane(hemovigilanceArea);
            scrollPane.setBorder(new LineBorder(Color.GRAY, 1));
            
            JButton refreshButton = createMenuButton("Refresh", new Color(128, 0, 128));
            refreshButton.addActionListener(e -> loadHemovigilanceData());
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(refreshButton);
            
            contentPanel.add(titleLabel, BorderLayout.NORTH);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            loadHemovigilanceData();
            contentPanel.revalidate();
            contentPanel.repaint();
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Admin dashboard panel not found.", 
                "Panel Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassCastException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Invalid panel configuration.", 
                "Configuration Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: GUI components not properly initialized.", 
                "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
   private void loadDonorsData() {
    try {
        donorsModel.setRowCount(0);
        
        // Clear existing donors first
        admin.getDonors().clear();
        
        // Read donors from blood_units.txt
        File bloodUnitsFile = new File("blood_units.txt");
        if (bloodUnitsFile.exists() && bloodUnitsFile.canRead()) {
            try (BufferedReader br = new BufferedReader(new FileReader(bloodUnitsFile))) {
                String line;
                Set<String> processedCNICs = new HashSet<>(); // To avoid duplicates
                
                while ((line = br.readLine()) != null) {
                    try {
                        String[] arr = line.split(",");
                        if (arr.length < 7) continue;
                        
                        String donorName = arr[4];
                        String donorCnic = arr[5];
                        
                        // Skip if we've already processed this donor
                        if (processedCNICs.contains(donorCnic)) {
                            continue;
                        }
                        
                        processedCNICs.add(donorCnic);
                        
                        // Create donor (using default values for missing info)
                        Donor donor = new Donor(
                            donorName, 
                            0, // Age not stored in blood_units.txt
                            donorCnic, 
                            "Not stored", // Address not stored
                            "Not stored", // Contact not stored
                            "Healthy", // Assume healthy (they donated!)
                            true // Assume consent given
                        );
                        
                        admin.getDonors().add(donor);
                        
                        // Add to table
                        donorsModel.addRow(new Object[]{
                            donor.getName(),
                            donor.getage(),
                            donor.getcnic(),
                            donor.getcontact(),
                            donor.getHealthStatus(),
                            donor.getconsent() ? "Yes" : "No",
                            donor.checkEligibility() ? "Yes" : "No"
                        });
                        
                    } catch (Exception e) {
                        // Skip invalid lines
                        continue;
                    }
                }
            }
        }
        
        // If no donors found in file, show message
        if (donorsModel.getRowCount() == 0) {
            donorsModel.addRow(new Object[]{
                "No donors found", 
                "", "", "", "", "", ""
            });
        }
        
    } catch (FileNotFoundException e) {
        donorsModel.addRow(new Object[]{
            "No blood units file found", 
            "", "", "", "", "", ""
        });
    } catch (IOException e) {
        donorsModel.addRow(new Object[]{
            "Error reading file: " + e.getMessage(), 
            "", "", "", "", "", ""
        });
    } catch (NullPointerException e) {
        JOptionPane.showMessageDialog(mainFrame, 
            "Error: Donors list is not available.", 
            "Data Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(mainFrame, 
            "Error loading donors data: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    private void loadRequestorsData() {
        try {
            requestorsModel.setRowCount(0);
            for (Requestor requestor : admin.getRequestors()) {
                if (requestor != null) {
                    requestorsModel.addRow(new Object[]{
                        requestor.getName(),
                        requestor.getage(),
                        requestor.getcnic(),
                        requestor.getcontact(),
                        requestor.getAddress()
                    });
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Requestors list is not available.", 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error loading requestors data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPendingRequestsData() {
        try {
            pendingModel.setRowCount(0);
            ArrayList<Request> pendingRequests = admin.getPendingRequests();
            
            if (pendingRequests == null) {
                throw new IllegalStateException("Pending requests list is null.");
            }
            
            for (int i = 0; i < pendingRequests.size(); i++) {
                Request req = pendingRequests.get(i);
                if (req != null && req.getRequestor() != null) {
                    final int index = i;
                    JButton issueButton = new JButton("Issue Blood");
                    issueButton.addActionListener(e -> issueBloodForRequest(index));
                    
                    pendingModel.addRow(new Object[]{
                        req.getRequestor().getName(),
                        req.getBloodType(),
                        req.getUnitsRequested(),
                        req.getStatus(),
                        issueButton
                    });
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Pending requests data is not available.", 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error loading pending requests: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadInventoryData() {
        try {
            inventoryModel.setRowCount(0);
            for (BloodUnit unit : admin.getInventory().getUnits()) {
                if (unit != null && unit.getDonor() != null) {
                    // Check if this unit has been issued
                    boolean isIssued = false;
                    
                    inventoryModel.addRow(new Object[]{
                        unit.getUnitID(),
                        unit.getBloodType(),
                        unit.getDonationAmount(),
                        unit.getDonationDate(),
                        unit.getExpiryDate(),
                        unit.getDonor().getName(),
                        isIssued ? " " : "Available"
                    });
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error: Inventory data is not available.", 
                "Data Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error loading inventory data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadHemovigilanceData() {
        try {
            File file = new File("hemovigilance.txt");
            if (file.exists() && file.canRead()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    hemovigilanceArea.setText(content.toString());
                }
            } else {
                hemovigilanceArea.setText("No hemovigilance events recorded yet.");
            }
        } catch (FileNotFoundException e) {
            hemovigilanceArea.setText("Hemovigilance file not found.");
        } catch (IOException e) {
            hemovigilanceArea.setText("Error reading hemovigilance file: " + e.getMessage());
        } catch (SecurityException e) {
            hemovigilanceArea.setText("Permission denied to read hemovigilance file.");
        } catch (Exception e) {
            hemovigilanceArea.setText("Error loading hemovigilance data: " + e.getMessage());
        }
    }
    
    private void issueBloodForRequest(int requestIndex) {
    ArrayList<Request> pendingRequests = null;
    try {
        pendingRequests = admin.getPendingRequests();
        if (pendingRequests == null) {
            throw new IllegalStateException("Pending requests list is not available.");
        }
        
        if (requestIndex < 0 || requestIndex >= pendingRequests.size()) {
            throw new IndexOutOfBoundsException("Invalid request selection.");
        }
    } catch (IllegalStateException e) {
        JOptionPane.showMessageDialog(mainFrame, e.getMessage(), 
            "Data Error", JOptionPane.ERROR_MESSAGE);
        return;
    } catch (IndexOutOfBoundsException e) {
        JOptionPane.showMessageDialog(mainFrame, "Invalid request selection.", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    Request selectedRequest = pendingRequests.get(requestIndex);
    
    if (selectedRequest == null || selectedRequest.getRequestor() == null) {
        JOptionPane.showMessageDialog(mainFrame, 
            "Error: Invalid request data.", 
            "Data Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(mainFrame,
        "Issue blood for:\n" +
        "Requester: " + selectedRequest.getRequestor().getName() + "\n" +
        "Blood Type: " + selectedRequest.getBloodType() + "\n" +
        "Units: " + selectedRequest.getUnitsRequested() + "\n\n" +
        "Are you sure?",
        "Confirm Issue", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            // Capture the console output by redirecting it
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);
            
            // Execute the blood issue
            admin.issueBlood(selectedRequest);
            
            // Restore original System.out
            System.setOut(old);
            
            // Get all console messages
            String consoleOutput = baos.toString();
            
            // Show ALL console messages in one big popup
            StringBuilder guiMessage = new StringBuilder();
            guiMessage.append("BLOOD ISSUED PROCESS\n");
            guiMessage.append("====================\n\n");
            
            // Parse and format the console output for GUI
            String[] lines = consoleOutput.split("\n");
            for (String line : lines) {
                if (line.contains("We didn't have")) {
                    guiMessage.append(" ").append(line.trim()).append("\n\n");
                } else if (line.contains("Issuing")) {
                    guiMessage.append(" ").append(line.trim()).append("\n\n");
                } else if (line.contains("Hemovigilance: Monitoring")) {
                    guiMessage.append(" ").append(line.trim()).append("\n");
                } else if (line.contains("Adverse Reaction Detected")) {
                    guiMessage.append("").append(line.trim()).append("\n");
                    if (line.contains("Calling an ambulance")) {
                        guiMessage.append(" Calling an ambulance!\n");
                    }
                } else if (line.contains("No adverse reaction")) {
                    guiMessage.append(" ").append(line.trim()).append("\n");
                } else if (line.contains("Blood issued successfully")) {
                    guiMessage.append("\n ").append(line.trim()).append("\n");
                }
            }
            
            JOptionPane.showMessageDialog(mainFrame,
                guiMessage.toString(),
                "Blood Issued - Complete Process", 
                JOptionPane.INFORMATION_MESSAGE);
    
            JOptionPane.showMessageDialog(mainFrame,
                "BLOOD ISSUED SUCCESSFULLY!\n\n" +
                "Requester: " + selectedRequest.getRequestor().getName() + "\n" +
                "Blood Type: " + selectedRequest.getBloodType() + "\n" +
                "Units: " + selectedRequest.getUnitsRequested(),
                "Blood Issued", JOptionPane.INFORMATION_MESSAGE);
            
            loadPendingRequestsData();
            loadInventoryData();
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Invalid blood issue request: " + e.getMessage(), 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Cannot issue blood: " + e.getMessage(), 
                "Issue Error", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "System error: Missing required data for blood issue.", 
                "System Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Error issuing blood: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private void saveAndExit() {
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Save data and exit?", 
            "Confirm Exit", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                admin.getInventory().saveToFile("blood_units.txt");
                JOptionPane.showMessageDialog(mainFrame, 
                    "Data saved. Goodbye!", 
                    "Exit", JOptionPane.INFORMATION_MESSAGE);
                 } catch (SecurityException e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Warning: Permission denied to save inventory data.", 
                    "Security Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Warning: Could not save inventory data: " + e.getMessage(), 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            }
            System.exit(0);
        }
    }
    
    // Button renderer for JTable
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            try {
                if (value instanceof JButton) {
                    return (JButton) value;
                } else if (value instanceof String) {
                    setText((String) value);
                } else {
                    setText("Issue");
                }
            } catch (ClassCastException e) {
                setText("Error");
                System.err.println("ClassCastException in renderer: " + e.getMessage());
            }
            return this;
        }
    }
    
    // Button editor for JTable
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int clickedRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            clickedRow = row;
            try {
                if (value instanceof JButton) {
                    button = (JButton) value;
                } else {
                    button.setText("Issue");
                }
            } catch (ClassCastException e) {
                button.setText("Error");
                System.err.println("ClassCastException in editor: " + e.getMessage());
            }
            return button;
        }
        
        public Object getCellEditorValue() {
            try {
                for (ActionListener al : button.getActionListeners()) {
                    al.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, ""));
                }
            } catch (Exception e) {
                System.err.println("Error in button editor action: " + e.getMessage());
            }
            return button;
        }
    }
}