package nl.gertjanidema.netex.chb_dataload.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "chb")
@Getter
@Setter
public class DlChbStopPlace {
    @Id
    private String id;
    private LocalDateTime validFrom;
    private String stopPlaceCode;
    private String stopPlaceType;
    private String publicName;
    private String town;
    private String publicNameMedium;
    private String publicNameLong;
    private String description;
    private String stopPlaceIndication;
    private String street;
    private String stopPlaceStatus;
    private LocalDateTime mutationDate;
    private Long uicCode;
    private String internalName;
    private String stopPlaceOwner;
    private String placeCode;
}
