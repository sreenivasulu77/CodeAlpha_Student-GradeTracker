import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Student Grade Tracker
 * A high-quality, dual-mode (GUI/Console) Java application for managing student grades.
 */
public class GradeTracker {

    // ==========================================
    // Core Data Models
    // ==========================================

    public static class Student {
        private String name;
        private double grade;

        public Student(String name, double grade) {
            this.name = name;
            this.grade = grade;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getGrade() {
            return grade;
        }

        public void setGrade(double grade) {
            this.grade = grade;
        }

        public String getLetterGrade() {
            if (grade >= 90.0) return "A";
            else if (grade >= 80.0) return "B";
            else if (grade >= 70.0) return "C";
            else if (grade >= 60.0) return "D";
            else return "F";
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f%% - %s)", name, grade, getLetterGrade());
        }
    }

    public static class GradeManager {
        private final List<Student> students = new ArrayList<>();

        public void addStudent(String name, double grade) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Student name cannot be empty.");
            }
            if (grade < 0.0 || grade > 100.0) {
                throw new IllegalArgumentException("Grade must be between 0 and 100.");
            }
            students.add(new Student(name.trim(), grade));
        }

        public void removeStudent(int index) {
            if (index >= 0 && index < students.size()) {
                students.remove(index);
            }
        }

        public void clear() {
            students.clear();
        }

        public List<Student> getStudents() {
            return students;
        }

        public int getCount() {
            return students.size();
        }

        public double getAverage() {
            if (students.isEmpty()) return 0.0;
            double sum = 0;
            for (Student s : students) {
                sum += s.getGrade();
            }
            return sum / students.size();
        }

        public double getHighestGrade() {
            if (students.isEmpty()) return 0.0;
            double max = -1.0;
            for (Student s : students) {
                if (s.getGrade() > max) {
                    max = s.getGrade();
                }
            }
            return max;
        }

        public List<Student> getHighestStudents() {
            if (students.isEmpty()) return Collections.emptyList();
            double max = getHighestGrade();
            return students.stream()
                    .filter(s -> Math.abs(s.getGrade() - max) < 0.0001)
                    .collect(Collectors.toList());
        }

        public double getLowestGrade() {
            if (students.isEmpty()) return 0.0;
            double min = 101.0;
            for (Student s : students) {
                if (s.getGrade() < min) {
                    min = s.getGrade();
                }
            }
            return min;
        }

        public List<Student> getLowestStudents() {
            if (students.isEmpty()) return Collections.emptyList();
            double min = getLowestGrade();
            return students.stream()
                    .filter(s -> Math.abs(s.getGrade() - min) < 0.0001)
                    .collect(Collectors.toList());
        }

        public int[] getDistribution() {
            int[] counts = new int[5]; // A, B, C, D, F
            for (Student s : students) {
                String letter = s.getLetterGrade();
                switch (letter) {
                    case "A" -> counts[0]++;
                    case "B" -> counts[1]++;
                    case "C" -> counts[2]++;
                    case "D" -> counts[3]++;
                    case "F" -> counts[4]++;
                }
            }
            return counts;
        }

        /**
         * Runs a suite of self-tests to verify business logic calculations.
         * @return true if all assertions pass, false otherwise.
         */
        public boolean runSelfTests() {
            clear();
            try {
                // Test 1: Empty state
                if (getAverage() != 0.0 || getHighestGrade() != 0.0 || getLowestGrade() != 0.0) {
                    return false;
                }

                // Test 2: Standard calculations
                addStudent("Alice", 95.5);
                addStudent("Bob", 82.0);
                addStudent("Charlie", 74.5);
                addStudent("David", 61.0);
                addStudent("Emma", 45.0);
                addStudent("Frank", 95.5); // Highest tie

                if (getCount() != 6) return false;

                // Sum: 95.5 + 82.0 + 74.5 + 61.0 + 45.0 + 95.5 = 453.5
                // Average: 453.5 / 6 = 75.58333...
                double avg = getAverage();
                if (Math.abs(avg - 75.5833) > 0.001) return false;

                if (getHighestGrade() != 95.5) return false;
                List<Student> highStudents = getHighestStudents();
                if (highStudents.size() != 2) return false;
                if (!highStudents.get(0).getName().equals("Alice") || !highStudents.get(1).getName().equals("Frank")) {
                    // Check if names match (regardless of order)
                    boolean hasAlice = highStudents.stream().anyMatch(s -> s.getName().equals("Alice"));
                    boolean hasFrank = highStudents.stream().anyMatch(s -> s.getName().equals("Frank"));
                    if (!hasAlice || !hasFrank) return false;
                }

                if (getLowestGrade() != 45.0) return false;
                List<Student> lowStudents = getLowestStudents();
                if (lowStudents.size() != 1 || !lowStudents.getFirst().getName().equals("Emma")) return false;

                // Distribution check: A=2, B=1, C=1, D=1, F=1
                int[] dist = getDistribution();
                if (dist[0] != 2 || dist[1] != 1 || dist[2] != 1 || dist[3] != 1 || dist[4] != 1) return false;

                // Test 3: Input validation check
                try {
                    addStudent("", 85);
                    return false; // Should have thrown exception
                } catch (IllegalArgumentException ignored) {}

                try {
                    addStudent("Invalid Grade", 105.0);
                    return false; // Should have thrown exception
                } catch (IllegalArgumentException ignored) {}

                try {
                    addStudent("Negative Grade", -5.0);
                    return false; // Should have thrown exception
                } catch (IllegalArgumentException ignored) {}

                clear();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // ==========================================
    // CLI Console Mode
    // ==========================================

    private static void runConsoleMode(GradeManager manager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==================================================");
        System.out.println("        STUDENT GRADE TRACKER - CONSOLE           ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Add Student");
            System.out.println("2. Remove Student");
            System.out.println("3. Display Summary Report");
            System.out.println("4. Run Self-Verification Tests");
            System.out.println("5. Clear All Students");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            String choiceStr = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student Name: ");
                    String name = scanner.nextLine().trim();
                    if (name.isEmpty()) {
                        System.out.println("Error: Student name cannot be empty.");
                        break;
                    }
                    System.out.print("Enter Grade (0.0 to 100.0): ");
                    String gradeStr = scanner.nextLine().trim();
                    try {
                        double grade = Double.parseDouble(gradeStr);
                        if (grade < 0.0 || grade > 100.0) {
                            System.out.println("Error: Grade must be between 0.0 and 100.0.");
                        } else {
                            manager.addStudent(name, grade);
                            System.out.println("Success: Added " + name + " with grade " + grade + "%.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Please enter a valid decimal number for the grade.");
                    }
                }
                case 2 -> {
                    List<Student> students = manager.getStudents();
                    if (students.isEmpty()) {
                        System.out.println("No students available to remove.");
                        break;
                    }
                    System.out.println("\n--- Student List ---");
                    for (int i = 0; i < students.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, students.get(i));
                    }
                    System.out.print("Enter the number of the student to remove (1-" + students.size() + "): ");
                    String indexStr = scanner.nextLine().trim();
                    try {
                        int index = Integer.parseInt(indexStr) - 1;
                        if (index >= 0 && index < students.size()) {
                            String removedName = students.get(index).getName();
                            manager.removeStudent(index);
                            System.out.println("Success: Removed student " + removedName + ".");
                        } else {
                            System.out.println("Error: Invalid selection number.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Invalid input. Please enter a number.");
                    }
                }
                case 3 -> displayConsoleSummary(manager);
                case 4 -> {
                    System.out.println("Running automated logic tests...");
                    // Store current state
                    List<Student> temp = new ArrayList<>(manager.getStudents());
                    boolean success = manager.runSelfTests();
                    if (success) {
                        System.out.println("[PASS] Core calculations and validators working perfectly.");
                    } else {
                        System.out.println("[FAIL] Core calculation anomalies detected.");
                    }
                    // Restore state
                    manager.clear();
                    manager.getStudents().addAll(temp);
                }
                case 5 -> {
                    manager.clear();
                    System.out.println("Success: Cleared all student records.");
                }
                case 6 -> {
                    System.out.println("Thank you for using Student Grade Tracker. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose a menu option (1-6).");
            }
        }
    }

    private static void displayConsoleSummary(GradeManager manager) {
        List<Student> students = manager.getStudents();
        if (students.isEmpty()) {
            System.out.println("\n--- Summary Report ---");
            System.out.println("No students recorded yet.");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("              STUDENT GRADE REPORT                ");
        System.out.println("==================================================");
        System.out.printf("%-20s %-12s %-12s\n", "Student Name", "Grade (%)", "Letter Grade");
        System.out.println("--------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-20s %-12.2f %-12s\n", s.getName(), s.getGrade(), s.getLetterGrade());
        }
        System.out.println("--------------------------------------------------");

        // Metrics Summary
        double avg = manager.getAverage();
        double highest = manager.getHighestGrade();
        double lowest = manager.getLowestGrade();
        List<Student> maxStudents = manager.getHighestStudents();
        List<Student> minStudents = manager.getLowestStudents();

        String maxStr = maxStudents.stream().map(Student::getName).collect(Collectors.joining(", "));
        String minStr = minStudents.stream().map(Student::getName).collect(Collectors.joining(", "));

        System.out.printf("Total Students : %d\n", manager.getCount());
        System.out.printf("Average Grade  : %.2f%%\n", avg);
        System.out.printf("Highest Grade  : %.2f%% (%s)\n", highest, maxStr);
        System.out.printf("Lowest Grade   : %.2f%% (%s)\n", lowest, minStr);
        System.out.println("==================================================");

        // Grade Distribution Histogram
        System.out.println("Grade Distribution:");
        int[] dist = manager.getDistribution();
        String[] labels = {"A (90-100)", "B (80-89) ", "C (70-79) ", "D (60-69) ", "F (<60)   "};
        for (int i = 0; i < 5; i++) {
            StringBuilder bar = new StringBuilder();
            bar.append("=".repeat(dist[i]));
            System.out.printf("  %s : [%-10s] (%d)\n", labels[i], bar.toString(), dist[i]);
        }
        System.out.println("==================================================");
    }

    // ==========================================
    // Modern UI Elements (Custom Swing Components)
    // ==========================================

    // Rounded card panel
    public static class RoundPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        public RoundPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }

    // Styled interactive button
    public static class StyledButton extends JButton {
        private final Color baseColor;
        private final Color hoverColor;
        private final Color clickColor;

        public StyledButton(String text, Color baseColor, Color hoverColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.clickColor = baseColor.darker();
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(baseColor);
                    repaint();
                }
            });
            setBackground(baseColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? clickColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Modern statistics card with left vertical color bar accent
    public static class StatCard extends JPanel {
        private final JLabel titleLabel;
        private final JLabel valueLabel;
        private final JLabel subTextLabel;
        private final Color accentColor;
        private final Color cardBg;

        public StatCard(String title, String initialValue, String initialSubText, Color bgColor, Color accentColor) {
            this.accentColor = accentColor;
            this.cardBg = bgColor;
            setLayout(new BorderLayout(4, 4));
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));
            setOpaque(false);

            titleLabel = new JLabel(title.toUpperCase());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            titleLabel.setForeground(new Color(150, 150, 150));

            valueLabel = new JLabel(initialValue);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            valueLabel.setForeground(Color.WHITE);

            subTextLabel = new JLabel(initialSubText);
            subTextLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            subTextLabel.setForeground(new Color(175, 175, 175));

            add(titleLabel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
            add(subTextLabel, BorderLayout.SOUTH);
            setBackground(bgColor);
        }

        public void updateData(String value, String subText) {
            valueLabel.setText(value);
            subTextLabel.setText(subText);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw card background
            g2.setColor(cardBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            // Draw accent bar on the left
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, 4, getHeight(), 6, 6);

            g2.dispose();
        }
    }

    // Custom Grade Distribution Chart
    public static class DistributionChart extends JPanel {
        private int[] counts = new int[5]; // A, B, C, D, F
        private final String[] labels = {"A (90-100)", "B (80-89)", "C (70-79)", "D (60-69)", "F (<60)"};
        private final Color[] colors = {
                new Color(46, 204, 113),  // Green for A
                new Color(52, 152, 219),  // Blue for B
                new Color(241, 196, 15),  // Yellow for C
                new Color(230, 126, 34),  // Orange for D
                new Color(231, 76, 60)    // Red for F
        };

        public DistributionChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(280, 180));
        }

        public void setCounts(int[] counts) {
            this.counts = counts;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int paddingLeft = 85;
            int paddingRight = 25;
            int paddingTop = 12;
            int paddingBottom = 12;

            int chartWidth = width - paddingLeft - paddingRight;
            int chartHeight = height - paddingTop - paddingBottom;

            if (chartHeight <= 0 || chartWidth <= 0) {
                g2.dispose();
                return;
            }

            int maxCount = 0;
            for (int c : counts) {
                if (c > maxCount) maxCount = c;
            }

            int barHeight = Math.max(8, chartHeight / 5 - 10);
            int gap = (chartHeight - (barHeight * 5)) / 6;

            for (int i = 0; i < 5; i++) {
                int y = paddingTop + gap + i * (barHeight + gap);

                // Draw label
                g2.setColor(new Color(200, 200, 200));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(labels[i], 10, y + barHeight / 2 + 4);

                // Draw background track
                g2.setColor(new Color(40, 40, 40));
                g2.fillRoundRect(paddingLeft, y, chartWidth, barHeight, 6, 6);

                // Draw value bar
                if (counts[i] > 0) {
                    int barWidth = (int) (((double) counts[i] / Math.max(1, maxCount)) * chartWidth);
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(paddingLeft, y, barWidth, barHeight, 6, 6);

                    // Draw count label
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    String valStr = String.valueOf(counts[i]);
                    int valWidth = g2.getFontMetrics().stringWidth(valStr);
                    if (barWidth > valWidth + 8) {
                        g2.drawString(valStr, paddingLeft + barWidth - valWidth - 4, y + barHeight / 2 + 4);
                    } else {
                        g2.drawString(valStr, paddingLeft + barWidth + 4, y + barHeight / 2 + 4);
                    }
                } else {
                    g2.setColor(new Color(110, 110, 110));
                    g2.drawString("0", paddingLeft + 4, y + barHeight / 2 + 4);
                }
            }
            g2.dispose();
        }
    }

    // Custom Table Cell Renderer for dark theme table & cell highlighting
    public static class CustomTableCellRenderer extends DefaultTableCellRenderer {
        private double highest = -1;
        private double lowest = -1;

        public void setExtremes(double highest, double lowest) {
            this.highest = highest;
            this.lowest = lowest;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            c.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            // Default dark theme alternating row colors
            if (isSelected) {
                c.setBackground(new Color(0, 173, 181));
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(row % 2 == 0 ? new Color(28, 28, 28) : new Color(34, 34, 34));
                c.setForeground(new Color(230, 230, 230));
            }

            // Highlights
            if (column == 1 && value instanceof Double) {
                double val = (Double) value;
                if (!isSelected) {
                    if (Math.abs(val - highest) < 0.0001 && highest > 0) {
                        c.setForeground(new Color(46, 204, 113)); // Teal/green for highest
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (Math.abs(val - lowest) < 0.0001 && lowest < 101) {
                        c.setForeground(new Color(231, 76, 60));  // Soft red for lowest
                        c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    }
                }
            }

            if (c instanceof JLabel label) {
                label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                if (column == 1 || column == 2) {
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    label.setHorizontalAlignment(JLabel.LEFT);
                }
            }

            return c;
        }
    }

    // Custom Table Model wrapping the Student list
    public static class StudentTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Name", "Grade (%)", "Letter Grade"};
        private final List<Student> students;

        public StudentTableModel(List<Student> students) {
            this.students = students;
        }

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (row < 0 || row >= students.size()) return null;
            Student s = students.get(row);
            return switch (col) {
                case 0 -> s.getName();
                case 1 -> s.getGrade();
                case 2 -> s.getLetterGrade();
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 1) return Double.class;
            return String.class;
        }
    }

    // ==========================================
    // GUI Dashboard Frame
    // ==========================================

    public static class TrackerGUI extends JFrame {
        private final GradeManager manager;

        private JTextField nameField;
        private JTextField gradeField;
        private JLabel errorLabel;
        private JTable studentTable;
        private StudentTableModel tableModel;
        private CustomTableCellRenderer cellRenderer;

        // Stats Cards
        private StatCard avgCard;
        private StatCard maxCard;
        private StatCard minCard;
        private StatCard countCard;

        // Visuals
        private DistributionChart distChart;

        public TrackerGUI(GradeManager manager) {
            this.manager = manager;
            setTitle("Academic Performance & Grade Tracker");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setMinimumSize(new Dimension(980, 680));
            setLocationRelativeTo(null);

            // Configure global UI parameters
            initStyles();

            // Set main background panel
            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBackground(new Color(18, 18, 18));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            // 1. TOP HEADER PANEL
            mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

            // 2. CENTER PANEL (splits into control sidebar and dashboard charts/tables)
            JPanel dashboardGrid = new JPanel(new GridBagLayout());
            dashboardGrid.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;

            // Sidebar Left: Form Inputs & Actions
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.3;
            dashboardGrid.add(createSidebarPanel(), gbc);

            // Right Panel: Stats Grid, Table, and Distribution Chart
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.insets = new Insets(0, 15, 0, 0);
            dashboardGrid.add(createDashboardRight(), gbc);

            mainPanel.add(dashboardGrid, BorderLayout.CENTER);
            setContentPane(mainPanel);

            // Populate initial state
            updateDashboard();
        }

        private void initStyles() {
            // Set look & feel defaults
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {}
            ToolTipManager.sharedInstance().setDismissDelay(8000);
        }

        private JPanel createHeaderPanel() {
            JPanel header = new JPanel(new BorderLayout(5, 0));
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

            JLabel title = new JLabel("ACADEMIC ANALYTICS DASHBOARD");
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(Color.WHITE);

            JLabel subtitle = new JLabel("Class Performance Tracking & Grade Distributions");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            subtitle.setForeground(new Color(150, 150, 150));

            JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 2));
            titleContainer.setOpaque(false);
            titleContainer.add(title);
            titleContainer.add(subtitle);

            // Header Action Buttons
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            actions.setOpaque(false);

            StyledButton btnTest = new StyledButton("Run Logic Verification Tests", new Color(44, 62, 80), new Color(52, 73, 94));
            btnTest.addActionListener(e -> {
                boolean pass = manager.runSelfTests();
                if (pass) {
                    JOptionPane.showMessageDialog(this,
                            "All automated verification test routines completed successfully!\n" +
                                    "All business rules and logic matrices match expected values.",
                            "Self-Verification Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Verification test routines reported discrepancies. Check system integrity.",
                            "Logic Test Failed", JOptionPane.ERROR_MESSAGE);
                }
                updateDashboard();
            });

            StyledButton btnClear = new StyledButton("Clear All", new Color(192, 57, 43), new Color(231, 76, 60));
            btnClear.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to clear all student records?",
                        "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.clear();
                    updateDashboard();
                }
            });

            actions.add(btnTest);
            actions.add(btnClear);

            header.add(titleContainer, BorderLayout.WEST);
            header.add(actions, BorderLayout.EAST);
            return header;
        }

        private JPanel createSidebarPanel() {
            RoundPanel card = new RoundPanel(16, new Color(26, 26, 26));
            card.setLayout(new GridBagLayout());
            card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 0, 15, 0);

            // Panel Title
            JLabel formTitle = new JLabel("MANAGE STUDENT RECORDS");
            formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            formTitle.setForeground(new Color(0, 173, 181));
            card.add(formTitle, gbc);

            // Student Name Input
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 5, 0);
            JLabel nameLabel = new JLabel("STUDENT NAME");
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            nameLabel.setForeground(new Color(150, 150, 150));
            card.add(nameLabel, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 15, 0);
            nameField = createStyledTextField("e.g. Alice Smith");
            card.add(nameField, gbc);

            // Student Grade Input
            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 5, 0);
            JLabel gradeLabel = new JLabel("GRADE PERCENTAGE (0 - 100)");
            gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            gradeLabel.setForeground(new Color(150, 150, 150));
            card.add(gradeLabel, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(0, 0, 5, 0);
            gradeField = createStyledTextField("e.g. 88.5");
            card.add(gradeField, gbc);

            // Inline Error label
            gbc.gridy = 5;
            gbc.insets = new Insets(0, 0, 15, 0);
            errorLabel = new JLabel(" ");
            errorLabel.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 11));
            errorLabel.setForeground(new Color(231, 76, 60));
            card.add(errorLabel, gbc);

            // Add Record Button
            gbc.gridy = 6;
            gbc.ipady = 10;
            StyledButton btnAdd = new StyledButton("Add Grade Record", new Color(0, 173, 181), new Color(0, 229, 235));
            btnAdd.addActionListener(e -> processAddStudent());
            card.add(btnAdd, gbc);

            // Delete Selected Button
            gbc.gridy = 7;
            gbc.ipady = 0;
            gbc.insets = new Insets(5, 0, 5, 0);
            StyledButton btnDeleteSelected = new StyledButton("Delete Selected Row", new Color(142, 68, 173), new Color(155, 89, 182));
            btnDeleteSelected.addActionListener(e -> processDeleteSelected());
            card.add(btnDeleteSelected, gbc);

            // Filler space to push form content up
            gbc.gridy = 8;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            card.add(Box.createGlue(), gbc);

            return card;
        }

        private JTextField createStyledTextField(String placeholder) {
            JTextField field = new JTextField();
            field.setBackground(new Color(36, 36, 36));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));

            // Set simple hint behavior
            field.setText(placeholder);
            field.setForeground(new Color(120, 120, 120));

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(Color.WHITE);
                    }
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(0, 173, 181), 1),
                            BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().trim().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(new Color(120, 120, 120));
                    }
                    field.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
                            BorderFactory.createEmptyBorder(8, 10, 8, 10)
                    ));
                }
            });
            return field;
        }

        private JPanel createDashboardRight() {
            JPanel container = new JPanel(new BorderLayout(0, 15));
            container.setOpaque(false);

            // 1. STATS ROW
            JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
            statsRow.setOpaque(false);
            statsRow.setPreferredSize(new Dimension(600, 85));

            avgCard = new StatCard("Class Average", "0.00%", "No records", new Color(26, 26, 26), new Color(0, 173, 181));
            maxCard = new StatCard("Highest Grade", "0.00", "No records", new Color(26, 26, 26), new Color(46, 204, 113));
            minCard = new StatCard("Lowest Grade", "0.00", "No records", new Color(26, 26, 26), new Color(231, 76, 60));
            countCard = new StatCard("Total Enrolled", "0", "Students total", new Color(26, 26, 26), new Color(52, 152, 219));

            statsRow.add(countCard);
            statsRow.add(avgCard);
            statsRow.add(maxCard);
            statsRow.add(minCard);

            container.add(statsRow, BorderLayout.NORTH);

            // 2. DATA TABLE & VISUALS (Horizontal split)
            JPanel dataPanel = new JPanel(new GridBagLayout());
            dataPanel.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;

            // Student Table Column
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0.55;
            dataPanel.add(createTableCard(), gbc);

            // Distribution Chart Column
            gbc.gridx = 1;
            gbc.weightx = 0.45;
            gbc.insets = new Insets(0, 15, 0, 0);
            dataPanel.add(createChartCard(), gbc);

            container.add(dataPanel, BorderLayout.CENTER);
            return container;
        }

        private JPanel createTableCard() {
            RoundPanel card = new RoundPanel(16, new Color(26, 26, 26));
            card.setLayout(new BorderLayout(10, 10));
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel titleLabel = new JLabel("STUDENT ROSTER & INDIVIDUAL GRADES");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(Color.WHITE);
            card.add(titleLabel, BorderLayout.NORTH);

            // Setup Table
            tableModel = new StudentTableModel(manager.getStudents());
            studentTable = new JTable(tableModel);
            studentTable.setRowHeight(32);
            studentTable.setBackground(new Color(26, 26, 26));
            studentTable.setGridColor(new Color(45, 45, 45));
            studentTable.setShowVerticalLines(false);
            studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // Custom table header
            JTableHeader header = studentTable.getTableHeader();
            header.setFont(new Font("Segoe UI", Font.BOLD, 11));
            header.setBackground(new Color(35, 35, 35));
            header.setForeground(new Color(180, 180, 180));
            header.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
            header.setReorderingAllowed(false);
            header.setPreferredSize(new Dimension(header.getWidth(), 30));

            // Custom cells rendering
            cellRenderer = new CustomTableCellRenderer();
            studentTable.setDefaultRenderer(String.class, cellRenderer);
            studentTable.setDefaultRenderer(Double.class, cellRenderer);

            // Wrap in styled scroll pane
            JScrollPane scrollPane = new JScrollPane(studentTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));
            scrollPane.getViewport().setBackground(new Color(26, 26, 26));
            scrollPane.getVerticalScrollBar().setUnitIncrement(12);

            card.add(scrollPane, BorderLayout.CENTER);
            return card;
        }

        private JPanel createChartCard() {
            RoundPanel card = new RoundPanel(16, new Color(26, 26, 26));
            card.setLayout(new BorderLayout(10, 10));
            card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel titleLabel = new JLabel("GRADE DISTRIBUTION ANALYTICS");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            titleLabel.setForeground(Color.WHITE);
            card.add(titleLabel, BorderLayout.NORTH);

            distChart = new DistributionChart();
            card.add(distChart, BorderLayout.CENTER);

            // Sub info box
            JLabel infoLabel = new JLabel("Highlights: Green = Class Highs | Red = Lows in table view", SwingConstants.CENTER);
            infoLabel.setFont(new Font("Segoe UI", Font.ITALIC | Font.BOLD, 10));
            infoLabel.setForeground(new Color(130, 130, 130));
            card.add(infoLabel, BorderLayout.SOUTH);

            return card;
        }

        private void processAddStudent() {
            String name = nameField.getText().trim();
            String gradeStr = gradeField.getText().trim();

            if (name.isEmpty() || name.equals("e.g. Alice Smith")) {
                setError("Please enter a valid student name.");
                nameField.requestFocus();
                return;
            }

            if (gradeStr.isEmpty() || gradeStr.equals("e.g. 88.5")) {
                setError("Please enter a grade.");
                gradeField.requestFocus();
                return;
            }

            double grade;
            try {
                grade = Double.parseDouble(gradeStr);
            } catch (NumberFormatException e) {
                setError("Grade must be a valid numeric percentage.");
                gradeField.requestFocus();
                return;
            }

            if (grade < 0.0 || grade > 100.0) {
                setError("Grade must be in the range 0.0 to 100.0.");
                gradeField.requestFocus();
                return;
            }

            // Valid inputs -> Add to manager
            try {
                manager.addStudent(name, grade);
                setError(" "); // clear error
                nameField.setText("");
                gradeField.setText("");

                // Refocus or restore placeholders
                nameField.requestFocus();
                nameField.transferFocus();

                updateDashboard();
            } catch (Exception e) {
                setError(e.getMessage());
            }
        }

        private void processDeleteSelected() {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                setError("Please select a student row from the table to remove.");
                return;
            }

            setError(" ");
            manager.removeStudent(selectedRow);
            updateDashboard();
        }

        private void setError(String msg) {
            errorLabel.setText(msg);
        }

        private void updateDashboard() {
            // Update table
            tableModel.fireTableDataChanged();

            // Refresh extremes for table renderer
            double max = manager.getHighestGrade();
            double min = manager.getLowestGrade();
            cellRenderer.setExtremes(max, min);

            // Update Stats Cards
            int count = manager.getCount();
            countCard.updateData(String.valueOf(count), count == 1 ? "1 student" : count + " students");

            if (count == 0) {
                avgCard.updateData("0.00%", "No records");
                maxCard.updateData("0.00", "No records");
                minCard.updateData("0.00", "No records");
            } else {
                avgCard.updateData(String.format("%.2f%%", manager.getAverage()), "Class average");

                List<Student> maxStudents = manager.getHighestStudents();
                String maxNames = maxStudents.stream().map(Student::getName).collect(Collectors.joining(", "));
                maxCard.updateData(String.format("%.2f", max), truncateText(maxNames, 15));

                List<Student> minStudents = manager.getLowestStudents();
                String minNames = minStudents.stream().map(Student::getName).collect(Collectors.joining(", "));
                minCard.updateData(String.format("%.2f", min), truncateText(minNames, 15));
            }

            // Update Distribution Chart
            distChart.setCounts(manager.getDistribution());
        }

        private String truncateText(String text, int maxChars) {
            if (text == null) return "";
            if (text.length() <= maxChars) return text;
            return text.substring(0, maxChars - 3) + "...";
        }
    }

    // ==========================================
    // Application Entry Point
    // ==========================================

    public static void main(String[] args) {
        GradeManager manager = new GradeManager();

        // Check if headless or console argument specified
        boolean consoleMode = GraphicsEnvironment.isHeadless();
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--console") || arg.equalsIgnoreCase("-c")) {
                consoleMode = true;
                break;
            }
        }

        if (consoleMode) {
            runConsoleMode(manager);
        } else {
            // Run GUI mode on the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> {
                TrackerGUI gui = new TrackerGUI(manager);
                gui.setVisible(true);
            });
        }
    }
}
