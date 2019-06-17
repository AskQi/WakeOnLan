import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.Integer.parseInt;

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

    void turnOff(){
        if (!this.getGebruikersnaam().isEmpty()) {
            System.out.println("removing" + this);
            String command = "c:/VR/psshutdown.exe \\\\"+ this.getNetwerknaam()+" -u VR5 -p vr -h -t 1 " ;
            Main.sendCommand(command);
        }
    }

    void wakeOnLan(String ipStr, String macStr){
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

}

