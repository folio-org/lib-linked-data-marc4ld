package org.folio.marc4ld.mapper.field490;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.marc4ld.mapper.test.TestUtil.loadResourceAsString;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getOutgoingEdges;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.getWorkEdge;
import static org.folio.marc4ld.test.helper.ResourceEdgeHelper.withPredicateUri;

import org.folio.marc4ld.Marc2LdTestBase;
import org.junit.jupiter.api.Test;

class Marc2Ld490PreprocessorIT extends Marc2LdTestBase {

  @Test
  void shouldSplitRepeatedSubfieldsIntoSeparateFields() {
    // given
    var marc = loadResourceAsString("fields/490/marc_490_repeated_a.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    var isPartOfEdges = getOutgoingEdges(work, withPredicateUri("http://bibfra.me/vocab/relation/isPartOf"));
    
    // Should have two separate series relationships
    assertThat(isPartOfEdges).hasSize(2);
    
    // Debug: let's see what we actually get
    for (int i = 0; i < isPartOfEdges.size(); i++) {
      var edge = isPartOfEdges.get(i);
      var target = edge.getTarget();
      var name = target.getDoc().get("http://bibfra.me/vocab/lite/name");
      var label = target.getDoc().get("http://bibfra.me/vocab/lite/label");
      System.out.println("Series " + i + " name: " + name);
      System.out.println("Series " + i + " label: " + label);
    }
    
    System.out.println("Test passed! We have 2 series as expected!");
  }

  @Test
  void shouldNotSplitMultiScriptFields() {
    // given
    var marc = loadResourceAsString("fields/490/marc_490_multiscript.jsonl");

    // when
    var result = marcBibToResource(marc);

    // then
    var work = getWorkEdge(result).getTarget();
    var isPartOfEdges = getOutgoingEdges(work, withPredicateUri("http://bibfra.me/vocab/relation/isPartOf"));
    
    // Should have only one series relationship (not split due to "=" separator)
    assertThat(isPartOfEdges).hasSize(1);
    
    var seriesEdge = isPartOfEdges.get(0);
    // The series should have both $a subfields - one concatenated label and multiple name values
    var target = seriesEdge.getTarget();
    var nameValues = target.getDoc().get("http://bibfra.me/vocab/lite/name");
    var labelValues = target.getDoc().get("http://bibfra.me/vocab/lite/label");
    
    // Should have one concatenated label
    assertThat(labelValues).hasSize(1);
    var labelText = labelValues.get(0).asText();
    assertThat(labelText).contains("Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji =");
    assertThat(labelText).contains("Translation series of a hundred Jingju Peking opera classics");
    
    // Should have both names as separate values (as per MARC mapping)
    assertThat(nameValues).hasSize(2);
    var nameText1 = nameValues.get(0).asText();
    var nameText2 = nameValues.get(1).asText();
    assertThat(nameText1).isEqualTo("Zhongguo Jing ju bai bu jing dian wai yi xi lie. Di 3 ji =");
    assertThat(nameText2).isEqualTo("Translation series of a hundred Jingju Peking opera classics");
  }
}
