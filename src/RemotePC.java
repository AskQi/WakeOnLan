import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

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
}

