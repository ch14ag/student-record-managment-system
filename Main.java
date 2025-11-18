

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Simple CLI to interact with StudentManager.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();

    public static void main(String[] args) {
        System.out.println("Welcome to the Student Record Management System (simple CLI)");
        boolean running = true;
        while (running) {
            printMenu();
            String cmd = prompt("Choose an option").trim();
            switch (cmd) {
                case "1" -> handleAdd();
                case "2" -> handleUpdate();
                case "3" -> handleRemove();
                case "4" -> handleView();
                case "5" -> handleList();
                case "6" -> handleSearch();
                case "7" -> handleSave();
                case "8" -> handleLoad();
                case "0" -> {
                    running = false;
                    System.out.println("Bye.");
                }
                default -> System.out.println("Unknown option.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1) Add student");
        System.out.println("2) Update student");
        System.out.println("3) Remove student");
        System.out.println("4) View student by ID");
        System.out.println("5) List all students");
        System.out.println("6) Search by name");
        System.out.println("7) Save to CSV");
        System.out.println("8) Load from CSV");
        System.out.println("0) Exit");
    }

    private static void handleAdd() {
        try {
            String first = prompt("First name");
            String last = prompt("Last name");
            LocalDate dob = readDate("Date of birth (yyyy-MM-dd)");
            String major = prompt("Major (press enter for none)");
            double gpa = readDouble("GPA (0.0 - 4.0)", 0.0, 4.0);
            var s = manager.addStudent(first, last, dob, major, gpa);
            System.out.println("Added: " + s);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private static void handleUpdate() {
        int id = readInt("Student ID to update");
        Optional<Student> opt = manager.getStudent(id);
        if (opt.isEmpty()) {
            System.out.println("No student with ID " + id);
            return;
        }
        Student s = opt.get();
        System.out.println("Current: " + s);
        String first = prompt("New first name (leave blank to keep)");
        String last = prompt("New last name (leave blank to keep)");
        String dobStr = prompt("New dob yyyy-MM-dd (leave blank to keep)");
        LocalDate dob = null;
        if (!dobStr.isBlank()) {
            try {
                dob = LocalDate.parse(dobStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Update cancelled.");
                return;
            }
        }
        String major = prompt("New major (leave blank to keep)");
        String gpaStr = prompt("New GPA (leave blank to keep)");
        Double gpa = null;
        if (!gpaStr.isBlank()) {
            try {
                gpa = Double.parseDouble(gpaStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid GPA. Update cancelled.");
                return;
            }
        }
        boolean ok = manager.updateStudent(id,
                first.isBlank() ? null : first,
                last.isBlank() ? null : last,
                dob,
                major.isBlank() ? null : major,
                gpa);
        System.out.println(ok ? "Updated." : "Update failed.");
    }

    private static void handleRemove() {
        int id = readInt("Student ID to remove");
        boolean ok = manager.removeStudent(id);
        System.out.println(ok ? "Removed." : "No student with ID " + id);
    }

    private static void handleView() {
        int id = readInt("Student ID to view");
        Optional<Student> opt = manager.getStudent(id);
        opt.ifPresentOrElse(
                s -> System.out.println(s),
                () -> System.out.println("No student with ID " + id));
    }

    private static void handleList() {
        List<Student> all = manager.listAll();
        if (all.isEmpty()) {
            System.out.println("No students.");
        } else {
            all.forEach(System.out::println);
        }
    }

    private static void handleSearch() {
        String q = prompt("Name query");
        List<Student> results = manager.searchByName(q);
        if (results.isEmpty()) {
            System.out.println("No matches.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void handleSave() {
        String path = prompt("Path to save CSV (e.g., students.csv)");
        try {
            manager.saveToCsv(path);
            System.out.println("Saved to " + path);
        } catch (Exception e) {
            System.out.println("Failed to save: " + e.getMessage());
        }
    }

    private static void handleLoad() {
        String path = prompt("Path to load CSV (e.g., students.csv)");
        try {
            manager.loadFromCsv(path);
            System.out.println("Loaded from " + path);
        } catch (Exception e) {
            System.out.println("Failed to load: " + e.getMessage());
        }
    }

    // helpers
    private static String prompt(String label) {
        System.out.print(label + ": ");
        return scanner.nextLine();
    }

    private static int readInt(String label) {
        while (true) {
            String s = prompt(label);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Enter an integer.");
            }
        }
    }

    private static double readDouble(String label, double min, double max) {
        while (true) {
            String s = prompt(label);
            try {
                double v = Double.parseDouble(s);
                if (v < min || v > max) {
                    System.out.printf("Value must be between %.2f and %.2f%n", min, max);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Enter a numeric value.");
            }
        }
    }

    private static LocalDate readDate(String label) {
        while (true) {
            String s = prompt(label);
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("Date must be in yyyy-MM-dd format.");
            }
        }
    }
}
