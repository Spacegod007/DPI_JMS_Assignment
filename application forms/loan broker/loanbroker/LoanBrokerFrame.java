package loanbroker;

import messaging.MessageReceiver;
import messaging.MessageSender;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanReply;
import model.loan.LoanRequest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanBrokerFrame extends JFrame {

	private static final Logger LOGGER = Logger.getLogger(LoanBrokerFrame.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;

	private Map<String, LoanRequest> loanRequestById = new HashMap<>();

	/**
	 * Loaunches the application
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LoanBrokerFrame frame = new LoanBrokerFrame();
				frame.setVisible(true);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_APPLICATION_EXECUTION, e);
			}
		});
	}

	/**
	 * Constructs the class
	 */
	private LoanBrokerFrame() throws NamingException
	{
		LoadFrame();
		prepareMessageListener();
	}

	/**
	 * Loads the GUI frame
	 */
	private void LoadFrame()
	{
		setTitle("Loan Broker");
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
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);

		list = new JList<>(listModel);
		scrollPane.setViewportView(list);
	}

	/**
	 * Prepares the broker to listen for messages
	 * @throws NamingException
	 */
	private void prepareMessageListener() throws NamingException
	{
		MessageReceiver clientMessageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_CLIENT_DESTINATION);
		clientMessageReceiver.PrepareReceiveMessage(this::clientMessageReceived);
		MessageReceiver bankMessageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_BANK_DESTINATION);
		bankMessageReceiver.PrepareReceiveMessage(this::bankMessageReceived);
	}

	/**
	 * Gets fired when a message is received from the client-channel
	 * @param message the received message
	 */
	private void clientMessageReceived(Message message)
	{
		ObjectMessage objectMessage = (ObjectMessage) message;
		try {
			Object receivedObject = objectMessage.getObject();
			String correlationID = message.getJMSCorrelationID();

			if (receivedObject instanceof LoanRequest)
			{
				LoanRequest loanRequest = (LoanRequest) receivedObject;
				loanRequestById.put(correlationID, loanRequest);

				add(loanRequest);

				SendBankInterestRequest(loanRequest, correlationID);
			}
			else
			{
				LOGGER.log(Level.WARNING, StaticNames.LOGGER_WARNING_INVALID_OBJECT_RECEIVED);
			}
		}
		catch (JMSException e)
		{
			LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE, e);
		}
	}

	/**
	 * Sends a request to the bank from the loan-request send by a client
	 * @param loanRequest The request from the client
	 * @param correlationID An id which refers to the request over multiple systems
	 */
	private void SendBankInterestRequest(LoanRequest loanRequest, String correlationID)
	{
		BankInterestRequest bankInterestRequest = bankInterestRequestFromLoanRequest(loanRequest);
		try
		{
			MessageSender messageSender = new MessageSender(StaticNames.ABN_AMRO_BANK_DESTINATION);
			add(loanRequest, bankInterestRequest);
			messageSender.SendMessage(bankInterestRequest, correlationID);
		}
		catch (NamingException e)
		{
			LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_SENDING_MESSAGE);
		}
	}

	/**
	 * Gets fired on a message received on the bank-channel
	 * @param message The received message
	 */
	private void bankMessageReceived(Message message)
	{
		ObjectMessage objectMessage = (ObjectMessage) message;
		try
		{
			Object receivedObject = objectMessage.getObject();
			if (receivedObject instanceof BankInterestReply)
			{
				BankInterestReply bankInterestReply = (BankInterestReply) receivedObject;
				String correlationID = message.getJMSCorrelationID();
				LoanRequest loanRequest = loanRequestById.get(correlationID);
				add(loanRequest, bankInterestReply);
				sendClientLoanReply(bankInterestReply, correlationID);
			}
			else
			{
				LOGGER.log(Level.WARNING, StaticNames.LOGGER_WARNING_INVALID_OBJECT_RECEIVED);
			}
		}
		catch (JMSException e)
		{
			LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_RECEIVING_MESSAGE, e);
		}
	}

	/**
	 * Sends a LoanReply to the client from the BankInterestReply
	 * @param bankInterestReply The reply from the bank which answers the request
	 * @param correlationID An id which refers to the request over multiple systems
	 */
	private void sendClientLoanReply(BankInterestReply bankInterestReply, String correlationID)
	{

		LoanReply loanReply = loanReplyFromBankInterestReply(bankInterestReply);
		try
		{
			MessageSender messageSender = new MessageSender(StaticNames.CLIENT_DESTINATION);
			messageSender.SendMessage(loanReply, correlationID);
			loanRequestById.remove(correlationID);
		}
		catch (NamingException e)
		{
			LOGGER.log(Level.SEVERE, StaticNames.LOGGER_ERROR_SENDING_MESSAGE);
		}

	}

	private JListLine getRequestReply(LoanRequest request){
	     
	     for (int i = 0; i < listModel.getSize(); i++){
	    	 JListLine rr =listModel.get(i);
	    	 if (rr.getLoanRequest() == request){
	    		 return rr;
	    	 }
	     }
	     
	     return null;
	}

	private void add(LoanRequest loanRequest){
		listModel.addElement(new JListLine(loanRequest));		
	}

	private void add(LoanRequest loanRequest, BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	private void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}

	/**
	 * Converts a LoanRequest to a BankInterestRequest which is fit to send to banks
	 * @param loanRequest The LoanRequest to convert
	 * @return A new BankInterestRequest
	 */
	private BankInterestRequest bankInterestRequestFromLoanRequest(LoanRequest loanRequest) {
		return new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());
	}

	/**
	 * Converts a BankInterestReply to a LoanReply which is fit to send to clients
	 * @param bankInterestReply The BankInterestReply to convert
	 * @return A new LoanReply
	 */
	private LoanReply loanReplyFromBankInterestReply(BankInterestReply bankInterestReply) {
		return new LoanReply(bankInterestReply.getInterest(), bankInterestReply.getQuoteId());
	}
}
