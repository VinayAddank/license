package org.rta.citizen.common.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class having basic utility methods for processing on objects.
 * 
 * @author rahul.sharma
 *
 */
public final class ObjectsUtil {

    // Dummy value to associate with an Object
    public static final Object PRESENT = new Object();
    
    public static <T> boolean isNull(T t) {
        return Objects.isNull(t);
    }
    
    public static <T> boolean isNullOrEmpty(Collection<T> t) {
        return isNull(t) || t.isEmpty();
    }
    
    public static <K,V> boolean isNullOrEmpty(Map<K, V> t) {
        return isNull(t) || t.isEmpty();
    }
    
    /**
     * Return first occurring AP (Arithmetic Progression) in given {@code list}
     * @param list
     * @param subsetSize
     * @param commonDifference
     * @return
     */
    public static List<Long> findAP(List<Long> list, int subsetSize, int commonDifference) {
        Stack<Long> stack = new Stack<>();
        for (int i=0; i<list.size();i++) {
            if (!stack.isEmpty()) {
                if (stack.peek() + commonDifference == list.get(i)) {
                    stack.push(list.get(i));
                } else {
                    if (stack.size() != subsetSize) {
                        stack.clear();
                    }
                    stack.push(list.get(i));
                }
            } else {
                stack.push(list.get(i));
            }
            if (stack.size() >= subsetSize) {
                break;
            }
        }
        if (stack.size() < subsetSize) {
            stack.clear();
        }
        return new ArrayList<>(stack);
    }
    
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        String a = "{\"ab\":{\"a1\":\"1\",\"a2\":\"2\"}}";
        JsonNode jsonData = null;
        try {
            jsonData = mapper.readTree(a);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String username = jsonData.get("ab").get("a1").toString();
        System.out.println(username);
    }
    
}
