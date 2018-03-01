package org.rta.citizen.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RTAOfficeNameComparator implements Comparator<RTAOfficeModel> {

    @Override
    public int compare(RTAOfficeModel o1, RTAOfficeModel o2) {
        return o1.getName().compareTo(o2.getName());
    }
    
    public static void main(String[] args) {
        RTAOfficeModel off1 = new RTAOfficeModel();
        off1.setName("A");
        off1.setCode("C1");
        RTAOfficeModel off2 = new RTAOfficeModel();
        off2.setName("B");
        off2.setCode("C2");
        RTAOfficeModel off3 = new RTAOfficeModel();
        off3.setName("C");
        off3.setCode("C3");
        List<RTAOfficeModel> list = new ArrayList<>();
        list.add(off1);
        list.add(off2);list.add(off3);
        Collections.sort(list, new RTAOfficeNameComparator());
        list.stream().forEach(a->System.out.print(a.getName()));
    }

}
