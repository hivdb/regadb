package net.sf.regadb.service.ioAssist;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.align.Aligner;
import net.sf.regadb.align.local.LocalAlignmentService;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportHandler;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.SubtypeAnalysis;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.service.wts.util.Utils;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IOAssistImportHandler implements ImportHandler<ViralIsolate>
{
    private int countViralIsolates = 0;
    private Aligner aligner_;
    
    private Test subType_;
    
    private ExportToXML export_;
    private XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
    
    private FileWriter fileWriter_;
    
    private List<Test> resistanceTests_;
    
    private List<AaSequence> aaSeqs_;
    
    public IOAssistImportHandler(FileWriter fw)
    {    
        this(fw, null);
    }
    
    public IOAssistImportHandler(FileWriter fw, String wtsURL)
    {        
        aligner_ = new Aligner(new LocalAlignmentService());
        
        subType_ = RegaDBWtsServer.getSubtypeTest();
        
        export_ = new ExportToXML();
        fileWriter_ = fw;
        
        //find resistance tests --start
        resistanceTests_ = Utils.getResistanceTests();
        //find resistance tests --end
        
        if(wtsURL!=null) {
            subType_.getAnalysis().setUrl(wtsURL);
            
            for(Test rt : resistanceTests_) {
                rt.getAnalysis().setUrl(wtsURL);
            }
        }
    }
    
    public void importObject(ViralIsolate object) {
        Genome genome = getGenome(object);
        if(genome == null){
            System.err.println("Unknown organism for viral isolate: "+ object.getSampleId());
            return;
        } else {
        	System.err.println(genome.getOrganismDescription());
        }
        
        for(final NtSequence ntseq : object.getNtSequences()) {
            align(ntseq, genome);

            doSubtypeAnalysis(ntseq, subType_, genome);
        }
        
        calculateRI(object, genome);
        
        Element parent = new Element("viralIsolates-el");
        export_.writeViralIsolate(object, parent);
        try {
            fileWriter_.write(outputter.outputString(parent)+'\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        countViralIsolates++;
        System.err.println("Processed viral isolate nr "+countViralIsolates);
    }
    
    private void align(final NtSequence ntseq, Genome genome) {
        try {
            aaSeqs_ = aligner_.align(ntseq, genome);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(aaSeqs_!=null) {
            for(AaSequence aaseq : aaSeqs_) {
                aaseq.setNtSequence(ntseq);
                ntseq.getAaSequences().add(aaseq);
            }
        }
    }
    
    private void calculateRI(ViralIsolate object, Genome genome) {
        for(final Test resistanceTest : resistanceTests_)
        {
            if(Equals.isSameGenome(genome, resistanceTest.getTestType().getGenome())){
                byte[] result = ViralIsolateAnalysisHelper.run(object, resistanceTest, 500);
                final ViralIsolate isolate = object;
                ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
                {
                    @Override
                    public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
                    {
                        TestResult resistanceInterpretation = new TestResult();
                        resistanceInterpretation.setViralIsolate(isolate);
                        resistanceInterpretation.setDrugGeneric(new DrugGeneric(null, drug, ""));
                        resistanceInterpretation.setValue(gss+"");
                        resistanceInterpretation.setTestDate(new Date(System.currentTimeMillis()));
                        resistanceInterpretation.setTest(resistanceTest);
                        
                        StringBuffer data = new StringBuffer();
                        data.append("<interpretation><score><drug>");
                        data.append(drug);
                        data.append("</drug><level>");
                        data.append(level);
                        data.append("</level><description>");
                        data.append(description);
                        data.append("</description><sir>");
                        data.append(sir);
                        data.append("</sir><gss>");
                        data.append(gss);
                        data.append("</gss><mutations>");
                        int size = mutations.size();
                        for(int i = 0; i<size; i++)
                        {
                            data.append(mutations.get(i));
                            if(i!=size-1)
                                data.append(' ');
                        }
                        data.append("</mutations><remarks>");
                        data.append(remarks);
                        data.append("</remarks></score></interpretation>");
                        resistanceInterpretation.setData(data.toString().getBytes());
                        
                        isolate.getTestResults().add(resistanceInterpretation);
                    }
                };
                try 
                {
                    inp.parse(new InputSource(new ByteArrayInputStream(result)));
                } 
                catch (SAXException e) 
                {
                    e.printStackTrace();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static TestResult doSubtypeAnalysis(NtSequence ntseq, Test subtypeTest, Genome genome){
        SubtypeAnalysis subtypeAnalyis = new SubtypeAnalysis(ntseq, subtypeTest, genome);
        try {
            subtypeAnalyis.launch();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return subtypeAnalyis.getTestResult();
    }
 
    
    public static Genome getGenome(NtSequence ntseq)
    {
        BlastAnalysis blastAnalysis = new BlastAnalysis(ntseq);
        try {
            blastAnalysis.launch();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return blastAnalysis.getGenome();
    }
    
    public static Genome getGenome(ViralIsolate viralIsolate){
        return getGenome(viralIsolate.getNtSequences().iterator().next());
    }
}
