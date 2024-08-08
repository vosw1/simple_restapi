package com.example.simpe_restapi._core;

import com.example.simpe_restapi.item.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class FileStorage {

    private static final String FILE_PATH = "data/items.xml";

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();

    // JSON 데이터를 XML로 저장
    public void saveItemsAsXml(String jsonData) throws IOException {
        // JSON 데이터를 객체로 변환
        List<Item> items = jsonMapper.readValue(jsonData, jsonMapper.getTypeFactory().constructCollectionType(List.class, Item.class));

        // 객체를 XML로 변환하고 파일로 저장
        xmlMapper.writeValue(new File(FILE_PATH), items);
    }

    // XML 파일에서 JSON 데이터 읽기
    public String getItemsAsJson() throws IOException {
        // XML 파일에서 객체로 변환
        List<Item> items = xmlMapper.readValue(new File(FILE_PATH), xmlMapper.getTypeFactory().constructCollectionType(List.class, Item.class));

        // 객체를 JSON으로 포맷팅하여 변환
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(items);
    }
}
