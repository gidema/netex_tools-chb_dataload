package nl.gertjanidema.netex.chb_dataload.mapping;

import org.springframework.batch.item.ItemProcessor;

import nl.chb.Stopplace;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbStopPlace;

public class ChbStopPlaceProcessor implements ItemProcessor<Stopplace, DlChbStopPlace> {

    @Override
    public DlChbStopPlace process(Stopplace stopplace) throws Exception {
        var dto = new DlChbStopPlace();

        dto.setId(stopplace.getID());
        dto.setValidFrom(stopplace.getValidfrom());
        dto.setStopPlaceCode(stopplace.getStopplacecode());
        dto.setStopPlaceType(stopplace.getStopplacetype());
        dto.setPublicName(stopplace.getStopplacename().getPublicname());
        dto.setTown(stopplace.getStopplacename().getTown());
        dto.setPublicNameMedium(stopplace.getStopplacename().getPublicnamemedium());
        dto.setPublicNameLong(stopplace.getStopplacename().getPublicnamelong());
        dto.setDescription(stopplace.getStopplacename().getDescription());
        dto.setStopPlaceIndication(stopplace.getStopplacename().getStopplaceindication());
        dto.setStreet(stopplace.getStopplacename().getStreet());
        dto.setStopPlaceStatus(stopplace.getStopplacestatusdata().getStopplacestatus());
        dto.setMutationDate(stopplace.getMutationdate());
        dto.setUicCode(stopplace.getUiccode());
        dto.setInternalName(stopplace.getInternalname());
        dto.setStopPlaceOwner(stopplace.getStopplaceowner().getStopplaceownercode());
        dto.setPlaceCode(stopplace.getPlacecode());
        return dto;
    }

}
