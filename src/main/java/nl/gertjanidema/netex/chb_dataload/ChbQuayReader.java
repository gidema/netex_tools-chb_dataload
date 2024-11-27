package nl.gertjanidema.netex.chb_dataload;

import java.util.Iterator;

import org.springframework.batch.item.xml.StaxEventItemReader;

import nl.chb.Quay;
import nl.chb.Stopplace;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbQuay;
import nl.gertjanidema.netex.core.batch.ParentChildMapper;
import nl.gertjanidema.netex.core.batch.StaxParentChildEventItemReader;

public class ChbQuayReader extends StaxParentChildEventItemReader<Quay, DlChbQuay, Stopplace> {

    public ChbQuayReader(StaxEventItemReader<Stopplace> parentReader,
            ParentChildMapper<Quay, DlChbQuay, Stopplace> mapper) {
        super(parentReader, mapper);
    }

    @Override
    protected Iterator<Quay> getChildIterator(Stopplace parent) {
        return parent.getQuays().getQuay().iterator();
    }

}
