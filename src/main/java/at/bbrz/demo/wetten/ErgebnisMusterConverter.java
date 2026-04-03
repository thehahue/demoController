package at.bbrz.demo.wetten;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ErgebnisMusterConverter implements AttributeConverter<ErgebnisMuster, String> {

    @Override
    public String convertToDatabaseColumn(ErgebnisMuster attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public ErgebnisMuster convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ErgebnisMuster.fromValue(dbData);
    }
}
