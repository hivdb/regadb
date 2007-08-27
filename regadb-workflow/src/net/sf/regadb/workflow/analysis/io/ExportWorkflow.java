package net.sf.regadb.workflow.analysis.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;
import net.sf.regadb.workflow.analysis.Attribute;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ExportWorkflow {
    public void exportAnalysis(Analysis analysis, Element analysisEls) {
        Element analysisEl = new Element("analysis");
        analysisEls.addContent(analysisEl);
        
        Element nameEl = new Element("name");
        nameEl.addContent(new Text(analysis.getName()));
        analysisEl.addContent(nameEl);
        
        Element typeEl = new Element("type");
        typeEl.addContent(new Text(analysis.getType()));
        analysisEl.addContent(typeEl);
        
        Element attributes = new Element("attributes");
        analysisEl.addContent(attributes);
        for(Map.Entry<String, Attribute> a : analysis.getAttributes().entrySet()) {
            Element attributeEl = new Element("attribute");
            attributes.addContent(attributeEl);
            Element attrNameEl = new Element("name");
            attributeEl.addContent(attrNameEl);
            attrNameEl.addContent(new Text(a.getValue().getName()));
            Element attrValueEl = new Element("value");
            attributeEl.addContent(attrValueEl);
            attrValueEl.addContent(new Text(a.getValue().getValue()));
        }
        
        Element inputs = new Element("inputs");
        for(AnalysisInput ai : analysis.getInputs()) {
            Element input = new Element("input");
            input.addContent(new Text(ai.getName()));
            inputs.addContent(input);
        }
        analysisEl.addContent(inputs);
        
        Element outputs = new Element("outputs");
        for(AnalysisOutput ao : analysis.getOutputs()) {
            Element output = new Element("output");
            output.addContent(new Text(ao.getName()));
            outputs.addContent(output);
        }
        analysisEl.addContent(outputs);
    }
    
    public void exportAnalyses(ArrayList<Analysis> analyses, Element workflow) {
        Element analysesEls = new Element("analyses");
        for(Analysis a : analyses) {
            exportAnalysis(a, analysesEls);
        }
        workflow.addContent(analysesEls);
    }
    
    public void writeXMLFile(ArrayList<Analysis> analyses, File xmlFile) {
        Element root = new Element("regadb-workflow");
        exportAnalyses(analyses, root);
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        
        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(xmlFile);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
