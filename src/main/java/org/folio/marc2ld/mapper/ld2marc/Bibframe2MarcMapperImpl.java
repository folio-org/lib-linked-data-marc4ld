package org.folio.marc2ld.mapper.ld2marc;

import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc2ld.util.BibframeUtil.isNotEmptyResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.marc2ld.mapper.ld2marc.resource.ResourceMapper;
import org.folio.marc2ld.model.Resource;
import org.marc4j.MarcJsonWriter;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Bibframe2MarcMapperImpl implements Bibframe2MarcMapper {

  private static final MarcFactory MARC_FACTORY = MarcFactory.newInstance();
  private final ObjectMapper objectMapper;
  private final ResourceMapper resourceMapper;

  @Override
  public String map(Resource bibframe) {
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
      var marcRecord = MARC_FACTORY.newRecord();
      resourceMapper.handleResource(bibframe, null, marcRecord);
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
    var leader = MARC_FACTORY.newLeader();
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
