import javax.swing.*;
import java.awt.*;
import java.nio.file.*;

public class SimulatorUI extends JFrame {
    private final Simulator simulator = new Simulator();
    private final JTextArea trace = new JTextArea();
    private final JLabel state = new JLabel();
    private final javax.swing.table.DefaultTableModel memoryModel =
    new javax.swing.table.DefaultTableModel(
        new String[]{"Address", "Value"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

private final JTable memoryTable = new JTable(memoryModel);

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

        trace.setEditable(false);
        trace.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));

JScrollPane traceScroll = new JScrollPane(trace);
traceScroll.setBorder(
    BorderFactory.createTitledBorder("Execution Trace")
);

memoryTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
memoryTable.setRowHeight(22);
memoryTable.getTableHeader().setReorderingAllowed(false);

JScrollPane memoryScroll = new JScrollPane(memoryTable);
memoryScroll.setBorder(
    BorderFactory.createTitledBorder("Memory Viewer")
);

center.add(traceScroll);
center.add(memoryScroll);

add(center, BorderLayout.CENTER);
        add(state, BorderLayout.SOUTH);

        stateArea.setText(s.toString());
    }
 
    private void refreshMemoryTable() {
    memoryModel.setRowCount(0);

    Memory memory = simulator.getCPU().getMemory();

    for (int address = 0; address < memory.size(); address++) {
        memoryModel.addRow(new Object[]{
            String.format("%02X", address),
            String.format("%02X", memory.read(address))
        });
    }
}

    private void refresh(String message) {
        CPU c = simulator.getCPU();
        trace.setText(message + "\n\n" +
            "MEMORY [00-1F]\n" + c.getMemory().dump(0, 31) + "\n" +
            "STACK SP=" + c.getStack().getSP() + "  " + c.getStack().display() + "\n" +
            "QUEUE front=" + c.getQueue().getFrontIndex() +
            " rear=" + c.getQueue().getRearIndex() +
            " size=" + c.getQueue().size() + "  " + c.getQueue().display());
        state.setText(String.format("PC=%02X   A=%02X   R0=%02X R1=%02X R2=%02X   Z=%s C=%s   Halted=%s",
            c.getPC(), c.getAccumulator(), c.getRegisters()[0], c.getRegisters()[1],
            c.getRegisters()[2], c.isZeroFlag(), c.isCarryFlag(), c.isHalted()));
            refreshMemoryTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulatorUI().setVisible(true));
    }
}
