package controller;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.lang.IgniteBiTuple;
import org.apache.ignite.transactions.TransactionException;
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
    IgniteCache<String, String> igniteCache;

    @GetMapping(value = "/test", produces = MediaType.TEXT_PLAIN_VALUE)
    public String test(){
        return "working";
    }

    @GetMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllCacheData() {
        Map<Object, Object> data = new HashMap<>();
        LOGGER.info("Cache reading Start");

        try {
            Iterator<Cache.Entry<String, String>> iter = igniteCache.iterator();
            while (iter.hasNext()){
                Cache.Entry<String, String> entry = iter.next();
                data.put(entry.getKey(), entry.getValue());
            }
            LOGGER.info("Cache reading End");
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

    @PutMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> putCacheData(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            igniteCache.put(key, value);
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
    public ResponseEntity<?> getCacheData(@PathVariable(value = "key") String key) {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            String s = igniteCache.get(key);
            data.put("data", s);
            LOGGER.info("Data retrieve from cache successfully.");
        } catch (TransactionException e) {
            errMsg = e.getMessage();
            LOGGER.error("Data retrieving failed. ", e.getMessage());
        }

        if (data.get("data")==null) {
            data.put("msg", errMsg);
            return new ResponseEntity<Object>(data,HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }

    @DeleteMapping(value = "/cache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> deleteAllCacheData() {
        Map<Object, Object> data = new HashMap<>();
        String errMsg = "";
        try {
            igniteCache.clear();
            LOGGER.info("Cache clear successfully.");
        } catch (TransactionException e) {
            errMsg = e.getMessage();
            LOGGER.error("Cache clearing failed. ", e.getMessage());
        }

        if (errMsg != "") {
            data.put("msg", errMsg);
            return new ResponseEntity<Object>(data,HttpStatus.NO_CONTENT);
        } else {
            data.put("msg", "Cache clear successfully.");
            return new ResponseEntity<Object>(data, HttpStatus.OK);
        }
    }
}
