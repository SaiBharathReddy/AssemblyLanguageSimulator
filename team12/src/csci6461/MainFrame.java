package csci6461;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.util.BitSet;
/*This is the class that contains all UI components and methods to update those components.
 * 
 */
public class MainFrame extends JFrame {
	
	private static Simulator simulator = Simulator.getInstance();
	
	
	private JPanel contentPanel;
	private static JTextField gpr0_TextField;
	private static JTextField gpr1_TextField;
	private static JTextField gpr2_TextField;
	private static JTextField input_TextField;
	private static JTextField gpr3_TextField;
	private static JTextField ixr1_TextField;
	private static JTextField ixr2_TextField;
	private static JTextField ixr3_TextField;
	private static JTextField pc_TextField;
	private static JTextField mar_TextField;
	private static JTextField MBR_TextField;
	private static JTextField ir_TextField;
	private static JTextArea textField_Printer;
	private static JTextArea textField_Keyboard;
	private static JTextField mfr_TextField;
	private static JTextField priv_TextField;
	private static JTextField cc_TextField;
	private static JLabel halt;
	private static JLabel file_Label = new JLabel("File Selection:");
	private static int count=0;
	public static String fullText="";
	/* Method to reset the Input Field
	 * 
	 */
	public void clearInput() {
		input_TextField.setText("");
		halt.setForeground(Color.cyan);
		file_Label.setForeground(Color.cyan);
		textField_Keyboard.setText("");
		textField_Printer.setText("");
		count=0;
		fullText="";
	}
	/**
	* Method to Get input from UI keyboard
	* @return
	*/
 public static int getKeyboard() {
	 String text = textField_Keyboard.getText();
	 String token = text.split(",")[0]; // get first element
	 int len=token.length();
	 int i=0;
	 int result=0;
	 try {
		 result = Integer.parseInt(token);
	 } catch (NumberFormatException n) {
		 while(i<len) {
			 try {
				 result = result*10+Integer.parseInt(String.valueOf(token.charAt(i)));
			 }
			 catch (NumberFormatException m) {
				 result=0;
				 break;
			 }
			 i++;
		 }
		 
	 }

	 if (text.indexOf(",") != -1) {
		 fullText=fullText+text.substring(0,text.indexOf(",") + 1);
		 text = text.substring(text.indexOf(",") + 1);}
	 
	 else text = "";
	 textField_Keyboard.setText(text);
	 return result;
 }
 
 /**
	* Method to set printer on UI using integer output
	* @param output
	*/
 public static void setPrinter(int output) {
	 count+=1;
	 System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"+count+"$$$$$$$$$$$$$$$$$$$$$$$$");
	 if(count==1) {
		 textField_Printer.setText("Given Array:"+" "+ output);
	 }
	 else if(count>0 && count<=20){
		 //System.out.println("Field Length:"+textField_Printer.getText().length());
	 textField_Printer.setText(textField_Printer.getText() +" "+ output);
	 
	 }
	 else if(count==21) {
		 textField_Printer.setText(textField_Printer.getText()+"\n"+"Search Number:"+output);
		 textField_Keyboard.setText(fullText);
		 //count=-1;
	 }
	 else if(count==22){
	 textField_Printer.setText(textField_Printer.getText() +"\n"+"Closest Number:"+output);}
	 
 }
 
 /**
	* Method to set printer on UI using string output
	* @param output
	*/
 public static void setPrinter(String output) {
	 textField_Printer.setText(textField_Printer.getText() + output);
 }
	/*
	 * Method to get the text field object for respective registers
	 */
	private static JTextField getRegisterGUI(String reg) {

		if (reg == "R0") {
			return gpr0_TextField;
		}
		if (reg == "R1") {
			return gpr1_TextField;
		}
		if (reg == "R2") {
			return gpr2_TextField;
		}
		if (reg == "R3") {
			return gpr3_TextField;
		}
		if (reg == "X1") {
			return ixr1_TextField;
		}
		if (reg == "X2") {
			return ixr2_TextField;
		}
		if (reg == "X3") {
			return ixr3_TextField;
		}
		if (reg == "PC") {
			return pc_TextField;
		}
		if (reg == "MAR") {
			return mar_TextField;
		}
		if (reg == "MBR") {
			return MBR_TextField;
		}
		if (reg == "IR") {
			return ir_TextField;
		}
		if (reg == "CC") {
			return cc_TextField;
		}
		return null;
	}
	
	
	/**
	 * Method to update Registers on the UI
	 */
	public static void updateRegUI(String regStr, BitSet value, int length) {
		JTextField register = getRegisterGUI(regStr);
		// no update needed if there is no corresponding GUI element
		if (register == null) {
			return;
		}
		String result = Integer.toBinaryString(Util.bitSet2Int(value));
		int zeros = length - result.length();
		for (int i = 0; i < zeros; i++) {
			result = "0" + result;
		}
		register.setText(result);
	}
	
	public static void updateLabel() {
		halt.setForeground(Color.red);
	}
	
	/**
	 * Program starts from here by launching the UI
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	/**
	 * Function to create the frame.
	 */
	public MainFrame() {
		setTitle("UI of Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1300, 800);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(6, 6, 6, 6));
		contentPanel.setBackground(Color.cyan);

		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		JLabel gpr0Label = new JLabel("GPR 0");
		gpr0Label.setBounds(15, 22, 50, 25);
		contentPanel.add(gpr0Label);
		
		JLabel gpr1Label = new JLabel("GPR 1");
		gpr1Label.setBounds(15, 52, 50, 25);
		contentPanel.add(gpr1Label);
		
		JLabel gpr2Label = new JLabel("GPR 2");
		gpr2Label.setBounds(15, 82, 50, 25);
		contentPanel.add(gpr2Label);
		
		JLabel gpr3Label = new JLabel("GPR 3");
		gpr3Label.setBounds(15, 112, 50, 25);
		contentPanel.add(gpr3Label);
		
		gpr0_TextField = new JTextField();
		gpr0_TextField.setBounds(136, 25, 250, 20);
		gpr0_TextField.setText("0000000000000000");
		gpr0_TextField.setColumns(10);
		gpr0_TextField.setEditable(false);
		contentPanel.add(gpr0_TextField);
		
		
		gpr1_TextField = new JTextField();
		gpr1_TextField.setBounds(136, 55, 250, 20);
		gpr1_TextField.setText("0000000000000000");
		gpr1_TextField.setEditable(false);
		gpr1_TextField.setColumns(10);
		contentPanel.add(gpr1_TextField);
		
		gpr2_TextField = new JTextField();
		gpr2_TextField.setBounds(136, 85, 250, 20);
		gpr2_TextField.setText("0000000000000000");
		gpr2_TextField.setEditable(false);
		gpr2_TextField.setColumns(10);
		contentPanel.add(gpr2_TextField);
		
		
		gpr3_TextField = new JTextField();
		gpr3_TextField.setBounds(136, 115, 250, 20);
		gpr3_TextField.setText("0000000000000000");
		gpr3_TextField.setEditable(false);
		gpr3_TextField.setColumns(10);
		contentPanel.add(gpr3_TextField);
		
		JButton GPR0_load = new JButton("LD");
		GPR0_load.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("R0", input_TextField.getText());

			}
		});
		GPR0_load.setBounds(391, 25, 85, 21);
		contentPanel.add(GPR0_load);
		
		JButton GPR1_load = new JButton("LD");
		GPR1_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("R1", input_TextField.getText());
			}
		});
		GPR1_load.setBounds(391, 55, 85, 21);
		contentPanel.add(GPR1_load);
		
		JButton GPR2_load = new JButton("LD");
		GPR2_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("R2", input_TextField.getText());
			}
		});
		GPR2_load.setBounds(391, 85, 85, 21);
		contentPanel.add(GPR2_load);
		
		JButton GPR3_load = new JButton("LD");
		GPR3_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("R3", input_TextField.getText());
			}
		});
		GPR3_load.setBounds(391, 115, 85, 21);
		contentPanel.add(GPR3_load);
		
				
		JLabel ixr1_Label = new JLabel("IXR 1");
		ixr1_Label.setBounds(15, 192, 50, 25);
		contentPanel.add(ixr1_Label);
		
		ixr1_TextField = new JTextField();
		ixr1_TextField.setEditable(false);
		ixr1_TextField.setText("0000000000000000");
		ixr1_TextField.setColumns(10);
		ixr1_TextField.setBounds(136, 195, 250, 20);
		
		contentPanel.add(ixr1_TextField);
		
		JButton IXR1_load = new JButton("LD");
		IXR1_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("X1", input_TextField.getText());
			}
		});
		IXR1_load.setBounds(391, 195, 85, 21);
		contentPanel.add(IXR1_load);
		
		JLabel ixr2_Label = new JLabel("IXR 2");
		ixr2_Label.setBounds(15, 222, 50, 25);
		contentPanel.add(ixr2_Label);
		
		ixr2_TextField = new JTextField();
		ixr2_TextField.setEditable(false);
		ixr2_TextField.setText("0000000000000000");
		ixr2_TextField.setColumns(10);
		ixr2_TextField.setBounds(136, 225, 250, 20);
		

		contentPanel.add(ixr2_TextField);
		
		JButton IXR2_load = new JButton("LD");
		IXR2_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("X2", input_TextField.getText());
			}
		});
		IXR2_load.setBounds(391, 225, 85, 21);
		contentPanel.add(IXR2_load);
		
		JLabel ixr3_Label = new JLabel("IXR 3");
		ixr3_Label.setBounds(15, 252, 50, 25);
		contentPanel.add(ixr3_Label);
		
		ixr3_TextField = new JTextField();
		ixr3_TextField.setEditable(false);
		ixr3_TextField.setText("0000000000000000");
		ixr3_TextField.setColumns(10);
		ixr3_TextField.setBounds(136, 255, 250, 20);
		

		contentPanel.add(ixr3_TextField);
		
		
		
		JButton IXR3_load = new JButton("LD");
		IXR3_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("X3", input_TextField.getText());
			}
		});
		IXR3_load.setBounds(391, 255, 85, 21);
		contentPanel.add(IXR3_load);
		
		JLabel input_Label = new JLabel("Input");
		input_Label.setHorizontalAlignment(SwingConstants.CENTER);
		input_Label.setBounds(120, 440, 300, 20);
		contentPanel.add(input_Label);
		
		input_TextField = new JTextField();
		input_TextField.setBounds(136, 410, 335, 20);
		contentPanel.add(input_TextField);
		input_TextField.setColumns(10);
		
		JLabel pc_Label = new JLabel("PC");
		pc_Label.setBounds(680, 22, 50, 25);
		contentPanel.add(pc_Label);
		
		pc_TextField = new JTextField();
		pc_TextField.setEditable(false);
		pc_TextField.setText("000000000000");
		pc_TextField.setColumns(10);
		pc_TextField.setBounds(780, 25, 250, 20);
		
		
		contentPanel.add(pc_TextField);
		
		JButton PC_load = new JButton("LD");
		PC_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("PC", input_TextField.getText());
			}
		});
		PC_load.setBounds(1100, 25, 85, 21);
		contentPanel.add(PC_load);
		
		JLabel mar_Label = new JLabel("MAR");
		mar_Label.setBounds(680,52, 50, 25);
		contentPanel.add(mar_Label);
		
		mar_TextField = new JTextField();
		mar_TextField.setEditable(false);
		mar_TextField.setColumns(10);
		mar_TextField.setText("000000000000");
		mar_TextField.setBounds(780, 55, 250, 20);
		

		contentPanel.add(mar_TextField);
		
		JButton MAR_load = new JButton("LD");
		MAR_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("MAR", input_TextField.getText());
			}
		});
		MAR_load.setBounds(1100, 55, 85, 21);
		contentPanel.add(MAR_load);
		
		JLabel mbr_Label = new JLabel("MBR");
		mbr_Label.setBounds(680,82, 50, 25);
		contentPanel.add(mbr_Label);
		
		MBR_TextField = new JTextField();
		MBR_TextField.setEditable(false);
		MBR_TextField.setColumns(10);
		MBR_TextField.setBounds(780, 85, 250, 20);
		MBR_TextField.setText("0000000000000000");
		contentPanel.add(MBR_TextField);
		
		JButton MBR_load = new JButton("LD");
		MBR_load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("MBR", input_TextField.getText());
			}
		});
		MBR_load.setBounds(1100, 85, 85, 21);
		contentPanel.add(MBR_load);
		
		JLabel lblNewLabel_4_1_1_1 = new JLabel("IR");
		lblNewLabel_4_1_1_1.setBounds(680,112, 50, 25);
		contentPanel.add(lblNewLabel_4_1_1_1);
		
		ir_TextField = new JTextField();
		ir_TextField.setEditable(false);
		ir_TextField.setColumns(10);
		ir_TextField.setText("0000000000000000");
		ir_TextField.setBounds(780, 115, 250, 20);
		contentPanel.add(ir_TextField);
		
		JLabel mfr_Label = new JLabel("MFR");
		mfr_Label.setBounds(680,142, 50, 25);
		contentPanel.add(mfr_Label);
		
		mfr_TextField = new JTextField();
		mfr_TextField.setText("0000");
		mfr_TextField.setEditable(false);
		mfr_TextField.setColumns(10);
		mfr_TextField.setBounds(780, 145, 250, 20);
		contentPanel.add(mfr_TextField);
		
		JLabel priv_Label = new JLabel("Priviledged");
		priv_Label.setBounds(680,172, 75, 25);
		contentPanel.add(priv_Label);
		
		priv_TextField = new JTextField();
		priv_TextField.setText("0");
		priv_TextField.setEditable(false);
		priv_TextField.setColumns(10);
		priv_TextField.setBounds(780, 175, 250, 20);
		contentPanel.add(priv_TextField);
		
		JLabel cc_Label = new JLabel("CC");
		cc_Label.setBounds(680,202, 50, 25);
		contentPanel.add(cc_Label);
		
		cc_TextField = new JTextField();
		cc_TextField.setText("0000");
		cc_TextField.setEditable(false);
		cc_TextField.setColumns(10);
		cc_TextField.setBounds(780, 205, 250, 20);
		contentPanel.add(cc_TextField);
		
		JButton cc_Button = new JButton("LD");
		cc_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.loadRegisterFromInput("CC", input_TextField.getText());
			}
		});
		cc_Button.setBounds(1100, 205, 85, 21);
		contentPanel.add(cc_Button);

		
		JButton load_Button = new JButton("Load");
		load_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.load();
			}
		});
		load_Button.setBounds(673, 353, 74, 37);
		contentPanel.add(load_Button);
		
		JButton store_Button = new JButton("Store");
		store_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.store();
			}
		});
		store_Button.setBounds(673, 396, 74, 37);
		
		contentPanel.add(store_Button);
		
		JButton ss_Button = new JButton("SS");
		ss_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.singleStep();
			}
		});
		ss_Button.setBounds(854, 365, 94, 59);
		contentPanel.add(ss_Button);	
		

		JButton run_Button = new JButton("Run");
		run_Button.setBounds(958, 365, 94, 59);
		run_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simulator.run();
			}
		});
		contentPanel.add(run_Button);
		
		JButton init_Button = new JButton("INIT");
		init_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearInput();
				simulator.init("program1.txt");
			}
		});
		init_Button.setBounds(754, 376, 74, 37);
		init_Button.setForeground(Color.MAGENTA);
		contentPanel.add(init_Button);
		
		JButton clear_Button = new JButton("Clear");
		clear_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearInput();
				simulator.initializeRegisters();
			}
		});
		clear_Button.setBounds(1074, 376, 74, 37);
		contentPanel.add(clear_Button);
		JButton file_Choose = new JButton("InputFile");
		file_Choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt)
		    {
				clearInput();
				simulator.initializeRegisters();
		                   // create an object of JFileChooser class
				JFileChooser open = new JFileChooser();
	    		file_Label.setBounds(673,700, 1000, 25);
	    		contentPanel.add(file_Label);

	 
	            // invoke the showsOpenDialog function to show the save dialog
	            int r = open.showOpenDialog(null);
	            if (r == JFileChooser.APPROVE_OPTION)
	            	 
	            {
	            	
	                // set the label to the path of the selected file
	                file_Label.setText("File Selected:"+open.getSelectedFile().getAbsolutePath());
	                simulator.encodeInputFile(open.getSelectedFile().getAbsolutePath());
	            }
	            // if the user cancelled the operation
	            else {
	            	file_Label.setText("the user cancelled the operation");}
	            file_Label.setForeground(Color.BLACK);
	        }
		            
		    
		            
		    
		});
		file_Choose.setBounds(1174, 365, 94, 50);
		contentPanel.add(file_Choose);
		halt = new JLabel("Program is Halted!");
		halt.setBounds(373,600, 300, 150);
		halt.setForeground(Color.cyan);
		halt.setBackground(Color.cyan);
		textField_Keyboard = new JTextArea();
		 textField_Keyboard.setLineWrap(true);
		 textField_Keyboard.setBounds(673, 479, 223, 183);
		 contentPanel.add(textField_Keyboard);
		 textField_Keyboard.setColumns(10);
		 
		 JLabel lblNewLabel_4 = new JLabel("Keyboard Console");
		 lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		 lblNewLabel_4.setBounds(673, 672, 223, 13);
		 contentPanel.add(lblNewLabel_4);
		 
		 textField_Printer = new JTextArea();
		 JScrollPane scrollPane = new JScrollPane (textField_Printer, 
		 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		 textField_Printer.setEditable(false);
		 textField_Printer.setLineWrap(true);
		 textField_Printer.setColumns(10);
		 scrollPane.setBounds(939, 479, 223, 183);
		 contentPanel.add(scrollPane);

		 
		 JLabel lblNewLabel_4_2 = new JLabel("Printer Display");
		 lblNewLabel_4_2.setHorizontalAlignment(SwingConstants.CENTER);
		 lblNewLabel_4_2.setBounds(939, 670, 223, 17);
		 contentPanel.add(lblNewLabel_4_2);
		contentPanel.add(halt);
		
				
	}


}

