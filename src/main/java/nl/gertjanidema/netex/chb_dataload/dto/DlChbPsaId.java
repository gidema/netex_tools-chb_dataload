package nl.gertjanidema.netex.chb_dataload.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class DlChbPsaId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userStopOwnerCode;
    private String userStopCode;

    public DlChbPsaId() {
        // No Arg constructor to prevent hibernate exception
    }
    
    public DlChbPsaId(String userStopOwnerCode, String userStopCode) {
        super();
        this.userStopOwnerCode = userStopOwnerCode;
        this.userStopCode = userStopCode;
    }
    
    
}