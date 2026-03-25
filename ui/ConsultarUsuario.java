package com.mycompany.gestorcontrasenyas.ui;

import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.service.CuentaService;
import com.mycompany.gestorcontrasenyas.service.RiotService;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;

public class ConsultarUsuario extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ConsultarUsuario.class.getName());

    public ConsultarUsuario() {
        initComponents();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
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

        cmbCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "Gmail", "Riot account", "Albion", "Otros" }));
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

        DefaultTableModel tabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (categoriaSeleccionada.equals("Riot account")) {
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

                    if (categoriaSeleccionada.equals("Riot account")) {
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

        if (filaSeleccionada != -1) {
            String nombreActual = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));

            JTextField txtNuevoUsuario = new JTextField(nombreActual);
            JPasswordField txtNuevoPassword = new JPasswordField();

            Object[] message = {
                "Nuevo Nombre de Usuario:", txtNuevoUsuario,
                "Nueva Contraseña:", txtNuevoPassword
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Editar Cuenta", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String nuevoUser = txtNuevoUsuario.getText().trim();
                char[] nuevoPass = txtNuevoPassword.getPassword();

                if (nuevoUser.isEmpty() || nuevoPass.length == 0) {
                    JOptionPane.showMessageDialog(this, "Error: Los campos no pueden estar vacíos.", "Validación", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String id = CuentaService.getCuentaid(nombreActual);
                CuentaService.actualizarCuenta(id, nuevoUser, nuevoPass);
                java.util.Arrays.fill(nuevoPass, '\0');
                JOptionPane.showMessageDialog(this, "Cuenta actualizada correctamente.");
                cargarTabla(); 
                
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila de la tabla.");
        }
    }
    
    private void tblUsuariosGuardadosMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() != 2) return;

        int filaSeleccionada = tblUsuariosGuardados.getSelectedRow();
        if (filaSeleccionada == -1) return;

        String usuario = String.valueOf(tblUsuariosGuardados.getValueAt(filaSeleccionada, 1));
        String cuentaId = CuentaService.getCuentaid(usuario);
        if (cuentaId == null || cuentaId.isBlank()) {
            JOptionPane.showMessageDialog(this, "No se pudo localizar la cuenta seleccionada.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String password = CuentaService.obtenerPasswordDescifradaPorId(cuentaId);
        if (password == null) {
            JOptionPane.showMessageDialog(this, "No se pudo recuperar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Contraseña de \"" + usuario + "\":\n" + password,
                "Ver contraseña",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Variables declaration - do not modify
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEditar;
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