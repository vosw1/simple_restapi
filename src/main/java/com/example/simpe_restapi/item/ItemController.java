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
@CrossOrigin(origins = "http://localhost:8080")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private FileStorage fileStorage;

    @GetMapping
    public ResponseEntity<String> getItems() {
        try {
            String jsonFromXml = fileStorage.getItemsAsJson();
            return new ResponseEntity<>(jsonFromXml, HttpStatus.OK);
        } catch (IOException e) {
            List<Item> items = itemRepository.findAll();
            try {
                String jsonFromDb = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(items);
                return new ResponseEntity<>(jsonFromDb, HttpStatus.OK);
            } catch (IOException jsonException) {
                return new ResponseEntity<>("Error occurred while converting data to JSON", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable int id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        try {
            Item createdItem = itemRepository.save(item);
            saveAllItemsToXml();
            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable int id, @RequestBody Item item) {
        Optional<Item> existingItem = itemRepository.findById(id);
        if (existingItem.isPresent()) {
            try {
                item.setId(id);
                Item updatedItem = itemRepository.save(item);
                saveAllItemsToXml();
                return new ResponseEntity<>(updatedItem, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable int id) {
        itemRepository.deleteById(id);
        try {
            saveAllItemsToXml();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveAllItemsToXml() throws IOException {
        List<Item> items = itemRepository.findAll();
        String jsonData = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(items);
        fileStorage.saveItemsAsXml(jsonData);
    }
}