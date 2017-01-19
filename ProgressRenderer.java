import javax.swing.table.*;

import java.awt.Component;

import javax.swing.*;

class ProgressRenderer extends JProgressBar implements TableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) 
	{
		this.setValue((int)((Float)value).floatValue());
		return (this);
	}
	
	public ProgressRenderer(int min,int max)
	{
		super(min,max);
	}
}
