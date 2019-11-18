package model;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp  extends Remote {

    //firma metodo conta righe
    public int conta_righe(String fileName, int soglia) throws RemoteException;

    //firma metodo elimina riga
    public int elimina_riga(String fileName, int nRiga) throws RemoteException;
}
