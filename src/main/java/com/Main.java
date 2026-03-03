package com;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.exception.GlobalExceptionHandler;
import com.security.CurrentUser;

import com.ui.dialog.LoginDialog;
import com.ui.frame.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // 1. Register global exception handler (must be first)
        GlobalExceptionHandler.register();

        // 2. Set Swing look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(Main::launchLogin);
    }

    /** Shows the login dialog. On success, opens MainFrame. */
    public static void launchLogin() {
        LoginDialog dlg = new LoginDialog(null);
        dlg.setVisible(true);
        CurrentUser user = dlg.getLoggedInUser();
        if (user == null) {
            // Dialog closed without login → exit
            System.exit(0);
        }
        MainFrame frame = new MainFrame(user);
        frame.setVisible(true);
        log.info("MainFrame opened for user: {}", user.username());
    }
}
