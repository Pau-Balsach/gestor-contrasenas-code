package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;

public class login extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(login.class.getName());

    // ── Paleta de colores ──────────────────────────────────────────────
    private static final Color C_BG         = new Color(0xF7F8FA);
    private static final Color C_PANEL      = Color.WHITE;
    private static final Color C_NAVY       = new Color(0x1A2B4A);
    private static final Color C_GOLD       = new Color(0xC8A951);
    private static final Color C_BORDER     = new Color(0xDDE1E9);
    private static final Color C_TEXT       = new Color(0x1A2B4A);
    private static final Color C_MUTED      = new Color(0x7A869A);
    private static final Color C_BTN_HOVER  = new Color(0x243D6B);
    private static final Color C_LINK       = new Color(0x2563EB);

    // ── Anti-brute-force ──────────────────────────────────────────────
    private static int loginFallidos = 0;
    private static long loginBloqueoHastaMs = 0;
    private static final int LOGIN_MAX_INTENTOS_SIN_BLOQUEO = 5;
    private static final long LOGIN_BLOQUEO_BASE_MS = 5_000L;
    private static final long LOGIN_BLOQUEO_MAX_MS = 300_000L;

    public login() {
        initComponents();
        setTitle("GestorContrasenyas - Iniciar sesión");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        // ── Silenciar beeps ───────────────────────────────────────────
        java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == java.awt.event.KeyEvent.KEY_PRESSED
                    && e.getKeyCode() == java.awt.event.KeyEvent.VK_BACK_SPACE) {
                Object src = e.getSource();
                if (src instanceof JTextField tf && tf.getText().isEmpty()) {
                    e.consume(); return true;
                }
            }
            return false;
        });

        UIManager.put("OptionPane.errorSound",       null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound",    null);
        UIManager.put("OptionPane.warningSound",     null);

        // ── Componentes ───────────────────────────────────────────────
        jPanel1        = new JPanel();
        headerPanel    = new JPanel();
        jLabel1        = new JLabel();
        subtitleLabel  = new JLabel();
        jPanel2        = new JPanel();
        jLabel2        = new JLabel();
        jLabel3        = new JLabel();
        txtPassword    = new JPasswordField();
        txtUser        = new JTextField();
        btnLogin       = new JButton();
        btnSignin      = new JButton();
        btnResetPasswd = new JButton();
        dividerLabel   = new JLabel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, C_BG));

        // ── Panel principal ───────────────────────────────────────────
        jPanel1.setBackground(C_BG);
        jPanel1.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────
        headerPanel.setBackground(C_NAVY);
        headerPanel.setPreferredSize(new Dimension(480, 100));
        headerPanel.setLayout(new GridBagLayout());

        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));

        jLabel1.setFont(loadFont("Georgia", Font.PLAIN, 28));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Gestor de contraseñas");
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator headerSep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_GOLD);
                g.fillRect(0, 0, 50, 2);
            }
        };
        headerSep.setPreferredSize(new Dimension(50, 2));
        headerSep.setMaximumSize(new Dimension(50, 2));
        headerSep.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel.setFont(loadFont("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0xAEC3D8));
        subtitleLabel.setText("Accede de forma segura a tus credenciales");
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerContent.add(jLabel1);
        headerContent.add(Box.createVerticalStrut(6));
        headerContent.add(headerSep);
        headerContent.add(Box.createVerticalStrut(6));
        headerContent.add(subtitleLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 36, 0, 36);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        headerPanel.add(headerContent, gbc);

        jPanel1.add(headerPanel, BorderLayout.NORTH);

        // ── Formulario ────────────────────────────────────────────────
        jPanel2.setBackground(C_PANEL);
        jPanel2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER),
            BorderFactory.createEmptyBorder(36, 40, 28, 40)
        ));
        jPanel2.setLayout(new GridBagLayout());

        // Labels
        jLabel2.setText("Correo electrónico");
        jLabel2.setFont(loadFont("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXT);

        jLabel3.setText("Contraseña");
        jLabel3.setFont(loadFont("Segoe UI", Font.BOLD, 12));
        jLabel3.setForeground(C_TEXT);

        // Campos
        styleTextField(txtUser);
        styleTextField(txtPassword);
        txtPassword.setEchoChar('●');

        txtUser.addActionListener(this::txtUserActionPerformed);

        // Botón login principal
        styleButtonPrimary(btnLogin, "Iniciar sesión");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        // Divisor
        dividerLabel.setFont(loadFont("Segoe UI", Font.PLAIN, 11));
        dividerLabel.setForeground(C_MUTED);
        dividerLabel.setText("-- ó --");
        dividerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Botón registro
        styleButtonSecondary(btnSignin, "Crear cuenta nueva");
        btnSignin.addActionListener(this::btnSigninActionPerformed);

        // Link recuperar
        btnResetPasswd.setText("¿Olvidaste tu contraseña?");
        btnResetPasswd.setFont(loadFont("Segoe UI", Font.PLAIN, 12));
        btnResetPasswd.setForeground(C_LINK);
        btnResetPasswd.setBorderPainted(false);
        btnResetPasswd.setContentAreaFilled(false);
        btnResetPasswd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnResetPasswd.setFocusPainted(false);
        btnResetPasswd.addActionListener(this::btnResetPasswdActionPerformed);

        // Layout del formulario
        GridBagConstraints f = new GridBagConstraints();
        f.fill = GridBagConstraints.HORIZONTAL;
        f.weightx = 1.0;
        f.insets = new Insets(0, 0, 6, 0);
        f.gridx = 0; f.gridy = 0;
        jPanel2.add(jLabel2, f);

        f.gridy = 1; f.insets = new Insets(0, 0, 20, 0);
        jPanel2.add(txtUser, f);

        f.gridy = 2; f.insets = new Insets(0, 0, 6, 0);
        jPanel2.add(jLabel3, f);

        f.gridy = 3; f.insets = new Insets(0, 0, 24, 0);
        jPanel2.add(txtPassword, f);

        f.gridy = 4; f.insets = new Insets(0, 0, 16, 0);
        jPanel2.add(btnLogin, f);

        f.gridy = 5; f.insets = new Insets(0, 0, 16, 0);
        jPanel2.add(dividerLabel, f);

        f.gridy = 6; f.insets = new Insets(0, 0, 16, 0);
        jPanel2.add(btnSignin, f);

        jPanel1.add(jPanel2, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(C_BG);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        footerPanel.add(btnResetPasswd);
        jPanel1.add(footerPanel, BorderLayout.SOUTH);

        // ── Frame ─────────────────────────────────────────────────────
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 480, 480, 480));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }

    // ── Helpers de estilo ─────────────────────────────────────────────
    private Font loadFont(String name, int style, int size) {
        return new Font(name, style, size);
    }

    private void styleTextField(JComponent field) {
        field.setFont(loadFont("Segoe UI", Font.PLAIN, 14));
        field.setForeground(C_TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, C_BORDER),
            BorderFactory.createEmptyBorder(10, 12, 11, 12)
        ));
        field.setPreferredSize(new Dimension(320, 48));
        if (field instanceof JTextField tf) {
            tf.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override public void focusGained(java.awt.event.FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(8, C_NAVY),
                        BorderFactory.createEmptyBorder(10, 12, 11, 12)
                    ));
                }
                @Override public void focusLost(java.awt.event.FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(8, C_BORDER),
                        BorderFactory.createEmptyBorder(10, 12, 11, 12)
                    ));
                }
            });
        }
    }

    private void styleButtonPrimary(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(loadFont("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(C_NAVY);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(320, 44));
        btn.setBorder(new RoundedBorder(8, C_NAVY));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(C_BTN_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(C_NAVY); }
        });
    }

    private void styleButtonSecondary(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(loadFont("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(C_NAVY);
        btn.setBackground(Color.WHITE);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(320, 44));
        btn.setBorder(new RoundedBorder(8, C_BORDER));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0xF0F4FF)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
    }

    // ── Borde redondeado reutilizable ─────────────────────────────────
    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, w - 1, h - 1, radius, radius));
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.set(radius/2, radius/2, radius/2, radius/2); return i;
        }
    }

    // ── Lógica de negocio (sin modificar) ─────────────────────────────
    private void txtUserActionPerformed(java.awt.event.ActionEvent evt) {}

    private void btnResetPasswdActionPerformed(java.awt.event.ActionEvent evt) {
        String email = txtUser.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce tu email primero.");
            return;
        }
        String error = com.mycompany.gestorcontrasenyas.service.AuthService.enviarRecuperacion(email);
        if (error == null) {
            JOptionPane.showMessageDialog(this, "Correo de recuperación enviado. Revisa tu bandeja de entrada.");
        } else {
            JOptionPane.showMessageDialog(this, error);
        }
    }

    private static long calcularLoginBackoffMs(int intentos) {
        int exceso = Math.max(0, intentos - LOGIN_MAX_INTENTOS_SIN_BLOQUEO);
        if (exceso == 0) return 0;
        long backoff = LOGIN_BLOQUEO_BASE_MS * (1L << Math.min(exceso - 1, 10));
        return Math.min(backoff, LOGIN_BLOQUEO_MAX_MS);
    }

    private static String tiempoLoginRestante(long restanteMs) {
        long segundos = Math.max(1, (restanteMs + 999) / 1000);
        return segundos + " segundos";
    }

    private static void registrarLoginFallido() {
        loginFallidos++;
        long backoff = calcularLoginBackoffMs(loginFallidos);
        if (backoff > 0) loginBloqueoHastaMs = System.currentTimeMillis() + backoff;
    }

    private static void reiniciarLoginBruteforce() {
        loginFallidos = 0;
        loginBloqueoHastaMs = 0;
    }

    private static boolean cumplePoliticaPassword(char[] passwordChars) {
        if (passwordChars == null || passwordChars.length < 10) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : passwordChars) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {
        String email    = txtUser.getText().trim();
        char[] pwdChars = txtPassword.getPassword();
        try {
            long ahora = System.currentTimeMillis();
            if (loginBloqueoHastaMs > ahora) {
                long restante = loginBloqueoHastaMs - ahora;
                JOptionPane.showMessageDialog(this,
                    "Demasiados intentos. Espera " + tiempoLoginRestante(restante) + " antes de reintentar.");
                return;
            }
            if (email.isEmpty() || pwdChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
                return;
            }
            String error = com.mycompany.gestorcontrasenyas.service.AuthService.login(email, pwdChars);
            if (error != null) {
                registrarLoginFallido();
                JOptionPane.showMessageDialog(this, error);
                return;
            }
            reiniciarLoginBruteforce();
            String userId       = SupabaseAuth.getUserId();
            boolean tieneMaster = com.mycompany.gestorcontrasenyas.service.AuthService.tieneMasterKey(userId);
            MasterPassword master = new MasterPassword(!tieneMaster);
            master.setLocationRelativeTo(null);
            master.setVisible(true);
            dispose();
        } finally {
            Arrays.fill(pwdChars, '\0');
            txtPassword.setText("");
        }
    }

    private void btnSigninActionPerformed(java.awt.event.ActionEvent evt) {
        String email    = txtUser.getText().trim();
        char[] pwdChars = txtPassword.getPassword();
        try {
            if (email.isEmpty() || pwdChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Rellena todos los campos.");
                return;
            }
            if (!cumplePoliticaPassword(pwdChars)) {
                JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener 10+ caracteres e incluir mayúscula, minúscula, número y símbolo.");
                return;
            }
            String error = com.mycompany.gestorcontrasenyas.service.AuthService.signup(email, pwdChars);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
                return;
            }
            JOptionPane.showMessageDialog(this, "Registro correcto. Revisa tu email para verificar la cuenta.");
        } finally {
            Arrays.fill(pwdChars, '\0');
            txtPassword.setText("");
        }
    }

    // Variables declaration
    private JButton btnLogin, btnResetPasswd, btnSignin;
    private JLabel jLabel1, jLabel2, jLabel3, subtitleLabel, dividerLabel;
    private JPanel jPanel1, jPanel2, headerPanel;
    private JPasswordField txtPassword;
    private JTextField txtUser;
}