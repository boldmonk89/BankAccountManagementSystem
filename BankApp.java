import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

interface BankServices {
    void changePassword(Scanner sc);
    void changePin(Scanner sc);
}

abstract class BankAccount implements BankServices {
    private long accountNumber;
    private String fullName;
    private String firstName;
    private String dob; // dd/MM/yyyy
    private int age;
    private String guardianName;
    private String guardianRelation;
    private double balance;
    private String password; // Master password (complex)
    private String pin; // 4-digit pin stored as String to preserve leading zeros
    private List<String> transactions = new ArrayList<>();
    protected static int accountCount = 0; // static variable

    // Constructors
    public BankAccount() {
        this.accountNumber = generateAccountNumber();
        accountCount++;
    }

    public BankAccount(String fullName, String dob) {
        this(); // call default constructor
        this.fullName = fullName.trim();
        this.firstName = extractFirstName(fullName);
        this.dob = dob;
        this.age = calculateAgeFromDOB(dob);
        if (this.age < 18) {
            // guardian will be set by caller
        }
        this.balance = 0.0;
    }

    // Helper methods
    protected long generateAccountNumber() {
        // generate 8-digit random account number
        return ThreadLocalRandom.current().nextLong(10000000L, 100000000L);
    }

    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : fullName;
    }

    private int calculateAgeFromDOB(String dob) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate birth = LocalDate.parse(dob, fmt);
            LocalDate now = LocalDate.now();
            return (int) ChronoUnit.YEARS.between(birth, now);
        } catch (Exception ex) {
            return 0; // invalid format, caller should ensure valid DOB
        }
    }

    protected void addTransaction(String txn) {
        transactions.add(txn);
    }

    // Abstract methods
    public abstract void deposit(double amount);

    public abstract void withdraw(double amount);

    public abstract void checkBalance();

    // Overloaded methods (polymorphism overloading)
    public void deposit(double amount, String description) {
        deposit(amount);
        addTransaction("Deposit: " + amount + " " + description + " | Date: " + LocalDateTime.now());
    }

    public void withdraw(double amount, String purpose) {
        withdraw(amount);
        addTransaction("Withdraw: " + amount + " | Purpose: " + purpose + " | Date: " + LocalDateTime.now());
    }

    // Encapsulation: getters & setters with validations
    public long getAccountNumber() {
        return accountNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDob() {
        return dob;
    }

    public int getAge() {
        return age;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianRelation() {
        return guardianRelation;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPassword() {
        return password;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    public String getPin() {
        return pin;
    }

    protected void setPin(String pin) {
        this.pin = pin;
    }

    public List<String> getTransactions() {
        return transactions;
    }

    public static int getAccountCount() {
        return accountCount;
    }

    public void setGuardian(String name, String relation) {
        this.guardianName = name;
        this.guardianRelation = relation;
    }

    public void displayAccountDetails() {
        System.out.println("=== Account Details ===");
        System.out.println("Account Number: " + getAccountNumber());
        System.out.println("Name (Displayed): " + getFirstName()); // only first name visible
        System.out.println("DOB: " + getDob());
        System.out.println("Age: " + getAge());
        if (getAge() < 18) {
            System.out.println("Guardian: " + getGuardianName() + " (" + getGuardianRelation() + ")");
        }
        System.out.println("Balance: ₹" + String.format("%.2f", getBalance()));
    }

    public void showLastNTransactions(int n) {
        System.out.println("=== Last " + n + " Transactions ===");
        int size = transactions.size();
        if (size == 0) {
            System.out.println("No transactions yet.");
            return;
        }
        int start = Math.max(0, size - n);
        for (int i = start; i < size; i++) {
            System.out.println(transactions.get(i));
        }
    }

    // Interface implementations
    @Override
    public void changePassword(Scanner sc) {
        System.out.println("Change Password:");
        while (true) {
            System.out.print("Enter new password: ");
            String newPass = sc.nextLine();
            if (BankApp.isValidPassword(newPass)) {
                setPassword(newPass);
                System.out.println("Password updated successfully.");
                addTransaction("Password changed on " + LocalDateTime.now());
                break;
            } else {
                System.out.println("Password does not meet complexity requirements. Try again.");
            }
        }
    }

    @Override
    public void changePin(Scanner sc) {
        System.out.println("Change PIN:");
        while (true) {
            System.out.print("Enter new 4-digit PIN: ");
            String newPin = sc.nextLine().trim();
            if (BankApp.isValidPin(newPin, this.getDob())) {
                setPin(newPin);
                System.out.println("PIN updated successfully.");
                addTransaction("PIN changed on " + LocalDateTime.now());
                break;
            } else {
                System.out.println("Invalid PIN. It must be 4 digits and not match DOB patterns (DDMM, MMDD, YYYY). Try again.");
            }
        }
    }
}

// Savings account with minimum balance & interest
class SavingsAccount extends BankAccount {
    private static final double MIN_BALANCE = 500.0;
    private double interestRate = 4.0; // simple annual percentage for demonstration

    public SavingsAccount() {
    }

    public SavingsAccount(String fullName, String dob) {
        super(fullName, dob);
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        setBalance(getBalance() + amount);
        addTransaction("Deposit: " + amount + " | Date: " + LocalDateTime.now());
        System.out.println("₹" + amount + " deposited. New balance: ₹" + String.format("%.2f", getBalance()));
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdraw amount must be positive.");
            return;
        }
        if (getBalance() - amount < MIN_BALANCE) {
            System.out.println("Withdrawal denied. Savings account must maintain minimum balance of ₹" + MIN_BALANCE);
            return;
        }
        setBalance(getBalance() - amount);
        addTransaction("Withdraw: " + amount + " | Date: " + LocalDateTime.now());
        System.out.println("₹" + amount + " withdrawn. New balance: ₹" + String.format("%.2f", getBalance()));
    }

    @Override
    public void checkBalance() {
        System.out.println("Current Savings Balance: ₹" + String.format("%.2f", getBalance()));
        addTransaction("Balance inquiry on " + LocalDateTime.now());
    }

    // Overriding toString for account type
    @Override
    public String toString() {
        return "SavingsAccount";
    }

    public void applySimpleInterest(int years) {
        if (years <= 0) return;
        double principal = getBalance();
        double interest = (principal * interestRate * years) / 100.0;
        setBalance(getBalance() + interest);
        addTransaction("Interest applied: " + interest + " for " + years + " years");
        System.out.println("Interest ₹" + String.format("%.2f", interest) + " added. New balance: ₹" + String.format("%.2f", getBalance()));
    }
}

// Current account with overdraft
class CurrentAccount extends BankAccount {
    private static final double OVERDRAFT_LIMIT = 5000.0;

    public CurrentAccount() {
    }

    public CurrentAccount(String fullName, String dob) {
        super(fullName, dob);
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        setBalance(getBalance() + amount);
        addTransaction("Deposit: " + amount + " | Date: " + LocalDateTime.now());
        System.out.println("₹" + amount + " deposited. New balance: ₹" + String.format("%.2f", getBalance()));
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdraw amount must be positive.");
            return;
        }
        if (getBalance() - amount < -OVERDRAFT_LIMIT) {
            System.out.println("Withdrawal denied. Exceeds overdraft limit of ₹" + OVERDRAFT_LIMIT);
            return;
        }
        setBalance(getBalance() - amount);
        addTransaction("Withdraw: " + amount + " | Date: " + LocalDateTime.now());
        System.out.println("₹" + amount + " withdrawn. New balance: ₹" + String.format("%.2f", getBalance()));
    }

    @Override
    public void checkBalance() {
        System.out.println("Current Account Balance: ₹" + String.format("%.2f", getBalance()));
        addTransaction("Balance inquiry on " + LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "CurrentAccount";
    }
}

public class BankApp {
    private static Scanner sc = new Scanner(System.in);

    // Password validation: min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special char
    public static boolean isValidPassword(String pwd) {
        if (pwd == null || pwd.length() < 8) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : pwd.toCharArray()) {
            if (Character.isUpperCase(c)) upper = true;
            else if (Character.isLowerCase(c)) lower = true;
            else if (Character.isDigit(c)) digit = true;
            else special = true;
        }
        return upper && lower && digit && special;
    }

    // PIN validation: 4 digits, not DDMM, not MMDD, not YYYY from DOB
    public static boolean isValidPin(String pin, String dob) {
        if (pin == null || !pin.matches("\\d{4}")) return false;
        if (dob == null || !dob.matches("\\d{2}/\\d{2}/\\d{4}")) return true; // if DOB not known, only check digits
        String dd = dob.substring(0, 2);
        String mm = dob.substring(3, 5);
        String yyyy = dob.substring(6, 10);
        String ddmm = dd + mm;
        String mmdd = mm + dd;
        String yy = yyyy.substring(2); // last two digits of the year
        if (pin.equals(ddmm) || pin.equals(mmdd) || pin.equals(yyyy)) {
            return false;
        }
        return true;
    }

    // Helper to read non-empty line
    private static String readNonEmptyLine(String prompt) {
        String line;
        do {
            System.out.print(prompt);
            line = sc.nextLine().trim();
        } while (line.isEmpty());
        return line;
    }

    // DOB validation
    private static boolean isValidDOB(String dob) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate birth = LocalDate.parse(dob, fmt);
            // reasonable DOB check: not in future and year > 1900
            return !birth.isAfter(LocalDate.now()) && birth.getYear() > 1900;
        } catch (Exception ex) {
            return false;
        }
    }

    // Create account flow
    private static BankAccount createAccount() {
        System.out.println("=== Create New Account ===");
        String fullName = readNonEmptyLine("Enter full name: ");
        String dob;
        while (true) {
            dob = readNonEmptyLine("Enter Date of Birth (dd/MM/yyyy): ");
            if (isValidDOB(dob)) break;
            System.out.println("Invalid DOB format or unrealistic date. Please enter again.");
        }
        // Calculate age
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate birth = LocalDate.parse(dob, fmt);
        int age = (int) ChronoUnit.YEARS.between(birth, LocalDate.now());
        String accType;
        while (true) {
            System.out.print("Choose account type (1. Savings, 2. Current): ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) {
                accType = "Savings";
                break;
            } else if (choice.equals("2")) {
                accType = "Current";
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        BankAccount account;
        if (accType.equals("Savings")) {
            account = new SavingsAccount(fullName, dob);
        } else {
            account = new CurrentAccount(fullName, dob);
        }
        if (age < 18) {
            System.out.println("Applicant is a minor (age " + age + "). Guardian details required.");
            String gName = readNonEmptyLine("Enter Guardian/Adult Name: ");
            String gRelation = readNonEmptyLine("Enter relation with guardian/adult: ");
            account.setGuardian(gName, gRelation);
        }
        // Password setup loop
        while (true) {
            System.out.print("Set a password (min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special): ");
            String pwd = sc.nextLine();
            if (isValidPassword(pwd)) {
                account.setPassword(pwd);
                break;
            } else {
                System.out.println("Password doesn't meet complexity requirements. Try again.");
            }
        }
        // PIN setup loop
        while (true) {
            System.out.print("Set a 4-digit PIN (must not equal DDMM, MMDD, or YYYY of DOB): ");
            String pin = sc.nextLine().trim();
            if (isValidPin(pin, dob)) {
                account.setPin(pin);
                break;
            } else {
                System.out.println("Invalid PIN. It cannot be DOB patterns. Try again.");
            }
        }
        System.out.println("Account successfully created!");
        account.addTransaction("Account created on " + LocalDateTime.now());
        account.displayAccountDetails();
        return account;
    }

    // Authentication: allow 3 attempts for (password + pin)
    private static boolean authenticate(BankAccount account) {
        if (account == null) return false;
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Enter password: ");
            String pwd = sc.nextLine();
            System.out.print("Enter 4-digit PIN: ");
            String pin = sc.nextLine().trim();
            if (pwd.equals(account.getPassword()) && pin.equals(account.getPin())) {
                account.addTransaction("Successful login on " + LocalDateTime.now());
                return true;
            } else {
                attempts++;
                System.out.println("Invalid credentials. Attempts left: " + (3 - attempts));
                account.addTransaction("Failed login attempt on " + LocalDateTime.now());
            }
        }
        System.out.println("Account locked due to 3 failed attempts.");
        account.addTransaction("Account locked after 3 failed attempts on " + LocalDateTime.now());
        return false;
    }

    private static void showMenu(BankAccount account) {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== Transaction Menu ===");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Check Balance");
            System.out.println("4. Change Password");
            System.out.println("5. Change PIN");
            System.out.println("6. View Account Details (first name shown)");
            System.out.println("7. Show last 5 transactions");
            System.out.println("8. Apply interest (Savings only)");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1":
                    try {
                        System.out.print("Enter amount to deposit: ");
                        double amt = Double.parseDouble(sc.nextLine());
                        System.out.print("Optional description (press enter to skip): ");
                        String desc = sc.nextLine();
                        if (desc.trim().isEmpty()) account.deposit(amt);
                        else account.deposit(amt, desc);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid amount.");
                    }
                    break;
                case "2":
                    try {
                        System.out.print("Enter amount to withdraw: ");
                        double wamt = Double.parseDouble(sc.nextLine());
                        System.out.print("Optional purpose (press enter to skip): ");
                        String purp = sc.nextLine();
                        if (purp.trim().isEmpty()) account.withdraw(wamt);
                        else account.withdraw(wamt, purp);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid amount.");
                    }
                    break;
                case "3":
                    account.checkBalance();
                    break;
                case "4":
                    account.changePassword(sc);
                    break;
                case "5":
                    account.changePin(sc);
                    break;
                case "6":
                    account.displayAccountDetails();
                    break;
                case "7":
                    account.showLastNTransactions(5);
                    break;
                case "8":
                    if (account instanceof SavingsAccount) {
                        try {
                            System.out.print("Enter number of years to apply simple interest: ");
                            int yrs = Integer.parseInt(sc.nextLine());
                            ((SavingsAccount) account).applySimpleInterest(yrs);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid number.");
                        }
                    } else {
                        System.out.println("Interest application is only for Savings Account.");
                    }
                    break;
                case "9":
                    System.out.println("Exiting. Thank you!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid menu option.");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the Bank Account Management System (Demo)\n");
        List<BankAccount> accounts = new ArrayList<>();
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Create Account");
            System.out.println("2. Login to Account");
            System.out.println("3. Show total accounts created");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1":
                    BankAccount acc = createAccount();
                    accounts.add(acc);
                    break;
                case "2":
                    if (accounts.isEmpty()) {
                        System.out.println("No accounts found. Please create an account first.");
                        break;
                    }
                    System.out.print("Enter account number to login: ");
                    String accNumStr = sc.nextLine().trim();
                    try {
                        long accNum = Long.parseLong(accNumStr);
                        BankAccount found = null;
                        for (BankAccount a : accounts) {
                            if (a.getAccountNumber() == accNum) {
                                found = a;
                                break;
                            }
                        }
                        if (found == null) {
                            System.out.println("Account not found.");
                            break;
                        }
                        if (authenticate(found)) {
                            // After successful auth -> show menu
                            showMenu(found);
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid account number format.");
                    }
                    break;
                case "3":
                    System.out.println("Total accounts created: " + BankAccount.getAccountCount());
                    break;
                case "4":
                    System.out.println("Application terminating. Goodbye.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}