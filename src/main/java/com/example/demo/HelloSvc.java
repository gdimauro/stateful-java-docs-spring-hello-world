package com.example.demo;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
* This is a REST Controller handling requests for the "/api" endpoint.
*/
@RestController
@RequestMapping("/api")
public class HelloSvc {

    /**
    * This is the data store to hold key-value pairs.
    */
    private static Map<String, String> dataStore = new Hashtable<>();

    /**
    * This is a static block that populates the data store with some initial values.
    */
    static {
        dataStore.put("hello", "World");
        dataStore.put("greeting", "Hello");
        dataStore.put("planet", "World");
    }

    @Cacheable(value = "mycache")
    @GetMapping("/allDataAsString")
    public ResponseEntity<String> getAllDataAsString() {
        dataStore.put("time", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(dataStore.toString());
    }

    /**
     * This method is cached, but the cache is evicted when the data is updated.
     * 
     * @return
     */
    @Cacheable(value = "mycache")
    @GetMapping("/allDataAsDictionary")
    public ResponseEntity<Map<String, String>> getAllDataAsDictionary() {
        dataStore.put("time", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(dataStore);
    }

    /**
     * Recupera i dati dalla cache o dallo store di dati.
     * 
     * @param key la chiave per recuperare i dati
     * @return una ResponseEntity contenente il valore associato alla chiave se
     *         presente,
     *         altrimenti una ResponseEntity con una risposta "not found"
     */
    @Cacheable(value = "mycache", key = "#key")
    @GetMapping("/data/{key}")
    public ResponseEntity<String> getData(@PathVariable String key) {
        String value = dataStore.get(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(value);
        }
    }

    /**
     * This method is cached, but the cache is evicted when the data is updated.
     * 
     * @param data
     * @return
     */
    @PostMapping(path = "/data", consumes = "application/json")
    @CacheEvict(value = "mycache", allEntries = true)
    public ResponseEntity<Void> updateData(@RequestBody Map<String, String> data) {
        if (data.containsKey("key") && data.containsKey("value")) {
            String key = data.get("key");
            String value = data.get("value");
            dataStore.put(key, value);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * This method updates the value of a key in the data store and clears the
     * associated cache.
     * 
     * @param key   The key whose value needs to be updated.
     * @param value The new value for the key.
     * @return A ResponseEntity object that indicates if the update was successful
     *         or not.
     */
    @PutMapping("/data/{key}")
    @CacheEvict(value = "mycache")
    public ResponseEntity<Void> updateData(@PathVariable String key, @RequestParam String value) {
        if (dataStore.containsKey(key)) {
            dataStore.put(key, value);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This method deletes a key-value pair from the data store and clears the
     * associated cache.
     *
     * @param key The key whose corresponding value needs to be deleted.
     * @return A ResponseEntity object that indicates if the delete operation was
     *         successful or not.
     */
    @DeleteMapping("/data/{key}")
    @CacheEvict(value = "mycache")
    public ResponseEntity<Void> deleteData(@PathVariable String key) {
        if (dataStore.containsKey(key)) {
            dataStore.remove(key);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
