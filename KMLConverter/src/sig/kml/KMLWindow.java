package sig.kml;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

public class KMLWindow extends JFrame{
	JFrame f = new JFrame("KMLConverter 1.0");
	String input_fileloc="./input.txt", output_fileloc="./output.txt";
	ButtonLabel input = null,output = null;
	JTextArea status_window = null;
	JScrollPane pane;
	JPanel panel = new JPanel();
	KMLWindow() {
		try {
			input=new ButtonLabel("Browse...",new File(".").getCanonicalPath(),"Input",this);
			output=new ButtonLabel("Browse...",new File(".").getCanonicalPath(),"Output",this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ActionListener convert_action = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new KMLConverter(input_fileloc,output_fileloc);
			}
		};
		
		JButton convert_but = new JButton("Convert");
		convert_but.setMinimumSize(new Dimension(128,24));
		convert_but.addActionListener(convert_action);
		
		
		status_window = new JTextArea(8,32);
		status_window.setAutoscrolls(true);
		status_window.setEditable(false);
		status_window.setLineWrap(true);
		status_window.setWrapStyleWord(true);
		status_window.setBackground(new Color(0,0,96));
		status_window.setForeground(new Color(220,220,220));
		
		pane = new JScrollPane(status_window);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setPreferredSize(new Dimension(320,64));
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
		input.Initialize(panel);
		output.Initialize(panel);
		panel.add(Box.createVerticalStrut(6));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(6));
		JPanel window_panel = new JPanel();
		window_panel.setLayout(new BoxLayout(window_panel,BoxLayout.LINE_AXIS));
		window_panel.add(pane);
		
		panel.add(window_panel);
		panel.add(Box.createVerticalStrut(6));
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(6));
		panel.add(convert_but);
		panel.setBackground(new Color(170,180,200));
		
		f.add(panel);
		f.pack();
		f.setLocationByPlatform(true);
		f.setSize(640, 240);
		f.setVisible(true);
	}
	
	public void addStatusMessage(String msg) {
		status_window.setText(status_window.getText()+"\n"+msg);
	}
}

class ButtonLabel{
	private KMLWindow parent;
	private JButton b = new JButton();
	private JLabel l = new JLabel();
	private JPanel panel = new JPanel();
	private String label_type = "";
	private String label_header = "";
	private ActionListener listener;
	ButtonLabel(String button_label,String label_text, String label_type,KMLWindow parent) {
		this.parent=parent;
		listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser browse = new JFileChooser();
				browse.setDialogTitle("Select "+e.getActionCommand()+" File");
				switch (e.getActionCommand()) {
					case "Input":{
						File f = new File(parent.input_fileloc);
						if (!f.exists()) {
							try {
							f.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}}
						browse.setCurrentDirectory(f);
					}break;
					case "Output":{
						File f = new File(parent.output_fileloc);
						if (!f.exists()) {
							try {
							f.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}}
						browse.setCurrentDirectory(f);
					}break;
				}
				if (browse.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String fileloc = browse.getSelectedFile().getAbsolutePath();
					try {
						String path = new File(fileloc).getCanonicalPath();
						l.setText(label_header+path);
						switch (e.getActionCommand()) {
							case "Input":{
								parent.input_fileloc = fileloc;
								parent.addStatusMessage("New Input File Location: "+fileloc);
							}break;
							case "Output":{
								parent.output_fileloc = fileloc;
								parent.addStatusMessage("New Output File Location: "+fileloc);
							}break;
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		label_header = label_type+":     ";
		b.setText(button_label);
		b.setActionCommand(label_type);
		b.addActionListener(listener);
		l.setText(label_header+label_text);
		panel.setLayout(new BoxLayout(panel,BoxLayout.LINE_AXIS));
		panel.setBackground(new Color(170,180,200));
		l.setBackground(new Color(170,180,200));
		this.label_type = label_type;
	}
	
	JButton getButton() {
		return b;
	}
	JLabel getLabel() {
		return l;
	}
	
	void Initialize(JPanel p) {
		panel.add(Box.createHorizontalStrut(32));
		panel.add(l);
		panel.add(Box.createHorizontalStrut(64));
		panel.add(b);
		p.add(panel);
	}
}