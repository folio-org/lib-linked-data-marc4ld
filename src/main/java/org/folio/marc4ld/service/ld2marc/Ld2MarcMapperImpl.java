package org.folio.marc4ld.service.ld2marc;

import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.LdUtil.isEmpty;
import static org.folio.marc4ld.util.MarcUtil.sortFields;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.RawMarc;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.configuration.Marc4LdObjectMapper;
import org.folio.marc4ld.enums.UnmappedMarcHandling;
import org.folio.marc4ld.service.ld2marc.leader.LeaderGenerator;
import org.folio.marc4ld.service.ld2marc.postprocessor.Ld2MarcPostProcessor;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Ld2MarcMapperImpl implements Ld2MarcMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(INSTANCE);

  private final Marc4LdObjectMapper objectMapper;
  private final LeaderGenerator leaderGenerator;
  private final Resource2MarcRecordMapper resourceMapper;
  private final MarcReaderProcessor marcReaderProcessor;
  private final List<Ld2MarcPostProcessor> postProcessors;

  @Override
  public String toMarcJson(Resource resource) {
    return toMarcJson(resource, UnmappedMarcHandling.MERGE);
  }

  @Override
  public String toMarcJson(Resource resource, UnmappedMarcHandling marcHandling) {
    if (isValid(resource)) {
      var marcRecord = resourceMapper.toMarcRecord(resource);
      postProcessors
        .forEach(processor -> processor.postProcess(resource, marcRecord));
      getUnmappedMarc(resource)
        .ifPresent(unmappedMarc -> addUnmappedMarc(marcHandling, unmappedMarc, marcRecord));
      leaderGenerator.addLeader(marcRecord, resource);
      return toJsonString(marcRecord);
    } else {
      return null;
    }
  }

  private boolean isValid(Resource resource) {
    if (isEmpty(resource)) {
      log.warn("Given resource is empty, there is no doc and edges [{}]", resource);
      return false;
    }
    if (notEqual(resource.getTypes(), SUPPORTED_TYPES)) {
      log.warn("Given resource is not an Instance [{}]", resource);
      return false;
    }
    return true;
  }

  private Optional<Record> getUnmappedMarc(Resource resource) {
    return Optional.ofNullable(resource.getUnmappedMarc())
      .map(RawMarc::getContent)
      .map(marcReaderProcessor::readMarc)
      .flatMap(Stream::findFirst);
  }

  private void addUnmappedMarc(UnmappedMarcHandling marcHandling, Record unmappedMarcRecord, Record marcRecord) {
    if (marcHandling == UnmappedMarcHandling.APPEND) {
      sortFields(marcRecord);
      unmappedMarcRecord.getVariableFields()
        .stream()
        .sorted(comparing(VariableField::getTag))
        .forEach(marcRecord::addVariableField);
    } else {
      unmappedMarcRecord.getVariableFields()
        .forEach(marcRecord::addVariableField);
      sortFields(marcRecord);
    }
  }

  private String toJsonString(Record marcRecord) {
    try (var os = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(os);
      writer.write(marcRecord);
      writer.close();
      return toPrettyJson(os.toString());
    } catch (IOException e) {
      log.error("Exception during resource to marc conversion", e);
      return null;
    }
  }

  private String toPrettyJson(String jsonString) throws JsonProcessingException {
    var jsonObject = objectMapper.readValue(jsonString, Object.class);
    return objectMapper.writeValueAsString(jsonObject);
  }

}
