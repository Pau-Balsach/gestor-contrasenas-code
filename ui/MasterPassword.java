package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.service.AuthService;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;

public class MasterPassword extends javax.swing.JFrame {

    private final boolean esPrimeraVez;

    private static final Color C_BG        = new Color(0xF7F8FA);
    private static final Color C_PANEL     = Color.WHITE;
    private static final Color C_NAVY      = new Color(0x1A2B4A);
    private static final Color C_GOLD      = new Color(0xC8A951);
    private static final Color C_BORDER    = new Color(0xDDE1E9);
    private static final Color C_TEXT      = new Color(0x1A2B4A);
    private static final Color C_MUTED     = new Color(0x7A869A);
    private static final Color C_WARN_BG   = new Color(0xFFFBEB);
    private static final Color C_WARN_BDR  = new Color(0xF5C842);

    private static int intentosFallidos = 0;
    private static long bloqueoHastaMs = 0;
    private static final int MAX_INTENTOS_SIN_BLOQUEO = 5;
    private static final long BLOQUEO_BASE_MS = 5_000L;
    private static final long BLOQUEO_MAX_MS = 300_000L;

    public MasterPassword(boolean esPrimeraVez) {
        this.esPrimeraVez = esPrimeraVez;
        initComponents();
        setTitle("GestorContrasenyas - Contraseña maestra");
        configurarVista();
    }

    private void configurarVista() {
        if (esPrimeraVez) {
            jLabel1.setText("Crear contraseña maestra");
            jLabel4.setVisible(true);
            txtConfirmar.setVisible(true);
            jLabel5.setText("Esta contraseña protege tus datos. No la olvides, no se puede recuperar.");
            jLabel5.setForeground(new Color(0x92400E));
            warningPanel.setVisible(true);
        } else {
            jLabel1.setText("Contraseña maestra");
            jLabel4.setVisible(false);
            txtConfirmar.setVisible(false);
            jLabel5.setText("Introduce tu contraseña maestra para acceder a tus credenciales.");
            jLabel5.setForeground(C_MUTED);
            warningPanel.setVisible(false);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        UIManager.put("OptionPane.errorSound",       null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound",    null);
        UIManager.put("OptionPane.warningSound",     null);

        jPanel1       = new JPanel();
        headerPanel   = new JPanel();
        jLabel1       = new JLabel();
        jPanel2       = new JPanel();
        jLabel2       = new JLabel();
        jLabel3       = new JLabel();
        jLabel4       = new JLabel();
        jLabel5       = new JLabel();
        txtMaster     = new JPasswordField();
        txtConfirmar  = new JPasswordField();
        btnAceptar    = new JButton();
        btnCancelar   = new JButton();
        warningPanel  = new JPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ── Panel principal ───────────────────────────────────────────
        jPanel1.setBackground(C_BG);
        jPanel1.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────
        headerPanel.setBackground(C_NAVY);
        headerPanel.setPreferredSize(new Dimension(460, 80));
        headerPanel.setLayout(new GridBagLayout());

        JPanel headerInner = new JPanel();
        headerInner.setOpaque(false);
        headerInner.setLayout(new BoxLayout(headerInner, BoxLayout.Y_AXIS));

        JLabel lockIcon = new JLabel("🔐");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lockIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        jLabel1.setFont(new Font("Georgia", Font.PLAIN, 22));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_GOLD);
                g.fillRect(0, 0, 40, 2);
            }
        };
        sep.setPreferredSize(new Dimension(40, 2));
        sep.setMaximumSize(new Dimension(40, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerInner.add(lockIcon);
        headerInner.add(Box.createVerticalStrut(4));
        headerInner.add(jLabel1);
        headerInner.add(Box.createVerticalStrut(5));
        headerInner.add(sep);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 32, 0, 32);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        headerPanel.add(headerInner, gbc);
        jPanel1.add(headerPanel, BorderLayout.NORTH);

        // ── Formulario ────────────────────────────────────────────────
        jPanel2.setBackground(C_PANEL);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(30, 40, 24, 40));
        jPanel2.setLayout(new GridBagLayout());

        jLabel5.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        jLabel5.setForeground(C_MUTED);

        jLabel2.setText("Contraseña maestra");
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel2.setForeground(C_TEXT);

        jLabel4.setText("Confirmar contraseña");
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel4.setForeground(C_TEXT);

        // jLabel3 (unused visually but kept in declaration)
        jLabel3 = new JLabel();

        styleField(txtMaster);
        styleField(txtConfirmar);
        txtMaster.setEchoChar('●');
        txtConfirmar.setEchoChar('●');

        styleButtonPrimary(btnAceptar, "Continuar");
        btnAceptar.addActionListener(this::btnAceptarActionPerformed);

        btnCancelar.setText("Volver al inicio de sesión");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setForeground(new Color(0x2563EB));
        btnCancelar.setBorderPainted(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        // Aviso primera vez
        warningPanel.setLayout(new BorderLayout(8, 0));
        warningPanel.setBackground(C_WARN_BG);
        warningPanel.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(6, C_WARN_BDR),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel warnIcon = new JLabel("⚠");
        warnIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        warnIcon.setForeground(new Color(0x92400E));
        JLabel warnText = new JLabel("<html>Esta contraseña <b>no se puede recuperar</b>. Guárdala en un lugar seguro.</html>");
        warnText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warnText.setForeground(new Color(0x92400E));
        warningPanel.add(warnIcon, BorderLayout.WEST);
        warningPanel.add(warnText, BorderLayout.CENTER);

        // Layout
        GridBagConstraints f = new GridBagConstraints();
        f.fill = GridBagConstraints.HORIZONTAL;
        f.weightx = 1.0;
        f.gridx = 0;

        f.gridy = 0; f.insets = new Insets(0, 0, 16, 0);
        jPanel2.add(jLabel5, f);

        f.gridy = 1; f.insets = new Insets(0, 0, 6, 0);
        jPanel2.add(jLabel2, f);

        f.gridy = 2; f.insets = new Insets(0, 0, 20, 0);
        jPanel2.add(txtMaster, f);

        f.gridy = 3; f.insets = new Insets(0, 0, 6, 0);
        jPanel2.add(jLabel4, f);

        f.gridy = 4; f.insets = new Insets(0, 0, 20, 0);
        jPanel2.add(txtConfirmar, f);

        f.gridy = 5; f.insets = new Insets(0, 0, 20, 0);
        jPanel2.add(warningPanel, f);

        f.gridy = 6; f.insets = new Insets(0, 0, 12, 0);
        jPanel2.add(btnAceptar, f);

        jPanel1.add(jPanel2, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(C_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 12, 0));
        footer.add(btnCancelar);
        jPanel1.add(footer, BorderLayout.SOUTH);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 460, 460, 460));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }

    private void styleField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(C_TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(8, C_BORDER),
            BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        field.setPreferredSize(new Dimension(300, 42));
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(8, C_NAVY),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)
                ));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(8, C_BORDER),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)
                ));
            }
        });
    }

    private void styleButtonPrimary(JButton btn, String text) {
        btn.setText(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(C_NAVY);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(300, 44));
        btn.setBorder(new login.RoundedBorder(8, C_NAVY));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(0x243D6B)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(C_NAVY); }
        });
    }

    // ── Lógica sin modificar ──────────────────────────────────────────
    private static boolean isBlank(char[] chars) {
        for (char c : chars) if (!Character.isWhitespace(c)) return false;
        return true;
    }
    private static boolean equalsChars(char[] a, char[] b) { return Arrays.equals(a, b); }

    private static long calcularBackoffMs(int intentos) {
        int exceso = Math.max(0, intentos - MAX_INTENTOS_SIN_BLOQUEO);
        if (exceso == 0) return 0;
        long backoff = BLOQUEO_BASE_MS * (1L << Math.min(exceso - 1, 10));
        return Math.min(backoff, BLOQUEO_MAX_MS);
    }

    private static String tiempoRestante(long restanteMs) {
        long segundos = Math.max(1, (restanteMs + 999) / 1000);
        return segundos + " segundos";
    }

    private void registrarFallo() {
        intentosFallidos++;
        long backoff = calcularBackoffMs(intentosFallidos);
        if (backoff > 0) bloqueoHastaMs = System.currentTimeMillis() + backoff;
    }

    private void reiniciarProteccionBruteforce() {
        intentosFallidos = 0;
        bloqueoHastaMs = 0;
    }

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {
        char[] masterChars    = txtMaster.getPassword();
        char[] confirmarChars = txtConfirmar.getPassword();
        try {
            long ahora = System.currentTimeMillis();
            if (bloqueoHastaMs > ahora) {
                long restante = bloqueoHastaMs - ahora;
                JOptionPane.showMessageDialog(this,
                    "Demasiados intentos. Espera " + tiempoRestante(restante) + " antes de reintentar.");
                return;
            }
            if (masterChars.length == 0 || isBlank(masterChars)) {
                JOptionPane.showMessageDialog(this, "Introduce la contraseña maestra.");
                return;
            }
            if (esPrimeraVez) {
                if (!equalsChars(masterChars, confirmarChars)) {
                    JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
                    return;
                }
                if (masterChars.length < 8) {
                    JOptionPane.showMessageDialog(this, "La contraseña maestra debe tener al menos 8 caracteres.");
                    return;
                }
            }
            String error = AuthService.configurarMasterKey(masterChars);
            if (error != null) {
                registrarFallo();
                JOptionPane.showMessageDialog(this, error);
                return;
            }
            reiniciarProteccionBruteforce();
            MenuPrincipal principal = new MenuPrincipal();
            principal.setLocationRelativeTo(null);
            principal.setVisible(true);
            dispose();
        } finally {
            Arrays.fill(masterChars, '\0');
            Arrays.fill(confirmarChars, '\0');
            txtMaster.setText("");
            txtConfirmar.setText("");
        }
    }

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        login loginScreen = new login();
        loginScreen.setLocationRelativeTo(null);
        loginScreen.setVisible(true);
    }

    // Variables declaration
    private JButton btnAceptar, btnCancelar;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5;
    private JPanel jPanel1, jPanel2, headerPanel, warningPanel;
    private JPasswordField txtConfirmar, txtMaster;
}