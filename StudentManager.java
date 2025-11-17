
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    // CSV persistence removed
}