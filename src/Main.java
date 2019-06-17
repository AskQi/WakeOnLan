import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;

public class Main  {
    private static Properties prop = new Properties();
    private static ArrayList<RemotePC> remotePCs = new ArrayList<>();
    private static String  saveTextField;
    public static void main(String[]args) throws IOException {
        loadProperties();
        createNetStat();
        createWindow();
    }

    private static void loadProperties() throws IOException {
        String fileName = "c:/VR/application.properties";
        InputStream is = new FileInputStream(fileName);
        prop.load(is);
        for(int i =0; i<6; i++){
            remotePCs.add(buildRemotePCs(i+1));
        }
    }

    private static RemotePC buildRemotePCs(int i) {
        System.out.println("Adding pc"+i);
        String pcNumber = "pc" + i;
        return new RemotePC(
                prop.getProperty(pcNumber+"."+"gebruikersnaam"),
                prop.getProperty(pcNumber+"."+"netwerknaam"),
                prop.getProperty(pcNumber+"."+"wachtwoord"),
                prop.getProperty(pcNumber+"."+"macadres")
                );
    }

    private static void turnOnAllPCs(ArrayList<RemotePC> temp) {
        for(RemotePC pc: temp){
            System.out.println(pc);
            pc.turnOn(prop.getProperty("subnetmask"), pc.getMacadres());
        }
    }

    private static void turnOnxPCs(String pcArray) {
        ArrayList<RemotePC> temp = new ArrayList<>();
        for (char i: pcArray.toCharArray()){
            temp.add(remotePCs.get(parseInt(String.valueOf(i))-1));
        }
        turnOnAllPCs(temp);
    }


    private static void turnOffAllPCs(ArrayList<RemotePC> temp)  {
        for(RemotePC pc: temp){
            pc.turnOff();
        }
    }

    private static boolean ping(String host) throws IOException, InterruptedException {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

        ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows? "-n" : "-c", "1", host);
        Process proc = processBuilder.start();

        int returnVal = proc.waitFor();
        return returnVal == 0;
    }

    private static void turnOffxPCs(String pcArray) {
        if(pcArray.isEmpty()){
            return;
        }
        ArrayList<RemotePC> temp = new ArrayList<>();
        for (char i: pcArray.toCharArray()){
            temp.add(remotePCs.get(parseInt(String.valueOf(i))-1));
        }
        turnOffAllPCs(temp);
    }

    private static void createNetStat(){
        for(RemotePC pc: remotePCs){
            if (!pc.getGebruikersnaam().isEmpty()) {
                System.out.println("removing" + pc);
                String command = "net use \\\\" +
                        pc.getNetwerknaam() + " /user:" + pc.getGebruikersnaam() +  " "+ pc.getWachtwoord();
                sendCommand(command);
                System.out.println(command);

            }
        }
        }

    static void sendCommand(String command) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("cmd.exe", "/c", command);
        builder.directory(new File(System.getProperty("user.home")));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert process != null;
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
    }






    static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;
        StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }
        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    private static void createWindow(){
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame f=new JFrame("Start/shutdown pc's");
        //submit button
        JButton allOnButton=new JButton("Alles aan");
        JButton allOffButton=new JButton("Alles uit");
        JButton turnOnXButton=new JButton("Zet x aan");
        JButton turnOffXButton=new JButton("Zet x uit");

        allOnButton.setBounds(10,10,140, 40);
        allOffButton.setBounds(10,60,140, 40);
        turnOnXButton.setBounds(200,10,140, 40);
        turnOffXButton.setBounds(200,60,140, 40);

        JTextField textField = new JTextField("PC nummers simpel achter elkaar. BV: '124'");

        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
            }
            @Override
            public void focusLost(FocusEvent e) {
                    textField.setForeground(Color.GRAY);
                    saveTextField = textField.getText();
                    textField.setText("PC nummers achter elkaar: bijvoorbeeld :'143'");

            }
        });

        textField.setBounds(350,35,350, 35);
        JTextField outputTextField = new JTextField(prop.getProperty(String.valueOf(remotePCs.get(0).getWachtwoord())));
        outputTextField.setBounds(10,200,560, 35);
        outputTextField.setEditable(false);

        //add to frame
        f.add(allOnButton);
        f.add(allOffButton);
        f.add(turnOnXButton);
        f.add(turnOffXButton);
        f.add(outputTextField, BorderLayout.SOUTH);

        f.add(textField);
        f.setSize(800,300);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //action listener
        allOnButton.addActionListener(arg0 -> turnOnAllPCs(remotePCs));
        allOffButton.addActionListener(arg0 -> turnOffAllPCs(remotePCs));
        turnOnXButton.addActionListener(arg0 -> turnOnxPCs(saveTextField));
        turnOffXButton.addActionListener(arg0 -> turnOffxPCs(saveTextField));

    }
}
