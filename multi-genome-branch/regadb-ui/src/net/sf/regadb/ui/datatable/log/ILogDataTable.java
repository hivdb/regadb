package net.sf.regadb.ui.datatable.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.file.FileUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class ILogDataTable implements IDataTable<File> {
    private static WString[] colNames_ = {
        WString.tr("dataTable.log.colName.date"),
        WString.tr("dataTable.log.colName.name"),
        WString.tr("dataTable.log.colName.size")};
    
    private static int[] colWidths = {20,60,20};

    private static Comparator<File>[] comparators_ = new Comparator[]{ new FileDateComparator(), new FileNameComparator(), new FileSizeComparator() };
    private static boolean[] sortable_ = {true, true, true};

    public WString[] getColNames() {
        return colNames_;
    }
    
    
    public List<File> getDataBlock(Transaction t, int startIndex, int amountOfRows,
            int sortIndex, boolean isAscending) {
        
        List<File> list = null;
        File dir = getLogDir();
        if(exists(dir)){
            list = sort(dir.listFiles(),startIndex,amountOfRows,sortIndex,isAscending);
        }
        
        return list;
    }
    
    private File getLogDir(){
        File f =  RegaDBSettings.getInstance().getInstituteConfig().getLogDir();
        if (f!= null && !f.exists()) {
        	f.mkdirs();
        }
        return f;
    }
    
    private boolean exists(File f){
        if(f != null && f.exists())
            return true;
        else
            return false;
    }
    
    protected List<File> sort(File[] files, int startIndex, int amountOfRows, int sortIndex, boolean isAscending){
        if(files.length > 0){
            Comparator<File> comparator;
            if(isAscending){
                comparator = comparators_[sortIndex];
            }
            else{
                comparator = new InverseComparator<File>(comparators_[sortIndex]);
            }
            Arrays.sort(files, comparator);
            List<File> list = Arrays.asList(files);
            int endIndex = java.lang.Math.min(startIndex+amountOfRows,files.length);
            return list.subList(startIndex, endIndex);
        }
        else
            return new ArrayList<File>();
    }

    public long getDataSetSize(Transaction t) {
        File dir = getLogDir();
        if(exists(dir)){
            return dir.list().length;
        }
        return 0;
    }

    public String[] getFieldNames() {
        return null;
    }

    public IFilter[] getFilters() {
        return null;
    }

    public String[] getRowData(File type) {
        String data[] = new String[3];
        
        if(exists(type)){
            data[0] = getFormattedFileDate(type.lastModified());
            data[1] = getFormattedFileName(type.getName());
            data[2] = getFormattedFileSize(type.length());
        }
        
        return data;
    }
    
    private String getFormattedFileDate(long timestamp){
        return DateUtils.format(new Date(timestamp));
    }
    
    private String getFormattedFileName(String filename){
        return filename;
    }
    
    private String getFormattedFileSize(long fs){
        return FileUtils.getHumanReadableFileSize(fs);
    }

    public void init(Transaction t) {
    }

    public void selectAction(File selectedItem) {
        RegaDBMain.getApp().getTree().getTreeContent().logSelectedItem.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().logSelectedItem.expand();
        RegaDBMain.getApp().getTree().getTreeContent().logSelectedItem.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().logView.selectNode();
    }

    public boolean[] sortableFields() {
        return sortable_;
    }
    
    
    //Comparator classes for sorting
    
    private static class FileDateComparator implements Comparator<File>{
        public int compare(File o1, File o2) {
            if(o1.lastModified() == o2.lastModified())
                return 0;
            if(o1.lastModified() < o2.lastModified())
                return -1;
            return 1;
        }
    }
    private static class FileNameComparator implements Comparator<File>{
        public int compare(File o1, File o2) {
            return o1.compareTo(o2);
        }
    }
    private static class FileSizeComparator implements Comparator<File>{
        public int compare(File o1, File o2) {
            if(o1.length() == o2.length())
                return 0;
            if(o1.length() < o2.length())
                return -1;
            return 1;
        }
    }
    
    private class InverseComparator<T> implements Comparator<T>{
        private Comparator<T> comparator;
        
        public InverseComparator(Comparator<T> comparator){
            this.comparator = comparator;
        }
        
        public int compare(T o1, T o2) {
            return -comparator.compare(o1,o2);
        }
    }

	public int[] getColumnWidths() {
		return colWidths;
	}

	public String[] getRowTooltips(File type) {
		return null;
	}
}
