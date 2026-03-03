package com.ui.frame;

import com.db.JpaUtil;
import com.security.CurrentUser;
import com.security.SecurityContext;
import com.ui.panel.*;
import com.ui.util.UiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame.
 * Layout: left sidebar with role-gated navigation buttons + right CardLayout
 * content area.
 */
public class MainFrame extends JFrame {

    // Card names (used as keys in CardLayout)
    public static final String CARD_DASHBOARD = "Dashboard";
    public static final String CARD_STUDENTS = "Students";
    public static final String CARD_TEACHERS = "Teachers";
    public static final String CARD_COURSES = "Courses";
    public static final String CARD_CLASSES = "Classes";
    public static final String CARD_ENROLLMENTS = "Enrollments";
    public static final String CARD_ROOMS = "Rooms";
    public static final String CARD_SCHEDULES = "Schedules";
    public static final String CARD_ATTENDANCE = "Attendance";
    public static final String CARD_RESULTS = "Results";
    public static final String CARD_INVOICES = "Invoices";
    public static final String CARD_PAYMENTS = "Payments";
    public static final String CARD_STAFF = "Staff";
    public static final String CARD_USERACCOUNT = "UserAccounts";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final JPanel sidebarPanel = new JPanel();

    public MainFrame(CurrentUser user) {
        super("Language Center Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 680));

        buildContent();
        buildSidebar(user);
        buildHeader(user);

        JPanel root = new JPanel(new BorderLayout());
        root.add(sidebarPanel, BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        add(root, BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                JpaUtil.close();
                SecurityContext.logout();
            }
        });

        // Show dashboard by default
        cardLayout.show(contentPanel, CARD_DASHBOARD);
    }

    // ---------- content panels ----------

    private void buildContent() {
        contentPanel.setBackground(UiUtil.COLOR_BG);
        contentPanel.add(new DashboardPanel(), CARD_DASHBOARD);
        contentPanel.add(new StudentsPanel(), CARD_STUDENTS);
        contentPanel.add(new TeachersPanel(), CARD_TEACHERS);
        contentPanel.add(new CoursesPanel(), CARD_COURSES);
        contentPanel.add(new ClassesPanel(), CARD_CLASSES);
        contentPanel.add(new EnrollmentsPanel(), CARD_ENROLLMENTS);
        contentPanel.add(new RoomsPanel(), CARD_ROOMS);
        contentPanel.add(new SchedulesPanel(), CARD_SCHEDULES);
        contentPanel.add(new AttendancePanel(), CARD_ATTENDANCE);
        contentPanel.add(new ResultsPanel(), CARD_RESULTS);
        contentPanel.add(new InvoicesPanel(), CARD_INVOICES);
        contentPanel.add(new PaymentsPanel(), CARD_PAYMENTS);
        contentPanel.add(new StaffPanel(), CARD_STAFF);
        contentPanel.add(new UserAccountsPanel(), CARD_USERACCOUNT);
    }

    // ---------- sidebar ----------

    private void buildSidebar(CurrentUser user) {
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(UiUtil.COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(210, 0));

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UiUtil.COLOR_SIDEBAR);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // --- Menu items: {label, cardName, visible for roles} ---
        addNavItem(nav, "Tổng quan", CARD_DASHBOARD, true); // all
        addNavItem(nav, "Học viên", CARD_STUDENTS, canSeeStudents(user));
        addNavItem(nav, "Giáo viên", CARD_TEACHERS, user.isAdmin() || user.isConsultant());
        addNavItem(nav, "Khóa học", CARD_COURSES, user.isAdmin() || user.isConsultant() || user.isTeacher());
        addNavItem(nav, "Lớp học", CARD_CLASSES, user.isAdmin() || user.isConsultant() || user.isTeacher());
        addNavItem(nav, "Đăng ký học", CARD_ENROLLMENTS,
                user.isAdmin() || user.isConsultant() || user.isAccountant() || user.isStudent());
        addNavItem(nav, "Phòng học", CARD_ROOMS, user.isAdmin() || user.isConsultant());
        addNavItem(nav, "Lịch học", CARD_SCHEDULES,
                user.isAdmin() || user.isConsultant() || user.isTeacher() || user.isStudent());
        addNavItem(nav, "Điểm danh", CARD_ATTENDANCE,
                user.isAdmin() || user.isConsultant() || user.isTeacher() || user.isStudent());
        addNavItem(nav, "Kết quả học tập", CARD_RESULTS,
                user.isAdmin() || user.isConsultant() || user.isTeacher() || user.isStudent());
        addNavItem(nav, "Hóa đơn", CARD_INVOICES, user.isAdmin() || user.isAccountant() || user.isStudent());
        addNavItem(nav, "Thanh toán", CARD_PAYMENTS, user.isAdmin() || user.isAccountant() || user.isStudent());
        addNavItem(nav, "Nhân viên", CARD_STAFF, user.isAdmin());
        addNavItem(nav, "Tài khoản", CARD_USERACCOUNT, user.isAdmin());

        nav.add(Box.createVerticalGlue());

        // Logout button at bottom
        JButton btnLogout = createNavButton("Đăng xuất");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?",
                    "Đăng xuất", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                JpaUtil.close();
                SecurityContext.logout();
                dispose();
                SwingUtilities.invokeLater(com.Main::launchLogin);
            }
        });
        nav.add(btnLogout);

        JScrollPane scroll = new JScrollPane(nav,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebarPanel.add(scroll, BorderLayout.CENTER);
    }

    private void addNavItem(JPanel parent, String label, String cardName, boolean visible) {
        if (!visible)
            return;
        JButton btn = createNavButton(label);
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        parent.add(btn);
    }

    private JButton createNavButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UiUtil.COLOR_SIDEBAR_FG);
        btn.setBackground(UiUtil.COLOR_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color normal = btn.getBackground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(72, 100, 120));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(normal);
            }
        });
        return btn;
    }

    // ---------- header ----------

    private void buildHeader(CurrentUser user) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtil.COLOR_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel appName = new JLabel("Language Center Management System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appName.setForeground(Color.WHITE);
        header.add(appName, BorderLayout.WEST);

        String roleLabel = buildRoleLabel(user);
        JLabel userInfo = new JLabel("Người dùng: " + user.username() + "  |  " + roleLabel);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(new Color(200, 225, 255));
        header.add(userInfo, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    private String buildRoleLabel(CurrentUser u) {
        return switch (u.role()) {
            case ADMIN -> "Quản trị viên";
            case TEACHER -> "Giáo viên";
            case STUDENT -> "Học viên";
            case STAFF -> (u.staffRole() == com.model.user.StaffRole.ACCOUNTANT)
                    ? "Kế toán"
                    : "Tư vấn viên";
        };
    }

    // ---------- helpers ----------

    private boolean canSeeStudents(CurrentUser u) {
        return u.isAdmin() || u.isConsultant() || u.isAccountant() || u.isStudent();
    }
}
