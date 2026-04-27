package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MenuPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MenuPrincipal.class.getName());
    private static final long RENOVACION_CADA_MINUTOS = 45L;

    private final AtomicBoolean cerrandoSesion = new AtomicBoolean(false);
    private ScheduledExecutorService tokenScheduler;

    // ── Paleta ────────────────────────────────────────────────────────
    private static final Color C_BG       = new Color(0xF7F8FA);
    private static final Color C_PANEL    = Color.WHITE;
    private static final Color C_NAVY     = new Color(0x1A2B4A);
    private static final Color C_GOLD     = new Color(0xC8A951);
    private static final Color C_BORDER   = new Color(0xDDE1E9);
    private static final Color C_TEXT     = new Color(0x1A2B4A);
    private static final Color C_MUTED    = new Color(0x7A869A);

    public MenuPrincipal() {
        initComponents();
        setTitle("GestorContrasenyas");
        iniciarRenovacionToken();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent evt) {
                cerrarSesionYVolverALogin(false);
            }
            @Override public void windowClosed(java.awt.event.WindowEvent evt) {
                detenerRenovacionToken();
            }
        });
    }

    private void iniciarRenovacionToken() {
        if (tokenScheduler != null && !tokenScheduler.isShutdown()) return;
        ThreadFactory factory = r -> {
            Thread hilo = new Thread(r, "token-refresh-worker");
            hilo.setDaemon(true);
            return hilo;
        };
        tokenScheduler = Executors.newSingleThreadScheduledExecutor(factory);
        tokenScheduler.scheduleAtFixedRate(() -> {
            if (cerrandoSesion.get()) return;
            String error = SupabaseAuth.renovarToken();
            if (error != null) {
                SwingUtilities.invokeLater(() -> {
                    if (isDisplayable() && !cerrandoSesion.get()) {
                        JOptionPane.showMessageDialog(this,
                            "La sesión expiró y no pudo renovarse. Debes iniciar sesión de nuevo.");
                    }
                    cerrarSesionYVolverALogin(true);
                });
            }
        }, RENOVACION_CADA_MINUTOS, RENOVACION_CADA_MINUTOS, TimeUnit.MINUTES);
    }

    private void detenerRenovacionToken() {
        if (tokenScheduler != null) { tokenScheduler.shutdownNow(); tokenScheduler = null; }
    }

    private void cerrarSesionYVolverALogin(boolean redirigirALogin) {
        if (!cerrandoSesion.compareAndSet(false, true)) return;
        detenerRenovacionToken();
        SupabaseAuth.cerrarSesion();
        if (redirigirALogin && isDisplayable()) {
            login loginPantalla = new login();
            loginPantalla.setLocationRelativeTo(null);
            loginPantalla.setVisible(true);
        }
        if (isDisplayable()) dispose();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        UIManager.put("OptionPane.errorSound",       null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound",    null);
        UIManager.put("OptionPane.warningSound",     null);

        jPanel1            = new JPanel();
        headerPanel        = new JPanel();
        jLabel1            = new JLabel();
        jPanel2            = new JPanel();
        btnConsultarCuenta = new JButton();
        btnAñadirCuenta    = new JButton();
        btnCerrarSesion    = new JButton();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ── Layout principal ──────────────────────────────────────────
        jPanel1.setBackground(C_BG);
        jPanel1.setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────────
        headerPanel.setBackground(C_NAVY);
        headerPanel.setPreferredSize(new Dimension(480, 110));
        headerPanel.setLayout(new GridBagLayout());

        JPanel hInner = new JPanel();
        hInner.setOpaque(false);
        hInner.setLayout(new BoxLayout(hInner, BoxLayout.Y_AXIS));

        jLabel1.setFont(new Font("Georgia", Font.PLAIN, 26));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("Gestor de contraseñas");
        jLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator goldLine = new JSeparator() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_GOLD);
                g.fillRect(0, 0, 50, 2);
            }
        };
        goldLine.setPreferredSize(new Dimension(50, 2));
        goldLine.setMaximumSize(new Dimension(50, 2));
        goldLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Panel de control");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(0xAEC3D8));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        hInner.add(jLabel1);
        hInner.add(Box.createVerticalStrut(6));
        hInner.add(goldLine);
        hInner.add(Box.createVerticalStrut(5));
        hInner.add(subtitle);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 36, 0, 36);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        headerPanel.add(hInner, gbc);
        jPanel1.add(headerPanel, BorderLayout.NORTH);

        // ── Tarjetas de acción ────────────────────────────────────────
        jPanel2.setBackground(C_BG);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(32, 36, 16, 36));
        jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));

        // Sección título
        JLabel sectionLabel = new JLabel("Acciones");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLabel.setForeground(C_MUTED);
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        jPanel2.add(sectionLabel);

        // Botón añadir
        styleActionCard(btnAñadirCuenta, "Añadir cuenta", "➕", "Guarda nuevas credenciales de forma segura");
        btnAñadirCuenta.addActionListener(this::btnAñadirCuentaActionPerformed);
        jPanel2.add(btnAñadirCuenta);
        jPanel2.add(Box.createVerticalStrut(12));

        // Botón consultar
        styleActionCard(btnConsultarCuenta, "Consultar cuentas", "🔍", "Visualiza y gestiona tus credenciales guardadas");
        btnConsultarCuenta.addActionListener(this::btnConsultarCuentaActionPerformed);
        jPanel2.add(btnConsultarCuenta);

        jPanel1.add(jPanel2, BorderLayout.CENTER);

        // ── Footer con cerrar sesión ──────────────────────────────────
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(C_BG);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER),
            BorderFactory.createEmptyBorder(12, 36, 16, 36)
        ));

        btnCerrarSesion.setText("Cerrar sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCerrarSesion.setForeground(new Color(0xDC2626));
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setContentAreaFilled(false);
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);
        footerPanel.add(btnCerrarSesion);
        jPanel1.add(footerPanel, BorderLayout.SOUTH);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, 480, 480, 480));
        layout.setVerticalGroup(layout.createParallelGroup()
            .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }

    /** Crea un botón de acción estilo "tarjeta" con icono y descripción. */
    private void styleActionCard(JButton btn, String title, String icon, String desc) {
        btn.setLayout(new BorderLayout(12, 0));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new login.RoundedBorder(10, C_BORDER),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        btn.setBackground(C_PANEL);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icono
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setOpaque(false);

        // Texto
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(C_TEXT);
        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(C_MUTED);
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        // Flecha
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        arrow.setForeground(C_MUTED);

        btn.add(iconLabel, BorderLayout.WEST);
        btn.add(textPanel, BorderLayout.CENTER);
        btn.add(arrow, BorderLayout.EAST);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0xF0F4FF));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(10, C_NAVY),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
                ));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(C_PANEL);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    new login.RoundedBorder(10, C_BORDER),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
                ));
            }
        });
    }

    // ── Acciones (sin modificar) ──────────────────────────────────────
    private void btnAñadirCuentaActionPerformed(java.awt.event.ActionEvent evt) {
        NuevoUsuario pantalla = new NuevoUsuario();
        pantalla.setLocationRelativeTo(this);
        pantalla.setVisible(true);
        pantalla.toFront();
        pantalla.requestFocus();
    }

    private void btnConsultarCuentaActionPerformed(java.awt.event.ActionEvent evt) {
        ConsultarUsuario pantalla = new ConsultarUsuario();
        pantalla.setLocationRelativeTo(this);
        pantalla.setVisible(true);
        pantalla.toFront();
        pantalla.requestFocus();
    }

    private void btnCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {
        cerrarSesionYVolverALogin(true);
    }

    // Variables declaration
    private JButton btnAñadirCuenta, btnConsultarCuenta, btnCerrarSesion;
    private JLabel jLabel1;
    private JPanel jPanel1, jPanel2, headerPanel;
}