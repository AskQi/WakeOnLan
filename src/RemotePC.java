import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;

public class RemotePC {
    private String netwerknaam;
    private String gebruikersnaam;
    private String wachtwoord;
    private String macadres;

    RemotePC(String gebruikersnaam, String netwerknaam, String wachtwoord, String macadres){
        this.gebruikersnaam = gebruikersnaam;
        this.netwerknaam = netwerknaam;
        this.wachtwoord = wachtwoord;
        this.macadres = macadres;
    }

    String getNetwerknaam() {
        return netwerknaam;
    }

    String getGebruikersnaam() {
        return gebruikersnaam;
    }

    String getWachtwoord() {
        return wachtwoord;
    }

    String getMacadres() {
        return macadres;
    }

    @Override
    public String toString() {
        return "RemotePC{" +
                "netwerknaam='" + netwerknaam + '\'' +
                ", gebruikersnaam='" + gebruikersnaam + '\'' +
                ", wachtwoord='" + wachtwoord + '\'' +
                ", macadres='" + macadres + '\'' +
                '}';
    }

    void turnOff(RemotePC pc)  {
        if (!this.getGebruikersnaam().isEmpty()) {
            String command = "c:/VR/psshutdown.exe \\\\"+ this.getNetwerknaam()+" -u "+ this.gebruikersnaam + " -p " +  this.wachtwoord + " -h -f -t 1  " ;
            CommandThread shutdownThread = new CommandThread(command, pc);
            shutdownThread.run();
        }
    }

    void turnOn(String ipStr, String macStr){
        final int PORT = 9;
        try {
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();
            System.out.println("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet:" + e);
            System.exit(1);
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }



    class CommandThread implements Runnable{
        String command;
        RemotePC pc;
        CommandThread(String command, RemotePC pc){
            this.command = command;
            this.pc=  pc;
        }

        private Boolean isOn(String pcName) throws IOException {
            InetAddress pc = InetAddress.getByName(pcName);
            for(int i = 0; i <=2;i++){
                System.out.println("Sending Ping Request to " + pcName);
                if(pc.isReachable(1)){
                    System.out.println("PC" + pcName + "turned on");
                    return true;
                }
            }
            System.out.println("PC" + pcName + "turned OFF");
            return false;
        }

        void sendCommand(String command) {
            String netCommand = "net use \\\\" +
                    pc.getNetwerknaam() + " /user:" + pc.getGebruikersnaam() +  " "+ pc.getWachtwoord();

            System.out.println("Sending command  " + command);
            System.out.println("Sending NET command  " + netCommand);


            ProcessBuilder netBuilder = new ProcessBuilder();
            netBuilder.command("cmd.exe", "/c", netCommand);
            netBuilder.directory(new File(System.getProperty("user.home")));

            ProcessBuilder builder = new ProcessBuilder();
            builder.command("cmd.exe", "/c", command);
            builder.directory(new File(System.getProperty("user.home")));

            Process netProcess = null;
            Process process = null;
            try {
                netProcess = netBuilder.start();
                process = builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert netProcess != null;
            assert process != null;
            Main.StreamGobbler netstreamGobbler =
                    new Main.StreamGobbler(netProcess.getInputStream(), System.out::println);
            Main.StreamGobbler streamGobbler =
                    new Main.StreamGobbler(process.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(netstreamGobbler);
            Executors.newSingleThreadExecutor().submit(streamGobbler);

        }

        @Override
        public void run() {
            try {
                System.out.println("checking on");
                if(isOn(pc.netwerknaam)){
                    System.out.println(pc.netwerknaam + " is on!");
                    sendCommand(command);
                }
            }
            catch(Exception e){
                System.out.println(e.toString());
            }
        }
    }
}





