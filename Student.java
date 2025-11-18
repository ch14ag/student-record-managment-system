import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Simple Student model demonstrating encapsulation and basic validation.
 */
public class Student implements Comparable<Student> {
    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth; // YYYY-MM-DD
    private String major;
    private double gpa;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public Student() {}

    public Student(String firstName, String lastName, LocalDate dateOfBirth, String major, double gpa) {
        setFirstName(firstName);
        setLastName(lastName);
        setDateOfBirth(dateOfBirth);
        setMajor(major);
        setGpa(gpa);
    }

    // ID is set by StudentManager
    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("firstName cannot be empty");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("lastName cannot be empty");
        }
        this.lastName = lastName.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public final void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("dateOfBirth cannot be null");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public void setDateOfBirth(String isoDate) {
        try {
            setDateOfBirth(LocalDate.parse(isoDate, DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("dateOfBirth must be in yyyy-MM-dd format");
        }
    }

    public String getMajor() {
        return major;
    }

    public final void setMajor(String major) {
        this.major = (major == null) ? "" : major.trim();
    }

    public double getGpa() {
        return gpa;
    }

    public final void setGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new IllegalArgumentException("gpa must be between 0.0 and 4.0");
        }
        this.gpa = gpa;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name=%s %s, dob=%s, major=%s, gpa=%.2f}",
                id,
                firstName,
                lastName,
                (dateOfBirth == null ? "N/A" : dateOfBirth.format(DATE_FORMAT)),
                (major == null || major.isEmpty() ? "Undeclared" : major),
                gpa);
    }

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // CSV helpers: id,first,last,yyyy-MM-dd,major,gpa
    public String toCsvLine() {
        return String.format("%d,%s,%s,%s,%s,%.2f",
                id,
                escapeCsv(firstName),
                escapeCsv(lastName),
                (dateOfBirth == null ? "" : dateOfBirth.format(DATE_FORMAT)),
                escapeCsv(major),
                gpa);
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        return s.replace(",", "\\,");
    }

    private static String unescapeCsv(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",");
    }

    public static Student fromCsvLine(String line) {
        // naive split which respects escaped commas
        String[] parts = splitCsvLine(line);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV line (expected 6 fields): " + line);
        }
        Student s = new Student();
        s.id = Integer.parseInt(parts[0]);
        s.firstName = unescapeCsv(parts[1]);
        s.lastName = unescapeCsv(parts[2]);
        if (!parts[3].isBlank()) {
            s.dateOfBirth = LocalDate.parse(parts[3], DATE_FORMAT);
        }
        s.major = unescapeCsv(parts[4]);
        s.gpa = Double.parseDouble(parts[5]);
        return s;
    }

    private static String[] splitCsvLine(String line) {
        // simple parser handling escaped commas "\,"
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escape) {
                if (c == ',') {
                    cur.append(',');
                } else {
                    // backslash was literal
                    cur.append('\\').append(c);
                }
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else if (c == ',') {
                    parts.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }
}
