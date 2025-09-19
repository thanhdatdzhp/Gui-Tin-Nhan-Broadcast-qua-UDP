import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UDPReceiverLTM extends JFrame {
    private final DefaultTableModel modelMsg = new DefaultTableModel(
            new Object[]{"⏱ Thời gian", "🌐 IP nguồn", "🔌 Cổng", "💬 Nội dung"}, 0) {
        @Override public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tableMsg = new JTable(modelMsg);

    private final DefaultTableModel modelFile = new DefaultTableModel(
            new Object[]{"📄 Tệp", "Kích thước", "Đã nhận", "Tiến độ"}, 0) {
        @Override public boolean isCellEditable(int r, int c){ return false; }
    };
    private final JTable tableFile = new JTable(modelFile);

    private final Theme.PillButton btnToggle = new Theme.PillButton("▶ Bắt đầu lắng nghe", Theme.GREEN);
    private final Theme.PillButton btnClear  = new Theme.PillButton("🗑 Xóa", new Color(149, 165, 166));
    private final Theme.PillButton btnExport = new Theme.PillButton("💾 Xuất CSV", Theme.ORANGE);
    private final Theme.PillButton btnPlay   = new Theme.PillButton("▶ Phát tệp", Theme.BLUE);   // << NÚT MỚI

    private final JLabel status = Theme.statusBar("Đã dừng.");
    private final JSpinner spPort = new JSpinner(new SpinnerNumberModel(Config.DEFAULT_PORT, 1, 65535, 1));

    private final Theme.PillButton btnChooseDir = new Theme.PillButton("Thư mục lưu…", Theme.INDIGO);
    private final JCheckBox chkAutoPlayWav = new JCheckBox("Phát tự động WAV", true);
    private File saveDir = new File(System.getProperty("user.home"), "Downloads");

    private volatile boolean running = false;
    private Thread worker;
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    private final Map<Integer, Integer> fileRows = new HashMap<>();
    private FileTransfer.Reassembler reasm;

    public UDPReceiverLTM(){
        super("Bộ nhận UDP – LTM");
        Theme.applySystemLaf();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel root = new JPanel(new BorderLayout(8,8));
        root.setBorder(new EmptyBorder(10,10,10,10));
        root.setBackground(Theme.BG_SOFT);
        setContentPane(root);

        // Thanh công cụ
        Theme.GradientPanel bar = new Theme.GradientPanel(Theme.INDIGO, Theme.TEAL);
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 10,8));
        bar.add(styledLabel("Cổng:"));
        ((JSpinner.DefaultEditor) spPort.getEditor()).getTextField().setColumns(6);
        bar.add(spPort);
        bar.add(btnToggle);
        bar.add(btnClear);
        bar.add(btnExport);
        bar.add(btnPlay);       // << thêm nút Phát tệp
        bar.add(btnChooseDir);
        bar.add(chkAutoPlayWav);
        root.add(bar, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        styleTable(tableMsg); styleTable(tableFile);
        tabs.addTab("Tin nhắn", new JScrollPane(tableMsg));
        tabs.addTab("Tệp nhận", new JScrollPane(tableFile));
        root.add(tabs, BorderLayout.CENTER);

        root.add(status, BorderLayout.SOUTH);

        btnToggle.addActionListener(new java.awt.event.ActionListener(){ @Override public void actionPerformed(java.awt.event.ActionEvent e){ toggle(); }});
        btnClear.addActionListener(new java.awt.event.ActionListener(){ @Override public void actionPerformed(java.awt.event.ActionEvent e){ modelMsg.setRowCount(0); }});
        btnExport.addActionListener(new java.awt.event.ActionListener(){ @Override public void actionPerformed(java.awt.event.ActionEvent e){ CsvExporter.exportTable(UDPReceiverLTM.this, tableMsg); }});
        btnChooseDir.addActionListener(new java.awt.event.ActionListener(){ @Override public void actionPerformed(java.awt.event.ActionEvent e){ chooseDir(); }});
        btnPlay.addActionListener(new java.awt.event.ActionListener(){ @Override public void actionPerformed(java.awt.event.ActionEvent e){ playSelectedFile(); }});
        btnPlay.setEnabled(false); // mặc định disable

        // Enable nút phát khi có chọn dòng & file tồn tại
        tableFile.getSelectionModel().addListSelectionListener(e -> updatePlayEnabled());

        reasm = new FileTransfer.Reassembler(saveDir, buildListener());

        setSize(980, 600);
        setLocationRelativeTo(null);
    }

    // Listener để cập nhật tiến độ & auto play
    private FileTransfer.ProgressListener buildListener() {
        return new FileTransfer.ProgressListener() {
            @Override public void onProgress(final int fileId, final String name, final long size, final long receivedBytes, final int receivedChunks, final int totalChunks) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        updateFileRow(fileId, name, size, receivedBytes);
                        updatePlayEnabled();
                    }
                });
            }
            @Override public void onCompleted(final int fileId, final File savedFile) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        updateFileRow(fileId, savedFile.getName(), savedFile.length(), savedFile.length());
                        status.setText("Đã nhận xong: " + savedFile.getAbsolutePath());
                        updatePlayEnabled();
                        if (chkAutoPlayWav.isSelected() && savedFile.getName().toLowerCase().endsWith(".wav")) {
                            FileTransfer.playWav(savedFile);
                        }
                    }
                });
            }
            @Override public void onMeta(final int fileId, final FileTransfer.Meta meta) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() { addFileRow(fileId, meta.fileName, meta.fileSize); }
                });
            }
        };
    }

    private void styleTable(JTable t){
        t.setFillsViewportHeight(true);
        t.setRowHeight(24);
        t.getTableHeader().setBackground(new Color(237, 242, 252));
        t.getTableHeader().setForeground(new Color(52, 73, 94));
    }

    private JLabel styledLabel(String t){
        JLabel l=new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    // ==== Phát tệp đã chọn ====
    private void playSelectedFile() {
        int row = tableFile.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn một tệp trong tab 'Tệp nhận'.", "Chưa chọn tệp", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File f = fileFromRow(row);
        if (f == null || !f.exists()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tệp: " + (f==null?"(null)":f.getAbsolutePath()), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // WAV phát nội bộ, định dạng khác mở bằng app mặc định
        if (f.getName().toLowerCase().endsWith(".wav")) {
            FileTransfer.playWav(f);
        } else {
            try {
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(f);
                else JOptionPane.showMessageDialog(this, "Không hỗ trợ mở tệp trên hệ thống này.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể mở tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private File fileFromRow(int row){
        if (row < 0 || row >= modelFile.getRowCount()) return null;
        String name = String.valueOf(modelFile.getValueAt(row, 0));
        return new File(saveDir, name);
    }

    private void updatePlayEnabled(){
        int row = tableFile.getSelectedRow();
        boolean ok = false;
        if (row >= 0) {
            File f = fileFromRow(row);
            ok = (f != null && f.exists());
        }
        btnPlay.setEnabled(ok);
    }

    private void chooseDir() {
        JFileChooser fc = new JFileChooser(saveDir);
        fc.setDialogTitle("Chọn thư mục lưu file");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveDir = fc.getSelectedFile();
            reasm.closeAll();
            reasm = new FileTransfer.Reassembler(saveDir, buildListener());
            status.setText("Thư mục lưu: " + saveDir.getAbsolutePath());
            updatePlayEnabled();
        }
    }

    private void addFileRow(int fileId, String name, long size){
        int row = modelFile.getRowCount();
        modelFile.addRow(new Object[]{name, size, 0, "0%"});
        fileRows.put(fileId, row);
    }
    private void updateFileRow(int fileId, String name, long size, long recBytes){
        Integer row = fileRows.get(fileId);
        if (row == null) return;
        modelFile.setValueAt(name, row, 0);
        modelFile.setValueAt(size, row, 1);
        modelFile.setValueAt(recBytes, row, 2);
        int pct = size==0?0:(int)Math.min(100, (recBytes*100L)/Math.max(1,size));
        modelFile.setValueAt(pct+"%", row, 3);
    }

    private void toggle(){
        if (running){
            running=false;
            btnToggle.setText("▶ Bắt đầu lắng nghe");
            btnToggle.setBg(Theme.GREEN);
            status.setText("Đang dừng…");
        } else {
            running=true;
            btnToggle.setText("⏹ Dừng");
            btnToggle.setBg(Theme.RED);
            final int port=((Number)spPort.getValue()).intValue();
            status.setText("Đang lắng nghe cổng " + port + " …");
            worker = new Thread(new Runnable() {
                @Override public void run() { listenLoop(port); }
            }, "udp-listener");
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void listenLoop(int port){
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            socket.setSoTimeout(Config.SOCKET_TIMEOUT_MS);

            byte[] buf = new byte[Config.UDP_BUFFER_SIZE];
            while (running){
                try{
                    DatagramPacket p = new DatagramPacket(buf, buf.length);
                    socket.receive(p);

                    int len = p.getLength();
                    byte[] data = new byte[len];
                    System.arraycopy(p.getData(), p.getOffset(), data, 0, len);

                    if (FileTransfer.isFilePacket(data, len)) {
                        try {
                            Object parsed = FileTransfer.parsePacket(data, len);
                            if (parsed instanceof FileTransfer.Meta) {
                                FileTransfer.Meta m = (FileTransfer.Meta) parsed;
                                reasm.onMeta(m);
                            } else {
                                Object[] arr = (Object[]) parsed;
                                int fileId = ((Integer) arr[0]).intValue();
                                int index  = ((Integer) arr[1]).intValue();
                                byte[] payload = (byte[]) arr[2];
                                if (index >= 0) reasm.onChunk(fileId, index, payload);
                                else reasm.onEnd(fileId);
                            }
                        } catch (IOException ignore) { }
                    } else {
                        String msg = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        String time = sdf.format(new Date());
                        String ip = p.getAddress().getHostAddress();
                        int rport = p.getPort();
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() { modelMsg.addRow(new Object[]{time, ip, rport, msg}); }
                        });
                    }
                } catch (IOException timeout) { /* kiểm tra cờ running */ }
            }
            SwingUtilities.invokeLater(new Runnable() { @Override public void run() { status.setText("Đã dừng."); }});
        } catch (Exception ex){
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    JOptionPane.showMessageDialog(UDPReceiverLTM.this, "Lỗi khi lắng nghe UDP: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    status.setText("Lỗi. Đã dừng.");
                    btnToggle.setText("▶ Bắt đầu lắng nghe");
                    btnToggle.setBg(Theme.GREEN);
                    running=false;
                }
            });
        } finally {
            reasm.closeAll();
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() { new UDPReceiverLTM().setVisible(true); }
        });
    }
}
