package com.ui.panel;

import com.ui.util.UiUtil;
import javax.swing.*;
import java.awt.*;

/** Reusable scaffold panel with a title and "coming soon" placeholder. */
public class ScaffoldPanel extends JPanel {

    public ScaffoldPanel(String panelTitle) {
        setLayout(new BorderLayout(10, 10));
        setBackground(UiUtil.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = UiUtil.sectionTitle(panelTitle);
        add(title, BorderLayout.NORTH);

        JLabel placeholder = new JLabel(
                "<html><center><br/><br/><br/>" +
                        "<span style='font-size:14pt; color:#999;'>🚧 Tính năng đang phát triển...</span>" +
                        "<br/><span style='font-size:10pt; color:#bbb;'>Module này đã được cấu trúc và sẽ được triển khai đầy đủ.</span>"
                        +
                        "</center></html>",
                SwingConstants.CENTER);
        add(placeholder, BorderLayout.CENTER);
    }
}
