package com.example;

import java.io.Serializable;

public class MessageReseau implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TypeMessage {
        CONFIGURATION_JEU,
        DEPLACEMENT_RAQUETTE,
        ETAT_BALLE,
        ETAT_PIECES,
        FIN_JEU,
        CHANGEMENT_VITESSE,
        SYNCHRONISATION_COMPLETE
    }
    
    private TypeMessage type;
    private Object data;
    
    public MessageReseau(TypeMessage type, Object data) {
        this.type = type;
        this.data = data;
    }
    
    public TypeMessage getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
}
