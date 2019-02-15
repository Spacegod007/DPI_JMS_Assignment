package loanbroker;

import loanbroker.gateway.MainGateway;
import model.bank.BankInterestReply;
import model.bank.BankInterestRequest;
import model.loan.LoanRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoanBrokerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;

	private MainGateway mainGateway;

	/**
	 * Constructs the class
	 */
	LoanBrokerFrame()
	{
		LoadFrame();
		mainGateway = new MainGateway();

		mainGateway.AddLoanRequestReceivedEventListener(this::loanRequestReceived);
		mainGateway.AddBankReplyReceivedEventListener(this::bankInterestRequestReceived);
	}

	private void loanRequestReceived(LoanRequest loanRequest, String id)
	{
		add(loanRequest);
		BankInterestRequest bankInterestRequest = mainGateway.GetBankInterestRequestById(id);
		add(loanRequest, bankInterestRequest);
	}

	private void bankInterestRequestReceived(BankInterestReply bankInterestReply, String id)
	{
		LoanRequest loanRequest = mainGateway.GetLoanRequestById(id);
		add(loanRequest, bankInterestReply);
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
}
