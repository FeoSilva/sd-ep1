import java.util.*;

import Mensagem.Mensagem;

import java.io.*;
import java.net.*;
import java.lang.Thread;

@SuppressWarnings({ "resource", })

public class Peer {
    public static InetAddress peerAddress;
    public static int peerPort;

    public static InetAddress peerAddress1;
    public static int peerPort1;

    public static InetAddress peerAddress2;
    public static int peerPort2;

    public static boolean timeoutFinished = true;

    public static void main(String args[]) throws Exception {
        // menu
        Scanner in = new Scanner(System.in);
        DatagramSocket serverSocket = null;

        printMenu();

        while (true) {
            try {
                int option = in.nextInt();

                if (option == 1) { // INICIALIZA
                    Scanner input = new Scanner(System.in);

                    // peer address
                    System.out.print("Entre com o endereco IP deste peer (ex: 127.0.0.1): ");
                    String peerIp = input.nextLine();
                    if (peerIp == "") {
                        peerIp = "127.0.0.1";
                    }
                    peerAddress = InetAddress.getByName(peerIp);

                    // peer port
                    System.out.print("Entre com a porta deste peer (ex: 44): ");
                    String peerPortChar = input.nextLine();
                    peerPort = Integer.parseInt(peerPortChar != "" ? peerPortChar : "44");

                    // peer folder
                    System.out.print("Entre com o endereço da pasta: ");
                    String folderPath = input.nextLine();
                    File[] listFiles = new File(folderPath).listFiles();
                    while (listFiles == null) {
                        System.out.print("Endereco invalido, insira novamente: ");
                        folderPath = input.nextLine();
                        listFiles = new File(folderPath).listFiles();
                    }

                    // peer file names
                    ArrayList<String> fileNames = new ArrayList<String>();
                    for (File f : listFiles) {
                        if (f.isFile())
                            fileNames.add(f.getName());
                    }

                    // peer address 1
                    System.out.print("Informe o endereço IP de outro peer 1 (ex: 127.0.0.1): ");
                    String peerIp1 = input.nextLine();
                    if (peerIp1 == "") {
                        peerIp1 = "127.0.0.1";
                    }
                    peerAddress1 = InetAddress.getByName(peerIp1);

                    // peer port 1
                    System.out.print("Informe a porta deste peer 1 (ex: 23): ");
                    String peerPortChar1 = input.nextLine();
                    peerPort1 = Integer.parseInt(peerPortChar1 != "" ? peerPortChar1 : "23");

                    // peer address 2
                    System.out.print("Informe o endereço IP de outro peer 2 (ex: 127.0.0.1): ");
                    String peerIp2 = input.nextLine();
                    if (peerIp2 == "") {
                        peerIp2 = "127.0.0.1";
                    }
                    peerAddress2 = InetAddress.getByName(peerIp2);

                    // peer port 2
                    System.out.print("Informe a porta deste peer 2 (ex: 24): ");
                    String peerPortChar2 = input.nextLine();
                    peerPort2 = Integer.parseInt(peerPortChar2 != "" ? peerPortChar2 : "24");

                    System.out.print("arquivos da pasta: ");
                    printArray(fileNames);

                    PeerWatchdog wd = new PeerWatchdog(peerIp, peerPort, folderPath);
                    wd.start();

                    serverSocket = new DatagramSocket(peerPort);

                    PeerHandler peer = new PeerHandler(serverSocket, folderPath);
                    peer.start();

                    printMenu();
                } else if (option == 2) { // SEARCH
                    if (serverSocket == null) {
                        System.out.print("Peer ainda não foi inicializado\n");
                        printMenu();
                        throw new Exception();
                    }

                    Scanner input = new Scanner(System.in);

                    // filename
                    System.out.print("Informe o nome do arquivo para buscar (ex: video.mp4): ");
                    String fileName = input.nextLine();

                    // send request and name of file to search
                    Mensagem UDPRequest = new Mensagem();
                    UDPRequest.setMessage("SEARCH");
                    UDPRequest.setFileName(fileName);
                    UDPRequest.addAddress(peerAddress, peerPort);

                    int peerIndex = getRandomPeerIndex();
                    InetAddress peerA;
                    int peerP;
                    if (peerIndex == 0) {
                        peerA = peerAddress1;
                        peerP = peerPort1;
                    } else {
                        peerA = peerAddress2;
                        peerP = peerPort2;
                    }

                    sendMessage(UDPRequest, serverSocket, peerA, peerP);
                    waitResponse(serverSocket, fileName);
                } else {
                    System.out.println("Opção invalida!");
                }
            } catch (Exception e) {
                System.out.println("Opção invalida!");
            }
        }

    }

    public static void sendMessage(Mensagem req, DatagramSocket socket, InetAddress ip, int port) {
        new Thread(() -> {
            try {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream(1024);
                ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(byteStream));
                objectOut.flush();
                objectOut.writeObject(req);
                objectOut.flush();
                byte[] sendData = byteStream.toByteArray();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, port);
                socket.send(sendPacket);
                objectOut.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }).start();
    }

    private static void waitResponse(DatagramSocket s, String f) {
        timeoutFinished = false;
        int timeout = 10000; // time in miliseconds

        // periodically check response timeout
        new Thread(() -> {
            int t = 0;
            while (!timeoutFinished) {
                try {
                    Thread.sleep(100);
                    t = t + 100;

                    if (t >= timeout) {
                        timeoutFinished = true;
                        System.out.println("ninguém no sistema possui o arquivo " + f);
                    }
                } catch (Exception e) {

                }
            }
        }).start();
    }

    public static int getRandomPeerIndex() {
        // get a value from 0 - 1
        Random random = new Random();
        return random.nextInt(2);
    }

    public static void printArray(ArrayList<String> lista) {
        for (String item : lista) {
            System.out.print(" " + item);
        }
        System.out.print("\n");
    }

    public static void printMenu() {
        System.out.print("MENU:\n[1] INICIALIZA\n[2] SEARCH\nEscolha uma opção: ");
    }

    static class PeerWatchdog extends Thread {
        private String peerIp;
        private int peerPort;
        private String folderPath;

        public PeerWatchdog(String peerIp, int peerPort, String folderPath) {
            this.peerIp = peerIp;
            this.peerPort = peerPort;
            this.folderPath = folderPath;
        }

        public void run() {
            // check if peers are alive every 30 seconds
            while (true) {
                try {
                    Thread.sleep(30000);
                    File[] listFiles = new File(folderPath).listFiles();

                    // peer file names
                    ArrayList<String> names = new ArrayList<String>();
                    for (File f : listFiles) {
                        if (f.isFile())
                            names.add(f.getName());
                    }

                    String files = "";
                    for (int i = 0; i < names.size(); i++) {
                        files = files + " " + names.get(i);
                    }
                    System.out.println("Sou peer " + peerIp + ":" + peerPort + " com arquivos " + files);
                } catch (Exception e) {

                }
            }
        }
    }

    static class PeerHandler extends Thread {
        public Mensagem UDPRequest;
        public DatagramSocket serverSocket;
        private String folderPath;
        private ArrayList<String> filesProceeded = new ArrayList<>();
        private ArrayList<String> ipPortProceeded = new ArrayList<>();

        public PeerHandler(DatagramSocket serverSocket, String folderPath) {
            this.serverSocket = serverSocket;
            this.folderPath = folderPath;
        }

        public void run() {

            // waits peers UDP contact
            while (true) {
                try {
                    byte[] recBuffer = new byte[1024];
                    DatagramPacket recPacket = new DatagramPacket(recBuffer, recBuffer.length);
                    this.serverSocket.receive(recPacket);

                    // deserialize Mensagem object
                    ByteArrayInputStream byteStream = new ByteArrayInputStream(recPacket.getData());
                    ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(byteStream));
                    Mensagem UDPRequest = (Mensagem) objectIn.readObject();

                    // get IP and port from peer
                    UDPRequest.addAddress(recPacket.getAddress(), recPacket.getPort());

                    this.UDPRequest = UDPRequest;

                    String message = this.UDPRequest.getMessage();
                    String fileName = this.UDPRequest.getFileName();
                    String ipOriginPortRequest = this.UDPRequest.getOriginIpPort();

                    // add origin ip as proceeded
                    if (!this.isProceeded(fileName, ipOriginPortRequest) && filesProceeded.isEmpty()) {
                        this.filesProceeded.add(fileName);
                        this.ipPortProceeded.add(ipOriginPortRequest);
                    }

                    Mensagem UDPResponse = new Mensagem();
                    // copy address list from request to response
                    UDPResponse.setAddressList(this.UDPRequest.getAddressList(), this.UDPRequest.getPortList());

                    if (message.equals("SEARCH")) { // SEARCH
                        Boolean fileFound = false;
                        File[] listFiles = new File(this.folderPath).listFiles();
                        String ipPort = "";

                        String peer1IpPort = getIpPort(Peer.peerAddress1, Peer.peerPort1);
                        String peer2IpPort = getIpPort(Peer.peerAddress2, Peer.peerPort2);

                        boolean peer1IsProceeded = this.isProceeded(fileName, peer1IpPort);
                        boolean peer2IsProceeded = this.isProceeded(fileName, peer2IpPort);

                        if (this.isProceeded(fileName, ipOriginPortRequest) && peer1IsProceeded && peer2IsProceeded) {
                            System.out.println("requisição já processada para " + fileName);
                            throw new Exception();
                        }

                        for (File f : listFiles) {
                            if (f.isFile() && fileName.equals(f.getName())) {
                                // send message to origin peer
                                InetAddress address = this.UDPRequest.getOriginAddress();
                                int port = this.UDPRequest.getOriginIp();
                                ipPort = this.UDPRequest.getOriginIpPort();

                                UDPResponse.setMessage("RESPONSE");
                                UDPResponse.setFileName(fileName);
                                UDPResponse.addAddress(address, port);

                                fileFound = true;
                                this.resetProceedList();

                                System.out.println("tenho " + fileName + " respondendo para " + ipPort);
                                Peer.sendMessage(UDPResponse, serverSocket, address, port);
                                break;
                            }
                        }

                        if (!fileFound) {
                            // randomly get a peer to retry request
                            int peerIndex = Peer.getRandomPeerIndex();
                            InetAddress peerA;
                            int peerP;
                            if (peerIndex == 0 && peer1IsProceeded) {
                                peerIndex = 1;
                            } else if (peerIndex == 1 && peer2IsProceeded) {
                                peerIndex = 0;
                            }

                            if (peerIndex == 0) {
                                peerA = Peer.peerAddress1;
                                peerP = Peer.peerPort1;
                            } else if (peerIndex == 1) {
                                peerA = Peer.peerAddress2;
                                peerP = Peer.peerPort2;
                            } else {
                                throw new Exception();
                            }

                            UDPResponse.setMessage("SEARCH");
                            UDPResponse.setFileName(fileName);
                            UDPResponse.addAddress(peerA, peerP);
                            ipPort = UDPResponse.getIpPort();

                            this.filesProceeded.add(fileName);
                            this.ipPortProceeded.add(ipPort);

                            System.out.println("não tenho " + fileName + " respondendo para " + ipPort);
                            Peer.sendMessage(UDPResponse, serverSocket, peerA, peerP);
                        }
                    } else if (message.equals("RESPONSE")) {
                        System.out.println("peer com arquivo procurado: " + ipOriginPortRequest + " " + fileName);
                        Peer.timeoutFinished = true;
                    }
                } catch (Exception e) {

                }
            }

        }

        private boolean isProceeded(String f, String i) {
            for (int j = 0; j < this.filesProceeded.size(); j++) {
                String fName = this.filesProceeded.get(j);
                String iPort = this.ipPortProceeded.get(j);
                if (fName.equals(f) && iPort.equals(i)) {
                    return true;
                }
            }
            return false;
        }

        private void resetProceedList() {
            this.filesProceeded.clear();
            this.ipPortProceeded.clear();
        }

        private String getIpPort(InetAddress address, int port) {
            String ip = address.toString().replace("/", "");
            return ip + ":" + String.valueOf(port);
        }
    }
}