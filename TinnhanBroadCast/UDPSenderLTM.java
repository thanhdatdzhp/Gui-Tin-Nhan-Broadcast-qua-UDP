import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.prefs.Preferences;

public class UDPSenderLTM extends JFrame {
    private final JComboBox<String> cbHistory = new JComboBox<>();
    private final Theme.PillButton btnUse     = new Theme.PillButton("Dùng", Theme.AMBER);

    // Ô nhập nhanh
    private final Theme.RoundedTextField tfQuickMsg = new Theme.RoundedTextField();

    // Tham số mạng
    private final Theme.RoundedTextField tfIp  = new Theme.RoundedTextField(Config.DEFAULT_BROADCAST_IP);
    private final JSpinner spPort  = new JSpinner(new SpinnerNumberModel(Config.DEFAULT_PORT, 1, 65535, 1));

    // Tùy chọn tin nhắn
    private final JSpinner spEvery = new JSpinner(new SpinnerNumberModel(Config.DEFAULT_INTERVAL_MS, 1, 1_000_000, 1));
    private final JCheckBox chkEpoch = new JCheckBox("Gắn epoch", true);
    private final JCheckBox chkSeq   = new JCheckBox("Gắn số thứ tự", true);
    private final Theme.PillButton btnAuto = new Theme.PillButton("▶ Tự động", Theme.GREEN);
    private final Theme.PillButton btnSend = new Theme.PillButton("Gửi", Theme.BLUE);

    // --- GỬI TỆP ---
    private final JLabel lbChosenFile = new JLabel("Chưa chọn tệp");
    private final Theme.PillButton btnChooseFile = new Theme.PillButton("Chọn tệp…", Theme.INDIGO);
    private final Theme.PillButton btnSendFile = new Theme.PillButton("Gửi tệp", Theme.ORANGE);
    private final JSpinner spChunk = new JSpinner(new SpinnerNumberModel(1200, 512, 1400, 8));
    private final JSpinner spDelay = new JSpinner(new SpinnerNumberModel(2, 0, 50, 1));
    private File selectedFile = null;

    private final JLabel status = Theme.statusBar("Tự động đã dừng.");

    // Lịch sử
    private final Preferences prefs = Preferences.userNodeForPackage(UDPSenderLTM.class);
    private static final String PREF_HISTORY = "sender.history";
    private static final int MAX_HISTORY = 30;
    private final Deque<String> history = new ArrayDeque<>();

    private Timer autoTimer;
    private boolean autoOn = false;
    private long seq = 0;

    public UDPSenderLTM(){
        super("Bộ gửi UDP – LTM");
        Theme.applySystemLaf();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(10,10,10,10));
        root.setBackground(Theme.BG_SOFT);
        setContentPane(root);

        // Header gradient: Lịch sử
        Theme.GradientPanel header = new Theme.GradientPanel(Theme.TEAL, Theme.INDIGO);
        header.setLayout(new BorderLayout());
        JPanel headerInner = new JPanel(new BorderLayout(8,8));
        headerInner.setOpaque(false);
        headerInner.setBorder(new EmptyBorder(8,10,8,10));

        JLabel lbHistory = new JLabel("Lịch sử:");
        lbHistory.setForeground(Color.WHITE);
        lbHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbHistory.setEditable(true);
        headerInner.add(lbHistory, BorderLayout.WEST);
        headerInner.add(cbHistory, BorderLayout.CENTER);
        headerInner.add(btnUse, BorderLayout.EAST);
        header.add(headerInner, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // Card nhập liệu
        Theme.CardPanel card = new Theme.CardPanel();
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        tfQuickMsg.setPlaceholder("Nhập tin nhắn tại đây rồi nhấn Enter để gửi…");
        tfQuickMsg.setInnerPadding(new Insets(10, 12, 10, 12));
        addLabeled(card, gc, "Tin nhắn nhanh:", tfQuickMsg, 0);

        JPanel netRow = new JPanel(new GridBagLayout());
        netRow.setOpaque(false);
        JLabel ipLabel = new JLabel("IP quảng bá:");
        tfIp.setPlaceholder("VD: 255.255.255.255 hoặc 192.168.1.255");
        tfIp.setColumns(13);

        GridBagConstraints n = new GridBagConstraints();
        n.insets = new Insets(0,0,0,8); n.fill = GridBagConstraints.HORIZONTAL; n.weightx=1;
        netRow.add(ipLabel, noFillLeft());
        netRow.add(tfIp, n);
        netRow.add(new JLabel("Cổng:"), noFillLeft());
        ((JSpinner.DefaultEditor) spPort.getEditor()).getTextField().setColumns(6);
        netRow.add(spPort, noFillLeft());
        addLabeled(card, gc, null, netRow, 1);

        JPanel optRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        optRow.setOpaque(false);
        optRow.add(new JLabel("Chu kỳ (ms):"));
        ((JSpinner.DefaultEditor) spEvery.getEditor()).getTextField().setColumns(6);
        optRow.add(spEvery);
        optRow.add(chkEpoch);
        optRow.add(chkSeq);
        optRow.add(btnAuto);
        optRow.add(btnSend);
        addLabeled(card, gc, null, optRow, 2);

        // --- Khu vực gửi tệp ---
        JPanel filePanel = new JPanel(new GridBagLayout());
        filePanel.setOpaque(false);
        GridBagConstraints f = new GridBagConstraints();
        f.insets = new Insets(4,4,4,4);
        f.gridy=0; f.gridx=0; f.anchor=GridBagConstraints.WEST;
        filePanel.add(new JLabel("Tệp:"), f);

        f.gridx=1; f.weightx=1; f.fill=GridBagConstraints.HORIZONTAL;
        lbChosenFile.setForeground(new Color(70,70,80));
        filePanel.add(lbChosenFile, f);

        f.gridx=2; f.weightx=0; f.fill=GridBagConstraints.NONE;
        filePanel.add(btnChooseFile, f);

        f.gridy=1; f.gridx=0;
        filePanel.add(new JLabel("Kích thước gói (bytes):"), f);
        f.gridx=1;
        ((JSpinner.DefaultEditor) spChunk.getEditor()).getTextField().setColumns(6);
        filePanel.add(spChunk, f);

        f.gridx=2;
        filePanel.add(new JLabel("Độ trễ mỗi gói (ms):"), f);
        f.gridx=3;
        ((JSpinner.DefaultEditor) spDelay.getEditor()).getTextField().setColumns(4);
        filePanel.add(spDelay, f);

        f.gridy=2; f.gridx=0; f.gridwidth=4; f.anchor=GridBagConstraints.WEST;
        filePanel.add(btnSendFile, f);

        addLabeled(card, gc, "Gửi tệp (radio/audio):", filePanel, 3);

        root.add(card, BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH);

        // Sự kiện
        btnUse.addActionListener(this::onUseHistory);
        btnSend.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(ActionEvent e) { sendFromUI(); }});
        btnAuto.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(ActionEvent e) { onToggleAuto(); }});
        tfQuickMsg.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(ActionEvent e) { sendFromUI(); }});

        btnChooseFile.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(ActionEvent e) { chooseFile(); }});
        btnSendFile.addActionListener(new java.awt.event.ActionListener() { @Override public void actionPerformed(ActionEvent e) { sendSelectedFile(); }});

        loadHistory();

        setSize(880, 430);
        setLocationRelativeTo(null);
    }

    // Helpers layout
    private static GridBagConstraints noFillLeft(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,0,0,8);
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        return c;
    }
    private static void addLabeled(Theme.CardPanel card, GridBagConstraints gc, String label, JComponent comp, int row){
        gc.gridx=0; gc.gridy=row;
        if (label != null) {
            JLabel lb = new JLabel(label);
            lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lb.setForeground(new Color(60, 60, 70));
            card.add(lb, gc);
            gc.gridy=row+1;
        }
        card.add(comp, gc);
    }

    // ==== Tin nhắn ====
    private void onUseHistory(ActionEvent e){
        Object sel = cbHistory.getSelectedItem();
        if (sel != null) addToHistory(sel.toString());
    }

    private void onToggleAuto(){
        if (!autoOn) {
            int interval = ((Number) spEvery.getValue()).intValue();
            if (interval < 1) interval = 1;
            autoTimer = new Timer(interval, new java.awt.event.ActionListener() {
                @Override public void actionPerformed(ActionEvent ev) { sendOne(getMessageToSend()); }
            });
            autoTimer.start();
            autoOn = true;
            btnAuto.setText("⏹ Dừng");
            btnAuto.setBg(Theme.RED);
            status.setText("Tự động đang chạy…");
        } else {
            if (autoTimer != null) autoTimer.stop();
            autoOn = false;
            btnAuto.setText("▶ Tự động");
            btnAuto.setBg(Theme.GREEN);
            status.setText("Tự động đã dừng.");
        }
    }

    private String getMessageToSend(){
        String quick = tfQuickMsg.getText().trim();
        if (!quick.isEmpty()) return quick;
        Object o = cbHistory.getEditor().getItem();
        return o == null ? "" : o.toString();
    }

    private void sendFromUI(){
        String base = getMessageToSend();
        if (base.isEmpty()){
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tin nhắn.", "Thiếu nội dung", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sendOne(base);
        tfQuickMsg.setText("");
    }

    private void sendOne(String base){
        String ip = tfIp.getText().trim();
        int port = ((Number) spPort.getValue()).intValue();
        String toSend = buildMessage(base);
        try (DatagramSocket socket = new DatagramSocket()){
            socket.setBroadcast(true);
            DatagramPacket pkt = FileTransfer.pkt(toSend.getBytes(java.nio.charset.StandardCharsets.UTF_8), ip, port);
            socket.send(pkt);
            status.setText("Đã gửi tin nhắn tới " + ip + ":" + port);
            addToHistory(base);
        } catch (Exception ex){
            JOptionPane.showMessageDialog(this, "Lỗi gửi UDP: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            status.setText("Gửi lỗi.");
        }
    }

    private String buildMessage(String base){
        String msg = base;
        if (chkEpoch.isSelected()) msg += " | epoch=" + Instant.now().toEpochMilli();
        if (chkSeq.isSelected())   msg += " | #" + (seq++);
        return msg;
    }

    private void addToHistory(String text){
        text=text.trim(); if(text.isEmpty()) return;
        history.remove(text); history.addFirst(text);
        while (history.size()>MAX_HISTORY) history.removeLast();
        cbHistory.removeAllItems(); for (String s: history) cbHistory.addItem(s);
        cbHistory.getEditor().setItem(text); saveHistory();
    }

    private void loadHistory(){
        String saved=prefs.get(PREF_HISTORY,"");
        if(!saved.isBlank()) {
            String[] arr = saved.split("\n");
            for(String s: arr) if(!s.isBlank()) history.addLast(s);
        } else {
            history.add("Hello Broadcast");
        }
        cbHistory.removeAllItems(); for(String s:history) cbHistory.addItem(s);
        cbHistory.setSelectedIndex(0);
    }

    private void saveHistory(){
        StringBuilder sb=new StringBuilder();
        for(String s:history) sb.append(s).append('\n');
        prefs.put(PREF_HISTORY, sb.toString());
    }

    // ==== Gửi tệp ====
    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn tệp để gửi");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            lbChosenFile.setText(selectedFile.getName() + " (" + selectedFile.length() + " bytes)");
        }
    }

    private void sendSelectedFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa chọn tệp.", "Thiếu tệp", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final String ip = tfIp.getText().trim();
        final int port = ((Number) spPort.getValue()).intValue();
        final int chunk = ((Number) spChunk.getValue()).intValue();
        final int delay = ((Number) spDelay.getValue()).intValue();

        new Thread(new Runnable() {
            @Override public void run() {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setBroadcast(true);

                    long size = selectedFile.length();
                    int totalChunks = (int) Math.ceil(size * 1.0 / chunk);
                    int fileId = FileTransfer.randomFileId();

                    // META
                    socket.send(FileTransfer.pkt(FileTransfer.buildMeta(fileId, selectedFile.getName(), size, chunk, totalChunks), ip, port));

                    // CHUNKS
                    byte[] buf = new byte[chunk];
                    FileInputStream fis = new FileInputStream(selectedFile);
                    try {
                        int index = 0;
                        int read;
                        while ((read = fis.read(buf)) != -1) {
                            byte[] pkt = FileTransfer.buildChunk(fileId, index, buf, 0, read);
                            socket.send(FileTransfer.pkt(pkt, ip, port));
                            index++;
                            if (delay > 0) Thread.sleep(delay);
                        }
                    } finally {
                        fis.close();
                    }

                    // END
                    socket.send(FileTransfer.pkt(FileTransfer.buildEnd(fileId), ip, port));

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            status.setText("Đã gửi tệp: " + selectedFile.getName() + " (" + size + " bytes)");
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override public void run() {
                            JOptionPane.showMessageDialog(UDPSenderLTM.this, "Lỗi gửi tệp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
        }, "file-sender").start();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() { new UDPSenderLTM().setVisible(true); }
        });
    }
}
