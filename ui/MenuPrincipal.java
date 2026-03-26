package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MenuPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MenuPrincipal.class.getName());
    private static final long RENOVACION_CADA_MINUTOS = 45L;

    private final AtomicBoolean cerrandoSesion = new AtomicBoolean(false);
    private ScheduledExecutorService tokenScheduler;

    public MenuPrincipal() {
        initComponents();
        setTitle("GestorContrasenyas");
        iniciarRenovacionToken();

        // Limpiar datos sensibles de memoria al cerrar la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cerrarSesionYVolverALogin(false);
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
                detenerRenovacionToken();
            }
        });
    }

    private void iniciarRenovacionToken() {
        if (tokenScheduler != null && !tokenScheduler.isShutdown()) {
            return;
        }

        ThreadFactory factory = r -> {
            Thread hilo = new Thread(r, "token-refresh-worker");
            hilo.setDaemon(true);
            return hilo;
        };

        tokenScheduler = Executors.newSingleThreadScheduledExecutor(factory);
        tokenScheduler.scheduleAtFixedRate(() -> {
            // Si ya estamos cerrando sesión, no lanzar más renovaciones.
            if (cerrandoSesion.get()) return;

            String error = SupabaseAuth.renovarToken();
            if (error != null) {
                SwingUtilities.invokeLater(() -> {
                    if (isDisplayable() && !cerrandoSesion.get()) {
                        javax.swing.JOptionPane.showMessageDialog(this,
                                "La sesión expiró y no pudo renovarse. Debes iniciar sesión de nuevo.");
                    }
                    cerrarSesionYVolverALogin(true);
                });
            }
        }, RENOVACION_CADA_MINUTOS, RENOVACION_CADA_MINUTOS, TimeUnit.MINUTES);
    }

    private void detenerRenovacionToken() {
        if (tokenScheduler != null) {
            tokenScheduler.shutdownNow();
            tokenScheduler = null;
        }
    }

    private void cerrarSesionYVolverALogin(boolean redirigirALogin) {
        if (!cerrandoSesion.compareAndSet(false, true)) {
            return;
        }

        detenerRenovacionToken();
        SupabaseAuth.cerrarSesion();

        if (redirigirALogin && isDisplayable()) {
            login loginPantalla = new login();
            loginPantalla.setVisible(true);
            loginPantalla.setLocationRelativeTo(null);
        }

        if (isDisplayable()) {
            dispose();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        UIManager.put("OptionPane.errorSound", null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound", null);
        UIManager.put("OptionPane.warningSound", null);
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnConsultarCuenta = new javax.swing.JButton();
        btnAñadirCuenta = new javax.swing.JButton();
        btnCerrarSesion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48));
        jLabel1.setText("Gestor de contraseñas");
        jLabel1.setFocusable(false);

        btnConsultarCuenta.setFont(new java.awt.Font("Segoe UI", 0, 18));
        btnConsultarCuenta.setText("Consultar cuenta");
        btnConsultarCuenta.addActionListener(this::btnConsultarCuentaActionPerformed);

        btnAñadirCuenta.setFont(new java.awt.Font("Segoe UI", 0, 18));
        btnAñadirCuenta.setText("Añadir cuenta");
        btnAñadirCuenta.addActionListener(this::btnAñadirCuentaActionPerformed);

        btnCerrarSesion.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnCerrarSesion.setText("Cerrar sesión");
        btnCerrarSesion.addActionListener(this::btnCerrarSesionActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnAñadirCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnConsultarCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(109, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(btnAñadirCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(btnConsultarCuenta, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                                .addComponent(btnCerrarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap(142, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(128, 128, 128))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(53, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

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
    private javax.swing.JButton btnAñadirCuenta;
    private javax.swing.JButton btnConsultarCuenta;
    private javax.swing.JButton btnCerrarSesion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration
}