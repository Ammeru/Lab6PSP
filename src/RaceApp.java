import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RaceApp extends JFrame {

    private static final int NUM_RUNNERS = 3;

    private final Runner[] runners = new Runner[NUM_RUNNERS];
    private final Timer timer;
    private final CirclesPanel circlesPanel;
    private boolean isRunning = false;
    private int lapsCompleted = 0;

    public RaceApp() {
        setTitle("Гонка до первого пробежавшего");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);

        circlesPanel = new CirclesPanel();
        add(circlesPanel);

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startRace();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopRace();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        add(buttonPanel, BorderLayout.SOUTH);

        timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                circlesPanel.moveRunners();
            }
        });

        for (int i = 0; i < NUM_RUNNERS; i++) {
            runners[i] = new Runner(i);
        }

        setVisible(true);
    }

    private void startRace() {
        if (!isRunning) {
            for (Runner runner : runners) {
                runner.resetPosition();
            }
            timer.start();
            isRunning = true;
        }
    }

    private void stopRace() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
            lapsCompleted = 0;
        }
    }

    private class CirclesPanel extends JPanel {
        private int circleRadius = 80;
        private int gapBetweenCircles = 30;
        private Color[] trackColors = {Color.BLUE, Color.GREEN, Color.RED};

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            for (int i = 0; i < NUM_RUNNERS; i++) {
                int radius = circleRadius - i * gapBetweenCircles;

                // Рисуем окружность (беговую дорожку) с уникальным цветом
                g.setColor(trackColors[i]);
                g.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);

                // Рисуем линию, представляющую точку старта
                double angle = 0;
                int startX = centerX + (int) (Math.cos(angle) * radius);
                int startY = centerY + (int) (Math.sin(angle) * radius);
                int endX = centerX + (int) (Math.cos(angle) * (radius - 10));
                int endY = centerY + (int) (Math.sin(angle) * (radius - 10));
                g.setColor(Color.BLACK);
                g.drawLine(startX, startY, endX, endY);
            }

            for (Runner runner : runners) {
                runner.draw(g, centerX, centerY, circleRadius, gapBetweenCircles);
            }
        }

        public void moveRunners() {
            for (Runner runner : runners) {
                runner.move();
            }

            // Проверяем, сколько кругов пробежали
            if (runners[0].getAngle() >= 2 * Math.PI * 5) {
                stopRace();
                determinePlaces();
                return;
            }

            repaint();
        }
    }

    private class Runner {
        private double angle;
        private double angularSpeed;
        private int trackIndex;

        public Runner(int trackIndex) {
            this.trackIndex = trackIndex;
            angle = 0;
            angularSpeed = Math.toRadians(new java.util.Random().nextInt(10) + 5);
        }

        public void draw(Graphics g, int centerX, int centerY, int circleRadius, int gapBetweenCircles) {
            int radius = circleRadius - trackIndex * gapBetweenCircles;
            int x = centerX + (int) (Math.cos(angle) * radius);
            int y = centerY + (int) (Math.sin(angle) * radius);

            // У бегунов чёрный цвет
            g.setColor(Color.BLACK);
            g.fillOval(x - 5, y - 5, 10, 10);
        }

        public void move() {
            angle += angularSpeed;
        }

        public void resetPosition() {
            angle = 0;
        }

        public double getAngle() {
            return angle;
        }
    }

    private void determinePlaces() {
        int[] places = new int[NUM_RUNNERS];

        for (int i = 0; i < NUM_RUNNERS; i++) {
            places[i] = 1;
            for (int j = 0; j < NUM_RUNNERS; j++) {
                if (i != j && runners[i].getAngle() > runners[j].getAngle()) {
                    places[i]++;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Race completed!\nPlaces: " +
                "1st: Runner " + (findRunnerIndexByPlace(places, 1) + 1) +
                ", 2nd: Runner " + (findRunnerIndexByPlace(places, 2) + 1) +
                ", 3rd: Runner " + (findRunnerIndexByPlace(places, 3) + 1));

        lapsCompleted = 0;
    }

    private int findRunnerIndexByPlace(int[] places, int targetPlace) {
        for (int i = 0; i < places.length; i++) {
            if (places[i] == targetPlace) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RaceApp();
            }
        });
    }
}
