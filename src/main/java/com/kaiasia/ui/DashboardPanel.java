package com.kaiasia.ui;

import com.kaiasia.model.UserInfo;
import com.kaiasia.t24utils.T24UtilsApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.kaiasia.t24utils.T24UtilsApiClient.showAccountDetails;

public class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel accountPanel;

    public DashboardPanel(MainFrame mainFrame, UserInfo userInfo) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        System.out.println("DEBUG: userInfo trong DashboardPanel: " + userInfo);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(7, 1, 10, 10));
        sidebar.setBackground(new Color(240, 240, 240));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));

        JLabel lblUserName = new JLabel(userInfo.getCustomerName(), SwingConstants.CENTER);
        lblUserName.setFont(new Font("Arial", Font.BOLD, 16));
        lblUserName.setForeground(Color.BLUE);
        lblUserName.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Mở trang thông tin cá nhân
        lblUserName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showProfile(userInfo);
            }
        });

        JButton btnHome = new JButton("🏠 Trang chủ");
        JButton btnAccounts = new JButton("💳 Tài khoản & Thẻ");
        JButton btnInternalTransfer = new JButton("💳 Chuyển khoản nội bộ");
        JButton btnNapasTransfer = new JButton("🌍 Chuyển khoản NAPAS");
        JButton btnFeatures = new JButton("⚙️ Tính năng khác");
        JButton btnLogout = new JButton("🚪 Đăng xuất");

        // Xử lý sự kiện khi nhấn vào nút chuyển khoản nội bộ
        btnInternalTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showFundsTransferInScreen(mainFrame.getCurrentUser());
            }
        });

        // Xử lý sự kiện khi nhấn vào nút chuyển khoản NAPAS
        btnNapasTransfer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.showTransferScreen(mainFrame.getCurrentUser());
            }
        });

        btnFeatures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Tính năng đang được phát triển!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Session trước khi xóa: " + userInfo.getSessionId());
                int confirm = JOptionPane.showConfirmDialog(
                        mainFrame,
                        "Bạn có chắc chắn muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    userInfo.clearSession();
                    mainFrame.showLogin();
                }
            }
        });

        sidebar.add(lblUserName);
        sidebar.add(btnHome);
        sidebar.add(btnAccounts);
        sidebar.add(btnInternalTransfer);
        sidebar.add(btnNapasTransfer);
        sidebar.add(btnFeatures);
        sidebar.add(btnLogout);

        // Nội dung chính
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Xin chào, " + userInfo.getCustomerName(), SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Danh sách tài khoản
        accountPanel = new JPanel();
        accountPanel.setLayout(new GridLayout(0, 1, 10, 10));
        Font titleFont = new Font("Arial", Font.BOLD, 20);
        Border titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                "Danh sách tài khoản & thẻ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                titleFont,
                Color.BLACK
        );

        accountPanel.setBorder(titledBorder);

        loadAccounts(userInfo.getCustomerID());

        mainContent.add(lblWelcome, BorderLayout.NORTH);
        mainContent.add(new JScrollPane(accountPanel), BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createAccountPanel(String title, String accountNumber, String currency, String balance, String accountId) {
        // Panel chính
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setBackground(new Color(240, 247, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 150));

        // Panel bên trái chứa thông tin tài khoản
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        leftPanel.setOpaque(false);

        // Panel hàng trên chứa tên tài khoản
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        leftPanel.add(lblTitle);

        // Panel hàng dưới chứa số tài khoản và số dư
        JPanel bottomRow = new JPanel(new BorderLayout(20, 0));
        bottomRow.setOpaque(false);

        JLabel lblAccountNumber = new JLabel(accountNumber);
        lblAccountNumber.setFont(new Font("Arial", Font.PLAIN, 20));
        lblAccountNumber.setForeground(new Color(100, 100, 100));

        JLabel lblBalance = new JLabel(currency + " " + balance);
        lblBalance.setFont(new Font("Arial", Font.BOLD, 24));
        lblBalance.setHorizontalAlignment(SwingConstants.RIGHT);

        bottomRow.add(lblAccountNumber, BorderLayout.WEST);
        bottomRow.add(lblBalance, BorderLayout.EAST);

        leftPanel.add(bottomRow);

        // Nút chi tiết (>)
        JLabel btnDetail = new JLabel(">");
        btnDetail.setFont(new Font("Arial", Font.BOLD, 30));
        btnDetail.setForeground(new Color(150, 150, 150));
        btnDetail.setHorizontalAlignment(SwingConstants.RIGHT);
        btnDetail.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện khi nhấn vào ">"
        btnDetail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAccountDetails(accountId);
            }
        });

        // Thêm các hàng vào panel chính
        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(btnDetail, BorderLayout.EAST);

        return panel;
    }

    private void loadAccounts(String customerId) {
        accountPanel.removeAll();
        accountPanel.setLayout(new BorderLayout());
        accountPanel.setBackground(Color.WHITE);

        // Container cho tiêu đề và danh sách tài khoản
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel chứa danh sách tài khoản
        JPanel accountListPanel = new JPanel();
        accountListPanel.setLayout(new BoxLayout(accountListPanel, BoxLayout.Y_AXIS));
        accountListPanel.setBackground(Color.WHITE);

        // Thêm các tài khoản
        JSONObject response = T24UtilsApiClient.getCustomerAccounts(customerId);
        if (response != null && response.getJSONObject("body").getString("status").equals("OK")) {
            JSONArray accounts = response.getJSONObject("body").getJSONObject("enquiry").getJSONArray("accounts");
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject account = accounts.getJSONObject(i);
                String accountId = account.getString("accountId");

                JSONObject accountInfo = T24UtilsApiClient.getAccountInfo(accountId);
                if (accountInfo != null && accountInfo.getJSONObject("body").getString("status").equals("OK")) {
                    JSONObject detail = accountInfo.getJSONObject("body").getJSONObject("enquiry");

                    JPanel accountItemPanel = createAccountPanel(
                            detail.optString("shortTitle", "Tài khoản thanh toán"),
                            detail.optString("accountId", "N/A"),
                            detail.optString("currency", "VND"),
                            detail.optString("avaiBalance", "0"),
                            detail.optString("accountId", "N/A")
                    );
                    accountListPanel.add(accountItemPanel);

                    // Thêm khoảng cách giữa các ô tài khoản
                    if (i < accounts.length() - 1) {
                        accountListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
            }
        } else {
            JLabel noAccountLabel = new JLabel("Không có tài khoản nào!", SwingConstants.CENTER);
            noAccountLabel.setFont(new Font("Arial", Font.BOLD, 20));
            accountListPanel.add(noAccountLabel);
        }

        contentPanel.add(accountListPanel);
        accountPanel.add(contentPanel, BorderLayout.NORTH);

        revalidate();
        repaint();
    }
}