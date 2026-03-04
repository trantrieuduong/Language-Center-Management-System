package com.ui.panel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.exception.AppException;
import com.model.user.UserAccount;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.security.PermissionChecker;
import com.service.impl.UserAccountServiceImpl;
import com.stream.UserAccountStreamQueries;
import com.ui.util.MessageBox;
import com.ui.util.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccountsPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(UserAccountsPanel.class);

    private final UserAccountServiceImpl service = new UserAccountServiceImpl();
    private final AccountTableModel model = new AccountTableModel();
    private final JTable table = new JTable(model);
    private final JTextField tfSearch = UiUtil.searchField("Tìm theo username...");
    private final JButton btnResetPwd = UiUtil.primaryButton("Đặt lại mật khẩu");
    private final JButton btnRefresh = new JButton("Làm mới");
    private final JComboBox<UserRole> cbRoles = buildRoleComboBox();

    private static JComboBox<UserRole> buildRoleComboBox() {
        JComboBox<UserRole> cb = new JComboBox<>();
        cb.addItem(null); // "Tất cả"
        for (UserRole r : UserRole.values())
            cb.addItem(r);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tất cả" : value.toString());
                return this;
            }
        });
        return cb;
    }

    public UserAccountsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildToolbar(), BorderLayout.SOUTH);

        wireEvents();
        loadData(null);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.add(UiUtil.sectionTitle("Quản lý Tài khoản"), BorderLayout.WEST);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Vai trò:"));
        searchBar.add(cbRoles);
        searchBar.add(new JLabel("Tìm kiếm:"));
        searchBar.add(tfSearch);
        JButton btnSearch = UiUtil.primaryButton("Tìm");
        btnSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        searchBar.add(btnSearch);
        p.add(searchBar, BorderLayout.EAST);
        return p;
    }

    private JScrollPane buildTable() {
        UiUtil.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return sp;
    }

    private JPanel buildToolbar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        p.setOpaque(false);

        JLabel hint = new JLabel("Chọn tài khoản rồi bấm nút:");
        hint.setFont(UiUtil.FONT_REGULAR);
        p.add(hint);
        p.add(btnResetPwd);
        p.add(btnRefresh);
        return p;
    }

    private void wireEvents() {
        btnResetPwd.addActionListener(e -> onResetPassword());
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            cbRoles.setSelectedItem(null);
            loadData(null);
        });
        tfSearch.addActionListener(e -> loadData(tfSearch.getText().trim()));
        cbRoles.addActionListener(e -> loadData(tfSearch.getText().trim()));
    }

    private void onResetPassword() {
        int row = table.getSelectedRow();
        if (row < 0) {
            MessageBox.warn(this, "Vui lòng chọn một tài khoản.");
            return;
        }
        UserAccount selected = model.getRow(row);

        // Show password input dialog
        JPasswordField pfNew = new JPasswordField(20);
        JPasswordField pfConf = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.add(new JLabel("Mật khẩu mới cho tài khoản: " + selected.getUsername()));
        panel.add(pfNew);
        panel.add(new JLabel("Xác nhận mật khẩu:"));
        panel.add(pfConf);

        int option = JOptionPane.showConfirmDialog(
                this, panel, "Đặt lại mật khẩu",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION)
            return;

        String newPwd = new String(pfNew.getPassword()).trim();
        String confPwd = new String(pfConf.getPassword()).trim();

        if (newPwd.isEmpty()) {
            MessageBox.warn(this, "Mật khẩu không được để trống.");
            return;
        }
        if (!newPwd.equals(confPwd)) {
            MessageBox.warn(this, "Mật khẩu xác nhận không khớp.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                PermissionChecker.requireAdmin();
                String hashed = BCrypt.withDefaults().hashToString(12, newPwd.toCharArray());
                selected.setPasswordHash(hashed);
                service.update(selected);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    MessageBox.info(UserAccountsPanel.this,
                            "Đã đặt lại mật khẩu cho tài khoản: " + selected.getUsername());
                } catch (Exception ex) {
                    handleException(ex);
                }
            }
        }.execute();
    }

    private void loadData(String keyword) {
        btnResetPwd.setEnabled(false);
        UserRole selectedRole = (UserRole) cbRoles.getSelectedItem();

        new SwingWorker<List<UserAccount>, Void>() {
            @Override
            protected List<UserAccount> doInBackground() {
                PermissionChecker.requireAdmin();
                List<UserAccount> all = service.findAll();

                // filter by role
                if (selectedRole != null)
                    all = UserAccountStreamQueries.filterByRole(all, selectedRole);

                // filter by keyword
                if (keyword != null && !keyword.isBlank())
                    all = UserAccountStreamQueries.searchByUsername(all, keyword);

                return all;
            }

            @Override
            protected void done() {
                try {
                    model.setData(get());
                } catch (Exception ex) {
                    handleException(ex);
                } finally {
                    btnResetPwd.setEnabled(true);
                }
            }
        }.execute();
    }

    private void handleException(Exception ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof AppException ae)
            MessageBox.warn(this, ae.getUserMessage());
        else {
            log.error("Error in UserAccountsPanel", cause);
            MessageBox.error(this, "Lỗi hệ thống: " + cause.getMessage());
        }
    }

    // ---- inner table model ----

    private static class AccountTableModel extends AbstractTableModel {
        private static final String[] COLS = { "UserId", "Username", "Vai trò" };
        private List<UserAccount> data = new ArrayList<>();

        void setData(List<UserAccount> list) {
            this.data = list == null ? new ArrayList<>() : list;
            fireTableDataChanged();
        }

        UserAccount getRow(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return COLS.length;
        }

        @Override
        public String getColumnName(int col) {
            return COLS[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            UserAccount u = data.get(row);
            return switch (col) {
                case 0 -> u.getUserID();
                case 1 -> u.getUsername();
                case 2 -> u.getRole();
                default -> null;
            };
        }
    }
}
