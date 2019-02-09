package bank;
import messaging.MessageReceiver;
import messaging.MessageSender;
import messaging.requestreply.RequestReply;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.bank.BankInterestRequestReply;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JMSBankFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfReply;
	private DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>> listModel = new DefaultListModel<RequestReply<BankInterestRequest, BankInterestReply>>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JMSBankFrame frame = new JMSBankFrame();
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
	public JMSBankFrame() throws NamingException
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
		btnSendReply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RequestReply<BankInterestRequest, BankInterestReply> requestReply = list.getSelectedValue();
				double interest = Double.parseDouble((tfReply.getText()));
				BankInterestReply reply = new BankInterestReply(interest,"ABN AMRO");
				if (requestReply!= null && reply != null){
					requestReply.setReply(reply);
	                list.repaint();
	                sendMessage((BankInterestRequestReply) requestReply);
					// todo: sent JMS message with the reply to Loan Broker
				}
			}
		});
		GridBagConstraints gbc_btnSendReply = new GridBagConstraints();
		gbc_btnSendReply.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSendReply.gridx = 4;
		gbc_btnSendReply.gridy = 1;
		contentPane.add(btnSendReply, gbc_btnSendReply);

		prepareReceive();
	}

	private void sendMessage(BankInterestRequestReply requestReply)
	{
		try
		{
			MessageSender messageSender = new MessageSender(StaticNames.BROKER_FROM_BANK_DESTINATION);
			messageSender.SendMessage(requestReply);
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
	}

	private void prepareReceive() throws NamingException
	{
		MessageReceiver messageReceiver = new MessageReceiver(StaticNames.ABN_AMRO_BANK_DESTINATION);
		messageReceiver.PrepareReceiveMessage(this::messageReceived);
	}

	private void messageReceived(Message message)
	{
		ObjectMessage objectMessage = (ObjectMessage) message;
		try
		{
			Object receivedObject = objectMessage.getObject();
			if (receivedObject instanceof BankInterestRequestReply)
			{
				BankInterestRequestReply bankInterestRequestReply = (BankInterestRequestReply) receivedObject;
				listModel.add(listModel.getSize(), bankInterestRequestReply);
			}
			else
			{
				//todo throw new exception
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}
}
