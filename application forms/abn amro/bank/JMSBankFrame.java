package bank;
import bank.gateway.LoanBrokerGateway;
import messaging.requestreply.RequestReply;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;

import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JMSBankFrame extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(JMSBankFrame.class.getName());

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<>();

	private LoanBrokerGateway brokerGateway;
	
	/**
	 * Launches the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				JMSBankFrame frame = new JMSBankFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_APPLICATION_EXECUTION, e);
			}
		});
	}

	/**
	 * Constructs the class
	 */
	private JMSBankFrame() throws NamingException
	{
		brokerGateway = new LoanBrokerGateway();
		LoadFrame();

		brokerGateway.AddListener(this::addRequestToList);
	}

	/**
	 * Loads the GUI frame
	 */
	private void LoadFrame()
	{
		setTitle("JMS Bank - ABN AMRO");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 5;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		JList<RequestReply<BankInterestRequest, BankInterestReply>> list = new JList<RequestReply<BankInterestRequest, BankInterestReply>>(listModel);
		scrollPane.setViewportView(list);

		JLabel lblNewLabel = new JLabel("type reply");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		tfReply = new JTextField();
		GridBagConstraints gbc_tfReply = new GridBagConstraints();
		gbc_tfReply.gridwidth = 2;
		gbc_tfReply.insets = new Insets(0, 0, 0, 5);
		gbc_tfReply.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfReply.gridx = 1;
		gbc_tfReply.gridy = 1;
		contentPane.add(tfReply, gbc_tfReply);
		tfReply.setColumns(10);

		JButton btnSendReply = new JButton("send reply");
		btnSendReply.addActionListener(e -> {
			RequestReply<BankInterestRequest, BankInterestReply> requestReply = list.getSelectedValue();
			double interest = Double.parseDouble((tfReply.getText()));
			BankInterestReply reply = new BankInterestReply(interest,"ABN AMRO");
			if (requestReply != null){
				requestReply.setReply(reply);
				list.repaint();
				brokerGateway.SendBankInterestReply(requestReply.getRequest(), reply);
			}
		});
		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);
	}

	/**
	 * Adds a request to the list of requests
	 * @param bankInterestRequest The request to get added
	 * @param correlationID The Id which refers to this request over multiple systems
	 */
	private void addRequestToList(BankInterestRequest bankInterestRequest, String correlationID)
	{
		RequestReply<BankInterestRequest, BankInterestReply> bankInterestRequestReply = new RequestReply<>(bankInterestRequest, null);
		listModel.add(listModel.getSize(), bankInterestRequestReply);
	}
}
