import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;

class DownloadManager extends JFrame implements Observer 
{

	@Override
	public void update(Observable arg0, Object arg1) {
		if (currentlyselected != null && currentlyselected.equals(arg0))
			updateButtons();
		
	}
	private JTextField addurl;
	private DownloadsTableModel tablemodel;
	private JTable table;
	private JButton pause,resume,cancel,clear;
	private Download currentlyselected;
	private boolean clearing;
	
	public DownloadManager()
	{
		this.setTitle("Download Manager");
		this.setSize(640, 480);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				actionExit();
			}
		});
		
		JMenuBar menu=new JMenuBar();
		JMenu filemenu=new JMenu("File");
		filemenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem exit=new JMenuItem("Exit",KeyEvent.VK_X);
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionExit();
			}
		});
		filemenu.add(exit);
		menu.add(filemenu);
		this.setJMenuBar(menu);
		
		JPanel add=new JPanel();
		addurl=new JTextField(30);
		add.add(addurl);
		JButton addButton=new JButton("Add Download");
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionAdd();
			}
			
		});
		add.add(addButton);
		
		tablemodel=new DownloadsTableModel();
		table=new JTable(tablemodel);
		
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			
			public void valueChanged(ListSelectionEvent e)
			{
				tableSelectionChanged();
			}
		});
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		ProgressRenderer pr=new ProgressRenderer(0,100);
		pr.setStringPainted(true);
		table.setDefaultRenderer(JProgressBar.class, pr);
		
		table.setRowHeight((int)pr.getPreferredSize().getHeight());
		
		
		JPanel tablepanel=new JPanel();
		tablepanel.setLayout(new BorderLayout());
		tablepanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
		
		tablepanel.add(new JScrollPane(table),BorderLayout.CENTER);
		
		JPanel buttonspanel=new JPanel();
		
		pause=new JButton("Pause");
		pause.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionPause();
			}
		});
		pause.setEnabled(false);
		
		resume=new JButton("Resume");
		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionResume();
			}
		});
		resume.setEnabled(false);
		
		cancel=new JButton("Cancel");
		cancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionCancel();
			}
		});
		cancel.setEnabled(false);
		
		clear=new JButton("Clear");
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				actionClear();
			}
		});
		clear.setEnabled(false);
		
		buttonspanel.add(pause);
		buttonspanel.add(resume);
		buttonspanel.add(cancel);
		buttonspanel.add(clear);
		
		this.getContentPane().setLayout(new BorderLayout());
		
		getContentPane().add(add,BorderLayout.NORTH);
		getContentPane().add(tablepanel,BorderLayout.CENTER);
		getContentPane().add(buttonspanel,BorderLayout.SOUTH);
	}
	
	
	private void actionExit()
	{
		System.exit(0);
	}
	
	private void actionAdd()
	{
		URL verified=verifyUrl(addurl.getText());
		
		if(verified!=null)
		{
			tablemodel.addDownlod(new Download(verified));
			addurl.setText("");
		}
		
		else
		{
			JOptionPane.showMessageDialog(this,"Invalid Download URL", "Error",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private URL verifyUrl(String s)
	{
		if(!s.toLowerCase().startsWith("http://"))
		{
			return null;
		}
		URL url=null;
		try
		{
			url=new URL(s);
		}
		catch(Exception e)
		{
			return null;
		}
		
		if(url.getFile().length()<2)
			return null;
		return url;
	}
	
	private void tableSelectionChanged()
	{
		if(currentlyselected!=null)
		{
			currentlyselected.deleteObserver(DownloadManager.this);
		}
		
		if(!clearing && table.getSelectedRow()>-1)
		{
			currentlyselected=tablemodel.getDownload(table.getSelectedRow());
			currentlyselected.addObserver(DownloadManager.this);
			updateButtons();
		}
	}
	
	private void actionPause()
	{
		currentlyselected.pause();
		updateButtons();
	}
	
	private void actionResume()
	{
		currentlyselected.resume();
		updateButtons();
	}
	
	private void actionCancel()
	{
		currentlyselected.cancel();
		updateButtons();
	}
	
	private void actionClear()
	{
		clearing=true;
		tablemodel.removeDownload(table.getSelectedRow());
		clearing=false;
		currentlyselected=null;
		updateButtons();
	}

	private void updateButtons()
	{
		if (currentlyselected != null) {
			int status = currentlyselected.getStatus();
			switch (status) {
			case 0:
			pause.setEnabled(true);
			resume.setEnabled(false);
			cancel.setEnabled(true);
			clear.setEnabled(false);
			break;
			case 1:
			pause.setEnabled(false);
			resume.setEnabled(true);
			cancel.setEnabled(true);
			clear.setEnabled(false);
			break;
			case 4:
			pause.setEnabled(false);
			resume.setEnabled(true);
			cancel.setEnabled(false);
			clear.setEnabled(true);
			break;
			default: // COMPLETE or CANCELLED
			pause.setEnabled(false);
			resume.setEnabled(false);
			cancel.setEnabled(false);
			clear.setEnabled(true);
			}
			} else {
			// No download is selected in table.
			pause.setEnabled(false);
			resume.setEnabled(false);
			cancel.setEnabled(false);
			clear.setEnabled(false);
			}
	}
	
	public static void main(String arg[])
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			DownloadManager manager = new DownloadManager();
			manager.setVisible(true);
			}
			});
	}
}
