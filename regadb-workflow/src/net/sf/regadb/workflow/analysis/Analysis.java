package net.sf.regadb.workflow.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;

public abstract class Analysis {
    private String name;
    private Map<String, Attribute> attributes = new HashMap<String, Attribute>();
    private List<AnalysisInput> inputs = new ArrayList<AnalysisInput>();
    private List<AnalysisOutput> outputs = new ArrayList<AnalysisOutput>();
    private boolean ready = false;
    
    public Map<String, Attribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public List<AnalysisInput> getInputs() {
        return inputs;
    }
    
    public void setInputs(List<AnalysisInput> inputs) {
        this.inputs = inputs;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<AnalysisOutput> getOutputs() {
        return outputs;
    }
    
    public void setOutputs(List<AnalysisOutput> outputs) {
        this.outputs = outputs;
    }
    
    public abstract String getType();
    
    public String getSpecType() {
        if(attributes.get("general.specType")!=null)
            return attributes.get("general.specType").getValue();
        else
            return null;
    }
    
    public void setSpecType(String specType) {
        attributes.put("general.specType", new Attribute("general.specType", specType));
    }
    
    public String getDescription() {
        if(attributes.get("general.description")!=null)
            return attributes.get("general.description").getValue();
        else 
            return null;
    }
    
    public void setDescription(String description) {
        attributes.put("general.description", new Attribute("general.description", description));
    }
    
    public abstract IAnalysisUI getUI();
    
    public abstract boolean execute();
    
    public String getAttributeValue(String key) {
        if(attributes.get(key)!=null) {
            return attributes.get(key).getValue();
        }
        else {
            return null;
        }
    }
    
    public void putAttributeValue(String key, String value) {
        attributes.put(key, new Attribute(key, value));
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}