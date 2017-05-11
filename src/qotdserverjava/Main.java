package qotdserverjava;

import java.util.Scanner;

/**
 *
 * @author sunnybat
 */
public class Main {

  private static final int DEFAULT_PORT = 17;

  /**
   * The entry point of the program.
   *
   * @param args The command line arguments
   */
  public static void main(String[] args) {
    Scanner userIn = new Scanner(System.in);
    String addressToConnectTo = null;
    int portToUse = getPortToUse(args, userIn);
    // True = Client, False = Server
    boolean isClient = getShouldUseClient(args, userIn);
    if (isClient) {
      addressToConnectTo = getAddressToConnectTo(args, userIn);
    }
    boolean tcp = getUseTCP(args, userIn);
    boolean udp = getUseUDP(args, userIn);
    if (isClient) {
      startClient(tcp, udp, addressToConnectTo, portToUse, userIn);
    } else { // Is server
      startServer(tcp, udp, portToUse);
    }
  }

  /**
   * Checks to see if args contains arg. Not case sensitive.
   *
   * @param arg The String to check for
   * @param args The String[] to check
   * @return True if contained, false if not
   */
  private static boolean containsArg(String arg, String[] args) {
    for (String tempArg : args) {
      if (tempArg.equalsIgnoreCase(arg)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the argument after the given argument.
   *
   * @param arg The argument to search for
   * @param args The arguments to search within
   * @return The argument after arg
   */
  private static String getArg(String arg, String[] args) {
    for (int i = 0; i < args.length - 1; i++) {
      if (args[i].equalsIgnoreCase(arg)) {
        return args[i + 1];
      }
    }
    return null;
  }

  /**
   * Gets the port to use. Prompts for the port if not specified in args.
   *
   * @param args The arguments to search through for the port
   * @param userIn The Scanner to read user input from (if necessary)
   * @return The port to use
   */
  private static int getPortToUse(String[] args, Scanner userIn) {
    String customPort = getArg("-port", args);
    if (customPort != null) {
      try {
        return Integer.parseInt(customPort);
      } catch (NumberFormatException nfe) {
        System.err.println("ERROR: Invalid port specified (" + customPort + ") -- Using default of " + DEFAULT_PORT);
        return DEFAULT_PORT;
      }
    } else {
      return promptForPort(userIn);
    }
  }

  /**
   * Prompts the user for the port to use. If an invalid port is given, it uses the default port.
   *
   * @param userIn The Scanner to read user input from
   * @return The port to use
   */
  private static int promptForPort(Scanner userIn) {
    System.out.println("Port to use [" + DEFAULT_PORT + "]? ");
    String nextInput = userIn.nextLine();
    if (nextInput.isEmpty()) {
      return DEFAULT_PORT;
    }
    try {
      return Integer.parseInt(nextInput);
    } catch (NumberFormatException nfe) {
      System.err.println("ERROR: Invalid port specified. Using default port (" + DEFAULT_PORT + ")");
      return DEFAULT_PORT;
    }
  }

  /**
   * Gets whether or not to be a client. Only prompts the user if no arguments are found. Uses System.out to prompt the user.
   *
   * @param args The arguments given to the program
   * @param userIn The Scanner to read user input from (if necessary)
   * @return True if a client, false if a server
   * @throws NullPointerException if args or userIn is null
   */
  private static boolean getShouldUseClient(String[] args, Scanner userIn) {
    if (containsArg("-client", args)) {
      return true;
    } else if (containsArg("-server", args)) {
      return false;
    } else {
      return promptForClientOrServer(userIn);
    }
  }

  /**
   * Prompts the user whther to be a client or server. Uses System.out to prompt and userIn to read a response.
   *
   * @param userIn The Scanner to read from
   * @return True if a client, false if a server
   * @throws NullPointerException if userIn is null
   */
  private static boolean promptForClientOrServer(Scanner userIn) {
    while (true) {
      System.out.print("Client or Server (C/S)? ");
      String nextLine = userIn.nextLine();
      if (nextLine.toLowerCase().startsWith("c")) {
        return true;
      } else if (nextLine.toLowerCase().startsWith("s")) {
        return false;
      } else {
        System.err.println("Please select either Client (C) or Server (S).");
      }
    }
  }

  /**
   * Gets the address to connect to. If the address is specified in args, it is used. Otherwise, the user is prompted for the address using
   * System.out. Note that this does NOT do any sort of input validation.
   *
   * @param args The arguments given to the program
   * @param userIn The Scanner to read user input from
   * @return The address to connect to, never null
   */
  private static String getAddressToConnectTo(String[] args, Scanner userIn) {
    for (int i = 0; i < args.length - 1; i++) {
      if (args[i].equalsIgnoreCase("-address")) {
        return args[i + 1];
      }
    }
    return promptForAddressToConnectTo(userIn);
  }

  /**
   * Prompts the user for the address to connect to. Uses System.out to prompt and userIn to read responses. Note that this does NOT do any sort of
   * input validation.
   *
   * @param userIn The Scanner to read user input from
   * @return The address to connect to, never null.
   */
  private static String promptForAddressToConnectTo(Scanner userIn) {
    System.out.println("Note this does NOT do any sort of address validation. Be sure to enter the correct address.");
    System.out.println("Address to connect to? ");
    String address = userIn.nextLine();
    return address;
  }

  /**
   * Gets whether or not to use TCP. Only prompts the user if no arguments are found. Uses System.out to prompt the user.
   *
   * @param args The arguments given to the program
   * @param userIn The Scanner to read user input from
   * @return True if use TCP, false if not
   */
  private static boolean getUseTCP(String[] args, Scanner userIn) {
    if (containsArg("-tcp", args)) {
      return true;
    } else if (containsArg("-notcp", args)) {
      return false;
    } else {
      return promptForMode("TCP", userIn);
    }
  }

  /**
   * Gets whether or not to use UDP. Only prompts the user if no arguments are found. Uses System.out to prompt the user.
   *
   * @param args The arguments given to the program.
   * @param userIn The Scanner to read user input from
   * @return True if use UDP, false if not
   */
  private static boolean getUseUDP(String[] args, Scanner userIn) {
    if (containsArg("-udp", args)) {
      return true;
    } else if (containsArg("-noudp", args)) {
      return false;
    } else {
      return promptForMode("UDP", userIn);
    }
  }

  /**
   * Prompts the user for whether or not to use the given mode to connect. Uses System.out to prompt and reads input from userIn.
   *
   * @param mode The mode (normally TCP/UDP) to connect with
   * @param userIn The Scanner to read user input from
   * @return True if use mode, false if not
   */
  private static boolean promptForMode(String mode, Scanner userIn) {
    while (true) {
      System.out.print("Use " + mode + " to connect ([Y]/N)? ");
      String nextLine = userIn.nextLine();
      // User just pressed Enter results in an empty String, default to yes
      if (nextLine.isEmpty() || nextLine.toLowerCase().startsWith("y")) {
        return true;
      } else if (nextLine.toLowerCase().startsWith("n")) {
        return false;
      } else {
        System.err.println("Please select either Yes (Y) or No (N).");
      }
    }
  }

  /**
   * Starts a client. Continuously prompts the user using System.out on whether to connect again. This blocks until the user says not to connect
   * again.
   *
   * @param tcp True to use TCP, false to not
   * @param udp True to use UDP, false to not
   * @param address The address to connect to
   * @param userIn The Scanner to read user input from
   * @throws NullPointerException if address or userIn is null
   */
  private static void startClient(boolean tcp, boolean udp, String address, int port, Scanner userIn) {
    Client myClient = new Client(address, port);
    do {
      if (tcp) {
        System.out.println("Connecting with TCP to " + address + ":" + port + "...");
        myClient.connectWithTCP();
      }
      if (udp) {
        System.out.println("Connecting with UDP to " + address + ":" + port + "...");
        myClient.connectWithUDP();
      }
      System.out.println("Connect again (Y/[N])? ");
      if (!userIn.nextLine().toLowerCase().startsWith("y")) {
        break; // We're done, break out
      }
    } while (true);
  }

  /**
   * Starts a server. Creates one Thread for TCP and one for UDP depending on if they're enabled.
   *
   * @param tcp True to run TCP, false to not
   * @param udp True to run UDP, false to not
   */
  private static void startServer(boolean tcp, boolean udp, int port) {
    Server myServer = new Server(port);
    if (tcp) {
      myServer.startTCPServer();
    }
    if (udp) {
      myServer.startUDPServer();
    }
  }

}
