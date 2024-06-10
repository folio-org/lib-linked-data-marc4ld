package org.folio.marc4ld.service.marc2ld.reader;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.marc4j.MarcJsonReader;
import org.marc4j.MarcReader;
import org.springframework.stereotype.Component;

@Component
public class MarcReaderProcessorImpl implements MarcReaderProcessor {

  @Override
  public Stream<org.marc4j.marc.Record> readMarc(String marc) {
    var reader = getReader(marc);
    var iterator = new MarcReaderIterator(reader);
    return StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
      false
    );
  }

  private MarcReader getReader(String marc) {
    return new MarcJsonReader(new ByteArrayInputStream(marc.getBytes(StandardCharsets.UTF_8)));
  }

  private record MarcReaderIterator(MarcReader marcReader) implements Iterator<org.marc4j.marc.Record> {

    @Override
    public boolean hasNext() {
      return marcReader.hasNext();
    }

    @Override
    public org.marc4j.marc.Record next() {
      if (hasNext()) {
        return marcReader.next();
      }
      throw new NoSuchElementException("No more records");
    }
  }
}
