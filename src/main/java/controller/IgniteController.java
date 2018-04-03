package controller;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.transactions.TransactionException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.cache.Cache;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class IgniteController {
    private static final Logger LOGGER = LoggerFactory.getLogger(IgniteController.class);

    @Autowired
    Ignite ignite;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test() {
        return "working";
    }

    @GetMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllCacheData(@RequestParam("cache") String cacheName) {
        Map<Object, Object> data = new HashMap<>();
        try {
            LOGGER.info("Reading cache "+cacheName);
            IgniteCache<String, Object> cache = ignite.getOrCreateCache(cacheName);
            Iterator<Cache.Entry<String, Object>> iter = cache.iterator();
            while (iter.hasNext()) {
                Cache.Entry<String, Object> entry = iter.next();
                data.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            LOGGER.error("Error: " + e.getMessage());
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (data.isEmpty()) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }

    @PutMapping(value = "/putString", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") String value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @PutMapping(value = "/putInt", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") int value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @PutMapping(value = "/putlong", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") long value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @PutMapping(value = "/putFloat", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") float value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @PutMapping(value = "/putDouble", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") double value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @PutMapping(value = "/putBoolean", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key,
                                          @RequestParam(value = "value") boolean value,
                                          @RequestParam("cache") String cacheName) {
        return putDataInCache(key, value, cacheName);
    }

    @NotNull
    private ResponseEntity<?> putDataInCache(String key, Object value, String cacheName) {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            IgniteCache<String, Object> cache = ignite.getOrCreateCache(cacheName);
            cache.put(key, value);
            LOGGER.info("Data inserted in cache successfully.");
        } catch (TransactionException e) {
            errMsg = e.getMessage();
            LOGGER.error("Data insertion failed. ", e.getMessage());
        }

        if (errMsg != "") {
            data.put("msg", errMsg);
            return new ResponseEntity<Object>(data, HttpStatus.NOT_MODIFIED);
        } else {
            data.put("msg", "Data inserted in cache successfully.");
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/cache/{key}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> getCacheData(@PathVariable(value = "key") String key,
                                          @RequestParam("cache") String cacheName) {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            IgniteCache<String, Object> cache = ignite.getOrCreateCache(cacheName);
            Object s = cache.get(key);
            data.put("data", s);
            LOGGER.info("Data retrieve from cache successfully.");
        } catch (TransactionException e) {
            errMsg = e.getMessage();
            LOGGER.error("Data retrieving failed. ", e.getMessage());
        }

        if (data.get("data") == null) {
            data.put("msg", errMsg);
            return new ResponseEntity<Object>(data, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> deleteAllCacheData(@RequestParam("cache") String cacheName) {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            IgniteCache<String, Object> cache = ignite.getOrCreateCache(cacheName);
            cache.clear();
            LOGGER.info("Cache clear successfully.");
        } catch (TransactionException e) {
            errMsg = e.getMessage();
            LOGGER.error("Cache clearing failed. ", e.getMessage());
        }

        if (errMsg != "") {
            data.put("msg", errMsg);
            return new ResponseEntity<Object>(data, HttpStatus.NO_CONTENT);
        } else {
            data.put("msg", "Cache clear successfully.");
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }
}
