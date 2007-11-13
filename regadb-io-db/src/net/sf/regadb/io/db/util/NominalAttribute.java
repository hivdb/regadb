package net.sf.regadb.io.db.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.ValueType;

public class NominalAttribute {
    public static Map<String, AttributeNominalValue> createNominalValues(Attribute attribute,
            Table codeTable) {
        Map<String, AttributeNominalValue> result = new HashMap<String, AttributeNominalValue>();

        Map<String, AttributeNominalValue> unique = new HashMap<String, AttributeNominalValue>();
        
        for (int i = 1; i < codeTable.numRows(); ++i) {
            String id = codeTable.valueAt(0, i);
            String value = codeTable.valueAt(1, i);

            if (value.trim().length() != 0) {
                if (unique.get(value) != null) {
                    System.err.println("Duplicate nominal value: " + value);
                    result.put(id, unique.get(value));
                } else {                
                    AttributeNominalValue nv = new AttributeNominalValue(attribute, value);
                    attribute.getAttributeNominalValues().add(nv);

                    //System.err.println(id + " -> " + nv.getValue());
                    result.put(id, nv);
                    unique.put(value, nv);
                }
            }
        }
        
        return result;
    }
    
        private ValueType nominalValueType = new ValueType("nominal value");
        public int column;
        public Attribute attribute;
        public Map<String, AttributeNominalValue> nominalValueMap; // key = id in code table

        public NominalAttribute(String name, int column, Table t) {
            this.column = column;
            this.attribute = new Attribute(name);
            this.attribute.setValueType(nominalValueType);
            this.nominalValueMap = createNominalValues(attribute, t);
        }

        public NominalAttribute(String name, int column, String[] ids, String[] values) {
            this.column = column;
            this.attribute = new Attribute(name);
            this.attribute.setValueType(nominalValueType);
            
            this.nominalValueMap = new HashMap<String, AttributeNominalValue>();
            for (int i = 0; i < ids.length; ++i) {
                AttributeNominalValue value = new AttributeNominalValue(attribute, values[i]);
                attribute.getAttributeNominalValues().add(value);
                nominalValueMap.put(ids[i], value);
            }
       }
}
