package org.folio.marc4ld.service.ld2marc.field.param;

public record ControlFieldParameter(
  String tag,
  String value,
  int startPosition,
  int endPosition
) {
}
