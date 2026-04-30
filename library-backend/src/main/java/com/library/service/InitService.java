package com.library.service;

public interface InitService {
    boolean checkDatabaseTables();
    void initDatabase(boolean withData) throws Exception;
}
