/*
 * Created on May 16, 2007
 *
 * To change the template o2or this generated o2ile go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.meta;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Analysis;
import net.sf.regadb.db.AnalysisData;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestObject;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.util.date.DateUtils;

public class Equals {

	/**
	 * Compares Tests on description and testType
	 * @param o1
	 * @param o2
	 * @return
	 */
    public static boolean isSameTest(Test o1, Test o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()) && isSameTestType(o1.getTestType(),o2.getTestType()));
    }

    /**
     * Compares TestNominalValues on value
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTestNominalValue(TestNominalValue o1, TestNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }
    
    /**
     * Compares EventNominalValues on value
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameEventNominalValue(EventNominalValue o1, EventNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    /**
     * Compares TestTypes on description and genome
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTestType(TestType o1, TestType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription())
                && isSameGenome(o1.getGenome(), o2.getGenome()));
    }
    
    /**
     * Compares Genomes on organismName
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameGenome(Genome o1, Genome o2){
        return o1 == o2
        || (o1 != null && o2 != null && o1.getOrganismName().equals(o2.getOrganismName()));
    }

    /**
     * Compares Datasets on description
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameDataset(Dataset o1, Dataset o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    /**
     * Compares TestResults on testDate, test, drugGeneric, viralIsolate and ntSequence, not on patient
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTestResult(TestResult o1, TestResult o2) {
        return o1 == o2
        || (o1 != null && o2 != null && isSameDate(o1.getTestDate(),o2.getTestDate())
                && isSameTest(o1.getTest(), o2.getTest())
                && isSameDrugGeneric(o1.getDrugGeneric(), o2.getDrugGeneric())
                && isSameViralIsolate(o1.getViralIsolate(), o2.getViralIsolate())
                && isSameNtSequence(o1.getNtSequence(), o2.getNtSequence()));
    }
    
    public static boolean isSameDate(Date o1, Date o2){
    	return o1 == o2
    		|| ( o1 != null && o2 != null && o1.equals(o2)); 
    }

    /**
     * Compares DrugGenerics on genericId
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameDrugGeneric(DrugGeneric o1, DrugGeneric o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getGenericId().equals(o2.getGenericId()));
    }

    /**
     * Compares PatientAttributeValues on attribute, value and attributeNominalValue, not on patient 
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSamePatientAttributeValue(PatientAttributeValue o1, PatientAttributeValue o2) {
        if(o1 == o2) return true;
        
        String v1=null, v2=null;
        Attribute a1=null, a2=null;
        AttributeNominalValue nv1=null, nv2=null;

        if(o1 != null){
            v1 = o1.getValue();
            a1 = o1.getAttribute();
            nv1 = o1.getAttributeNominalValue();
        }
        if(o2 != null){
            v2 = o2.getValue();
            a2 = o2.getAttribute();
            nv2 = o2.getAttributeNominalValue();
        }

        return (   isSameAttributeNominalValue(nv1,nv2)
                && isSameAttribute(a1,a2)
                && isSameString(v1,v2));
    }
    
    /**
     * Compares PatientEventValues on event, value, eventNominalValue, startDate and endDate, not on patient
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSamePatientEventValue(PatientEventValue o1, PatientEventValue o2) {
        Date end1 = o1.getEndDate();
        Date end2 = o2.getEndDate();

        return o1 == o2
        || (o1 != null && o2 != null
                && o1.getStartDate().equals(o2.getStartDate())
                && (end1 == end2 || (end1 != null && end2 != null && end1.equals(end2)))
                && isSameEventNominalValue(o1.getEventNominalValue(),o2.getEventNominalValue())
                && isSameEvent(o1.getEvent(), o2.getEvent()));
    }

    /**
     * Compares ViralIsolates on sampleId
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameViralIsolate(ViralIsolate o1, ViralIsolate o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getSampleId().equals(o2.getSampleId()));
    }

    /**
     * Compares Therapies on startDate
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapy(Therapy o1, Therapy o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getStartDate().equals(o2.getStartDate()));
    }
    
    /**
     * Compares TherapyMotivations on value
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyMotivation(TherapyMotivation o1, TherapyMotivation o2){
    	return o1 == o2
    		|| (o1 != null && o2 != null && isSameString(o1.getValue(), o2.getValue()));
    }
    
    private static <T extends Comparable<T>> int compare(T t1, T t2){
		if(t1 != null && t2 != null)
			return t1.compareTo(t2);
		else if(t1 != null)
			return 1;
		else if(t2 != null)
			return -1;
		else
			return 0;
    }
    
    private static final Comparator<TherapyCommercial> therapyCommercialComparator = new Comparator<TherapyCommercial>(){
		@Override
		public int compare(TherapyCommercial o1, TherapyCommercial o2) {
			int c = o1.getId().getDrugCommercial().getName().compareTo(
					o2.getId().getDrugCommercial().getName());
			if(c != 0)
				return c;
			
			c = Equals.compare(o1.getDayDosageUnits(), o2.getDayDosageUnits());
			if(c != 0)
				return c;
			
			c = Equals.compare(o1.getFrequency(), o2.getFrequency());
			if(c != 0)
				return c;
			
			if(o1.isBlind() != o2.isBlind())
				return o1.isBlind() ? 1 : -1;
			
			if(o1.isPlacebo() != o2.isPlacebo())
				return o1.isPlacebo() ? 1 : -1;
			
			return 0;
		}
	};
	
	private static final Comparator<TherapyGeneric> therapyGenericComparator = new Comparator<TherapyGeneric>(){
		@Override
		public int compare(TherapyGeneric o1, TherapyGeneric o2) {
			int c = o1.getId().getDrugGeneric().getGenericId().compareTo(
					o2.getId().getDrugGeneric().getGenericId());
			if(c != 0)
				return c;
			
			c = Equals.compare(o1.getDayDosageMg(), o2.getDayDosageMg());
			if(c != 0)
				return c;
			
			c = Equals.compare(o1.getFrequency(), o2.getFrequency());
			if(c != 0)
				return c;
			
			if(o1.isBlind() != o2.isBlind())
				return o1.isBlind() ? 1 : -1;
			
			if(o1.isPlacebo() != o2.isPlacebo())
				return o1.isPlacebo() ? 1 : -1;
			
			return 0;
		}
	};
    
    /**
     * Compares Therapies on comment, startDate, stopDate, therapyCommercials, therapyGenerics and therapyMotivation
     * not on Patient
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyEx(Therapy o1, Therapy o2){
    	if(o1 == o2)
    		return true;
    	
    	if(!isSameTherapy(o1, o2))
    		return false;
    	if(!DateUtils.equals(o1.getStopDate(), o2.getStopDate()))
    			return false;
    	if(!isSameString(o1.getComment(), o2.getComment()))
    		return false;
    	if(!isSameTherapyMotivation(o1.getTherapyMotivation(), o2.getTherapyMotivation()))
    		return false;
    	if(o1.getTherapyCommercials().size() != o2.getTherapyCommercials().size())
    		return false;
    	if(o1.getTherapyGenerics().size() != o2.getTherapyGenerics().size())
    		return false;
    	
    	if(!Equals.isSameCollection(
    			o1.getTherapyCommercials(),
    			o2.getTherapyCommercials(),
    			therapyCommercialComparator))
    		return false;
    	
    	if(!Equals.isSameCollection(
    			o1.getTherapyGenerics(),
    			o2.getTherapyGenerics(),
    			therapyGenericComparator))
    		return false;
    	
    	return true;
    }
    
    public static <T> boolean isSameCollection(Collection<T> c1, Collection<T> c2, Comparator<T> comparator){
    	if(c1 == c2)
    		return true;
    	if(c1 == null 
    			|| c2 == null
    			|| c1.size() != c2.size())
    		return false;
    	
    	Set<T> s1 = new TreeSet<T>(comparator);
    	s1.addAll(c1);
    	
    	Set<T> s2 = new TreeSet<T>(comparator);
    	s2.addAll(c2);
    	
    	Iterator<T> i1 = s1.iterator();
    	Iterator<T> i2 = s2.iterator();
    	
    	while(i1.hasNext()){
    		if(comparator.compare(i1.next(), i2.next()) != 0)
    			return false;
    	}
    	
    	return true;
    }

    /**
     * Compares NtSequences on label
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameNtSequence(NtSequence o1, NtSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getLabel().equals(o2.getLabel()));
    }

    /**
     * Compares AaSequences on protein
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAaSequence(AaSequence o1, AaSequence o2) {
        return o1 == o2
        || (o1 != null && o2 != null && isSameProtein(o1.getProtein(), o2.getProtein()));
    }

    /**
     * Compares AaInsertions on insertionPosition and insertionOrder
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAaInsertion(AaInsertion o1, AaInsertion o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getInsertionPosition() == o2.getId().getInsertionPosition()
                && o1.getId().getInsertionOrder() == o2.getId().getInsertionOrder());
    }

    /**
     * Compares TherapyCommercials on drugCommercial
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyCommercial(TherapyCommercial o1, TherapyCommercial o2) {
        return o1 == o2
        || (o1 != null && o2 != null && isSameDrugCommercial(o1.getId().getDrugCommercial(),o2.getId().getDrugCommercial()));
    }
    
    /**
     * Compares TherapyCommercials on drugCommercial, dayDosageUnits, frequency, blind and placebo
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyCommercialEx(TherapyCommercial o1, TherapyCommercial o2) {
        return (o1 == null && o2 == null)
        || (isSameTherapyCommercial(o1, o2)
      		  && isSame(o1.getDayDosageUnits(),o2.getDayDosageUnits())
      		  && isSame(o1.getFrequency(),o2.getFrequency())
      		  && o1.isBlind() == o2.isBlind()
      		  && o1.isPlacebo() == o2.isPlacebo());
    }

    /**
     * Compares TherapyGenerics on drugGeneric
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyGeneric(TherapyGeneric o1, TherapyGeneric o2) {
        return o1 == o2
        || (o1 != null && o2 != null && isSameDrugGeneric(o1.getId().getDrugGeneric(),o2.getId().getDrugGeneric()));
    }
    
    /**
     * Compares TherapyGenerics on drugGeneric, dayDosageMg, frequency, blind and placebo
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTherapyGenericEx(TherapyGeneric o1, TherapyGeneric o2) {
        return (o1 == null && o2 == null)
          || (isSameTherapyGeneric(o1, o2)
        		  && isSame(o1.getDayDosageMg(),o2.getDayDosageMg())
          		  && isSame(o1.getFrequency(),o2.getFrequency())
          		  && o1.isBlind() == o2.isBlind()
          		  && o1.isPlacebo() == o2.isPlacebo());
    }
    
    private static boolean isSame(Object o1, Object o2){
    	return o1 == o2
    		|| (o1 != null && o2 != null && o1.equals(o2));
    }

    /**
     * Compares AaMutations on mutationPosition
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAaMutation(AaMutation o1, AaMutation o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getId().getMutationPosition() == o2.getId().getMutationPosition());
    }

    /**
     * Compares Attributes on name
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAttribute(Attribute o1, Attribute o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }
    
    /**
     * Compares Events on name
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameEvent(Event o1, Event o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getName().equals(o2.getName()));
    }

    /**
     * Compares AttributeNominalValues on value, not on attribute
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAttributeNominalValue(AttributeNominalValue o1, AttributeNominalValue o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getValue().equals(o2.getValue()));
    }

    /**
     * Compares ValueTypes on description
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameValueType(ValueType o1, ValueType o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }

    /**
     * Compares TestObjects on description
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameTestObject(TestObject o1, TestObject o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getDescription().equals(o2.getDescription()));
    }
    
    /**
     * Compares AttributeGroups on groupName
     * @param o1
     * @param o2
     * @return
     */
    public static boolean isSameAttributeGroup(AttributeGroup o1, AttributeGroup o2) {
        return o1 == o2
        || (o1 != null && o2 != null && o1.getGroupName().equals(o2.getGroupName()));
    }

    public static boolean isSameAnalysis(Analysis o1, Analysis o2) {
        return true; // only analysis in an object
    }

    public static boolean isSameAnalysisData(AnalysisData o1, AnalysisData o2) {
        return o1 == o2
        || (o1 != null && o2 !=null && o1.getName().equals(o2.getName()));
    }
    
    public static boolean isSameString(String s1, String s2){
        return s1 == s2
            || (s1 != null && s1.equals(s2));
    }

    /**
     * Compares Proteins on abbreviation and openReadingFrame 
     * @param protein
     * @param protein2
     * @return
     */
	public static boolean isSameProtein(Protein protein, Protein protein2) {
		return protein == protein2 
		|| (protein != null && protein2 != null && protein.getAbbreviation().equals(protein2.getAbbreviation())
		        && isSameOpenReadingFrame(protein.getOpenReadingFrame(), protein2.getOpenReadingFrame()));
	}
	
	/**
	 * Compares OpenReadingFrames on name and genome
	 * @param orf
	 * @param orf2
	 * @return
	 */
	public static boolean isSameOpenReadingFrame(OpenReadingFrame orf, OpenReadingFrame orf2) {
	    return orf == orf2 
        || (orf != null && orf2 != null && orf.getName().equals(orf2.getName())
                && isSameGenome(orf.getGenome(),orf2.getGenome()));
	}

	/**
	 * Compares DrugCommercials on name
	 * @param drugCommercial
	 * @param drugCommercial2
	 * @return
	 */
	public static boolean isSameDrugCommercial(DrugCommercial drugCommercial,
			DrugCommercial drugCommercial2) {
		return drugCommercial == drugCommercial2 
		|| (drugCommercial != null && drugCommercial2 != null && drugCommercial.getName().equals(drugCommercial2.getName()));
	}

}
