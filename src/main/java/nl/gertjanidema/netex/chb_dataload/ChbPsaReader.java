package nl.gertjanidema.netex.chb_dataload;

import java.util.Iterator;

import org.springframework.batch.item.xml.StaxEventItemReader;

import nl.chb.psa.Quay;
import nl.chb.psa.Userstopcodedata;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbPsa;
import nl.gertjanidema.netex.core.batch.ParentChildMapper;
import nl.gertjanidema.netex.core.batch.StaxParentChildEventItemReader;

public class ChbPsaReader extends StaxParentChildEventItemReader<Userstopcodedata, DlChbPsa, Quay> {

    public ChbPsaReader(StaxEventItemReader<Quay> parentReader,
            ParentChildMapper<Userstopcodedata, DlChbPsa, Quay> mapper) {
        super(parentReader, mapper);
    }

    @Override
    protected Iterator<Userstopcodedata> getChildIterator(Quay parent) {
        return parent.getUserstopcodes().getUserstopcodedata().iterator();
    }

}
