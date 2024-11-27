package nl.gertjanidema.netex.chb_dataload.mapping;

import java.util.stream.Collectors;

import nl.chb.Quay;
import nl.chb.Stopplace;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbQuay;
import nl.gertjanidema.netex.core.batch.ParentChildMapper;

public class QuayMapper implements ParentChildMapper<Quay, DlChbQuay, Stopplace> {
    @Override
    public DlChbQuay map(Quay quay, Stopplace stopPlace) {
        var chbQuay = new DlChbQuay();
        chbQuay.setId(quay.getID());
        chbQuay.setStopPlaceId(stopPlace.getID());
        chbQuay.setStopPlaceName(stopPlace.getStopplacename().getPublicname());
        chbQuay.setStopPlaceLongName(stopPlace.getStopplacename().getPublicnamelong());
        chbQuay.setQuayName(quay.getQuaynamedata().getQuayname());
        chbQuay.setStopSideCode(quay.getQuaynamedata().getStopsidecode());
        chbQuay.setTransportModes(quay.getQuaytransportmodes().getTransportmodedata()
            .stream().map(data -> 
            data.getTransportmode()).collect(Collectors.toList()));
        chbQuay.setMutationdate(quay.getMutationdate());
        chbQuay.setOnlygetout(quay.isOnlygetout());
        chbQuay.setQuaycode(quay.getQuaycode());
        chbQuay.setValidfrom(quay.getValidfrom());
        chbQuay.setQuayType(quay.getQuaytypedata().getQuaytype());
        chbQuay.setQuayStatus(quay.getQuaystatusdata().getQuaystatus());
        chbQuay.setXCoordinate(Double.valueOf(quay.getQuaylocationdata().getRdX()));
        chbQuay.setYCoordinate(Double.valueOf(quay.getQuaylocationdata().getRdY()));
        chbQuay.setBearing(quay.getQuaybearing().getCompassdirection());
        chbQuay.setTown(quay.getQuaylocationdata().getTown());
        chbQuay.setLevel(quay.getQuaylocationdata().getLevel());
        chbQuay.setStreet(quay.getQuaylocationdata().getStreet());
        chbQuay.setLocation(quay.getQuaylocationdata().getLocation());  

        return chbQuay;
    }
}
