package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements RemOp {

    private static final long serialVersionUID = 1L;
    private static final int registryPort = 1099;

    public ServerImpl() throws RemoteException {
        super();
    }

    public ServerImpl(int port) throws RemoteException {
        super(port);
    }

    @Override
    public synchronized int conta_righe(String fileName, int soglia) throws RemoteException {

        try {

            File file = new File(fileName);

            if (!file.isFile() || !file.canRead())
                throw new RemoteException("File non esistente o non leggibile, impossibile contare le righe!");

            int currChar;
            int nCurrWord = 0;  //numero parole linea corrente
            int res = 0;  //numero linee con più di "soglia" parole
            FileReader inputStream = null;

            try {

                inputStream = new FileReader(file);

                while ((currChar = inputStream.read()) != -1) {

                    //se incontro ' ' ho terminato una parola --> incremento
                    if (currChar == ' ')
                        nCurrWord++;

                    //se incontro '\n' ho terminato una parola --> incremento, valuto soglia e incremento eventualmente res
                    if (currChar == '\n') {
                        nCurrWord++;
                        if (nCurrWord > soglia)
                            res++;
                        nCurrWord = 0;
                    }
                }

                inputStream.close();
                return res;

            } catch (FileNotFoundException e) {
                throw new RemoteException("File not found");
            }


        } catch (IOException e) {
            throw new RemoteException("IO Exception: " + e.getMessage());
        }

    }

    @Override
    public synchronized int elimina_riga(String fileName, int nRiga) throws RemoteException {

        try {

            File file = new File(fileName);
            File tmpFile = new File("tmp.txt");

            if (tmpFile.createNewFile())
                System.out.println("File tmpFile creato correttamente");
            else
                throw new RemoteException("Impossibile creare tmpFile");

            if (!file.isFile() || !file.canRead())
                throw new RemoteException("File non esistente o non leggibile, impossibile contare le righe!");

            BufferedReader fin = new BufferedReader(new FileReader(file));


            String currLine = null;
            int nLinea = 1;

            while ((fin.readLine()) != null) {
                nLinea++;
            }

            if (nRiga > nLinea)
                return -1;

            nLinea = 1;
            fin.close();

            fin = new BufferedReader(new FileReader(file));
            BufferedWriter fout = new BufferedWriter(new FileWriter(tmpFile));

            while ((currLine = fin.readLine()) != null) {
                if (nLinea != nRiga){
                    fout.write(currLine);
                    fout.write('\n');
                }
                nLinea++;
            }

            fin.close();
            fout.close();

            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return nLinea-2;

        } catch (IOException e) {
            throw new RemoteException("IO Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        String registryHost = "localhost";
        String serviceName = "GestoreFile";

        try {

            ServerImpl server = new ServerImpl();
            String host = "//" + registryHost + ":" + registryPort + "/" + serviceName;
            Naming.rebind(host, server);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

}