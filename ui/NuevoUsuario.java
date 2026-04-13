package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.model.Categoria;
import com.mycompany.gestorcontrasenyas.service.CategoriaService;
import com.mycompany.gestorcontrasenyas.service.CuentaService;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class NuevoUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(NuevoUsuario.class.getName());

    // Categoría actualmente seleccionada, con su flag es_riot.
    private Categoria categoriaSeleccionada = null;

    public NuevoUsuario() {
        initComponents();
        setTitle("GestorContrasenyas - Añadir cuenta");
        cargarCategorias();
    }

    // ----------------------------------------------------------------
    // Carga las categorías desde Supabase y rellena el combo
    // ----------------------------------------------------------------
    private void cargarCategorias() {
        DefaultComboBoxModel<Object> model = new DefaultComboBoxModel<>();
        model.addElement("-");                                  // opción vacía
        List<Categoria> lista = CategoriaService.obtenerCategorias();
        for (Categoria c : lista) {
            model.addElement(c);                               // toString() devuelve nombre
        }
        cmbCategoria.setModel(model);
        categoriaSeleccionada = null;
        actualizarVisibilidadRiotId();
    }

    // ----------------------------------------------------------------
    // Muestra/oculta el campo Riot ID según la categoría elegida
    // ----------------------------------------------------------------
    private void actualizarVisibilidadRiotId() {
        boolean esRiot = categoriaSeleccionada != null && categoriaSeleccionada.isEsRiot();
        jLabel5.setVisible(esRiot);
        txtRiotId.setVisible(esRiot);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        UIManager.put("OptionPane.errorSound", null);
        UIManager.put("OptionPane.informationSound", null);
        UIManager.put("OptionPane.questionSound", null);
        UIManager.put("OptionPane.warningSound", null);

        jPanel1       = new javax.swing.JPanel();
        jLabel1       = new javax.swing.JLabel();
        jPanel2       = new javax.swing.JPanel();
        btnGuardar    = new javax.swing.JButton();
        btnAtras      = new javax.swing.JButton();
        btnNuevaCat   = new javax.swing.JButton();
        jLabel2       = new javax.swing.JLabel();
        jLabel3       = new javax.swing.JLabel();
        jLabel4       = new javax.swing.JLabel();
        jLabel5       = new javax.swing.JLabel();
        txtUsuario    = new javax.swing.JTextField();
        txtPassword   = new javax.swing.JPasswordField();
        txtRiotId     = new javax.swing.JTextField();
        cmbCategoria  = new javax.swing.JComboBox<>();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48));
        jLabel1.setText("       Añadir Usuario");
        jLabel1.setFocusable(false);

        btnGuardar.setFont(new java.awt.Font("Segoe UI", 0, 18));
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);

        btnAtras.setText("Atras");
        btnAtras.addActionListener(this::btnAtrasActionPerformed);

        // Botón para crear una nueva categoría sin salir de esta pantalla
        btnNuevaCat.setText("+ Nueva categoría");
        btnNuevaCat.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnNuevaCat.setForeground(new java.awt.Color(13, 110, 253));
        btnNuevaCat.setBorderPainted(false);
        btnNuevaCat.setContentAreaFilled(false);
        btnNuevaCat.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevaCat.addActionListener(this::btnNuevaCatActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel2.setText("Usuario:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel3.setText("Contraseña:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel4.setText("Categoria:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel5.setText("Riot ID:");
        jLabel5.setVisible(false);

        txtRiotId.setVisible(false);
        txtRiotId.setToolTipText("Ejemplo: NombreJugador#EUW");

        cmbCategoria.setModel(new DefaultComboBoxModel<>(new Object[]{"-"}));
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAtras)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsuario)
                            .addComponent(txtPassword)
                            .addComponent(txtRiotId)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cmbCategoria, 0, 129, Short.MAX_VALUE)
                                .addGap(6, 6, 6)
                                .addComponent(btnNuevaCat)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevaCat))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtRiotId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(btnAtras, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    // ----------------------------------------------------------------
    // Listener del combo: actualiza el campo es_riot
    // ----------------------------------------------------------------
    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        Object seleccion = cmbCategoria.getSelectedItem();
        categoriaSeleccionada = (seleccion instanceof Categoria) ? (Categoria) seleccion : null;
        actualizarVisibilidadRiotId();
    }

    // ----------------------------------------------------------------
    // Botón "+ Nueva categoría": diálogo inline, recarga el combo
    // ----------------------------------------------------------------
    private void btnNuevaCatActionPerformed(java.awt.event.ActionEvent evt) {
        JTextField txtNombre  = new JTextField(16);
        JCheckBox  chkRiot    = new JCheckBox("Es cuenta de Riot Games (activa campo Riot ID)");
        JLabel     lblAviso   = new JLabel("<html><small>Las categorías son personales y solo las ves tú.</small></html>");

        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Nombre de la nueva categoría:"));
        panel.add(txtNombre);
        panel.add(chkRiot);
        panel.add(lblAviso);

        int opcion = JOptionPane.showConfirmDialog(
                this, panel, "Nueva categoría",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) return;

        String nombre  = txtNombre.getText().trim();
        boolean esRiot = chkRiot.isSelected();

        String error = CategoriaService.crearCategoria(nombre, esRiot);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recargar el combo y seleccionar la nueva categoría automáticamente
        cargarCategorias();
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            Object item = cmbCategoria.getItemAt(i);
            if (item instanceof Categoria && ((Categoria) item).getNombre().equals(nombre)) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    // ----------------------------------------------------------------
    // Guardar cuenta
    // ----------------------------------------------------------------
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {
        String usuario  = txtUsuario.getText().trim();
        char[] pwdChars = txtPassword.getPassword();
        String riotId   = txtRiotId.getText().trim();

        try {
            if (categoriaSeleccionada == null || usuario.isEmpty() || pwdChars.length == 0) {
                JOptionPane.showMessageDialog(this, "Rellena todos los campos y elige una categoría.");
                return;
            }

            if (categoriaSeleccionada.isEsRiot() && riotId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Introduce el Riot ID.");
                return;
            }

            String nombreCategoria = categoriaSeleccionada.getNombre();
            String error = CuentaService.guardarCuenta(
                    usuario, pwdChars, nombreCategoria,
                    categoriaSeleccionada.isEsRiot() ? riotId : null);

            if (error != null) {
                JOptionPane.showMessageDialog(this, error);
            } else {
                JOptionPane.showMessageDialog(this, "Cuenta guardada correctamente.");
                txtUsuario.setText("");
                txtPassword.setText("");
                txtRiotId.setText("");
                cmbCategoria.setSelectedIndex(0);
                categoriaSeleccionada = null;
                actualizarVisibilidadRiotId();
            }

        } finally {
            Arrays.fill(pwdChars, '\0');
        }
    }

    private void btnAtrasActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    // Variables declaration
    private javax.swing.JButton   btnAtras;
    private javax.swing.JButton   btnGuardar;
    private javax.swing.JButton   btnNuevaCat;
    private javax.swing.JComboBox<Object> cmbCategoria;
    private javax.swing.JLabel    jLabel1;
    private javax.swing.JLabel    jLabel2;
    private javax.swing.JLabel    jLabel3;
    private javax.swing.JLabel    jLabel4;
    private javax.swing.JLabel    jLabel5;
    private javax.swing.JPanel    jPanel1;
    private javax.swing.JPanel    jPanel2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField     txtRiotId;
    private javax.swing.JTextField     txtUsuario;
    // End of variables declaration
}