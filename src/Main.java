import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Consumer;

import static java.lang.Integer.parseInt;

public class Main  {
    private static Properties prop = new Properties();
    private static ArrayList<RemotePC> remotePCs = new ArrayList<>();
    private static String  saveTextField;
    public static void main(String[]args) throws IOException {
        loadProperties();
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

    private static void createWindow()  {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame f=new JFrame("Start/shutdown pc's");
        //submit button
        JButton allOnButton=new JButton("Alles aan");
        JButton allHibernateButton=new JButton("Hibernate alles");
        JButton turnOnXButton=new JButton("Zet x aan");
        JButton hibernatexButton=new JButton("Hibernate x uit");
        JButton turnAllOffButton = new JButton("Zet alles uit") ;
        JButton turnxOffButton = new JButton("Zet x uit");

        allOnButton.setBounds(10,10,140, 40);
        turnOnXButton.setBounds(200,10,140, 40);
        allHibernateButton.setBounds(10,60,140, 40);
        hibernatexButton.setBounds(200,60,140, 40);
        turnAllOffButton.setBounds(10,110,140, 40);
        turnxOffButton.setBounds(200,110,140, 40);

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


        ImagePanel imagepanel = new ImagePanel();
        f.add(imagepanel);
        //add to frame
        f.add(allOnButton);
        f.add(allHibernateButton);
        f.add(turnOnXButton);
        f.add(hibernatexButton);
        f.add(turnAllOffButton);
        f.add(turnxOffButton);

        f.add(textField);
        f.setSize(800,300);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //action listener
        allOnButton.addActionListener(arg0 -> turnOnAllPCs(remotePCs));
        turnOnXButton.addActionListener(arg0 -> turnOnxPCs(saveTextField));
        turnAllOffButton.addActionListener(arg0 -> turnAllOff(remotePCs));
        turnxOffButton.addActionListener(arg0 -> {
            try {
                turnOfXPCs(saveTextField);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        allHibernateButton.addActionListener(arg0 -> {
            try {
                hibernateAllPcs(remotePCs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        hibernatexButton.addActionListener(arg0 -> {
            try {
                hibernateXPCs(saveTextField);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private static void turnAllOff(ArrayList<RemotePC> temp) {
            temp.forEach(pc -> pc.turnOff(pc));
    }

    private static void turnofAllpcs(ArrayList<RemotePC> temp) throws IOException {
        temp.forEach(pc -> pc.turnOff(pc));
    }

    private static void turnOfXPCs(String pcArray) throws IOException {
        if(pcArray.isEmpty()){
            return;
        }
        ArrayList<RemotePC> temp = new ArrayList<>();
        for (char i: pcArray.toCharArray()){
            temp.add(remotePCs.get(parseInt(String.valueOf(i))-1));
        }
        turnofAllpcs(temp);
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

    private static void hibernateXPCs(String pcArray) throws IOException {
        if(pcArray.isEmpty()){
            return;
        }
        ArrayList<RemotePC> temp = new ArrayList<>();
        for (char i: pcArray.toCharArray()){
            temp.add(remotePCs.get(parseInt(String.valueOf(i))-1));
        }
        hibernateAllPcs(temp);
    }

    private static void hibernateAllPcs(ArrayList<RemotePC> temp) throws IOException {
        temp.forEach(pc -> pc.sleep(pc));
    }


}
