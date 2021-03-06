/**
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.seaclouds.platform.discoverer.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;


public class DiscovererTest {

    @Test
    public void testSignificantInstance() {
        Discoverer d = Discoverer.instance();
        Assert.assertNotNull(d);
    }


    @Test
    public void testOfferingDirectoryExists() {
        Discoverer d = Discoverer.instance();
        File od = d.offeringManager.getOfferingDirectory();
        Assert.assertTrue(od.exists());
    }


    @Test
    public void testMetaDirectoryExists() {
        Discoverer d = Discoverer.instance();
        File md = d.offeringManager.getMetaDirectory();
        Assert.assertTrue(md.exists());
    }
}
