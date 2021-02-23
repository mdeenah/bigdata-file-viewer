package org.eugene.controller;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.eugene.core.avro.AVROReader;
import org.eugene.model.TableMeta;
import org.eugene.ui.Constants;
import org.eugene.ui.Notifier;

public class AVRODataParser extends DataParser {

    @Override
    public boolean parseData(Path path) {
        AVROReader reader = new AVROReader();
        List<GenericRecord> originalData = reader.read(path);
        if(originalData == null)
        {
            return false;
        }
        if (originalData.isEmpty()) {
            Notifier.info("The file is empty");
            return false;
        }

        GenericRecord firstRecord = originalData.get(0);
        Schema schema = firstRecord.getSchema();

        int rowNumber = originalData.size();
        List<String> propertyList = new ArrayList<>();
        for (Schema.Field field: schema.getFields())
        {
            String property = field.name();
            propertyList.add(property);
        }
        int columnNumber = propertyList.size();
        TableMeta tableMeta = new TableMeta();
        tableMeta.setRow(rowNumber);
        tableMeta.setColumn(columnNumber);

        List<List<String>> data = new ArrayList<>();
        for (int i = 0; i < originalData.size(); i++) {
            GenericRecord record = originalData.get(i);
            List<String> commonRecord = new ArrayList<>();
            for (int j = 0; j < columnNumber; j++) {
                if (record.get(j) == null){
                    commonRecord.add(Constants.NULL);
                }else{
                    LogicalType lt = LogicalTypes.fromSchema(schema.getField(propertyList.get(j)).schema());
                    if (lt != null && lt.getName().equals("date")) {
                        commonRecord.add(LocalDate.ofEpochDay(Long.valueOf(String.valueOf(record.get(j)))).format(DateTimeFormatter.ISO_DATE));
                    } else {
                        commonRecord.add(String.valueOf(record.get(j)));
                    }
                }
            }
            data.add(commonRecord);
        }

        super.persistData(schema, propertyList, data, tableMeta);

        return true;
    }
}
