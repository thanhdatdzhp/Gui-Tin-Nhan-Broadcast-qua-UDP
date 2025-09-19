import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public final class Theme {
    private Theme(){}

    // Màu chủ đạo
    public static final Color INDIGO = new Color(108, 92, 231);
    public static final Color TEAL   = new Color(32, 201, 151);
    public static final Color BLUE   = new Color(52, 152, 219);
    public static final Color ORANGE = new Color(246, 114, 62);
    public static final Color RED    = new Color(231, 76, 60);
    public static final Color GREEN  = new Color(46, 204, 113);
    public static final Color AMBER  = new Color(255, 179, 0);   // nút "Dùng"
    public static final Color BG_SOFT= new Color(248, 249, 252);
    public static final Color BORDER = new Color(210, 217, 226);
    public static final Color BORDER_FOCUS = new Color(120, 160, 255);

    public static void applySystemLaf() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
    }

    // Panel nền gradient ngang
    public static class GradientPanel extends JPanel {
        private final Color c1, c2;
        public GradientPanel(Color c1, Color c2) { this.c1=c1; this.c2=c2; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            var p=new GradientPaint(0,0,c1,getWidth(),0,c2);
            g2.setPaint(p);
            g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Nút bo góc có viền trắng để nổi bật
    public static class PillButton extends JButton {
        private Color bg = BLUE;
        public PillButton(String text, Color bg) { super(text); setBg(bg); basic(); }
        private void basic() {
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(new EmptyBorder(8,16,8,16));
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        public void setBg(Color c){ this.bg=c; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 16;
            int w = getWidth(), h = getHeight();
            g2.setColor(bg);
            g2.fillRoundRect(0,0,w,h,arc,arc);
            // viền trắng mờ
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(255,255,255,220));
            g2.drawRoundRect(1,1,w-2,h-2,arc,arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // TextField bo góc + placeholder + highlight viền khi focus
    public static class RoundedTextField extends JTextField {
        private String placeholder = "";
        private int arc = 14;
        private Insets insets = new Insets(8, 10, 8, 10);
        private Color borderColor = BORDER;

        public RoundedTextField() { this(""); }
        public RoundedTextField(String text) {
            super(text);
            setOpaque(false);
            setBorder(new EmptyBorder(insets));
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { borderColor = BORDER_FOCUS; repaint(); }
                @Override public void focusLost(FocusEvent e) { borderColor = BORDER; repaint(); }
            });
        }
        public void setPlaceholder(String ph) { this.placeholder = ph; repaint(); }
        public void setArc(int arc){ this.arc = arc; repaint(); }
        public void setInnerPadding(Insets i){ this.insets = i; setBorder(new EmptyBorder(i)); repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0,0,getWidth(),getHeight(), arc, arc);
            g2.setColor(borderColor);
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1, arc, arc);
            g2.dispose();
            super.paintComponent(g);

            if (!hasFocus() && getText().isEmpty() && !placeholder.isEmpty()) {
                Graphics2D g3 = (Graphics2D) g.create();
                g3.setColor(new Color(150, 160, 170));
                g3.setFont(getFont().deriveFont(Font.ITALIC));
                Insets ins = getInsets();
                g3.drawString(placeholder, ins.left, getHeight()/2 + g3.getFontMetrics().getAscent()/2 - 4);
                g3.dispose();
            }
        }
    }

    // Panel "card" bo góc có bóng nhẹ
    public static class CardPanel extends JPanel {
        public CardPanel() {
            setOpaque(false);
            setBorder(new EmptyBorder(10, 12, 10, 12));
            setLayout(new GridBagLayout());
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int arc = 18;
            g2.setColor(new Color(0,0,0,30));
            g2.fillRoundRect(3,5,getWidth()-6,getHeight()-6, arc, arc);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Thanh trạng thái
    public static JLabel statusBar(String text){
        JLabel lb=new JLabel(text);
        lb.setOpaque(true);
        lb.setForeground(Color.WHITE);
        lb.setBackground(BLUE);
        lb.setBorder(new EmptyBorder(8,12,8,12));
        lb.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lb;
    }
}
