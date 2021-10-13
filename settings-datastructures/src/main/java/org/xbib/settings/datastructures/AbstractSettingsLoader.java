package org.xbib.settings.datastructures;

import org.xbib.settings.SettingsLoader;
import org.xbib.datastructures.api.Builder;
import org.xbib.datastructures.api.DataStructure;
import org.xbib.datastructures.api.ListNode;
import org.xbib.datastructures.api.MapNode;
import org.xbib.datastructures.api.Node;
import org.xbib.datastructures.api.Parser;
import org.xbib.datastructures.api.ValueNode;
import org.xbib.datastructures.tiny.TinyMap;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractSettingsLoader implements SettingsLoader {

    public abstract DataStructure dataStructure();

    @Override
    public Map<String, String> load(Map<String, Object> map) throws IOException {
        Builder builder = dataStructure().createBuilder();
        builder.buildMap(map);
        return load(builder.build());
    }

    @Override
    public Map<String, String> load(String source) throws IOException {
        Parser parser = dataStructure().createParser();
        return load(parser, new StringReader(source));
    }

    private Map<String, String> load(Parser parser, Reader reader) throws IOException {
        List<CharSequence> path = new ArrayList<>();
        TinyMap.Builder<String, String> map = TinyMap.builder();
        Node<?> node = parser.parse(reader);
        parseObject(node, map, path, null);
        return map.build();
    }

    private void parseObject(Node<?> node,
                             TinyMap.Builder<String, String> map,
                             List<CharSequence> path,
                             CharSequence name) {
        if (node instanceof ValueNode) {
            ValueNode valueNode = (ValueNode) node;
            StringBuilder sb = new StringBuilder();
            for (CharSequence s : path) {
                sb.append(s).append('.');
            }
            sb.append(name);
            Object object = valueNode.get();
            map.put(sb.toString(), object != null ? object.toString() : null);
        } else if (node instanceof ListNode) {
            ListNode listNode = (ListNode) node;
            int counter = 0;
            for (Node<?> nn : listNode.get()) {
                parseObject(nn, map, path, name + "." + (counter++));
            }
        } else if (node instanceof MapNode) {
            if (name != null) {
                path.add(name);
            }
            MapNode mapNode = (MapNode) node;
            for (Map.Entry<CharSequence, Node<?>> me : mapNode.get().entrySet()) {
                parseObject(me.getValue(), map, path, me.getKey());
            }
            if (name != null) {
                path.remove(path.size() - 1);
            }
        }
    }
}
