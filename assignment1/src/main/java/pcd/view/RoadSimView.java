package pcd.view;

import java.util.List;

import pcd.engine.AbstractAgent;
import pcd.engine.AbstractEnvironment;
import pcd.engine.AbstractSimulation;
import pcd.engine.SimulationListener;
import pcd.base.*;

import javax.swing.*;
import java.awt.*;

public class RoadSimView extends JFrame implements SimulationListener {

	private final RoadSimViewPanel panel;
	private static final int CAR_DRAW_SIZE = 10;

	public RoadSimView(AbstractSimulation simulation) {
		super("RoadSim View");
		setSize(1500, 600);

		panel = new RoadSimViewPanel(1500, 600);
		panel.setSize(1500, 600);

		// Pannello per i pulsanti
		JPanel buttonPanel = new JPanel();
		JButton startButton = new JButton("Start");
		JButton stopButton = new JButton("Stop");

		stopButton.addActionListener(e -> {
			simulation.stop();
		});

		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);

		// Pannello per il numero di passaggi
		JPanel stepsPanel = new JPanel();
		JLabel stepsLabel = new JLabel("Number of Steps:");
		JTextField stepsNumber = new JTextField(10);
		stepsPanel.add(stepsLabel);
		stepsPanel.add(stepsNumber);

		startButton.addActionListener(e -> {

			String stepsText = stepsNumber.getText();
			int steps = Integer.parseInt(stepsText);
			if (steps > 0) {
				simulation.elaborateSteps(steps);
			}
		});

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BorderLayout());
		controlPanel.add(buttonPanel, BorderLayout.CENTER); // Aggiungi i pulsanti in alto
		controlPanel.add(stepsPanel, BorderLayout.AFTER_LAST_LINE); // Aggiungi il pannello dei passaggi al centro

		JPanel cp = new JPanel();
		LayoutManager layout = new BorderLayout();
		cp.setLayout(layout);
		cp.add(BorderLayout.CENTER, panel);
		cp.add(BorderLayout.NORTH, controlPanel); // Aggiungi il pannello di controllo con pulsanti e passaggi
		setContentPane(cp);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void display() {
		SwingUtilities.invokeLater(() -> {
			this.setVisible(true);
		});
	}

	@Override
	public void notifyInit(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyStepDone(int t, List<AbstractAgent> agents, AbstractEnvironment env) {
		var e = ((RoadsEnv) env);
		panel.update(e.getRoads(), e.getAgentInfo(), e.getTrafficLights());
	}

	static class RoadSimViewPanel extends JPanel {

		List<CarAgentInfo> cars;
		List<Road> roads;
		List<TrafficLight> sems;

		public RoadSimViewPanel(int w, int h) {
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.clearRect(0, 0, this.getWidth(), this.getHeight());

			if (roads != null) {
				for (var r : roads) {
					g2.drawLine((int) r.getFrom().x(), (int) r.getFrom().y(), (int) r.getTo().x(), (int) r.getTo().y());
				}
			}

			if (sems != null) {
				for (var s : sems) {
					if (s.isGreen()) {
						g.setColor(new Color(0, 255, 0, 255));
					} else if (s.isRed()) {
						g.setColor(new Color(255, 0, 0, 255));
					} else {
						g.setColor(new Color(255, 255, 0, 255));
					}
					g2.fillRect((int) (s.getPos().x() - 5), (int) (s.getPos().y() - 5), 10, 10);
				}
			}

			g.setColor(new Color(0, 0, 0, 255));

			if (cars != null) {
				for (var c : cars) {
					double pos = c.getPos();
					Road r = c.getRoad();
					V2d dir = V2d.makeV2d(r.getFrom(), r.getTo()).getNormalized().mul(pos);
					g2.drawOval((int) (r.getFrom().x() + dir.x() - CAR_DRAW_SIZE / 2),
							(int) (r.getFrom().y() + dir.y() - CAR_DRAW_SIZE / 2), CAR_DRAW_SIZE, CAR_DRAW_SIZE);
				}
			}
		}

		public void update(List<Road> roads,
				List<CarAgentInfo> cars,
				List<TrafficLight> sems) {
			this.roads = roads;
			this.cars = cars;
			this.sems = sems;
			repaint();
		}
	}
}
