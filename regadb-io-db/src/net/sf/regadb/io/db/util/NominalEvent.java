package net.sf.regadb.io.db.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.ValueType;

public class NominalEvent {
    public static Map<String, EventNominalValue> createNominalValues(Event event,
            Table codeTable) {
        Map<String, EventNominalValue> result = new HashMap<String, EventNominalValue>();

        Map<String, EventNominalValue> unique = new HashMap<String, EventNominalValue>();
        
        for (int i = 1; i < codeTable.numRows(); ++i) {
            String id = codeTable.valueAt(0, i);
            String value = codeTable.valueAt(1, i);

            if (value.trim().length() != 0) {
                if (unique.get(value) != null) {
                    System.err.println("Duplicate nominal value: " + value);
                    result.put(id, unique.get(value));
                } else {                
                    EventNominalValue nv = new EventNominalValue(event, value);
                    event.getEventNominalValues().add(nv);

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
        public Event event;
        public Map<String, EventNominalValue> nominalValueMap; // key = id in code table

        public NominalEvent(String name, int column, Table t) {
            this.column = column;
            this.event = new Event(name);
            this.event.setValueType(nominalValueType);
            this.nominalValueMap = createNominalValues(event, t);
        }

        public NominalEvent(String name, int column, String[] ids, String[] values) {
            this.column = column;
            this.event = new Event(name);
            this.event.setValueType(nominalValueType);
            
            this.nominalValueMap = new HashMap<String, EventNominalValue>();
            for (int i = 0; i < ids.length; ++i) {
                EventNominalValue value = new EventNominalValue(event, values[i]);
                event.getEventNominalValues().add(value);
                nominalValueMap.put(ids[i], value);
            }
       }
        
       public NominalEvent(String name, Table conversionTable, Event standard) {
            this.event = new Event(name);
            this.event.setValueType(nominalValueType);
            
            this.nominalValueMap = new HashMap<String, EventNominalValue>();
            for (int i = 1; i < (conversionTable.numRows()-1); ++i) {
                String valueS = conversionTable.valueAt(1, i);
                if( "".equals(conversionTable.valueAt(0, i)) || "".equals(valueS)) {
                    ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present in translation file for \"" + name + "\"");
                } else {
                    EventNominalValue value = null;
                    for(Map.Entry<String, EventNominalValue> v : nominalValueMap.entrySet()) {
                        if(v.getValue().getValue().equals(valueS)) {
                            value = v.getValue();
                            break;
                        }
                    }
                    if(value==null)
                        value = new EventNominalValue(event, valueS);
                    event.getEventNominalValues().add(value);
                    nominalValueMap.put(conversionTable.valueAt(0, i), value);
                    if(!checkStandardNominalValue(value.getValue(),standard)) {
                        ConsoleLogger.getInstance().logError("Usage of unstandard value " + value.getValue());
                    }
                }
            }
            
            if(standard!=null) {
                for(EventNominalValue senv : standard.getEventNominalValues()) {
                    boolean found = false;
                    for(EventNominalValue eenv : event.getEventNominalValues()) {
                        if(eenv.getValue().equals(senv.getValue())) {
                            found = true;
                        }
                    }
                    if(!found)
                        event.getEventNominalValues().add(new EventNominalValue(event, senv.getValue()));
                }
            }
       }
       
       private boolean checkStandardNominalValue(String value, Event standard) {
           if(standard==null)
               return true;
           boolean found = false;
           for(EventNominalValue env : standard.getEventNominalValues()) {
               if(env.getValue().equals(value)) {
                   found = true;
                   break;
               }
           }
           return found;
       }
}

