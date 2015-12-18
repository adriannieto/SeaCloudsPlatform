/*
 *  Copyright 2014 SeaClouds
 *  Contact: SeaClouds
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package eu.seaclouds.platform.dashboard.model;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;

public class SeaCloudsApplicationDataStorage {
    private static final Logger LOG = LoggerFactory.getLogger(SeaCloudsApplicationDataStorage.class);
    private static final String SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG = "seacloudsapplicationdata";
    private static final String SEACLOUDS_DB_FILENAME = "datastore.db";


    private static DB dataStore;
    private static SeaCloudsApplicationDataStorage instance;

    public static SeaCloudsApplicationDataStorage getInstance() {
        if (SeaCloudsApplicationDataStorage.instance == null) {
            SeaCloudsApplicationDataStorage.instance = new SeaCloudsApplicationDataStorage(false);
        }

        return SeaCloudsApplicationDataStorage.instance;
    }

    private SeaCloudsApplicationDataStorage(boolean persistency) {
        if (!persistency) {
            SeaCloudsApplicationDataStorage.LOG.warn("Persistency not enabled, the information will be lost after the application is closed");
            SeaCloudsApplicationDataStorage.dataStore = DBMaker.newMemoryDB().make();
        } else {
            SeaCloudsApplicationDataStorage.dataStore = DBMaker.newFileDB(new File(SeaCloudsApplicationDataStorage.SEACLOUDS_DB_FILENAME)).closeOnJvmShutdown().make();
            SeaCloudsApplicationDataStorage.LOG.info("Persistency enabled, all the changes will be persisted to " + SeaCloudsApplicationDataStorage.SEACLOUDS_DB_FILENAME);
        }
    }

    public SeaCloudsApplicationData addSeaCloudsApplicationData(SeaCloudsApplicationData newApplication) {
        ConcurrentNavigableMap<String, SeaCloudsApplicationData> treeMap = SeaCloudsApplicationDataStorage.dataStore.getTreeMap(SeaCloudsApplicationDataStorage.SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG);
        treeMap.put(newApplication.getSeaCloudsApplicationId(), newApplication);
        SeaCloudsApplicationDataStorage.dataStore.commit();
        return newApplication;
    }

    public SeaCloudsApplicationData getSeaCloudsApplicationDataById(String id) {
        ConcurrentNavigableMap<String, SeaCloudsApplicationData> treeMap = SeaCloudsApplicationDataStorage.dataStore.getTreeMap(SeaCloudsApplicationDataStorage.SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG);
        SeaCloudsApplicationData seaCloudsApplicationData = treeMap.get(id);
        return seaCloudsApplicationData;
    }

    public List<SeaCloudsApplicationData> listSeaCloudsApplicationData() {
        ConcurrentNavigableMap<String, SeaCloudsApplicationData> treeMap = SeaCloudsApplicationDataStorage.dataStore.getTreeMap(SeaCloudsApplicationDataStorage.SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG);
        Collection<SeaCloudsApplicationData> seaCloudsApplicationDataCollection = treeMap.values();
        return new ArrayList<>(seaCloudsApplicationDataCollection);
    }

    public SeaCloudsApplicationData removeSeaCloudsApplicationDataById(String id) {
        ConcurrentNavigableMap<String, SeaCloudsApplicationData> treeMap = SeaCloudsApplicationDataStorage.dataStore.getTreeMap(SeaCloudsApplicationDataStorage.SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG);
        SeaCloudsApplicationData remove = treeMap.remove(id);
        SeaCloudsApplicationDataStorage.dataStore.commit();
        return remove;
    }

    public void clearDataStore(){
        SeaCloudsApplicationDataStorage.dataStore.getTreeMap(SeaCloudsApplicationDataStorage.SEACLOUDS_APPLICATION_DATA_COLLECTION_TAG).clear();
        SeaCloudsApplicationDataStorage.dataStore.commit();
    }
}
