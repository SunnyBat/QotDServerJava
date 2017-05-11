package qotdserverjava;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author SunnyBat
 */
public class Server {

  private final int PORT;
  private final byte[] RESPONSE_BYTES;
  private static final String DEFAULT_QUOTE = "This is the Quote of the Day.";
  private static final int UDP_PACKET_MAX_SIZE = 512;

  public Server(int port) {
    this(port, DEFAULT_QUOTE);
  }

  public Server(int port, String QotD) {
    this.PORT = port;
    this.RESPONSE_BYTES = QotD.getBytes();
  }

  /**
   * Opens a new TCP Socket on the port associated with this Server on a new Thread. Does not block. If unable to open the Socket, prints out an error
   * message to System.err.
   */
  public void startTCPServer() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          ServerSocket tcpServerSocket = new ServerSocket(PORT);
          listenForeverTCP(tcpServerSocket);
        } catch (IOException ioe) {
          System.err.println("ERROR: Unable to bind to TCP port " + PORT + " -- is a server already up?");
        }
      }
    }).start();
  }

  /**
   * Opens a new UDP Socket on the port associated with this Server on a new Thread. Does not block. If unable to open the Socket, prints out an error
   * message to System.err.
   */
  public void startUDPServer() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          DatagramSocket udpSocket = new DatagramSocket(PORT);
          listenForeverUDP(udpSocket);
        } catch (IOException ioe) {
          System.err.println("ERROR: Unable to bind to UDP port " + PORT + " -- is a server already up?");
        }
      }
    }).start();
  }

  private void listenForeverTCP(ServerSocket listenOn) {
    while (true) {
      try {
        System.out.println("Listening on TCP Port " + PORT);
        Socket newConnection = listenOn.accept();
        System.out.println("TCP Connection found from " + newConnection.getInetAddress().getHostAddress() + ":" + newConnection.getPort());
        OutputStream out = newConnection.getOutputStream();
        out.write(RESPONSE_BYTES);
        newConnection.close();
      } catch (IOException ioe) {
        System.err.println("An error occurred while listening on TCP Port " + PORT);
      }
    }
  }

  private void listenForeverUDP(DatagramSocket udpSocket) {
    byte[] datagramBuffer = new byte[UDP_PACKET_MAX_SIZE];
    while (true) {
      try {
        System.out.println("Listening on UDP Port " + PORT);
        DatagramPacket received = new DatagramPacket(datagramBuffer, UDP_PACKET_MAX_SIZE);
        udpSocket.receive(received);
        System.out.println("UDP Connection found from " + received.getAddress().getHostAddress() + ":" + received.getPort());
        DatagramPacket send = new DatagramPacket(RESPONSE_BYTES, RESPONSE_BYTES.length, received.getSocketAddress());
        udpSocket.send(send);
      } catch (IOException ioe) {
        System.err.println("An error occurred while listening on UDP Port " + PORT);
      }
    }
  }

}
