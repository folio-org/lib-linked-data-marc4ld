package org.folio.marc4ld.service.marc2ld.reader;

import java.util.stream.Stream;

public interface MarcReaderProcessor {

  Stream<org.marc4j.marc.Record> readMarc(String marc);

}
