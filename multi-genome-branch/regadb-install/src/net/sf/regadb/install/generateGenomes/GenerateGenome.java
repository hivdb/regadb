package net.sf.regadb.install.generateGenomes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.SplicingPosition;

public class GenerateGenome {
    private String fullGenome;
    private Genome genome;

    private Collection<RegionValue<OpenReadingFrame>> orfRegions;
    private Collection<RegionValue<Protein>> proteinRegions;
    
    public static void main(String[] args){
        
        GenerateGenome hiv1gen = new GenerateGenome("HIV-1","K03455.1|HIVHXB2CG Human immunodeficiency virus type 1 (HXB2)","genbank 1",getReferenceSequence("k03455.fasta"));
        hiv1gen.generateFromFile("k03455.genome");
        

        String refPol = "CTACAAGGGAAGGCCAGGGAATTTTCTTCAGAGCAGACCAGAGCCAACAGCCCCACCAGAAGAGAGCTTC"
                                    + "AGGTCTGGGGTAGAGACAACAACTCCCCCTCAGAAGCAGGAGCCGATAGACAAGGAACTGTATCCTTTAA"
                                    + "CTTCCCTCAGGTCACTCTTTGGCAACGACCCCTCGTCACAATAAAGATAGGGGGGCAACTAAAGGAAGCT"
                                    + "CTATTAGATACAGGAGCAGATGATACAGTATTAGAAGAAATGAGTTTGCCAGGAAGATGGAAACCAAAAA"
                                    + "TGATAGGGGGAATTGGAGGTTTTATCAAAGTAAGACAGTATGATCAGATACTCATAGAAATCTGTGGACA"
                                    + "TAAAGCTATAGGTACAGTATTAGTAGGACCTACACCTGTCAACATAATTGGAAGAAATCTGTTGACTCAG"
                                    + "ATTGGTTGCACTTTAAATTTTCCCATTAGCCCTATTGAGACTGTACCAGTAAAATTAAAGCCAGGAATGG"
                                    + "ATGGCCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAGTAGAAATTTGTACAGA"
                                    + "GATGGAAAAGGAAGGGAAAATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCCATA"
                                    + "AAGAAAAAAGACAGTACTAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACT"
                                    + "TCTGGGAAGTTCAATTAGGAATACCACATCCCGCAGGGTTAAAAAAGAAAAAATCAGTAACAGTACTGGA"
                                    + "TGTGGGTGATGCATATTTTTCAGTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCT"
                                    + "AGTATAAACAATGAGACACCAGGGATTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCAC"
                                    + "CAGCAATATTCCAAAGTAGCATGACAAAAATCTTAGAGCCTTTTAGAAAACAAAATCCAGACATAGTTAT"
                                    + "CTATCAATACATGGATGATTTGTATGTAGGATCTGACTTAGAAATAGGGCAGCATAGAACAAAAATAGAG"
                                    + "GAGCTGAGACAACATCTGTTGAGGTGGGGACTTACCACACCAGACAAAAAACATCAGAAAGAACCTCCAT"
                                    + "TCCTTTGGATGGGTTATGAACTCCATCCTGATAAATGGACAGTACAGCCTATAGTGCTGCCAGAAAAAGA"
                                    + "CAGCTGGACTGTCAATGACATACAGAAGTTAGTGGGGAAATTGAATTGGGCAAGTCAGATTTACCCAGGG"
                                    + "ATTAAAGTAAGGCAATTATGTAAACTCCTTAGAGGAACCAAAGCACTAACAGAAGTAATACCACTAACAG"
                                    + "AAGAAGCAGAGCTAGAACTGGCAGAAAACAGAGAGATTCTAAAAGAACCAGTACATGGAGTGTATTATGA"
                                    + "CCCATCAAAAGACTTAATAGCAGAAATACAGAAGCAGGGGCAAGGCCAATGGACATATCAAATTTATCAA"
                                    + "GAGCCATTTAAAAATCTGAAAACAGGAAAATATGCAAGAATGAGGGGTGCCCACACTAATGATGTAAAAC"
                                    + "AATTAACAGAGGCAGTGCAAAAAATAACCACAGAAAGCATAGTAATATGGGGAAAGACTCCTAAATTTAA"
                                    + "ACTGCCCATACAAAAGGAAACATGGGAAACATGGTGGACAGAGTATTGGCAAGCCACCTGGATTCCTGAG"
                                    + "TGGGAGTTTGTTAATACCCCTCCCTTAGTGAAATTATGGTACCAGTTAGAGAAAGAACCCATAGTAGGAG"
                                    + "CAGAAACCTTCTATGTAGATGGGGCAGCTAACAGGGAGACTAAATTAGGAAAAGCAGGATATGTTACTAA"
                                    + "TAGAGGAAGACAAAAAGTTGTCACCCTAACTGACACAACAAATCAGAAGACTGAGTTACAAGCAATTTAT"
                                    + "CTAGCTTTGCAGGATTCGGGATTAGAAGTAAACATAGTAACAGACTCACAATATGCATTAGGAATCATTC"
                                    + "AAGCACAACCAGATCAAAGTGAATCAGAGTTAGTCAATCAAATAATAGAGCAGTTAATAAAAAAGGAAAA"
                                    + "GGTCTATCTGGCATGGGTACCAGCACACAAAGGAATTGGAGGAAATGAACAAGTAGATAAATTAGTCAGT"
                                    + "GCTGGAATCAGGAAAGTACTATTTTTAGATGGAATAGATAAGGCCCAAGATGAACATGAGAAATATCACA"
                                    + "GTAATTGGAGAGCAATGGCTAGTGATTTTAACCTGCCACCTGTAGTAGCAAAAGAAATAGTAGCCAGCTG"
                                    + "TGATAAATGTCAGCTAAAAGGAGAAGCCATGCATGGACAAGTAGACTGTAGTCCAGGAATATGGCAACTA"
                                    + "GATTGTACACATTTAGAAGGAAAAGTTATCCTGGTAGCAGTTCATGTAGCCAGTGGATATATAGAAGCAG"
                                    + "AAGTTATTCCAGCAGAAACAGGGCAGGAAACAGCATATTTTCTTTTAAAATTAGCAGGAAGATGGCCAGT"
                                    + "AAAAACAATACATACTGACAATGGCAGCAATTTCACCGGTGCTACGGTTAGGGCCGCCTGTTGGTGGGCG"
                                    + "GGAATCAAGCAGGAATTTGGAATTCCCTACAATCCCCAAAGTCAAGGAGTAGTAGAATCTATGAATAAAG"
                                    + "AATTAAAGAAAATTATAGGACAGGTAAGAGATCAGGCTGAACATCTTAAGACAGCAGTACAAATGGCAGT"
                                    + "ATTCATCCACAATTTTAAAAGAAAAGGGGGGATTGGGGGGTACAGTGCAGGGGAAAGAATAGTAGACATA"
                                    + "ATAGCAACAGACATACAAACTAAAGAATTACAAAAACAAATTACAAAAATTCAAAATTTTCGGGTTTATT"
                                    + "ACAGGGACAGCAGAAATCCACTTTGGAAAGGACCAGCAAAGCTCCTCTGGAAAGGTGAAGGGGCAGTAGT"
                                    + "AATACAAGATAATAGTGACATAAAAGTAGTGCCAAGAAGAAAAGCAAAGATCATTAGGGATTATGGAAAA"
                                    + "CAGATGGCAGGTGATGATTGTGTGGCAAGTAGACAGGATGAGGAT";
        
        System.out.println(hiv1gen);
        refPol = refPol.toLowerCase();
        System.out.println(refPol);
    }
    
    public GenerateGenome(String organismName, String organismDescription, String genbankNumber, String fullGenome){
        setFullGenome(fullGenome);
        setGenome(createGenome(organismName,organismDescription,genbankNumber));
        
        orfRegions = new TreeSet<RegionValue<OpenReadingFrame>>();
        proteinRegions = new TreeSet<RegionValue<Protein>>();
    }
    
    public OpenReadingFrame addOpenReadingFrame(String name, String description, int[][] regions){
        return addOpenReadingFrame(name, description, toRegions(regions));
    }
    public OpenReadingFrame addOpenReadingFrame(String name, String description, Collection<Region> regions){
        OpenReadingFrame orf = createOpenReadingFrame(getGenome(),name,description,null);
        
        String seq = "";
        for(Region r : regions){
            seq += getSequence(r);
            getOrfRegions().add(new RegionValue<OpenReadingFrame>(r,orf));
        }
        orf.setReferenceSequence(seq);
        
        int mod = (seq.length())%3;;
        if(mod != 0)
            System.err.println(name+ " length%3 "+mod);
        
        return orf;
    }
    
    public Protein addProtein(String name, String abbr, int[][] regions){
        return addProtein(name, abbr, toRegions(regions));
    }
    public Protein addProtein(String name, String abbr, Collection<Region> regions){
        RegionValue<OpenReadingFrame> rv = getOverlappingOrfRegions(regions).iterator().next();

        return addProtein(rv.value,rv.getStart(),name,abbr,regions);
    }
    
    public Protein addProtein(OpenReadingFrame orf, int orfOffset, String name, String abbr, Collection<Region> regions){
        int start = 0;
        int stop = 0;
        int pLength=0;
        int pOffset = -1;
        
        Protein p = createProtein(orf,name,abbr,0,0);
        
        for(Region r : regions){
        	proteinRegions.add(new RegionValue<Protein>(r, p));
            if(pLength == 0)
                pOffset = r.getStart();
            else
                createSplicingPosition(p, pLength+1);
            
            pLength += r.getLength();
        }
        
        start = pOffset - orfOffset;
        stop = pOffset + pLength - orfOffset;
        p.setStartPosition(start+1);
        p.setStopPosition(stop+1);

        int mod = (p.getStopPosition() - p.getStopPosition())%3;
        if(mod != 0)
            System.err.println(name+ " length%3 "+mod);
        
        return p;
    }
    
    public Collection<RegionValue<OpenReadingFrame>> getOverlappingOrfRegions(Collection<Region> regions){
        TreeSet<RegionValue<OpenReadingFrame>> overlaps = new TreeSet<RegionValue<OpenReadingFrame>>();
        
        for(Region r : regions){
            for(RegionValue<OpenReadingFrame> rv : getOrfRegions()){
                if(rv.overlaps(r)){
                    overlaps.add(rv);
                }
                else if(rv.getEnd() < r.getStart())
                    break;
            }
        }
        
        return overlaps;
    }
    
    public OpenReadingFrame getOpenReadingFrame(int start, int stop){
        Region r = new Region(start,stop);
        for(RegionValue<OpenReadingFrame> rv : getOrfRegions()){
            if(!rv.overlaps(r))
                continue;
            
            if((r.getStart()%3) != (rv.getStart()%3))
                continue;
            
            if((r.getEnd()%3) != (rv.getEnd()%3))
                continue;
            
            return rv.value;
        }
        return null;
    }
    
    public String getSequence(Region r){
        return getFullGenome().substring(r.getStart()-1, r.getEnd()-1);
    }
    
    public static Genome createGenome(String organismName, String organismDescription, String genbankNumber){
        Genome g = new Genome(organismName,organismDescription);
        g.setGenbankNumber(genbankNumber);
        return g;
    }
    
    public static OpenReadingFrame createOpenReadingFrame(Genome genome, String name, String description, String refSeq){
        OpenReadingFrame orf = new OpenReadingFrame(genome,name,description,refSeq);
        genome.getOpenReadingFrames().add(orf);
        return orf;
    }
    
    public static Protein createProtein(OpenReadingFrame orf, String name, String abbr, int start, int stop){
        Protein p = new Protein(orf,abbr,start,stop);
        p.setFullName(name);
        orf.getProteins().add(p);
        return p;
    }
    
    public static SplicingPosition createSplicingPosition(Protein p, int position){
        SplicingPosition sp = new SplicingPosition(p,position);
        p.getSplicingPositions().add(sp);
        return sp;
    }
    
    public static String getReferenceSequence(String fileName){
        File f = new File("genomes"+File.separatorChar+fileName);

        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            StringBuffer seq = new StringBuffer();

            String line = in.readLine();
            while((line = in.readLine()) != null)
                seq.append(line.trim().toLowerCase());
            
            return seq.toString();
            
        } catch (FileNotFoundException e) {
            System.err.println("Fasta file not found: "+ f.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Invalid fasta file: "+ f.getAbsolutePath());
        }
        return null;
    }
    
    public Genome generateFromFile(String fileName){
        File f = new File("genomes"+File.separatorChar+fileName);
        int i=0;
        String line="";
        try {
            OpenReadingFrame orf=null;
            int orfOffset=0;
            
            BufferedReader in = new BufferedReader(new FileReader(f));

            while((line = in.readLine()) != null){
                ++i;
                
                if(line.trim().length() == 0)
                    continue;
                
                if(line.toLowerCase().startsWith("orf")){
                    String[] fields = in.readLine().split(",");
                    ++i;
                    List<Region> regions = toRegions(fields[2].trim());
                    orf = addOpenReadingFrame(fields[0].trim(), fields[1].trim(), regions);
                    orfOffset = regions.get(0).getStart();
                }
                else if(orf != null){
                    String[] fields = line.split(",");
                    List<Region> regions = toRegions(fields[2].trim());
                    addProtein(orf, orfOffset, fields[0], fields[1], regions);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error on line "+ i +": "+ line);
            e.printStackTrace();
        }
        return getGenome();
    }
    
    public List<Region> toRegions(String s){
        List<Region> regions = new ArrayList<Region>();
        
        for(String r : s.split("\\+")){
            String[] pos = r.split("-");
            regions.add(new Region(Integer.parseInt(pos[0]),Integer.parseInt(pos[1])));
        }
        return regions;
    }
    
    public static Collection<Region> toRegions(int[][] regions){
        List<Region> res = new ArrayList<Region>(regions.length);
        for(int[] r : regions)
            res.add(new Region(r[0],r[1]));
        return res;
    }

    public void setFullGenome(String fullGenome) {
        this.fullGenome = fullGenome;
    }

    public String getFullGenome() {
        return fullGenome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }
    
    public void setOrfRegions(Collection<RegionValue<OpenReadingFrame>> orfRegions) {
        this.orfRegions = orfRegions;
    }

    public Collection<RegionValue<OpenReadingFrame>> getOrfRegions() {
        return orfRegions;
    }
    
    public Collection<RegionValue<Protein>> getProteinRegions() {
        return proteinRegions;
    }

    public class RegionValue<T> extends Region{
        public T value;
        
        public RegionValue(int start, int end, T value) {
            super(start, end);
            this.value = value;
        }
        
        public RegionValue(Region r, T value){
            super(r.getStart(),r.getEnd());
            this.value = value;
        }
    }
    
    public String toString(){
        StringBuffer sb = new StringBuffer();
        
        sb.append("Organism name:\t"+ getGenome().getOrganismName()  +"\n");
        sb.append("Organism description:\t"+ getGenome().getOrganismDescription() +"\n");
        sb.append("Genbank number:\t"+ getGenome().getGenbankNumber()  +"\n");
        sb.append("Full genome:\n"+ getFullGenome() +"\n");
        for(OpenReadingFrame orf : getGenome().getOpenReadingFrames()){
            sb.append(orf.getName() +":\t"+ orf.getReferenceSequence() +"\n");
            for(Protein p : orf.getProteins()){
                String pSeq = orf.getReferenceSequence().substring(p.getStartPosition()-1,p.getStopPosition()-1);
                sb.append("- "+ p.getFullName() +":\t"+ pSeq +"\n");
                
                TreeSet<Integer> ts = new TreeSet<Integer>();
                
                if(p.getSplicingPositions().size() > 0){
                    for(SplicingPosition sp : p.getSplicingPositions())
                        ts.add(sp.getPosition());
                    
                    ts.add(1);
                    for(Integer sp : ts){
                        String sSeq;
                        if(ts.tailSet(sp).size() > 1){
                            Iterator<Integer> it = ts.tailSet(sp).iterator();
                            it.next();
                            sSeq = pSeq.substring(sp-1,it.next());
                        }
                        else{
                            sSeq = pSeq.substring(sp-1,pSeq.length());
                        }
                        sb.append("-- "+ sp +":\t"+ sSeq +"\n");
                    }
                }
            }
        }
        
        return sb.toString();
    }
}
