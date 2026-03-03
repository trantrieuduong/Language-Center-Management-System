package com.ui.dialog;

import com.security.CurrentUser;
import com.service.AuthService;
import com.service.impl.AuthServiceImpl;
import com.ui.util.UiUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {

    private static final Logger log = LoggerFactory.getLogger(LoginDialog.class);

    private final AuthService authService = new AuthServiceImpl();

    @Getter
    private CurrentUser loggedInUser;

    private final JTextField tfUsername = new JTextField(18);
    private final JPasswordField pfPassword = new JPasswordField(18);
    private final JButton btnLogin = UiUtil.primaryButton("Đăng nhập");
    private final JLabel lblStatus = new JLabel(" ");

    public LoginDialog(Frame parent) {
        super(parent, "Đăng nhập - Hệ thống Quản lý Trung tâm Anh ngữ", true);
        setLayout(new BorderLayout());
        setResizable(false);

        add(buildContent(), BorderLayout.CENTER);
        pack();
        setMinimumSize(new Dimension(360, 0));
        setLocationRelativeTo(parent);

        // Enter key submits
        getRootPane().setDefaultButton(btnLogin);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC");
        getRootPane().getActionMap().put("ESC", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout());

        // title banner
        JLabel banner = new JLabel("LANGUAGE CENTER", SwingConstants.CENTER);
        banner.setFont(new Font("Segoe UI", Font.BOLD, 20));
        banner.setForeground(Color.WHITE);
        banner.setBackground(UiUtil.COLOR_PRIMARY);
        banner.setOpaque(true);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        outer.add(banner, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 5, 6, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        form.add(new JLabel("Tên đăng nhập:"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(tfUsername, c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        form.add(new JLabel("Mật khẩu:"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(pfPassword, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        lblStatus.setForeground(Color.RED);
        form.add(lblStatus, c);

        c.gridy = 3;
        btnLogin.addActionListener(e -> onLogin());
        form.add(btnLogin, c);

        outer.add(form, BorderLayout.CENTER);

        return outer;
    }

    private void onLogin() {
        String user = tfUsername.getText().trim();
        String pass = new String(pfPassword.getPassword());
        lblStatus.setText(" ");
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang xác thực...");

        new SwingWorker<CurrentUser, Void>() {
            @Override
            protected CurrentUser doInBackground() {
                return authService.login(user, pass);
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
                try {
                    loggedInUser = get();
                    dispose();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    log.warn("Login failed: {}", cause.getMessage());
                    lblStatus.setText(cause.getMessage());
                    pfPassword.setText("");
                    pfPassword.requestFocus();
                }
            }
        }.execute();
    }
}
