import java.util.Scanner;

public class ClassManagementSystem {

    static Scanner scanner = new Scanner(System.in);

    // student, teacher, and course databases
    static String[][] studentDB = {
            {"S1", "password", "Student A"},
            {"S2", "password", "Student B"},
            {"S3", "password", "Student C"}
    };
    static String[][] teacherDB = {
            {"T1", "password", "Teacher A"},
            {"T2", "password", "Teacher B"}
    };
    static String[][] courseDB = {
            {"Course A", "1", "Section 1", "9:00 AM - 10:30 AM", "T1"},
            {"Course A", "2", "Section 2", "10:45 AM - 12:15 PM", "T2"},
            {"Course A", "3", "Section 3", "1:00 PM - 2:30 PM", "T1"},
            {"Course B", "1", "Section 1", "9:00 AM - 10:30 AM", "T1"},
            {"Course C", "1", "Section 1", "10:45 AM - 12:15 PM", "T2"},
            {"Course C", "2", "Section 2", "1:00 PM - 2:30 PM", "T1"}
    };

    // enrollment database
    static String[][][] enrollmentDB = new String[studentDB.length][courseDB.length][8];

    // logged-in teacher's ID
    static String teacherId;

    public static void main(String[] args) {
        System.out.println("Welcome to the Class Management System!");

        while (true) {
            int userType = getUserType();
            switch (userType) {
                case 1:
                    studentMenu();
                    break;
                case 2:
                    teacherMenu();
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static int getUserType() {
        System.out.println("\nWho are you?");
        System.out.println("1: Student");
        System.out.println("2: Teacher");
        return scanner.nextInt();
    }

    // Student menu
    public static void studentMenu() {
        scanner.nextLine();
        System.out.print("\nProvide Student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Authenticate
        int studentIndex = authenticateStudent(studentId, password);
        if (studentIndex == -1) {
            System.out.println("Invalid student ID or password!");
            return;
        }

        String studentName = studentDB[studentIndex][2];
        System.out.println("Welcome " + studentName + "!");

        while (true) {
            int option = getStudentOption();
            switch (option) {
                case 1:
                    addCourse(studentIndex);
                    break;
                case 2:
                    removeCourse(studentIndex);
                    break;
                case 3:
                    viewCourses(studentIndex);
                    break;
                case 4:
                    System.out.println("Thank you for using the Class Management System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static int getStudentOption() {
        System.out.println("\n1: Add a course");
        System.out.println("2: Remove a course");
        System.out.println("3: View enrolled courses");
        System.out.println("4: Logout");
        return scanner.nextInt();
    }

    // Teacher menu
    public static void teacherMenu() {
        scanner.nextLine();
        System.out.print("Please enter your teacher ID:\n> ");
        teacherId = scanner.nextLine();
        System.out.print("Please enter your password:\n> ");
        String password = scanner.nextLine();

        // Authenticate
        int teacherIndex = authenticateTeacher(teacherId, password);
        if (teacherIndex == -1) {
            System.out.println("Invalid teacher ID or password!");
            return;
        }

        String teacherName = teacherDB[teacherIndex][2];
        System.out.println("Welcome, " + teacherName + "!");

        while (true) {
            int option = getTeacherOption();
            switch (option) {
                case 1:
                    viewEnrolledStudents();
                    break;
                case 2:
                    System.out.println("Thank you for using the Class Management System. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static int authenticateStudent(String studentId, String password) {
        for (int i = 0; i < studentDB.length; i++) {
            if (studentDB[i][0].equals(studentId) && studentDB[i][1].equals(password)) {
                return i;
            }
        }
        return -1;
    }

    public static int authenticateTeacher(String teacherId, String password) {
        for (int i = 0; i < teacherDB.length; i++) {
            if (teacherDB[i][0].equals(teacherId) && teacherDB[i][1].equals(password)) {
                return i;
            }
        }
        return -1;
    }

    // add course for student
    public static void addCourse(int studentIndex) {
        System.out.println("Which course would you like to add?");
        displayAvailableCourses();

        int courseChoice = scanner.nextInt();
        scanner.nextLine();

        // Validate course choice
        if (courseChoice < 1 || courseChoice > courseDB.length) {
            System.out.println("Invalid course choice!");
            return;
        }

        System.out.println("Which section of " + courseDB[courseChoice - 1][0] + " would you like to enroll in?");
        displayCourseSections(courseChoice - 1);

        int sectionChoice = scanner.nextInt();
        scanner.nextLine();

        // Validate section choice
        if (sectionChoice < 1 || sectionChoice > 3) { // 3 section for each courses
            System.out.println("Invalid section choice!");
            return;
        }

        String[] courseDetails = {
                courseDB[courseChoice - 1][0],
                courseDB[courseChoice - 1][1],
                courseDB[courseChoice - 1][2],
                courseDB[courseChoice - 1][3],
                studentDB[studentIndex][2],
                studentDB[studentIndex][0],
                "",
                ""
        };

        // Enroll in course
        for (int i = 0; i < enrollmentDB[studentIndex].length; i++) {
            if (enrollmentDB[studentIndex][i][0] == null) {
                enrollmentDB[studentIndex][i] = courseDetails;
                System.out.println("You have successfully enrolled in " + courseDetails[0] + ", " + courseDetails[1] + ".");
                return;
            }
        }

        System.out.println("You have reached the maximum number of courses you can enroll in.");
    }

    //  remove a course
    public static void removeCourse(int studentIndex) {
        System.out.println("Which course would you like to remove?");

        // Display enrolled course
        int count = 1;
        for (int j = 0; j < enrollmentDB[studentIndex].length; j++) {
            if (enrollmentDB[studentIndex][j][0] != null) {
                String courseName = enrollmentDB[studentIndex][j][0];
                System.out.println(count + ". " + courseName);
                count++;
            }
        }

        int courseChoice = scanner.nextInt();
        scanner.nextLine();

        // Validate course choice
        if (courseChoice < 1 || courseChoice > countEnrolledCourses(studentIndex)) {
            System.out.println("Invalid course choice!");
            return;
        }

        String courseName = getCourseNameByNumber(studentIndex, courseChoice);
        for (int i = 0; i < enrollmentDB[studentIndex].length; i++) {
            if (enrollmentDB[studentIndex][i][0] != null && enrollmentDB[studentIndex][i][0].equals(courseName)) {
                enrollmentDB[studentIndex][i] = new String[8]; // Clear course details
                System.out.println("Course removed successfully!");
                return;
            }
        }
    }

    //count enrolled courses
    public static int countEnrolledCourses(int studentIndex) {
        int count = 0;
        for (int j = 0; j < enrollmentDB[studentIndex].length; j++) {
            if (enrollmentDB[studentIndex][j][0] != null) {
                count++;
            }
        }
        return count;
    }

    public static String getCourseNameByNumber(int studentIndex, int number) {
        int count = 1;
        for (int j = 0; j < enrollmentDB[studentIndex].length; j++) {
            if (enrollmentDB[studentIndex][j][0] != null) {
                if (count == number) {
                    return enrollmentDB[studentIndex][j][0];
                }
                count++;
            }
        }
        return null;
    }

    // display enrolled courses for studen
    public static void viewCourses(int studentIndex) {
        System.out.println("Your Enrolled Courses:");
        System.out.println("+------------+----------+---------------------+---------+---------------+------------+--------------+-----------+");
        System.out.println("| Course     | Section  | Timing              | Teacher | StudentName   | StudentID  | TeacherName  | TeacherID |");
        System.out.println("+------------+----------+---------------------+---------+---------------+------------+--------------+-----------+");

        for (int j = 0; j < enrollmentDB[studentIndex].length; j++) {
            if (enrollmentDB[studentIndex][j][0] != null) {
                String courseName = enrollmentDB[studentIndex][j][0];
                int courseIndex = findCourseIndex(courseName);

                if (courseIndex != -1) {
                    String section = courseDB[courseIndex][1];
                    String timing = courseDB[courseIndex][3];

                    String teacherName = courseDB[courseIndex][4];
                    int teacherIndex = findTeacherIndex(teacherName);

                    if (teacherIndex != -1) {
                        System.out.printf("| %-10s | %-8s | %-19s | %-7s | %-13s | %-10s | %-12s | %-8s |%n",
                                courseName, section, timing, teacherName,
                                studentDB[studentIndex][2], studentDB[studentIndex][0],
                                teacherDB[teacherIndex][2], teacherDB[teacherIndex][0]);
                    } else {
                        System.out.println("Teacher details not found for: " + teacherName);
                    }
                } else {
                    System.out.println("Course details not found for: " + courseName);
                }
            }
        }
        System.out.println("+------------+----------+---------------------+---------+---------------+------------+--------------+-----------+");
    }

    // find teacher index by teacher name
    public static int findTeacherIndex(String teacherName) {
        for (int i = 0; i < teacherDB.length; i++) {
            if (teacherDB[i][0].equals(teacherName)) {
                return i;
            }
        }
        return -1;
    }

    //find course index by course name
    public static int findCourseIndex(String courseName) {
        for (int i = 0; i < courseDB.length; i++) {
            if (courseDB[i][0].equals(courseName)) {
                return i;
            }
        }
        return -1;
    }

    // view enrolled students in a course section
    public static void viewEnrolledStudents() {
        System.out.println("Which course would you like to view?");
        displayTeacherCourses();

        int courseChoice = scanner.nextInt();
        scanner.nextLine();

        // Validate course choice
        if (courseChoice < 1 || courseChoice > courseDB.length) {
            System.out.println("Invalid course choice!");
            return;
        }

        System.out.println("Which section of " + courseDB[courseChoice - 1][0] + " would you like to view?");
        displayCourseSections(courseChoice - 1);

        int sectionChoice = scanner.nextInt();
        scanner.nextLine();

        // Validate section choice
        if (sectionChoice < 1 || sectionChoice > 3) {
            System.out.println("Invalid section choice!");
            return;
        }

        // teacher ID and course details
        String teacherId = courseDB[courseChoice - 1][4];
        String courseName = courseDB[courseChoice - 1][0];
        String sectionName = courseDB[courseChoice - 1][2];

        System.out.println("The following students are enrolled in " + courseName + ", " + sectionName + " taught by Teacher " + teacherId + ":");

        // Display enrolled students
        for (int i = 0; i < enrollmentDB.length; i++) {
            if (enrollmentDB[i][courseChoice - 1][0] != null &&
                    enrollmentDB[i][courseChoice - 1][1].equals(String.valueOf(sectionChoice)) &&
                    teacherDB[findTeacherIndex(teacherId)][0].equals(teacherId)) {
                System.out.println("- " + enrollmentDB[i][courseChoice - 1][4]);
            }
        }
    }

    //  display teacher's courses
    public static void displayTeacherCourses() {
        System.out.println("Your Courses:");
        System.out.println("+-----+------------+----------+--------------------------------+---------+");
        System.out.println("| No. | Course     | Section  | Timing                         | Teacher |");
        System.out.println("+-----+------------+----------+--------------------------------+---------+");

        for (int i = 0; i < courseDB.length; i++) {
            if (courseDB[i][4].equals(teacherId)) {
                String course = courseDB[i][0];
                String section = courseDB[i][2];
                String timing = courseDB[i][3];

                System.out.printf("| %-3d | %-10s | %-8s | %-30s | %-7s |%n", (i + 1), course, section, timing, teacherId);
            }
        }

        System.out.println("+-----+------------+----------+--------------------------------+---------+");
    }

    // display available courses
    public static void displayAvailableCourses() {
        System.out.println("Available Courses:");
        System.out.println("+-----+------------+----------+---------------------------+---------+");
        System.out.println("| No. | Course     | Section  | Time                      | Teacher |");
        System.out.println("+-----+------------+----------+---------------------------+---------+");

        // Display all available courses
        for (int i = 0; i < courseDB.length; i++) {
            String course = courseDB[i][0];
            String section = courseDB[i][2];
            String timing = courseDB[i][3];
            String teacherId = courseDB[i][4];

            String teacherName = getTeacherNameById(teacherId);

            System.out.printf("| %-3d | %-10s | %-8s | %-25s | %-3s |%n", (i + 1), course, section, timing, teacherName);
        }
        System.out.println("+-----+------------+----------+---------------------------+---------+");
    }

    // Method to get teacher name by teacher ID
    public static String getTeacherNameById(String teacherId) {
        for (String[] teacher : teacherDB) {
            if (teacher[0].equals(teacherId)) {
                return teacher[2];
            }
        }
        return "Unknown";
    }

    // display available sections for a course
    public static void displayCourseSections(int courseIndex) {
        System.out.println("Available Sections for " + courseDB[courseIndex][0] + ":");
        System.out.println("No. | Section");
        System.out.println("--------------");

        for (int i = 0; i < courseDB.length; i++) {
            if (courseDB[i][0].equals(courseDB[courseIndex][0])) {
                System.out.printf("%-3d | %-8s%n", (i + 1), courseDB[i][2]);
            }
        }
    }

    public static int getTeacherOption() {
        System.out.println("What would you like to do?");
        System.out.println("1: View enrolled students");
        System.out.println("2: Logout");
        return scanner.nextInt();
    }
}
