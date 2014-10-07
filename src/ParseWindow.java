import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author James Laguardia
 * GUI for fun
 * Start Time: 9/12/2014; 11:20AM EST
 * End Time: 9/12/2014; 1:00PM EST
 */
public class ParseWindow extends JFrame{
	private static final long serialVersionUID = 38881189489234137L;
	private JTextArea taFileName, taReciever, taSender, taBody;
	private JScrollPane scroller;
	private JLabel lblFileName, lblReciever, lblSender, lblBody;
	private JButton btnFile1, btnFile2, btnFile3, btnClear;
	private JPanel panel;
	
	//Hardcoded Regular Expression and some guesswork stuff. Had the algorithim put together, but was
	//struggling with regex. I eventually pulled through; this was a good exercise!
	private String emailRegex = "[\\w.@{1}\\w.\\w{2,4}]+",
			          recieverClause = "(?:for\\s<){1}", 
			          senderClause = "(?:Return-Path:\\s<){1}", 
			          bodyClause = "(?:Content-Type:\\stext/plain;\\s){1}";
	private Pattern p;
	private Matcher m = null;
	private File selectedFile = null, 
			 fileTest = new File(ClassLoader.getSystemResource("./resources/test.txt").getPath()), 
			 fileNina = new File(ClassLoader.getSystemResource("./resources/nina.txt").getPath()),
			 fileAol = new File(ClassLoader.getSystemResource("./resources/aolEmail.txt").getPath());
	private StringBuffer sb = new StringBuffer();
	private FileInputStream fis;
	
	/**
	 * Constructor
	 * @param w - Width
	 * @param h - Height
	 */
	public ParseWindow(int w, int h){
		super();
		setResizable(false);
		setLayout(null);
		setBounds(10, 10, w, h);
		setTitle("Simple Email Parser");
		panel = new JPanel();
		panel.setBounds(0, 0, w, h);
		panel.setLayout(null);
		setup();
		add(panel);
	}
	
	private void setup(){
		taFileName = new JTextArea();
		taFileName.setEditable(false);
		taReciever = new JTextArea();
		taReciever.setEditable(false);
		taSender = new JTextArea();
		taSender.setEditable(false);
		taBody = new JTextArea();
		taBody.setEditable(false);
		
		scroller = new JScrollPane(taBody);
		
		lblFileName = new JLabel("File Name");
		lblReciever = new JLabel("Reciever");
		lblSender = new JLabel("Sender");
		//have to use html tags to add a line break
		lblBody = new JLabel("<html>Message<br>Body");
		
		btnFile1 = new JButton();
		btnFile2 = new JButton();
		btnFile3 = new JButton();
		btnClear = new JButton();
		
		btnFile1.setText("File 1");
		btnFile2.setText("File 2");
		btnFile3.setText("File 3");
		btnClear.setText("Clear Fields");
		
		btnFile1.setBounds(250, 5, 100, 20);
		btnFile2.setBounds(250, 35, 100, 20);
		btnFile3.setBounds(250, 65, 100, 20);
		btnClear.setBounds(250, 95, 100, 20);
		
		lblFileName.setBounds(5, 5, 100, 20);
		taFileName.setBounds(80, 5, 160, 20);
		
		lblReciever.setBounds(5, 45, 100, 20);
		taReciever.setBounds(80, 45, 160, 20);
		
		lblSender.setBounds(5, 85, 100, 20);
		taSender.setBounds(80, 85, 160, 20);
		
		lblBody.setBounds(5, 125, 100, 30);
		taBody.setBounds(80, 125, 260, 300);
		scroller.setBounds(80, 125, 260, 300);
		
		btnFile1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setFile(1);
			}
		});
		
		btnFile2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setFile(2);
			}
		});
		
		btnFile3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setFile(3);
			}
		});
		
		btnClear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				setFile(0);
				clearText();
			}
		});
		
		panel.add(lblFileName);
		panel.add(lblReciever);
		panel.add(lblSender);
		panel.add(lblBody);
		panel.add(taFileName);
		panel.add(taReciever);
		panel.add(taSender);
		panel.add(scroller);
		panel.add(btnFile1);
		panel.add(btnFile2);
		panel.add(btnFile3);
		panel.add(btnClear);
	}
	
	private void clearText(){
		taFileName.setText("");
		taReciever.setText("");
		taSender.setText(""); 
		taBody.setText("");
	}
	
	private String getReciever(StringBuffer sb){
		p = Pattern.compile(recieverClause + emailRegex);
		m = p.matcher(sb);
		String res = "";
		while(m.find()){
			res += m.group(0);
		}
		return res.split(recieverClause)[1];
	}
	
	private String getSender(StringBuffer sb){
		p = Pattern.compile(senderClause + emailRegex);
		m = p.matcher(sb);
		String res = "";
		while(m.find()){
			res += m.group(0);
		}
		return res.split(senderClause)[1];
	}
	
	private String getBody(StringBuffer sb){
		p = Pattern.compile(bodyClause + "[\\r]*[\\w+\\s\\W]+");
		//p = Pattern.compile(bodyClause);
		m = p.matcher(sb);
		String res = "";
		while(m.find()){
			res += m.group(0);
		}
		
		res = res.split("\\r\\n", 2)[1];		
		return res.split("--[\\w.|\\d.]")[0];
	}
	
	public void setFile(int selectedFile){
			switch(selectedFile){
			case 0:
				this.selectedFile = null;
				break;
			case 1:
				this.selectedFile = fileTest;
				break;
			case 2:
				this.selectedFile = fileNina;
				break;
			case 3:
				this.selectedFile = fileAol;
				break;
			default:
				System.out.println("No file selected error");
				break;
			}
			if(this.selectedFile != null){
				sb = new StringBuffer();
				try {
					fis = new FileInputStream(this.selectedFile);
					int index;
					while((index = fis.read()) != -1){
						sb.append((char)index);
					}
						taFileName.setText(this.selectedFile.getName());
						taReciever.setText(getReciever(sb));
						taSender.setText(getSender(sb));
						taBody.setText(getBody(sb));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	}
	
}
