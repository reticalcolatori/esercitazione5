package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    private static final int REG_PORT = 1099;

    public static void main(String[] args) {

        String registryHost = null;                    //host remoto con registry
        //String serviceName = "ServerCongresso";		//lookup name...Hardcoded
        String serviceName = "";
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        // Controllo dei parametri della riga di comando
        if (args.length != 2) {
            System.out.println("Sintassi: RMI_Registry_IP ServiceName");
            System.exit(1);
        }

        registryHost = args[0];
        serviceName = args[1];

        System.out.println("Invio richieste a " + registryHost + " per il servizio di nome " + serviceName);

        // Connessione al servizio RMI remoto
        try {
            String completeName = "//" + registryHost + ":" + REG_PORT + "/" + serviceName;

            RemOp serverRMI = (RemOp) Naming.lookup(completeName);
            System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

            System.out.println("\nRichieste di servizio fino a fine file");

            String service;
            System.out.print("Servizio (C=Conta Righe, E=Elimina Righe): ");

            /*ciclo accettazione richieste utente*/
            while ((service = stdIn.readLine()) != null) {

                if (service.equals("C")) {
                    String filename = "";
                    int soglia = 0;
                    boolean ok = true;

                    System.out.println("Inserisci il nome del file: ");
                    filename = stdIn.readLine();

                    if (filename == null) {
                        //Ha fatto EOF esco.
                        System.exit(0);
                    }

                    //Non posso fare check a priori sul nome file.

                    //Ora leggo la soglia
                    ok = false;

                    while (!ok) {
                        System.out.println("Inserisci la soglia (>0):");

                        try {
                            String tmp;
                            if ((tmp = stdIn.readLine()) == null) {
                                //Ha fatto EOF esco.
                                System.exit(0);
                            }

                            soglia = Integer.parseInt(tmp);
                            if (soglia >= 0)
                                ok = true;
                        } catch (NumberFormatException ex) {
                            System.out.println("Errore soglia ripeti.");
                        }
                    }

                    //Posso effettuare la richiesta RMI.
                    try {
                        int conteggioRighe = serverRMI.conta_righe(filename, soglia);
                        System.out.println("Le righe che soddisfano la soglia sono: " + conteggioRighe + " Per il file " + filename);
                    } catch (RemoteException ex) {
                        System.out.println("Il server ha restituito un errore: " + ex.getMessage());
                    }

                } // C=Conta Righe
                else if (service.equals("E")) {
                    String filename = "";
                    int numeroRiga = 0;
                    boolean ok = true;

                    System.out.println("Inserisci il nome del file: ");
                    filename = stdIn.readLine();

                    if (filename == null) {
                        //Ha fatto EOF esco.
                        System.exit(0);
                    }

                    //Non posso fare check a priori sul nome file.

                    //Ora leggo la soglia
                    ok = false;

                    while (!ok) {
                        System.out.println("Inserisci il numero riga da eliminare (>0):");

                        try {
                            String tmp;
                            if ((tmp = stdIn.readLine()) == null) {
                                //Ha fatto EOF esco.
                                System.exit(0);
                            }

                            numeroRiga = Integer.parseInt(tmp);
                            if (numeroRiga > 0)
                                ok = true;
                        } catch (NumberFormatException ex) {
                            System.out.println("Errore numero riga ripeti.");
                        }
                    }

                    //Posso effettuare la richiesta RMI.
                    try {
                        int numeroRighe = serverRMI.elimina_riga(filename, numeroRiga);
                        System.out.println("Ora il file " + filename + " ha " + numeroRighe + " righe.");
                    } catch (RemoteException ex) {
                        System.out.println("Il server ha restituito un errore: " + ex.getMessage());
                    }
                } // E=Elimina Righe

                else System.out.println("Servizio non disponibile");

                System.out.print("Servizio (R=Registrazione, P=Programma del congresso): ");
            } // while (!EOF), fine richieste utente

        } catch (NotBoundException nbe) {
            System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
            //nbe.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("ClientRMI: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }


    }

}
