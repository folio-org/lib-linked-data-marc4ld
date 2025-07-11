package org.folio.marc4ld.service.marc2ld.bib.preprocessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.util.Constants.A;
import static org.folio.marc4ld.util.Constants.TAG_490;

import org.folio.marc4ld.service.marc2ld.preprocessor.DataFieldPreprocessor.PreprocessorContext;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.impl.MarcFactoryImpl;

class Bib490PreprocessorTest {

  private final MarcFactory marcFactory = MarcFactoryImpl.newInstance();
  private final Bib490Preprocessor preprocessor = new Bib490Preprocessor(marcFactory);

  @Test
  void shouldSplitRepeatedSubfields() {
    // given - field with repeated $a subfields without "="
    var field = marcFactory.newDataField(TAG_490, '0', ' ');
    field.addSubfield(marcFactory.newSubfield(A, "Technical assistance publication (TAP) series ;"));
    field.addSubfield(marcFactory.newSubfield('v', "19."));
    field.addSubfield(marcFactory.newSubfield(A, "Criminal justice subseries ;"));
    field.addSubfield(marcFactory.newSubfield('v', "v. 2"));

    var context = new PreprocessorContext(null, field);

    // when
    var result = preprocessor.preprocess(context);

    // then
    assertThat(result).hasSize(2);
    
    // First field should have first $a and first $v
    var firstField = result.get(0);
    assertThat(firstField.getSubfields()).hasSize(2);
    assertThat(firstField.getSubfield(A).getData())
        .isEqualTo("Technical assistance publication (TAP) series ;");
    assertThat(firstField.getSubfield('v').getData()).isEqualTo("19.");
    
    // Second field should have second $a and second $v
    var secondField = result.get(1);
    assertThat(secondField.getSubfields()).hasSize(2);
    assertThat(secondField.getSubfield(A).getData()).isEqualTo("Criminal justice subseries ;");
    assertThat(secondField.getSubfield('v').getData()).isEqualTo("v. 2");
  }

  @Test
  void shouldNotSplitMultiScriptFields() {
    // given - field with $a ending with "=" (multi-script cataloging)
    var field = marcFactory.newDataField(TAG_490, '0', ' ');
    field.addSubfield(marcFactory.newSubfield(A, 
        "Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji ="));
    field.addSubfield(marcFactory.newSubfield(A, 
        "Translation series of a hundred Jingju (Peking opera) classics"));

    var context = new PreprocessorContext(null, field);

    // when
    var result = preprocessor.preprocess(context);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSubfields()).hasSize(2);
    assertThat(result.get(0).getSubfield(A).getData()).isEqualTo("Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji =");
  }

  @Test
  void shouldNotProcessSingleSubfield() {
    // given - field with only one $a subfield
    var field = marcFactory.newDataField(TAG_490, '0', ' ');
    field.addSubfield(marcFactory.newSubfield(A, "Single series name"));
    field.addSubfield(marcFactory.newSubfield('v', "volume 1"));
    field.addSubfield(marcFactory.newSubfield('x', "1234-5678"));

    var context = new PreprocessorContext(null, field);

    // when
    var result = preprocessor.preprocess(context);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isSameAs(field); // Should return the original field unchanged
  }

  @Test
  void shouldHandleComplexSubfieldGrouping() {
    // given - field with multiple $a subfields and various related subfields
    var field = marcFactory.newDataField(TAG_490, '1', ' ');
    field.addSubfield(marcFactory.newSubfield(A, "First series"));
    field.addSubfield(marcFactory.newSubfield('v', "vol. 1"));
    field.addSubfield(marcFactory.newSubfield('x', "1111-1111"));
    field.addSubfield(marcFactory.newSubfield(A, "Second series"));
    field.addSubfield(marcFactory.newSubfield('v', "vol. 2"));
    field.addSubfield(marcFactory.newSubfield('y', "0222-2222"));
    field.addSubfield(marcFactory.newSubfield('z', "0333-3333"));
    field.addSubfield(marcFactory.newSubfield(A, "Third series"));
    field.addSubfield(marcFactory.newSubfield('3', "materials specified"));

    var context = new PreprocessorContext(null, field);

    // when
    var result = preprocessor.preprocess(context);

    // then
    assertThat(result).hasSize(3);
    
    // First field: $a First series $v vol. 1 $x 1111-1111
    var firstField = result.get(0);
    assertThat(firstField.getSubfields()).hasSize(3);
    assertThat(firstField.getSubfield(A).getData()).isEqualTo("First series");
    assertThat(firstField.getSubfield('v').getData()).isEqualTo("vol. 1");
    assertThat(firstField.getSubfield('x').getData()).isEqualTo("1111-1111");
    
    // Second field: $a Second series $v vol. 2 $y 0222-2222 $z 0333-3333
    var secondField = result.get(1);
    assertThat(secondField.getSubfields()).hasSize(4);
    assertThat(secondField.getSubfield(A).getData()).isEqualTo("Second series");
    assertThat(secondField.getSubfield('v').getData()).isEqualTo("vol. 2");
    assertThat(secondField.getSubfield('y').getData()).isEqualTo("0222-2222");
    assertThat(secondField.getSubfield('z').getData()).isEqualTo("0333-3333");
    
    // Third field: $a Third series $3 materials specified
    var thirdField = result.get(2);
    assertThat(thirdField.getSubfields()).hasSize(2);
    assertThat(thirdField.getSubfield(A).getData()).isEqualTo("Third series");
    assertThat(thirdField.getSubfield('3').getData()).isEqualTo("materials specified");
  }

  @Test
  void shouldReturnCorrectTags() {
    // when
    var tags = preprocessor.getTags();

    // then
    assertThat(tags).containsExactly(TAG_490);
  }

  @Test
  void shouldHandleSpecificMultiScriptCase() {
    // given - exact same field structure as in marc_490_multiscript.jsonl
    var field = marcFactory.newDataField(TAG_490, '0', ' ');
    field.addSubfield(marcFactory.newSubfield(A, 
        "Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji ="));
    field.addSubfield(marcFactory.newSubfield(A, 
        "Translation series of a hundred Jingju (Peking opera) classics"));

    var context = new PreprocessorContext(null, field);

    // when
    var result = preprocessor.preprocess(context);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isSameAs(field); // Should return the original field unchanged
    assertThat(result.get(0).getSubfields()).hasSize(2);
  }
}
