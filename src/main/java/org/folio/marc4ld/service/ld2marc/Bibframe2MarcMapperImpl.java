package org.folio.marc4ld.service.ld2marc;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.BibframeUtil.isNotEmptyResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.marc4ld.model.Resource;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Bibframe2MarcMapperImpl implements Bibframe2MarcMapper {

  private final MarcFactory marcFactory;
  private final ObjectMapper objectMapper;
  private final Resource2MarcRecordMapper resourceMapper;

  @Override
  public String toMarcJson(Resource bibframe) {
    if (!isNotEmptyResource(bibframe)) {
      log.warn("Given bibframe resource is empty, there is no doc and edges [{}]", bibframe);
      return null;
    }
    if (!bibframe.getTypes().equals(Set.of(INSTANCE))) {
      log.warn("Given bibframe resource is not an Instance [{}]", bibframe);
      return null;
    }
    try (var os = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(os);
      var marcRecord = resourceMapper.toMarcRecord(bibframe);
      setLeader(marcRecord);
      writer.write(marcRecord);
      writer.close();
      return toPrettyJson(os.toString());
    } catch (IOException e) {
      log.error("Exception during bibframe to marc conversion", e);
      return null;
    }
  }

  private String toPrettyJson(String jsonString) throws JsonProcessingException {
    var jsonObject = objectMapper.readValue(jsonString, Object.class);
    return objectMapper.writeValueAsString(jsonObject);
  }

  private void setLeader(Record marcRecord) {
    var leader = marcFactory.newLeader();
    // tbd
    leader.setRecordStatus('n'); // new
    leader.setTypeOfRecord('a'); // book record
    leader.setImplDefined1(new char[] {});
    leader.setCharCodingScheme('a');  // unicode
    leader.setIndicatorCount(2);
    leader.setSubfieldCodeLength(1);
    leader.setBaseAddressOfData(0);
    leader.setImplDefined2(new char[] {});
    leader.setEntryMap(new char[] {});
    marcRecord.setLeader(leader);
  }

}
