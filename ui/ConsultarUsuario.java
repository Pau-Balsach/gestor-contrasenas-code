package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.service.CategoriaService;
import com.mycompany.gestorcontrasenyas.service.CuentaService;
import com.mycompany.gestorcontrasenyas.service.RiotService;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;

public class ConsultarUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ConsultarUsuario.class.getName());
    private static final int CLIPBOARD_CLEAR_SECONDS = 30;
    private ScheduledExecutorService clipboardCleaner;

    public ConsultarUsuario() {
        initComponents();
        setTitle("GestorContrasenyas - Consultar Usuario");
    }

    // ----------------------------------------------------------------
    // Carga las categorías desde Supabase y rellena el combo de filtro
    // ----------------------------------------------------------------
    private void cargarCategoriasFiltro() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-");
        List<String> nombres = CategoriaService.obtenerNombres();
        for (String nombre : nombres) {
            model.addElement(nombre);
        }
        cmbCategoria.setModel(model);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUsuariosGuardados = new javax.swing.JTable();
        cmbCategoria = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        lblCargando = new javax.swing.JLabel();
        btnEliminar = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnCopiar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                cargarCategoriasFiltro();
                cargarTabla();
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48));
        jLabel1.setText("Usuarios guardados");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel2.setText("Categoria:");

        lblCargando.setFont(new java.awt.Font("Segoe UI", 2, 13));
        lblCargando.setForeground(new java.awt.Color(150, 150, 150));
        lblCargando.setText("");

        cmbCategoria.setModel(new DefaultComboBoxModel<>(new String[]{"-"}));
        cmbCategoria.addActionListener(this::cmbCategoriaActionPerformed);

        tblUsuariosGuardados.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][] {},
            new String[] {}
        ));
        tblUsuariosGuardados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblUsuariosGuardados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblUsuariosGuardadosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblUsuariosGuardados);

        btnEliminar.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnEliminar.setText("Eliminar");
        btnEliminar.setBackground(new java.awt.Color(220, 53, 69));
        btnEliminar.setForeground(java.awt.Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);

        btnEditar.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnEditar.setText("Editar");
        btnEditar.setBackground(new java.awt.Color(13, 110, 253));
        btnEditar.setForeground(java.awt.Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.addActionListener(this::btnEditarActionPerformed);

        btnCopiar.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnCopiar.setText("Copiar...");
        btnCopiar.setBackground(new java.awt.Color(32, 201, 151));
        btnCopiar.setForeground(java.awt.Color.WHITE);
        btnCopiar.setFocusPainted(false);
        btnCopiar.addActionListener(this::btnCopiarActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblCargando))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnCopiar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cmbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCargando))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCopiar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(jLabel1)))
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
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

    private void cmbCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        cargarTabla();
    }

    private void cargarTabla() {
        String categoriaSeleccionada = (String) cmbCategoria.getSelectedItem();

        // Determinar si la categoría filtrada es de tipo Riot para mostrar columnas extra.
        // Se consulta el flag es_riot de la BD en lugar de comparar el nombre literal.
        boolean filtroEsRiot = CategoriaService.obtenerCategorias().stream()
                .anyMatch(c -> c.getNombre().equals(categoriaSeleccionada) && c.isEsRiot());

        DefaultTableModel tabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (filtroEsRiot) {
            tabla.setColumnIdentifiers(new String[]{"Categoria", "Usuario", "Contraseña", "Riot ID", "Rango Valorant", "Rango LoL"});
        } else {
            tabla.setColumnIdentifiers(new String[]{"Categoria", "Usuario", "Contraseña"});
        }
        tblUsuariosGuardados.setModel(tabla);

        cmbCategoria.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnEditar.setEnabled(false);
        lblCargando.setText("Cargando...");

        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                List<Cuenta> listaCuentas = CuentaService.obtenerCuentas();

                for (Cuenta cuenta : listaCuentas) {
                    if (!categoriaSeleccionada.equals("-") && !cuenta.getCategoria().equals(categoriaSeleccionada)) {
                        continue;
                    }

                    if (filtroEsRiot) {
                        // Filtro explícito de categoría Riot: mostrar rangos
                        String riotId        = cuenta.getRiotId();
                        String rangoValorant = "Sin rango";
                        String rangoLol      = "Sin rango";

                        if (riotId != null && !riotId.isEmpty()) {
                            String puuid = RiotService.obtenerPuuid(riotId);
                            if (puuid != null) {
                                rangoValorant = RiotService.obtenerRangoValorant(riotId);
                                rangoLol      = RiotService.obtenerRangoLol(puuid);
                            }
                        }
                        publish(new Object[]{cuenta.getCategoria(), cuenta.getUsuario(), cuenta.getPassword(), riotId, rangoValorant, rangoLol});
                    } else {
                        // Filtro "-" o categoría no-Riot: solo 3 columnas, sin llamadas a la API
                        publish(new Object[]{cuenta.getCategoria(), cuenta.getUsuario(), cuenta.getPassword()});
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> filas) {
                DefaultTableModel modelo = (DefaultTableModel) tblUsuariosGuardados.getModel();
                for (Object[] fila : filas) {
                    modelo.addRow(fila);
                }
            }

            @Override
            protected void done() {
                cmbCategoria.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnEditar.setEnabled(true);
                lblCargando.setText("");
            }
        };

        worker.execute();
    }
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de que deseas eliminar esta cuenta? Esta acción no se puede deshacer.", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) {
            return; 
        }

        try {
            CuentaService.eliminarCuenta(CuentaService.getCuentaid(String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1))));
            cargarTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error de Servidor", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila de la tabla.");
            return;
        }

        String nombreActual  = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));
        String categoriaFila = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 0));
        boolean esRiot       = CategoriaService.obtenerCategorias().stream()
                .anyMatch(c -> c.getNombre().equals(categoriaFila) && c.isEsRiot());

        // Campos comunes
        JTextField     txtNuevoUsuario  = new JTextField(nombreActual);
        JPasswordField txtNuevoPassword = new JPasswordField();

        // Campo extra solo para Riot account, pre-rellenado si la columna existe en la tabla
        JTextField txtNuevoRiotId = new JTextField();
        if (esRiot && tblUsuariosGuardados.getColumnCount() > 3) {
            Object riotIdActual = tblUsuariosGuardados.getValueAt(filaSeleccionada, 3);
            txtNuevoRiotId.setText(riotIdActual != null ? riotIdActual.toString() : "");
        }

        // Construir el panel del dialogo segun la categoria
        Object[] message = esRiot
                ? new Object[]{"Nuevo Nombre de Usuario:", txtNuevoUsuario, "Nueva Contraseña:", txtNuevoPassword, "Nuevo Riot ID:", txtNuevoRiotId}
                : new Object[]{"Nuevo Nombre de Usuario:", txtNuevoUsuario, "Nueva Contraseña:", txtNuevoPassword};

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Cuenta", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String nuevoUser   = txtNuevoUsuario.getText().trim();
        char[] nuevoPass   = txtNuevoPassword.getPassword();
        String nuevoRiotId = esRiot ? txtNuevoRiotId.getText().trim() : null;

        try {
            if (nuevoUser.isEmpty() || nuevoPass.length == 0) {
                JOptionPane.showMessageDialog(this, "Error: Los campos no pueden estar vacíos.", "Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (esRiot && (nuevoRiotId == null || nuevoRiotId.isEmpty())) {
                JOptionPane.showMessageDialog(this, "Error: El Riot ID no puede estar vacío.", "Validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String id = CuentaService.getCuentaid(nombreActual);
            if (id == null || id.isBlank()) {
                JOptionPane.showMessageDialog(this, "No se pudo localizar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CuentaService.actualizarCuenta(id, nuevoUser, nuevoPass, nuevoRiotId);
            JOptionPane.showMessageDialog(this, "Cuenta actualizada correctamente.");
            cargarTabla();

        } finally {
            java.util.Arrays.fill(nuevoPass, '\0');
        }
    }
    
    private void tblUsuariosGuardadosMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() != 2) return;
        mostrarDialogoCopiar();
    }

    private void btnCopiarActionPerformed(java.awt.event.ActionEvent evt) {
        mostrarDialogoCopiar();
    }

    /**
     * Abre un diálogo con botones para copiar el usuario o la contraseña
     * de la fila seleccionada. El portapapeles se limpia automáticamente
     * pasados CLIPBOARD_CLEAR_SECONDS segundos por seguridad.
     */
    private void mostrarDialogoCopiar() {
        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String usuario  = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));
        String cuentaId = CuentaService.getCuentaid(usuario);

        if (cuentaId == null || cuentaId.isBlank()) {
            JOptionPane.showMessageDialog(this, "No se pudo localizar la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog(this, "Copiar credenciales — " + usuario, true);
        dialogo.setLayout(new java.awt.BorderLayout(12, 12));
        dialogo.setResizable(false);

        JLabel lblInfo = new JLabel("<html><b>" + usuario + "</b><br><small>Elige qué quieres copiar al portapapeles.<br>"
                + "Se borrará automáticamente en " + CLIPBOARD_CLEAR_SECONDS + " segundos.</small></html>");
        lblInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 16, 4, 16));
        dialogo.add(lblInfo, java.awt.BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 14, 10));

        JButton btnCopiarUsuario = new JButton("📋  Copiar usuario");
        btnCopiarUsuario.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnCopiarUsuario.setBackground(new java.awt.Color(13, 110, 253));
        btnCopiarUsuario.setForeground(java.awt.Color.WHITE);
        btnCopiarUsuario.setFocusPainted(false);
        btnCopiarUsuario.addActionListener(e -> {
            copiarAlPortapapeles(usuario);
            dialogo.dispose();
            mostrarFeedbackCopia("Usuario copiado. Se borrará en " + CLIPBOARD_CLEAR_SECONDS + "s.");
        });

        JButton btnCopiarPassword = new JButton("🔑  Copiar contraseña");
        btnCopiarPassword.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btnCopiarPassword.setBackground(new java.awt.Color(32, 201, 151));
        btnCopiarPassword.setForeground(java.awt.Color.WHITE);
        btnCopiarPassword.setFocusPainted(false);
        btnCopiarPassword.addActionListener(e -> {
            String password = CuentaService.obtenerPasswordDescifradaPorId(cuentaId);
            if (password == null) {
                JOptionPane.showMessageDialog(dialogo, "No se pudo recuperar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            copiarAlPortapapeles(password);
            dialogo.dispose();
            mostrarFeedbackCopia("Contraseña copiada. Se borrará en " + CLIPBOARD_CLEAR_SECONDS + "s.");
        });

        JButton btnCerrar = new JButton("Cancelar");
        btnCerrar.setFont(new java.awt.Font("Segoe UI", 0, 13));
        btnCerrar.addActionListener(e -> dialogo.dispose());

        panelBotones.add(btnCopiarUsuario);
        panelBotones.add(btnCopiarPassword);
        panelBotones.add(btnCerrar);
        dialogo.add(panelBotones, java.awt.BorderLayout.CENTER);

        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Copia el texto al portapapeles y programa su borrado automático.
     */
    private void copiarAlPortapapeles(String texto) {
        if (clipboardCleaner != null && !clipboardCleaner.isShutdown()) {
            clipboardCleaner.shutdownNow();
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(texto), null);

        clipboardCleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "clipboard-cleaner");
            t.setDaemon(true);
            return t;
        });
        clipboardCleaner.schedule(() ->
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(""), null),
                CLIPBOARD_CLEAR_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Muestra un mensaje no bloqueante en el label de estado durante 3 segundos.
     */
    private void mostrarFeedbackCopia(String mensaje) {
        lblCargando.setForeground(new java.awt.Color(32, 160, 110));
        lblCargando.setText(mensaje);
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            lblCargando.setText("");
            lblCargando.setForeground(new java.awt.Color(150, 150, 150));
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnCopiar;
    private javax.swing.JComboBox<String> cmbCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblCargando;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUsuariosGuardados;
    // End of variables declaration
}