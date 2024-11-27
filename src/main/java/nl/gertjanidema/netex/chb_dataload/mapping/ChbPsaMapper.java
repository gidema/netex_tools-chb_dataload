package nl.gertjanidema.netex.chb_dataload.mapping;

import nl.chb.psa.Quay;
import nl.chb.psa.Userstopcodedata;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbPsa;

public class ChbPsaMapper implements nl.gertjanidema.netex.core.batch.ParentChildMapper<Userstopcodedata, DlChbPsa, Quay> {
    
    @Override
    public DlChbPsa map(Userstopcodedata stopcode, Quay quay) {
        var psa = new DlChbPsa();
        psa.setUserStopOwnerCode(stopcode.getDataownercode());
        psa.setUserStopCode(stopcode.getUserstopcode());
        psa.setUserStopValidFrom(stopcode.getValidfrom());
        psa.setUserStopValidThru(stopcode.getValidthru());
        psa.setQuayCode(quay.getQuaycode());
        psa.setQuayRef(quay.getQuayref());
        psa.setStopplaceCode(quay.getStopplacecode());
        psa.setStopplaceRef(quay.getStopplaceref());
        return psa;
    }
}
