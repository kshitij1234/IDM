import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.*;

class DownloadsTableModel extends AbstractTableModel implements Observer{

	private static final String columnNames[]={"URL","Size","Progress","Status"};
	
	private static final Class columnClasses[]={String.class,String.class,ProgressRenderer.class,String.class};
	
	private ArrayList<Download> downloadList=new ArrayList<Download>();
	
	@Override
	public void update(Observable arg0, Object arg1) {
		int index=downloadList.indexOf(arg0);
		this.fireTableRowsUpdated(index, index);
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return downloadList.size();
	}

	@Override
	public Object getValueAt(int r, int c) {
		
		Download d=downloadList.get(r);
		
		switch(c)
		{
		case 0: return d.getURL();
		
		case 1: return (d.getSize()==-1)?"":d.getSize();
		
		case 2: return new Float(d.getProgress());
		
		case 3: return Download.statuses[d.getStatus()];
		}
		return "";
	}
	
	public void addDownlod(Download d)
	{
		d.addObserver(this);
		downloadList.add(d);
		this.fireTableRowsInserted(getRowCount()-1,getRowCount()-1);
	}
	
	
	public void removeDownload(int r)
	{
		downloadList.remove(r);
		this.fireTableRowsDeleted(r, r);
	}

	public Download getDownload(int r)
	{
		return downloadList.get(r);
	}
	
	public String getColumnName(int c)
	{
		return columnNames[c];
	}
	
	public Class<?> getColumnClass(int c)
	{
		return columnClasses[c];
	}
	
}
