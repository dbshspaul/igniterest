package cachestore;

import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

@Component
public class HelloWorldCacheStore  extends CacheStoreAdapter<String,String> {
    public String load(String s) throws CacheLoaderException {
        return null;
    }

    public void write(Cache.Entry<? extends String, ? extends String> entry) throws CacheWriterException {

    }

    public void delete(Object o) throws CacheWriterException {

    }
}
