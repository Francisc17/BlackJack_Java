package pt.ipc.estgoh.projetoFinal.model;

public class RemotePlayer {

    private InterfacePlayer remotePlayer;
    private Player dataRemotePlayer;

    public RemotePlayer(InterfacePlayer aRemotePLayer, Player aDataRemotePlayer) {
        this.remotePlayer = aRemotePLayer;
        this.dataRemotePlayer = aDataRemotePlayer;
    }

    public InterfacePlayer getRemotePlayer() {
        return this.remotePlayer;
    }

    public Player getDataRemotePlayer() {
        return this.dataRemotePlayer;
    }
}
