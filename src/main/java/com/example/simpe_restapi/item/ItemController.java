package com.example.simpe_restapi.item;
import com.example.simpe_restapi._core.FileStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "http://localhost:8080") // 허용할 도메인
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private FileStorage fileStorage;

    @GetMapping
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable int id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item createdItem = itemRepository.save(item);
        try {
            // 모든 아이템을 JSON으로 변환하여 XML로 저장
            List<Item> items = itemRepository.findAll();
            String jsonData = new ObjectMapper().writeValueAsString(items);
            fileStorage.saveItemsAsXml(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item item) {
        Optional<Item> existingItem = itemRepository.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id);
            Item updatedItem = itemRepository.save(item);
            try {
                // 모든 아이템을 JSON으로 변환하여 XML로 저장
                List<Item> items = itemRepository.findAll();
                String jsonData = new ObjectMapper().writeValueAsString(items);
                fileStorage.saveItemsAsXml(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(updatedItem, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable int id) {
        itemRepository.deleteById(id);
        try {
            // 모든 아이템을 JSON으로 변환하여 XML로 저장
            List<Item> items = itemRepository.findAll();
            String jsonData = new ObjectMapper().writeValueAsString(items);
            fileStorage.saveItemsAsXml(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

