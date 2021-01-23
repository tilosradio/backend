package hu.tilos.radio.backend.mongoconverters;

import com.mongodb.DBRef;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class FromDbRef implements Converter<DBRef, String> {

    @Override
    public String convert(DBRef dbRef) {
        return dbRef.getId().toString();
    }

}