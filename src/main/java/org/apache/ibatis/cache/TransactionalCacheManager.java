/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.cache.decorators.TransactionalCache;
import org.apache.ibatis.util.MapUtil;

/**
 * 事务缓存管理器
 * @author Clinton Begin
 */
public class TransactionalCacheManager {

  // Cache 与 TransactionalCache 的映射关系表
  private final Map<Cache, TransactionalCache> transactionalCaches = new HashMap<>();

  public void clear(Cache cache) {
    // 获取TransactionalCache对象，并调⽤该对象的clear⽅法，下同
    getTransactionalCache(cache).clear();
  }

  public Object getObject(Cache cache, CacheKey key) {
    // 直接从TransactionalCache中获取缓存
    return getTransactionalCache(cache).getObject(key);
  }

  public void putObject(Cache cache, CacheKey key, Object value) {
    // 直接存⼊TransactionalCache的缓存中
    getTransactionalCache(cache).putObject(key, value);
  }

  public void commit() {
    for (TransactionalCache txCache : transactionalCaches.values()) {
      txCache.commit();
    }
  }

  public void rollback() {
    for (TransactionalCache txCache : transactionalCaches.values()) {
      txCache.rollback();
    }
  }

  private TransactionalCache getTransactionalCache(Cache cache) {
    // 从映射表中获取 TransactionalCache, 如果没有则创建⼀个新的TransactionalCache放进caches中
    // TransactionalCache 也是⼀种装饰类，为 Cache 增加事务功能
    return MapUtil.computeIfAbsent(transactionalCaches, cache, TransactionalCache::new);
  }

}
