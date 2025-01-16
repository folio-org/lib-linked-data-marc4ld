package org.folio.marc4ld.service.ld2marc.mapper.custom.impl;

import static org.folio.marc4ld.util.Constants.SPACE;

import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.folio.ld.dictionary.model.Resource;
import org.folio.marc4ld.service.ld2marc.mapper.custom.Ld2MarcCustomMapper;
import org.folio.marc4ld.service.ld2marc.resource.field.ControlFieldsBuilder;
import org.folio.marc4ld.service.marc2ld.reader.MarcReaderProcessor;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
@RequiredArgsConstructor
public class Ld2MarcRawDataMapper implements Ld2MarcCustomMapper {

  private final MarcReaderProcessor marcReaderProcessor;
  private final MarcFactory marcFactory;

  @Override
  public void map(Resource resource, Context context) {
    marcReaderProcessor.readMarc(resource.getRawData())
      .findFirst()
      .ifPresent(marcRecord -> {
        context.dataFields().addAll(marcRecord.getDataFields());
        mapControlFields(context.controlFieldsBuilder(), marcRecord);
      });
  }

  private void mapControlFields(ControlFieldsBuilder builder, Record marcRecord) {
    var mappedControlFields = builder.build(marcFactory)
      .collect(Collectors.toMap(ControlField::getTag, Function.identity()));

    marcRecord.getControlFields()
      .forEach(rawControlField -> {
        var tag = rawControlField.getTag();
        var data = rawControlField.getData();
        if (mappedControlFields.containsKey(tag)) {
          data = mergeData(data, mappedControlFields.get(tag).getData());
        }
        builder.addFieldValue(tag, data, 0, data.length());
      });
  }

  private String mergeData(String rawData, String mappedData) {
    var stringBuilder = new StringBuilder();
    var rawDataLength = rawData.length();
    var mappedDataLength = mappedData.length();
    var minLength = Math.min(mappedDataLength, rawDataLength);
    var maxLength = Math.max(mappedDataLength, rawDataLength);

    for (var i = 0; i < minLength; i++) {
      var mappedChar = mappedData.charAt(i);
      var rawChar = rawData.charAt(i);

      if (mappedChar != SPACE && rawChar != SPACE) {
        stringBuilder.append(mappedChar);
      } else if (rawChar != SPACE) {
        stringBuilder.append(rawChar);
      } else {
        stringBuilder.append(mappedChar);
      }
    }

    if (mappedDataLength > rawDataLength) {
      stringBuilder.append(mappedData, minLength, maxLength);
    } else if (rawDataLength > mappedDataLength) {
      stringBuilder.append(rawData, minLength, maxLength);
    }

    return stringBuilder.toString();
  }
}
