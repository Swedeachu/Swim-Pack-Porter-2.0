import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import java.awt.Color;
import javax.swing.JCheckBox;
import java.awt.Font;

@SuppressWarnings("unused")
public class Settings {

	public static JFrame frame;
	public static JTextPane txtpnSaved = new JTextPane();
	public static JCheckBox chckbxNewCheckBox = new JCheckBox("Automatically Zip and Convert Ported Pack to .mcpack");
	public static String pack = Window.configs.get(1);
	public static String sky = Window.configs.get(2);
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void initialize() {
		System.out.println("opening settings");
		JDialog dialog = new JDialog((JFrame) null, "Swim Pack Port Settings (Loads from swim_pack.config)", true);
		dialog.getContentPane().setBackground(new Color(51, 51, 51));

		dialog.setBounds(400, 400, 600, 300);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setResizable(false);
		dialog.getContentPane().setLayout(null);

		JButton applySettings = new JButton("Apply");
		applySettings.setBounds(379, 215, 89, 23);
		applySettings.setBorderPainted(false);
		applySettings.setFocusPainted(true);
		applySettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("saving settings");
				File config = new File(System.getenv("APPDATA") +"\\Swim Services\\swim_pack.config");
				try {
					FileWriter configWriter = new FileWriter(config);
					if(chckbxNewCheckBox.isSelected()) {
						System.out.println("box is checked to true");
						configWriter.write("true\n");
					} else {
						System.out.println("box is not checked, false");
						configWriter.write("false\n");
					}
					Window.packSavePath = pack;
					Window.skySavePath = sky;
					configWriter.write(Window.packSavePath+"\n");
					configWriter.write(Window.skySavePath);
					configWriter.close();
					Window.configs.clear();
					  Scanner configReader = new Scanner(config);
				      while (configReader.hasNextLine()) {
				        String data = configReader.nextLine();
				        Window.configs.add(data);
				      }
				      System.out.println(Window.configs.get(0));
				      System.out.println(Window.configs.get(1));
				      System.out.println(Window.configs.get(2));
				      configReader.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				try {
					txtpnSaved.setVisible(false);
					Thread.sleep(100);
					txtpnSaved.setVisible(true);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		dialog.getContentPane().add(applySettings);
		
		JButton close = new JButton("Close");
		close.setBounds(478, 215, 89, 23);
		close.setBorderPainted(false);
		close.setFocusPainted(true);
		dialog.getContentPane().add(close);
		
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		
		chckbxNewCheckBox.setForeground(new Color(0, 0, 0));
		chckbxNewCheckBox.setBackground(new Color(204, 204, 204));
		chckbxNewCheckBox.setBounds(29, 70, 538, 32);
		chckbxNewCheckBox.setBorderPainted(false);
		chckbxNewCheckBox.setFocusPainted(true);
		
		if(Window.configs.get(0).equals("true")) {
			chckbxNewCheckBox.setSelected(true);
		} else {
			chckbxNewCheckBox.setSelected(false);
		}
		
		dialog.getContentPane().add(chckbxNewCheckBox);
		
		chckbxNewCheckBox.addItemListener(new ItemListener() {    
            public void itemStateChanged(ItemEvent e) {                 
            	System.out.println(e.getStateChange());
            }    
         });  
		
		JButton PackLocation = new JButton("Ported Pack Save Location: " +Window.packSavePath);
		PackLocation.setBounds(29, 115, 538, 32);
		dialog.getContentPane().add(PackLocation);
		PackLocation.setBorderPainted(false);
		PackLocation.setFocusPainted(true);
		PackLocation.setText("Ported Pack Save Location: " +Window.packSavePath);
		
		PackLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("selecting new pack location");
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setCurrentDirectory(new File("C:\\Users\\" +System.getProperty("user.name") +"\\Desktop")); // sets current directory
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // makes it so only folders are selectable files 1/2
				fileChooser.setAcceptAllFileFilterUsed(false); // makes it so only folders are selectable files 2/2

				int response = fileChooser.showOpenDialog(null); // select file to open
				if (response == JFileChooser.APPROVE_OPTION) {
					File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // this is the pack which we will port
					PackLocation.setText("Ported Pack Save Location: " +file.getAbsolutePath());
					System.out.println(file);
					pack = file.getAbsolutePath();
				}
			}
		});
		
		JButton btnPortedSkySave = new JButton("Ported Sky Save Location: " +Window.skySavePath);
		btnPortedSkySave.setBounds(29, 160, 538, 32);
		btnPortedSkySave.setText("Ported Sky Save Location: " +Window.skySavePath);
		btnPortedSkySave.setBorderPainted(false);
		btnPortedSkySave.setFocusPainted(true);
		dialog.getContentPane().add(btnPortedSkySave);
		
		btnPortedSkySave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("selecting new sky location");
				JFileChooser fileChooser = new JFileChooser();

				fileChooser.setCurrentDirectory(new File("C:\\Users\\" +System.getProperty("user.name") +"\\Desktop")); // sets current directory
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // makes it so only folders are selectable files 1/2
				fileChooser.setAcceptAllFileFilterUsed(false); // makes it so only folders are selectable files 2/2

				int response = fileChooser.showOpenDialog(null); // select file to open
				if (response == JFileChooser.APPROVE_OPTION) {
					File file = new File(fileChooser.getSelectedFile().getAbsolutePath()); // this is the pack which we will port
					btnPortedSkySave.setText("Ported Sky Save Location: " +file.getAbsolutePath());
					System.out.println(file);
					sky = file.getAbsolutePath();
				}
			}
		});
		
		txtpnSaved.setEditable(false);
		txtpnSaved.setText("Saved!");
		txtpnSaved.setBackground(new Color(51, 51, 51));
		txtpnSaved.setBounds(327, 218, 42, 20);
		txtpnSaved.setVisible(false);
		dialog.getContentPane().add(txtpnSaved);
		
		JTextPane textPane = new JTextPane();
		textPane.setBackground(new Color(204, 204, 204));
		textPane.setBounds(29, 11, 538, 47);
		File config = new File(System.getenv("APPDATA") +"\\Swim Services\\swim_pack.config");
		textPane.setText("Config file location: (DOES NOT CHANGE)\n" + config.getAbsolutePath());
		textPane.setEditable(false);
		dialog.getContentPane().add(textPane);
		
		JButton btnResetToDefault = new JButton("Reset to Default");
		btnResetToDefault.setBounds(29, 215, 111, 23);
		btnResetToDefault.setBorderPainted(false);
		btnResetToDefault.setFocusPainted(true);
		dialog.getContentPane().add(btnResetToDefault);
		
		JButton openPack = new JButton("Pack Folder");
		openPack.setFont(new Font("Tahoma", Font.PLAIN, 11));
		openPack.setBounds(146, 215, 89, 23);
		openPack.setBorderPainted(false);
		openPack.setFocusPainted(true);
		dialog.getContentPane().add(openPack);
		
		openPack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + pack);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JButton openSky = new JButton("Skies");
		openSky.setFont(new Font("Tahoma", Font.PLAIN, 11));
		openSky.setBounds(242, 215, 76, 23);
		openSky.setBorderPainted(false);
		openSky.setFocusPainted(true);
		dialog.getContentPane().add(openSky);
		
		openSky.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("explorer.exe /select," + sky);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		btnResetToDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chckbxNewCheckBox.setSelected(true);
				File home = FileSystemView.getFileSystemView().getHomeDirectory();
				String desktop = home.getAbsolutePath();
				String defaultCube = desktop + "\\overworld_cubemap";
				pack = desktop;
				sky = defaultCube;
				PackLocation.setText("Ported Pack Save Location: " +pack);
				btnPortedSkySave.setText("Ported Sky Save Location: " +sky);
			}
		});
		
		dialog.setVisible(true);
	}
}
