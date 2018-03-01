package org.rta.citizen.common.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.rta.citizen.common.utils.ObjectsUtil;


/**
 * this is describe documents attachments device
 * 
 * @Author sohan.maurya created on Aug 11, 2016.
 */
public enum AttachmentFrom {

    MOBILE(1, "MOBILE"), DESKTOP(2, "DESKTOP");

    private static final Map<String, AttachmentFrom> labelToAttFrom = new HashMap<String, AttachmentFrom>();
    private static final Map<Integer, AttachmentFrom> valueToAttFrom = new HashMap<Integer, AttachmentFrom>();

    static {
        for (AttachmentFrom attachmentFrom : AttachmentFrom.values()) {
            labelToAttFrom.put(attachmentFrom.getLabel(), attachmentFrom);
        }
        for (AttachmentFrom attachmentFrom : EnumSet.allOf(AttachmentFrom.class)) {
            valueToAttFrom.put(attachmentFrom.getValue(), attachmentFrom);
        }
    }

    private String label;
    private Integer value;

    private AttachmentFrom(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static AttachmentFrom getAttachmentFrom(String label) {
        return ObjectsUtil.isNull(label) ? null : labelToAttFrom.get(label.toUpperCase());
    }

    public static AttachmentFrom getAttachmentFrom(Integer value) {
        return valueToAttFrom.get(value);
    }

}
