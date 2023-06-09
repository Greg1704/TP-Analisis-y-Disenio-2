package ventana;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import controlador.ControladorCliente;
import modelo.Server.ManejaConexiones;
import modelo.chat.Mensaje;
import ventana.interfaces.IVistaCliente;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.border.TitledBorder;

public class VentanaCliente extends JFrame implements MouseListener, IVistaCliente, KeyListener  {

	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private JPanel panelBusqueda;
	private JLabel lblNombreApp;
	private JLabel lblIp;
	private JLabel lblPuerto;
	private JTextField textFieldIp;
	private JTextField textFieldPuerto;
	private JButton btnConectar;
	private JPanel panelChat;
	private JTextArea textAreaChatText;
	private JScrollPane scrollPaneChatText;
	private JLabel lblMensajeUsuario;
	private JPanel panelChatBotones;
	private JButton btnEnviarMensaje;
	private JPanel panelChatAuxiliar;
	private JButton btnCerrarSesion;
	private JPanel panelBusquedaComponentes;
	private JTextField textFieldChatMensajeUsuario;
	private ControladorCliente c;
	private JLabel lblPuertoReferencia;
	private JPanel panelListaConectados;
	private JScrollPane scrollPaneConectados;
	private JList listConectados;
	private DefaultListModel<ManejaConexiones> modelListClientesConectados;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaCliente frame = new VentanaCliente();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VentanaCliente() {
		
		this.setVisible(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 878, 546);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(this.contentPane);
		
		this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		this.contentPane.add(this.tabbedPane, BorderLayout.CENTER);
		
		this.panelBusqueda = new JPanel();
		this.tabbedPane.addTab("Busqueda", null, this.panelBusqueda, null);
		this.panelBusqueda.setLayout(null);
		
		this.panelBusquedaComponentes = new JPanel();
		this.panelBusquedaComponentes.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		this.panelBusquedaComponentes.setBounds(143, 91, 290, 213);
		this.panelBusqueda.add(this.panelBusquedaComponentes);
		this.panelBusquedaComponentes.setLayout(null);
		
		this.lblNombreApp = new JLabel("ChitChat");
		this.lblNombreApp.setBounds(56, 11, 198, 45);
		this.panelBusquedaComponentes.add(this.lblNombreApp);
		this.lblNombreApp.setFont(new Font("Arial Black", Font.PLAIN, 28));
		
		this.lblIp = new JLabel("Ip:");
		this.lblIp.setBounds(22, 74, 20, 22);
		this.panelBusquedaComponentes.add(this.lblIp);
		
		this.textFieldIp = new JTextField();
		this.textFieldIp.setBounds(89, 75, 165, 20);
		this.panelBusquedaComponentes.add(this.textFieldIp);
		this.textFieldIp.setColumns(10);
		
		this.lblPuerto = new JLabel("Puerto:");
		this.lblPuerto.setBounds(22, 127, 46, 14);
		this.panelBusquedaComponentes.add(this.lblPuerto);
		
		this.textFieldPuerto = new JTextField();
		this.textFieldPuerto.setBounds(89, 121, 165, 20);
		this.panelBusquedaComponentes.add(this.textFieldPuerto);
		this.textFieldPuerto.setColumns(10);
		
		this.btnConectar = new JButton("Conectar");
		this.btnConectar.setBounds(103, 164, 89, 23);
		this.panelBusquedaComponentes.add(this.btnConectar);
		
		this.lblPuertoReferencia = new JLabel("Puerto: 0000");
		this.lblPuertoReferencia.setBounds(0, 446, 134, 23);
		this.panelBusqueda.add(this.lblPuertoReferencia);
		
		this.panelListaConectados = new JPanel();
		this.panelListaConectados.setBounds(487, 0, 360, 469);
		this.panelBusqueda.add(this.panelListaConectados);
		this.panelListaConectados.setLayout(new BorderLayout(0, 0));
		
		this.scrollPaneConectados = new JScrollPane();
		this.scrollPaneConectados.setViewportBorder(new TitledBorder(null, "Usuarios conectados al servidor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.panelListaConectados.add(this.scrollPaneConectados, BorderLayout.CENTER);
		
		this.listConectados = new JList();
		this.scrollPaneConectados.setViewportView(this.listConectados);
		this.modelListClientesConectados = new DefaultListModel<ManejaConexiones>();
		this.listConectados.setModel(modelListClientesConectados);
		this.listConectados.setName("listConectados");
		this.listConectados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.panelChat = new JPanel();
		this.tabbedPane.addTab("Chat", null, this.panelChat, null);
		this.panelChat.setLayout(null);
		
		this.scrollPaneChatText = new JScrollPane();
		this.scrollPaneChatText.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		this.scrollPaneChatText.setBounds(10, 11, 827, 289);
		this.panelChat.add(this.scrollPaneChatText);
		
		this.textAreaChatText = new JTextArea();
		this.scrollPaneChatText.setViewportView(this.textAreaChatText);
		this.textAreaChatText.setFont(new Font("Arial", Font.PLAIN, 12));
		
		this.lblMensajeUsuario = new JLabel("Mensaje:");
		this.lblMensajeUsuario.setFont(new Font("Tahoma", Font.PLAIN, 18));
		this.lblMensajeUsuario.setBounds(10, 311, 83, 36);
		this.panelChat.add(this.lblMensajeUsuario);
		
		this.panelChatBotones = new JPanel();
		this.panelChatBotones.setBounds(95, 373, 632, 62);
		this.panelChat.add(this.panelChatBotones);
		this.panelChatBotones.setLayout(new GridLayout(0, 3, 0, 0));
		
		this.btnEnviarMensaje = new JButton("Enviar");
		this.panelChatBotones.add(this.btnEnviarMensaje);
		
		this.panelChatAuxiliar = new JPanel();
		this.panelChatBotones.add(this.panelChatAuxiliar);
		
		this.btnCerrarSesion = new JButton("Cerrar Sesion");
		this.panelChatBotones.add(this.btnCerrarSesion);
		
		this.textFieldChatMensajeUsuario = new JTextField();
		this.textFieldChatMensajeUsuario.setBounds(95, 317, 723, 31);
		this.panelChat.add(this.textFieldChatMensajeUsuario);
		this.textFieldChatMensajeUsuario.setColumns(10);
		
	}
	
	public void setControlador(ControladorCliente c) {
		this.c = c;
		this.addWindowListener(c);
		setDefault();
	}
	
	public void setDefault() {
		
		this.tabbedPane.setEnabledAt(1, false);
		
		this.btnEnviarMensaje.addMouseListener(this);
		this.btnEnviarMensaje.setEnabled(false);
		this.btnEnviarMensaje.addActionListener(c);
		this.btnEnviarMensaje.setActionCommand(enviarMensaje);
		
		this.btnConectar.setEnabled(false);
		this.btnConectar.addMouseListener(this);
		this.btnConectar.addActionListener(c);
		this.btnConectar.setActionCommand(intentoDeConexion);
		
		this.btnCerrarSesion.addMouseListener(this);
		this.btnCerrarSesion.addActionListener(c);
		this.btnCerrarSesion.setActionCommand(cerrarSesion);
		
		this.textFieldChatMensajeUsuario.addKeyListener(this);
		
		this.textFieldIp.addKeyListener(this);
		this.textFieldIp.setText("localhost");
		
		this.textFieldPuerto.addKeyListener(this);
		
		this.listConectados.setEnabled(true);
		this.listConectados.addMouseListener(this);
	}
	
	
	public void actualizarListaConectados(ArrayList<ManejaConexiones> conexiones) {
		this.modelListClientesConectados.clear();
		ArrayList<ManejaConexiones> mc = conexiones;
		for(int i=0;i<mc.size();i++) {
			this.modelListClientesConectados.addElement(mc.get(i));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == this.btnConectar) {
			this.textFieldIp.setText("");
			this.textFieldPuerto.setText("");
			this.btnConectar.setEnabled(false);
		} else if(e.getSource() == this.btnEnviarMensaje) {
			this.textFieldChatMensajeUsuario.setText("");
			this.btnEnviarMensaje.setEnabled(false);
		}else if(e.getSource() == this.btnCerrarSesion) {
			
		}else if(e.getSource() == this.listConectados) {
			ManejaConexiones cliente = (ManejaConexiones) this.listConectados.getSelectedValue();
			this.textFieldIp.setText(c.getIpLocal());
			this.textFieldPuerto.setText(Integer.toString(cliente.getPuerto()));
			this.btnConectar.setEnabled(true);
		}
	}

	public String getTextFieldChatMensajeUsuario() {
		return textFieldChatMensajeUsuario.getText();
	}
	
	public void agregarMensajeAlChat(Mensaje mensaje) {
		this.textAreaChatText.append(mensaje.getIpEmisor() + ":" + mensaje.getPuertoEmisor() + ": " + mensaje.getMensaje() + "\n");
	}

	public String getTextFieldIp() {
		return textFieldIp.getText();
	}

	public String getTextFieldPuerto() {
		return textFieldPuerto.getText();
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == this.textFieldChatMensajeUsuario) {
			if (this.textFieldChatMensajeUsuario.getText().isEmpty()) {
				this.btnEnviarMensaje.setEnabled(false);
			} else {
				this.btnEnviarMensaje.setEnabled(true);
			}
		}else if(e.getSource() == this.textFieldIp || e.getSource() == this.textFieldPuerto) {
			if(this.textFieldIp.getText().isEmpty() || this.textFieldPuerto.getText().isEmpty()) {
				this.btnConectar.setEnabled(false);
			}else
				this.btnConectar.setEnabled(true);
		}

	}

	public String getLblPuertoReferencia() {
		return lblPuertoReferencia.getText();
	}

	public void setLblPuertoReferencia(String lblPuertoReferencia) {
		this.lblPuertoReferencia.setText(lblPuertoReferencia);
	}

	public JList getListConectados() {
		return listConectados;
	}

	public void setListConectados(JList listConectados) {
		this.listConectados = listConectados;
	}
	
	
}