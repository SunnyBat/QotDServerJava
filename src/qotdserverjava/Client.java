package qotdserverjava;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author SunnyBat
 */
public class Client {

  private final String ADDRESS;
  private final int PORT;
  // REQUEST_BYTES can be nothing, including an empty String, as it's ignored by the server
  private static final byte[] REQUEST_BYTES = "What is the Quote of the Day?".getBytes();
  private static final int UDP_PACKET_MAX_SIZE = 512;

  /**
   * Creates a new Client to connect to a Quote of the Day Server.
   *
   * @param address The address of the server
   * @param port The port to connect on
   */
  public Client(String address, int port) {
    this.ADDRESS = address;
    this.PORT = port;
  }

  /**
   * Connects to the server with TCP. Prints out the message to System.out or an error message to System.err if an error occurs.
   */
  public void connectWithTCP() {
    try {
      Socket mySocket = new Socket(ADDRESS, PORT);
      mySocket.setSoTimeout(2000); // Wait 2 seconds max for next packet
      InputStream in = mySocket.getInputStream();
      byte[] writeTo = new byte[512]; // Max 512 bytes in protocol
      int totalBytesRead = 0;
      int iterationBytesRead = 0;
      try {
        while ((iterationBytesRead
            = in.read(writeTo, totalBytesRead, writeTo.length - totalBytesRead)) != -1) {
          totalBytesRead += iterationBytesRead;
        }
      } catch (SocketTimeoutException ste) { // Finished reading
      }
      //System.out.println("=== Message Received (TCP) ===");
      System.out.println(new String(writeTo));
      //System.out.println("=== End Message ===");
      // IOE for connection issues
    } catch (IOException ioe) {
      System.err.println("An error occurred while connecting to the Quote of the Day server with TCP.");
    }
  }

  /**
   * Connects to the server with UDP. Prints out the message to System.out or an error message to System.err if an error occurs.
   */
  public void connectWithUDP() {
    byte[] datagramBuffer = new byte[UDP_PACKET_MAX_SIZE];
    try {
      DatagramSocket udpSocket = new DatagramSocket();
      udpSocket.setSoTimeout(2000); // Wait for up to 2000 seconds for a response
      DatagramPacket send = new DatagramPacket(REQUEST_BYTES, REQUEST_BYTES.length, new InetSocketAddress(ADDRESS, PORT));
      udpSocket.send(send);
      DatagramPacket received = new DatagramPacket(datagramBuffer, UDP_PACKET_MAX_SIZE);
      udpSocket.receive(received);
      //System.out.println("=== Message Received (UDP) ===");
      System.out.println(new String(received.getData()));
      //System.out.println("=== End Message ===");
      // IOE for connection issues
      // IAE for invalid/unresolvable address
    } catch (IOException | IllegalArgumentException ioe) {
      System.err.println("An error occurred while connecting to the Quote of the Day server with UDP.");
    }
  }

}
