
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages Students in memory. Demonstrates Single Responsibility:
 * this class handles student collection operations.
 */
public class StudentManager {
    private final Map<Integer, Student> students = new HashMap<>();
    private int nextId = 1;

    public synchronized Student addStudent(String firstName, String lastName, LocalDate dob, String major, double gpa) {
        Student s = new Student(firstName, lastName, dob, major, gpa);
        s.setId(nextId++);
        students.put(s.getId(), s);
        return s;
    }

    public synchronized boolean removeStudent(int id) {
        return students.remove(id) != null;
    }

    public synchronized Optional<Student> getStudent(int id) {
        return Optional.ofNullable(students.get(id));
    }

    public synchronized List<Student> listAll() {
        return students.values().stream().sorted().collect(Collectors.toList());
    }

    public synchronized List<Student> searchByName(String nameQuery) {
        String q = nameQuery.toLowerCase(Locale.ROOT);
        return students.values().stream()
                .filter(s -> s.getFirstName().toLowerCase(Locale.ROOT).contains(q)
                          || s.getLastName().toLowerCase(Locale.ROOT).contains(q)
                          || s.getFullName().toLowerCase(Locale.ROOT).contains(q))
                .sorted()
                .collect(Collectors.toList());
    }

    public synchronized boolean updateStudent(int id, String firstName, String lastName, LocalDate dob, String major, Double gpa) {
        Student s = students.get(id);
        if (s == null) return false;
        try {
            if (firstName != null && !firstName.isBlank()) s.setFirstName(firstName);
            if (lastName != null && !lastName.isBlank()) s.setLastName(lastName);
            if (dob != null) s.setDateOfBirth(dob);
            if (major != null) s.setMajor(major);
            if (gpa != null) s.setGpa(gpa);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Persistence: save/load CSV
    public synchronized void saveToCsv(String path) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            // header optional
            bw.write("#id,firstName,lastName,dob,major,gpa");
            bw.newLine();
            for (Student s : listAll()) {
                bw.write(s.toCsvLine());
                bw.newLine();
            }
        }
    }

    public synchronized void loadFromCsv(String path) throws IOException {
        Map<Integer, Student> loaded = new HashMap<>();
        int maxId = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.strip();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Student s = Student.fromCsvLine(line);
                loaded.put(s.getId(), s);
                if (s.getId() > maxId) maxId = s.getId();
            }
        }
        students.clear();
        students.putAll(loaded);
        nextId = Math.max(nextId, maxId + 1);
    }
}
