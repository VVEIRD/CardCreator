package vv3ird.populatecard.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import vv3ird.populatecard.CardCreator;
import vv3ird.populatecard.data.ParallelProcessing;
import vv3ird.populatecard.data.Project;

public class JConfigFrame extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6272080945215106722L;
	
	private JPanel contentPane;
	private JTextField tfCsvDelimiter;
	private JTextField tfCsvQuote;
	private JComboBox<String> cbCsvRecordSeparator;
	private JSpinner spPPThreads;
	private JRadioButton rbPPCustom;
	private JRadioButton rbPPCpuMinus1;
	private JRadioButton rbPPSingleProcess;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JConfigFrame frame = new JConfigFrame(null);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JConfigFrame(Frame parent) {
		super(parent, "Configuration", true);
		setTitle("Configuration");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds((int)parent.getBounds().getX()+50, (int)parent.getBounds().getY()+50, 450, 300);
		contentPane = new JPanel();
		contentPane.setToolTipText("");
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		Component rigidArea_8 = Box.createRigidArea(new Dimension(5, 5));
		contentPane.add(rigidArea_8);
		
		int selIndex = CardCreator.hasCurrentProject() && "\r\n".equalsIgnoreCase(CardCreator.getCurrentProject().getCsvRecordSeparator()) ? 1 : 0;
		
		JPanel pnGeneralConfig = new JPanel();
		pnGeneralConfig.setBorder(null);
		contentPane.add(pnGeneralConfig);
		pnGeneralConfig.setLayout(new BoxLayout(pnGeneralConfig, BoxLayout.Y_AXIS));
		
		Box hbGeneralConfig = Box.createHorizontalBox();
		pnGeneralConfig.add(hbGeneralConfig);
		
		Component rigidArea_15 = Box.createRigidArea(new Dimension(5, 5));
		hbGeneralConfig.add(rigidArea_15);
		
		JLabel lblGeneralConfiguration = new JLabel("General Configuration");
		lblGeneralConfiguration.setFont(lblGeneralConfiguration.getFont().deriveFont(lblGeneralConfiguration.getFont().getStyle() | Font.BOLD, lblGeneralConfiguration.getFont().getSize() + 2f));
		lblGeneralConfiguration.setAlignmentX(0.5f);
		hbGeneralConfig.add(lblGeneralConfiguration);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		hbGeneralConfig.add(horizontalGlue_4);
		
		Box hbPPCaption = Box.createHorizontalBox();
		pnGeneralConfig.add(hbPPCaption);
		
		Component rigidArea_14 = Box.createRigidArea(new Dimension(5, 20));
		hbPPCaption.add(rigidArea_14);
		
		JLabel lblParallelProcessing = new JLabel("Parallel Processing");
		lblParallelProcessing.setFont(new Font("Tahoma", Font.ITALIC, 11));
		hbPPCaption.add(lblParallelProcessing);
		
		Box hbSingleProcess = Box.createHorizontalBox();
		hbPPCaption.add(hbSingleProcess);
		
		Component horizontalGlue_6 = Box.createHorizontalGlue();
		hbSingleProcess.add(horizontalGlue_6);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		pnGeneralConfig.add(horizontalBox_1);
		
		Component rigidArea_16 = Box.createRigidArea(new Dimension(5, 20));
		horizontalBox_1.add(rigidArea_16);
		
		Component rigidArea_17 = Box.createRigidArea(new Dimension(60, 20));
		horizontalBox_1.add(rigidArea_17);
		
		rbPPSingleProcess = new JRadioButton("Single process");
		rbPPSingleProcess.setSelected(true);
		horizontalBox_1.add(rbPPSingleProcess);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		horizontalBox_1.add(horizontalBox_5);
		
		Component horizontalGlue_7 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_7);
		
		Box hbPPCpuMinus1 = Box.createHorizontalBox();
		pnGeneralConfig.add(hbPPCpuMinus1);
		
		Component rigidArea_18 = Box.createRigidArea(new Dimension(5, 20));
		hbPPCpuMinus1.add(rigidArea_18);
		
		Component rigidArea_19 = Box.createRigidArea(new Dimension(60, 20));
		hbPPCpuMinus1.add(rigidArea_19);
		
		rbPPCpuMinus1 = new JRadioButton("CPU-1 processes");
		hbPPCpuMinus1.add(rbPPCpuMinus1);
		
		Box horizontalBox_7 = Box.createHorizontalBox();
		hbPPCpuMinus1.add(horizontalBox_7);
		
		Component horizontalGlue_8 = Box.createHorizontalGlue();
		horizontalBox_7.add(horizontalGlue_8);
		
		Box hbPPCustom = Box.createHorizontalBox();
		pnGeneralConfig.add(hbPPCustom);
		
		Component rigidArea_20 = Box.createRigidArea(new Dimension(5, 20));
		hbPPCustom.add(rigidArea_20);
		
		Component rigidArea_21 = Box.createRigidArea(new Dimension(60, 20));
		hbPPCustom.add(rigidArea_21);
		
		rbPPCustom = new JRadioButton("Custom:");
		hbPPCustom.add(rbPPCustom);
		
		spPPThreads = new JSpinner();
		spPPThreads.setSize(50, 20);
		spPPThreads.setPreferredSize(new Dimension(50, 20));
		spPPThreads.setMinimumSize(new Dimension(50, 20));
		spPPThreads.setMaximumSize(new Dimension(50, 20));
		spPPThreads.setModel(new SpinnerNumberModel(1, 1, 96, 1));
		hbPPCustom.add(spPPThreads);
		
		Box horizontalBox_10 = Box.createHorizontalBox();
		hbPPCustom.add(horizontalBox_10);
		
		Component horizontalGlue_9 = Box.createHorizontalGlue();
		horizontalBox_10.add(horizontalGlue_9);
		
		Component verticalGlue = Box.createVerticalGlue();
		contentPane.add(verticalGlue);
		
		JPanel panel_2 = new JPanel();
		panel_2.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		contentPane.add(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		Box horizontalBox_8 = Box.createHorizontalBox();
		panel_2.add(horizontalBox_8);
		
		Component horizontalGlue_5 = Box.createHorizontalGlue();
		horizontalBox_8.add(horizontalGlue_5);
		
		JButton btnSave = new JButton("Ok");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Project p = CardCreator.getCurrentProject();
				if(p != null) {
					char delim = tfCsvDelimiter.getText().length() > 0 ? tfCsvDelimiter.getText().charAt(0) : p.getCsvDelimiter();
					char quote = tfCsvQuote.getText().length() > 0 ? tfCsvQuote.getText().charAt(0) : p.getCsvDelimiter();
					String recordSep = cbCsvRecordSeparator.getSelectedIndex() == 0 ? "\r\n" : "\n";
					p.setCsvDelimiter(delim);
					p.setCsvQuote(quote);
					p.setCsvRecordSeparator(recordSep);
					ParallelProcessing pp = rbPPCpuMinus1.isSelected() ? ParallelProcessing.CPU_MINUS_ONE : rbPPSingleProcess.isSelected() ? ParallelProcessing.SINGLE_THREAD : ParallelProcessing.CUSTOM;
					CardCreator.setParallelProcessing(pp, (Integer)spPPThreads.getValue());
					JConfigFrame.this.setVisible(false);
					JConfigFrame.this.dispose();
				}
			}
		});
		horizontalBox_8.add(btnSave);
		
		Component rigidArea_10 = Box.createRigidArea(new Dimension(20, 20));
		horizontalBox_8.add(rigidArea_10);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JConfigFrame.this.setVisible(false);
				JConfigFrame.this.dispose();
			}
		});
		horizontalBox_8.add(btnCancel);
		
		Component rigidArea_11 = Box.createRigidArea(new Dimension(5, 5));
		horizontalBox_8.add(rigidArea_11);
		
		Component rigidArea_12 = Box.createRigidArea(new Dimension(5, 5));
		contentPane.add(rigidArea_12);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbPPCpuMinus1);
		bg.add(rbPPSingleProcess);
		bg.add(rbPPCustom);
		
		Component verticalStrut_5 = Box.createVerticalStrut(5);
		pnGeneralConfig.add(verticalStrut_5);
		
		Box hbCsvConfigCaption = Box.createHorizontalBox();
		pnGeneralConfig.add(hbCsvConfigCaption);
		
		Component rigidArea_22 = Box.createRigidArea(new Dimension(5, 5));
		hbCsvConfigCaption.add(rigidArea_22);
		
		JLabel label = new JLabel("CSV Configuration");
		label.setFont(new Font("Tahoma", Font.ITALIC, 11));
		label.setAlignmentX(0.5f);
		hbCsvConfigCaption.add(label);
		
		Component horizontalGlue_10 = Box.createHorizontalGlue();
		hbCsvConfigCaption.add(horizontalGlue_10);
		
		Component verticalStrut_6 = Box.createVerticalStrut(5);
		pnGeneralConfig.add(verticalStrut_6);
		
		Box hbCsvDelim = Box.createHorizontalBox();
		pnGeneralConfig.add(hbCsvDelim);
		
		Component rigidArea = Box.createRigidArea(new Dimension(60, 20));
		hbCsvDelim.add(rigidArea);
		
		JLabel lblCsvseparator = new JLabel("CSV-Delimiter");
		hbCsvDelim.add(lblCsvseparator);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(61, 20));
		hbCsvDelim.add(rigidArea_3);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		hbCsvDelim.add(horizontalBox_2);
		
		tfCsvDelimiter = new JTextField(CardCreator.hasCurrentProject() ? String.valueOf(CardCreator.getCurrentProject().getCsvDelimiter()) : ";");
		horizontalBox_2.add(tfCsvDelimiter);
		tfCsvDelimiter.setHorizontalAlignment(SwingConstants.CENTER);
		tfCsvDelimiter.setColumns(10);
		tfCsvDelimiter.setPreferredSize(new Dimension(20, 20));
		tfCsvDelimiter.setMaximumSize(new Dimension(20, 20));
		tfCsvDelimiter.setMinimumSize(new Dimension(20, 20));
		tfCsvDelimiter.setSize(new Dimension(20, 20));
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_1);
		
		Component verticalStrut_4 = Box.createVerticalStrut(5);
		pnGeneralConfig.add(verticalStrut_4);
		
		JPanel pnCsvConfig = new JPanel();
		pnGeneralConfig.add(pnCsvConfig);
		pnCsvConfig.setLayout(new BoxLayout(pnCsvConfig, BoxLayout.LINE_AXIS));
		
		Box horizontalBox = Box.createHorizontalBox();
		pnCsvConfig.add(horizontalBox);
		
		Box hbCsvQuote = Box.createHorizontalBox();
		pnGeneralConfig.add(hbCsvQuote);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(60, 20));
		hbCsvQuote.add(rigidArea_1);
		
		JLabel lblCsvquotationmarks = new JLabel("CSV-Quotationmarks");
		hbCsvQuote.add(lblCsvquotationmarks);
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		hbCsvQuote.add(horizontalBox_4);
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(26, 20));
		horizontalBox_4.add(rigidArea_4);
		
		tfCsvQuote = new JTextField(CardCreator.hasCurrentProject() ? String.valueOf(CardCreator.getCurrentProject().getCsvQuote()): "\"");
		tfCsvQuote.setSize(new Dimension(20, 20));
		tfCsvQuote.setPreferredSize(new Dimension(20, 20));
		tfCsvQuote.setMinimumSize(new Dimension(20, 20));
		tfCsvQuote.setMaximumSize(new Dimension(20, 20));
		tfCsvQuote.setHorizontalAlignment(SwingConstants.CENTER);
		tfCsvQuote.setColumns(10);
		horizontalBox_4.add(tfCsvQuote);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_4.add(horizontalGlue_2);
		
		Component verticalStrut_1 = Box.createVerticalStrut(5);
		pnGeneralConfig.add(verticalStrut_1);
		
		Box hbCsvSep = Box.createHorizontalBox();
		pnGeneralConfig.add(hbCsvSep);
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(60, 20));
		hbCsvSep.add(rigidArea_2);
		
		JLabel lblCsvrecordSeparator = new JLabel("CSV-record Separator");
		hbCsvSep.add(lblCsvrecordSeparator);
		
		Component rigidArea_5 = Box.createRigidArea(new Dimension(20, 20));
		hbCsvSep.add(rigidArea_5);
		
		Box horizontalBox_6 = Box.createHorizontalBox();
		hbCsvSep.add(horizontalBox_6);
		
		cbCsvRecordSeparator = new JComboBox(new String[] {"Windows Style (\\r\\n)", "Linux Style (\\n)"});
		cbCsvRecordSeparator.setSize(new Dimension(150, 20));
		cbCsvRecordSeparator.setPreferredSize(new Dimension(150, 20));
		cbCsvRecordSeparator.setMinimumSize(new Dimension(150, 20));
		cbCsvRecordSeparator.setMaximumSize(new Dimension(150, 20));
		cbCsvRecordSeparator.setSelectedIndex(selIndex);
		horizontalBox_6.add(cbCsvRecordSeparator);
		
		Component horizontalGlue_3 = Box.createHorizontalGlue();
		horizontalBox_6.add(horizontalGlue_3);
		
		if(CardCreator.hasCurrentProject()) {
			switch (CardCreator.getCurrentProject().getProcessingMode()) {
			case CPU_MINUS_ONE:
				rbPPCpuMinus1.setSelected(true);
				break;
			case SINGLE_THREAD:
				rbPPSingleProcess.setSelected(true);
				break;
			case CUSTOM:
				rbPPCustom.setSelected(true);
				spPPThreads.setValue(CardCreator.getCurrentProject().getCustomParallelProcessingThreads());
			
			}
		}
	}

}
