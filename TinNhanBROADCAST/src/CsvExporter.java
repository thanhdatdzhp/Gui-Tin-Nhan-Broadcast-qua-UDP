import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;

public final class CsvExporter {
    private CsvExporter(){}
    public static void exportTable(JFrame parent, JTable table) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Xuất CSV");
        if (fc.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
        var file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(), file.getName()+".csv");
        }
        try (var bw = new BufferedWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
            TableModel tm = table.getModel();
            for (int c=0;c<tm.getColumnCount();c++){ if(c>0)bw.write(','); bw.write(esc(tm.getColumnName(c))); }
            bw.write("\n");
            for (int r=0;r<tm.getRowCount();r++){
                for (int c=0;c<tm.getColumnCount();c++){ if(c>0)bw.write(','); Object v=tm.getValueAt(r,c); bw.write(esc(v==null?"":String.valueOf(v))); }
                bw.write("\n");
            }
            JOptionPane.showMessageDialog(parent, "Đã xuất: "+file.getAbsolutePath(), "Xuất CSV", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Lỗi xuất CSV: "+ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static String esc(String s){ boolean q=s.contains(",")||s.contains("\"")||s.contains("\n"); s=s.replace("\"","\"\""); return q?"\""+s+"\"":s; }
}
