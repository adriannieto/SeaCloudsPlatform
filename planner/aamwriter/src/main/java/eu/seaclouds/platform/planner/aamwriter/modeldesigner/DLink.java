/**
 * Copyright 2015 Atos
 * Contact: Seaclouds
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
package eu.seaclouds.platform.planner.aamwriter.modeldesigner;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class DLink {

    public static final class Attributes {
        public static final String SOURCE = "source";
        public static final String TARGET = "target";
        public static final String PROPERTIES = "properties";

        public static final String CALLS = "calls";
    }

    private DGraph graph;
    private DNode source;
    private DNode target;
    private Map<String, String> properties;
    private String calls;

    public DLink(JSONObject jnode, DGraph graph) {
        this.graph = graph;

        String sourceName = (String) jnode.get(Attributes.SOURCE);
        this.source = this.graph.getNode(sourceName);

        String targetName = (String) jnode.get(Attributes.TARGET);
        this.target = this.graph.getNode(targetName);

        @SuppressWarnings("unchecked")
        Map<String, String> linkProperties = (Map<String, String>)jnode.get(Attributes.PROPERTIES);
        this.properties = new HashMap<String, String>();
        this.properties.putAll(linkProperties);
        this.calls = properties.containsKey(Attributes.CALLS)? properties.get(Attributes.CALLS) : "";
    }

    @Override
    public String toString() {
        return String.format("Link [source=%s, target=%s, properties=%s]", 
                source.getName(), target.getName(), properties);
    }

    public DNode getSource() {
        return source;
    }

    public DNode getTarget() {
        return target;
    }

    public String getCalls() {
        return calls;
    }
}
