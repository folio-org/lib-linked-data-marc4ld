package org.folio.marc4ld.service.ld2marc;

import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.folio.ld.dictionary.ResourceTypeDictionary.INSTANCE;
import static org.folio.marc4ld.util.LdUtil.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.ld.dictionary.ResourceTypeDictionary;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.leader.LeaderGenerator;
import org.folio.marc4ld.service.ld2marc.resource.Resource2MarcRecordMapper;
import org.marc4j.MarcJsonWriter;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class Ld2MarcMapperImpl implements Ld2MarcMapper {

  private static final Set<ResourceTypeDictionary> SUPPORTED_TYPES = Set.of(INSTANCE);

  private final ObjectMapper objectMapper;
  private final LeaderGenerator leaderGenerator;
  private final Resource2MarcRecordMapper resourceMapper;

  @Override
  public String toMarcJson(Resource resource) {
    if (isEmpty(resource)) {
      log.warn("Given resource is empty, there is no doc and edges [{}]", resource);
      return null;
    }
    if (notEqual(resource.getTypes(), SUPPORTED_TYPES)) {
      log.warn("Given resource is not an Instance [{}]", resource);
      return null;
    }
    try (var os = new ByteArrayOutputStream()) {
      var writer = new MarcJsonWriter(os);
      var marcRecord = resourceMapper.toMarcRecord(resource);
      leaderGenerator.addLeader(marcRecord);
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
