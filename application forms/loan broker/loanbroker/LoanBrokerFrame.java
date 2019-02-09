package loanbroker;

import messaging.MessageReceiver;
import messaging.MessageSender;
import messaging.requestreply.RequestReply;
import model.StaticNames;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.bank.BankInterestRequestReply;
import model.loan.LoanReply;
import model.loan.LoanRequest;
import model.loan.LoanRequestReply;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.naming.NamingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LoanBrokerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<JListLine>();
	private JList<JListLine> list;

	private Map<RequestReply, String> idByRequestReply = new HashMap<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
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
	public LoanBrokerFrame() throws NamingException
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
		
		list = new JList<JListLine>(listModel);
		scrollPane.setViewportView(list);

		prepareMessageListener();
	}

	private void prepareMessageListener() throws NamingException
	{
		MessageReceiver clientMessageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_CLIENT_DESTINATION);
		clientMessageReceiver.PrepareReceiveMessage(this::clientMessageReceived);
		MessageReceiver bankMessageReceiver = new MessageReceiver(StaticNames.BROKER_FROM_BANK_DESTINATION);
		bankMessageReceiver.PrepareReceiveMessage(this::bankMessageReceived);
	}

	private void clientMessageReceived(Message message)
	{
		ObjectMessage objectMessage = (ObjectMessage) message;
		try {
			Object receivedObject = objectMessage.getObject();
			if (receivedObject instanceof LoanRequestReply)
			{
				LoanRequestReply loanRequestReply = (LoanRequestReply) receivedObject;
				LoanRequest loanRequest = loanRequestReply.getRequest();
				add(loanRequest);

				SendBankInterestRequest(loanRequest);
			}
			else
			{
				//todo throw new Exception
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}

	private void SendBankInterestRequest(LoanRequest loanRequest)
	{
		BankInterestRequest bankInterestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());
		BankInterestRequestReply bankInterestRequestReply = new BankInterestRequestReply(bankInterestRequest, null);
		try
		{
			MessageSender messageSender = new MessageSender(StaticNames.ABN_AMRO_BANK_DESTINATION);
			String messageId = messageSender.SendMessage(bankInterestRequestReply);
			idByRequestReply.put(bankInterestRequestReply, messageId);
		}
		catch (NamingException e)
		{
			e.printStackTrace();
		}
	}

	private void bankMessageReceived(Message message)
	{
		ObjectMessage objectMessage = (ObjectMessage) message;
		try
		{
			Object receivedObject = objectMessage.getObject();
			if (receivedObject instanceof BankInterestRequestReply)
			{
				BankInterestRequestReply bankInterestRequestReply = (BankInterestRequestReply) receivedObject;
				bankInterestRequestReply.getReply();
				//todo find client request linked to this bank reply
				//todo construct client reply
				//todo send message to client
			}
			else
			{
				//todo throw new Exception
			}
		}
		catch (JMSException e)
		{
			e.printStackTrace();
		}
	}

	private void sendClientLoanReply(LoanRequestReply loanRequestReply)
	{
		try
		{
			MessageSender messageSender = new MessageSender(StaticNames.CLIENT_DESTINATION);
			messageSender.SendMessage(loanRequestReply);
		}
		catch (NamingException e)
		{
			e.printStackTrace();
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
	
	public void add(LoanRequest loanRequest){		
		listModel.addElement(new JListLine(loanRequest));		
	}
	

	public void add(LoanRequest loanRequest,BankInterestRequest bankRequest){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}		
	}
	
	public void add(LoanRequest loanRequest, BankInterestReply bankReply){
		JListLine rr = getRequestReply(loanRequest);
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}		
	}
}
