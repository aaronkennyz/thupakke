import javax.swing.*;
import java.awt.*;
import java.nio.file.*;

public class SimulatorUI extends JFrame {
    private final Simulator simulator = new Simulator();

    private final JTextArea programArea = new JTextArea();
    private final JTextArea stateArea = new JTextArea();
    private final JTextArea traceArea = new JTextArea();

    public SimulatorUI() {
        setTitle("Nuvoton MS51FB9AE Educational Simulator");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        programArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        stateArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        traceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton load = new JButton("Load");
        JButton reset = new JButton("Reset");
        JButton step = new JButton("Step");
        JButton run = new JButton("Run");

        buttons.add(load);
        buttons.add(reset);
        buttons.add(step);
        buttons.add(run);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(new JScrollPane(programArea));
        center.add(new JScrollPane(stateArea));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createTitledBorder("Execution Trace"));
        bottom.add(new JScrollPane(traceArea), BorderLayout.CENTER);

        add(buttons, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        load.addActionListener(e -> loadProgram());
        reset.addActionListener(e -> {
            simulator.reset();
            updateState();
            traceArea.setText("Simulator reset.");
        });
        step.addActionListener(e -> {
            try {
                traceArea.setText(simulator.step());
                updateState();
            } catch (Exception ex) {
                showError(ex);
            }
        });
        run.addActionListener(e -> {
            try {
                simulator.run();
                traceArea.setText(simulator.getCPU().getLastTrace()
                        + "\n\nProgram finished.");
                updateState();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        programArea.setText(
                "; Load programs/demo.asm using the Load button.\n" +
                "; Example:\n" +
                "MOV A, #05H\n" +
                "MOV R0, #03H\n" +
                "ADD A, R0\n" +
                "INC A\n" +
                "RET\n"
        );
        updateState();
    }

    private void loadProgram() {
        JFileChooser chooser = new JFileChooser("programs");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Path path = chooser.getSelectedFile().toPath();
                simulator.loadProgram(path);
                programArea.setText(Files.readString(path));
                updateState();
                traceArea.setText("Program loaded: " + path.getFileName());
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void updateState() {
        var c = simulator.getCPU();
        StringBuilder s = new StringBuilder();

        s.append("CPU STATE\n");
        s.append("---------\n");
        s.append(String.format("PC = %02X%n", c.getPC()));
        s.append(String.format("SP = %02X%n", c.getSP()));
        s.append(String.format("A  = %02X%n", c.getA()));

        for (int i = 0; i < 8; i++) {
            s.append(String.format("R%d = %02X%n", i, c.getR(i)));
        }

        s.append("\nFLAGS\n");
        s.append("-----\n");
        s.append("C = ").append(c.isCarry() ? 1 : 0).append("\n");
        s.append("Z = ").append(c.isZero() ? 1 : 0).append("\n");

        s.append("\nSTATUS\n");
        s.append("------\n");
        s.append(c.isHalted() ? "TERMINATED" : "RUNNING/READY");

        stateArea.setText(s.toString());
    }

    private void showError(Exception ex) {
        traceArea.setText("ERROR: " + ex.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulatorUI().setVisible(true));
    }
}
