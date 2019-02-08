import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.IntConsumer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SwingClock {

	public static void main(String[] args) {
		new SwingClock();
	}

	Simple s = new Simple();

	public SwingClock() {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("title");
		f.setSize(500, 500);
		f.setLocationRelativeTo(null);
		f.setLayout(new BorderLayout(5,5));
		f.add(s, BorderLayout.CENTER);

		JPanel manual = new JPanel(false);
		BoxLayout l = new BoxLayout(manual, BoxLayout.Y_AXIS);
		manual.setLayout(l);
		JSlider h = add(manual, "H: ", v -> s.hour = v, true);
		JSlider m = add(manual, "M: ", v -> s.minute = v, false);
		JSlider sec = add(manual, "S: ", v -> s.second = v, false);

		Timer timer = new Timer(1000, e -> {
			s.update();
			s.repaint();
		});

		JButton mbtn = new JButton("Manual");
		JButton nbtn = new JButton("Natural");

		mbtn.addActionListener(e -> {
			timer.stop();
			f.add(manual, BorderLayout.SOUTH);
			h.setValue(s.hour);
			m.setValue(s.minute);
			sec.setValue(s.second);

			f.remove(mbtn);
			f.add(nbtn, BorderLayout.NORTH);
			f.revalidate();
			f.repaint();
		});
		
		f.add(mbtn, BorderLayout.NORTH);
		
		nbtn.addActionListener(e -> {
			timer.start();
			f.remove(nbtn);
			f.remove(manual);
			f.add(mbtn, BorderLayout.NORTH);
			
			f.revalidate();
			f.repaint();
		});

		timer.start();
		SwingUtilities.invokeLater(() -> f.setVisible(true));
	}
	
	private JSlider add(JPanel parent, String title, IntConsumer setter, boolean isHour) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5), false);
		panel.add(new JLabel(title));
		Label value = new Label();
		JSlider s = isHour ? new JSlider(0, 24, 0) : new JSlider(0, 60, 0);
		panel.add(s);
		panel.add(value);
		s.addChangeListener(e -> {
			int n = s.getValue();
			setter.accept(n);
			value.setText(Integer.toString(n));
			this.s.repaint();
		});
		parent.add(panel);
		return s;
	}

	class Simple extends JLabel {
		final int radius = 100;
		private final double[] radians = new double[61];
		{
			for (int i = 0, n = 0; i <= 360; i+=6, n++) 
				radians[n] = Math.toRadians(180 - i);
		}

		int hour, minute, second;
		int w, h;
		@Override
		protected void paintComponent(Graphics g) {
			w = getWidth();
			h = getHeight();
			int x = getWidth()/2 - radius;
			int y = getHeight()/2 - radius;

			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawArc(x, y, radius*2, radius*2, 0, 360);

			// update();
			draw(g2, second, 5, Color.red, 0.5f);
			draw(g2, minute, 15, Color.blue, 2f);
			draw(g2, (hour%12)*5, 35, Color.green, 4f);
		}
		void update() {
			LocalDateTime t = LocalDateTime.now(); 
			hour = t.getHour();
			minute = t.getMinute();
			second = t.getSecond();
		}
		private void draw(Graphics2D g2, int index, int reduceLineSize, Color color, float width) {
			g2.setStroke(new BasicStroke(width));
			double rad = radians[index];
			g2.setColor(color);
			g2.drawLine(w/2, h/2, (int)(w/2+(radius - reduceLineSize)*Math.sin(rad)), (int)(h/2+(radius - reduceLineSize)*Math.cos(rad)));
		}
	} 
}
