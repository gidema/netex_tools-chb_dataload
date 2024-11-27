package nl.gertjanidema.netex.chb_dataload.mapping;

import nl.chb.Stopplace;
import nl.gertjanidema.netex.chb_dataload.dto.DlChbStopPlace;
import nl.gertjanidema.netex.core.batch.ParentChildMapper;

public class StopPlaceMapper implements ParentChildMapper<Stopplace, DlChbStopPlace, Void> {
    private ChbStopPlaceProcessor processor = new ChbStopPlaceProcessor();

    @Override
    public DlChbStopPlace map(Stopplace stopplace, Void dummy) throws Exception {
        return processor.process(stopplace);
    }
}
