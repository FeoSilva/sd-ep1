package Mensagem;

import java.net.InetAddress;
import java.util.ArrayList;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensagem implements Serializable {
    private String message = " ";
    private InetAddress address;
    private int port;
    private String fileName = "";
    private LocalDateTime dateTime = LocalDateTime.now();
    private InetAddress originAddress;
    private int originPort;
    private ArrayList<InetAddress> addressList = new ArrayList<>();
    private ArrayList<String> portList = new ArrayList<>();

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String m) {
        this.message = m;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    private void setAddress(InetAddress a) {
        this.address = a;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int p) {
        this.port = p;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String f) {
        this.fileName = f;
    }

    public void setDateTime(LocalDateTime d) {
        this.dateTime = d;
    }

    public LocalDateTime getDateTime() {
        return this.dateTime;
    }

    public void setAddressList(ArrayList<InetAddress> aList, ArrayList<String> pList) {
        this.addressList = aList;
        this.portList = pList;
    }

    public ArrayList<InetAddress> getAddressList() {
        return this.addressList;
    }

    public ArrayList<String> getPortList() {
        return this.portList;
    }

    public void addAddress(InetAddress s, int p) {
        this.setAddress(s);
        this.setPort(p);
        this.addressList.add(s);
        this.portList.add(String.valueOf(p));
    }

    public void setOrigin(InetAddress a, int p) {
        this.originAddress = a;
        this.originPort = p;
    }

    public InetAddress getOriginAddress() {
        return this.originAddress;
    }

    public int getOriginPort() {
        return this.originPort;
    }

    public String getIpPort() {
        String ip = address.toString().replace("/", "");
        return ip + ":" + String.valueOf(port);
    }

    public String getOriginIpPort() {
        String ip = getOriginAddress().toString().replace("/", "");
        return ip + ":" + String.valueOf(getOriginPort());
    }
}
