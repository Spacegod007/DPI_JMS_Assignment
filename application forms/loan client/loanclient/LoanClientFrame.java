package loanclient;
import loanclient.gateway.LoanBrokerGateway;
import messaging.requestreply.RequestReply;
import model.loan.LoanReply;
import model.loan.LoanRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoanClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField tfSSN;
	private DefaultListModel<RequestReply<LoanRequest,LoanReply>> listModel = new DefaultListModel<RequestReply<LoanRequest,LoanReply>>();
	private JList<RequestReply<LoanRequest,LoanReply>> requestReplyList;

	private JTextField tfAmount;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JTextField tfTime;

	private LoanBrokerGateway brokerGateway;

	/**
	 * Constructs the class
	 */
	LoanClientFrame()
	{
		brokerGateway = new LoanBrokerGateway();
		LoadFrame();

		brokerGateway.AddListener(this::BankInterestReplyReceived);
	}

	/**
	 * Loads the GUI frame
	 */
	private void LoadFrame()
	{
		setTitle("Loan Client");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 684, 619);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {0, 0, 30, 30, 30, 30, 0};
		gbl_contentPane.rowHeights = new int[] {30,  30, 30, 30, 30};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JLabel lblBody = new JLabel("ssn");
		GridBagConstraints gbc_lblBody = new GridBagConstraints();
		gbc_lblBody.insets = new Insets(0, 0, 5, 5);
		gbc_lblBody.gridx = 0;
		gbc_lblBody.gridy = 0;
		contentPane.add(lblBody, gbc_lblBody);

		tfSSN = new JTextField();
		GridBagConstraints gbc_tfSSN = new GridBagConstraints();
		gbc_tfSSN.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSSN.insets = new Insets(0, 0, 5, 5);
		gbc_tfSSN.gridx = 1;
		gbc_tfSSN.gridy = 0;
		contentPane.add(tfSSN, gbc_tfSSN);
		tfSSN.setColumns(10);

		lblNewLabel = new JLabel("amount");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		contentPane.add(lblNewLabel, gbc_lblNewLabel);

		tfAmount = new JTextField();
		GridBagConstraints gbc_tfAmount = new GridBagConstraints();
		gbc_tfAmount.anchor = GridBagConstraints.NORTH;
		gbc_tfAmount.insets = new Insets(0, 0, 5, 5);
		gbc_tfAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfAmount.gridx = 1;
		gbc_tfAmount.gridy = 1;
		contentPane.add(tfAmount, gbc_tfAmount);
		tfAmount.setColumns(10);

		lblNewLabel_1 = new JLabel("time");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		contentPane.add(lblNewLabel_1, gbc_lblNewLabel_1);

		tfTime = new JTextField();
		GridBagConstraints gbc_tfTime = new GridBagConstraints();
		gbc_tfTime.insets = new Insets(0, 0, 5, 5);
		gbc_tfTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfTime.gridx = 1;
		gbc_tfTime.gridy = 2;
		contentPane.add(tfTime, gbc_tfTime);
		tfTime.setColumns(10);

		JButton btnQueue = new JButton("send loan request");
		btnQueue.addActionListener(arg0 -> {
			int ssn = Integer.parseInt(tfSSN.getText());
			int amount = Integer.parseInt(tfAmount.getText());
			int time = Integer.parseInt(tfTime.getText());

			LoanRequest request = new LoanRequest(ssn,amount,time);
			listModel.addElement(new RequestReply<>(request, null));
			brokerGateway.ApplyForLoan(request);
		});
		GridBagConstraints gbc_btnQueue = new GridBagConstraints();
		gbc_btnQueue.insets = new Insets(0, 0, 5, 5);
		gbc_btnQueue.gridx = 2;
		gbc_btnQueue.gridy = 2;
		contentPane.add(btnQueue, gbc_btnQueue);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 7;
		gbc_scrollPane.gridwidth = 6;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		contentPane.add(scrollPane, gbc_scrollPane);

		requestReplyList = new JList<>(listModel);
		scrollPane.setViewportView(requestReplyList);
	}

	/**
	 * Gets triggered when a BankInterestReply is received
	 * @param loanReply The received reply
	 * @param id The id which was linked to the message
	 */
	private void BankInterestReplyReceived(LoanReply loanReply, String id)
	{
		LoanRequest loanRequest = brokerGateway.GetLoanRequestById(id);
		setRequestReply(loanRequest, loanReply);
	}

	/**
	 * This method returns the RequestReply line that belongs to the request from requestReplyList (JList).
	 * You can call this method when an reply arrives in order to add this reply to the right request in requestReplyList.
	 * @param request
	 * @return
	 */
   private RequestReply<LoanRequest,LoanReply> getRequestReply(LoanRequest request){

     for (int i = 0; i < listModel.getSize(); i++){
    	 RequestReply<LoanRequest,LoanReply> requestReply = listModel.get(i);
    	 if (requestReply.getRequest() == request){
    		 return requestReply;
    	 }
     }

     return null;
   }

	/**
	 * Sets the reply to a request
	 * @param loanRequest The request of which the reply should be set
	 * @param loanReply The reply to set
	 * @return Returns true if the reply is set otherwise false
	 */
	private boolean setRequestReply(LoanRequest loanRequest, LoanReply loanReply)
	{
		RequestReply<LoanRequest, LoanReply> requestReply = getRequestReply(loanRequest);
		if (requestReply != null)
		{
			requestReply.setReply(loanReply);
			return true;
		}
		return false;
	}
}
