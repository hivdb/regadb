/*
 * PatientVisualizationSettings.java
 *
 * Created on October 14, 2003, 5:30 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

//import com.pharmadm.custom.rega.plate.io.SpectraMaxSetting;
import java.awt.Rectangle;
import java.io.*;
import java.util.*;

//import com.pharmadm.prolog.PrologSetting;
import com.pharmadm.util.settings.*;
import com.pharmadm.util.gui.mdi.RecentDocumentsSetting;

/**
 *
 * @author kristof, kdg
 */
public class RegaSettings extends XMLSettings {
    
    protected static RegaSettings instance = null;
    
    private static final String MIN_CD4 = "Minimum_CD4_Absolute";
    private static final String MIN_CD4_PERC = "Minimum_CD4_Percentage";
    private static final String MAX_CD4 = "Maximum_CD4_Absolute";
    private static final String MAX_CD4_PERC = "Maximum_CD4_Percentage";
    private static final String MIN_VIRAL_LOAD = "Minimum_Viral_Load";
    private static final String MAX_VIRAL_LOAD = "Maximum_Viral_Load";
    private static final String MIN_DATE = "Minimum_Date";
    private static final String MAX_DATE = "Maximum_Date";
    
    private static final String CD4_CNT_ABS = "CD4_Count_Absolute";
    private static final String VIRAL_LOAD_LOG = "Viral_Load_on_Log_Scale";
    private static final String AUTO_VAL_SCALE_PATIENT = "Automatic_Value_Scale_per_Patient";
    private static final String AUTO_TIME_SCALE_PATIENT = "Automatic_Time_Scale_per_Patient";
    private static final String HIV_MED_ONLY = "Show_HIV_Medication_Only";
    private static final String LAST_DB_LOGIN = "LastDBLogin";
    private static final String DB_TYPE = "DBType";
    private static final String DB_HOST = "Host";
    private static final String DB_PORT = "Port";
    private static final String DB_DATABASE = "Database";
    
    private static final String QUERY_EDITOR_FRAME_BOUNDS = "QueryEditorFrameBounds";
    private static final String REPORT_EDITOR_FRAME_BOUNDS = "ReportBuilderFrameBounds";
    private static final String HIERARCHIES_DIRECTORY = "HierarchiesDirectory";
    private static final String TMPDATA_DIRECTORY = "TemporaryDataDirectory";
    private static final String HTML_DIRECTORY = "HTMLDirectory";
    private static final String MINING_DATA_DIRECTORY = "MiningDataDirectory";
    private static final String CUBES_DIRECTORY = "CubesDirectory";
    private static final String CLEAN_TEMPORARY_MINING_DATA = "CleanTemporaryMiningData";
    private static final String RECENT_QUERIES = "RecentQueries";
    
    private final static String EXAMPLE_CACHE_SIZE = "ExampleCacheSize";
    private static final String SPECTRAMAX = "PlateMeasurements";
    
    
    protected RegaSettings() {
//        add(new PrologSetting());
        
        FixedComposedSetting patientVisualizationOptions = new FixedComposedSetting();
        patientVisualizationOptions.setName("PatientVisualizationOptions");
        
        BooleanSetting absoluteCD4Count = new BooleanSetting(null, CD4_CNT_ABS);
        absoluteCD4Count.setValue(Boolean.TRUE);
        patientVisualizationOptions.add(absoluteCD4Count);
        
        BooleanSetting logViralLoad = new BooleanSetting(null, VIRAL_LOAD_LOG);
        logViralLoad.setValue(Boolean.FALSE);
        patientVisualizationOptions.add(logViralLoad);
        
        BooleanSetting relativeGraphicsScale = new BooleanSetting(null, AUTO_VAL_SCALE_PATIENT);
        relativeGraphicsScale.setValue(Boolean.TRUE);
        patientVisualizationOptions.add(relativeGraphicsScale);
        
        BooleanSetting relativeTimeScale = new BooleanSetting(null, AUTO_TIME_SCALE_PATIENT);
        relativeTimeScale.setValue(Boolean.TRUE);
        patientVisualizationOptions.add(relativeTimeScale);
        
        BooleanSetting hIVMedicationOnly = new BooleanSetting(null, HIV_MED_ONLY);
        hIVMedicationOnly.setValue(Boolean.FALSE);
        patientVisualizationOptions.add(hIVMedicationOnly);
        
        DoubleSetting minViralLoad = new DoubleSetting(null, MIN_VIRAL_LOAD);
        minViralLoad.setValue(new Double(50));
        patientVisualizationOptions.add(minViralLoad);
        
        DoubleSetting maxViralLoad = new DoubleSetting(null, MAX_VIRAL_LOAD);
        maxViralLoad.setValue(new Double(500000));
        patientVisualizationOptions.add(maxViralLoad);
        
        DoubleSetting minCD4 = new DoubleSetting(null, MIN_CD4);
        minCD4.setValue(new Double(0));
        patientVisualizationOptions.add(minCD4);
        
        DoubleSetting maxCD4 = new DoubleSetting(null, MAX_CD4);
        maxCD4.setValue(new Double(2000));
        patientVisualizationOptions.add(maxCD4);
        
        DoubleSetting minCD4rel = new DoubleSetting(null, MIN_CD4_PERC);
        minCD4rel.setValue(new Double(0));
        patientVisualizationOptions.add(minCD4rel);
        
        DoubleSetting maxCD4rel = new DoubleSetting(null, MAX_CD4_PERC);
        maxCD4rel.setValue(new Double(100));
        patientVisualizationOptions.add(maxCD4rel);
        
        DateSetting minDate = new DateSetting(null, MIN_DATE);
        try {
            minDate.setValue(DateSetting.getFormat().parse("1990-01-01"));
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
        }
        patientVisualizationOptions.add(minDate);
        
        DateSetting maxDate = new DateSetting(null, MAX_DATE);
        maxDate.setValue(new Date());
        patientVisualizationOptions.add(maxDate);
        
        add(patientVisualizationOptions);
        
        FixedComposedSetting lastDBLogin = new FixedComposedSetting();
        lastDBLogin.setName(LAST_DB_LOGIN);
        
        StringSetting dbType = new StringSetting(null, DB_TYPE);
        lastDBLogin.add(dbType);
        
        StringSetting host = new StringSetting(null, DB_HOST);
        lastDBLogin.add(host);
        
        IntegerSetting port = new IntegerSetting(null, DB_PORT);
        port.setValue(new Integer(1521));
        lastDBLogin.add(port);
        
        StringSetting database = new StringSetting(null, DB_DATABASE);
        lastDBLogin.add(database);
        
        add(lastDBLogin);
        
        RectangleSetting frameBounds = new RectangleSetting(QUERY_EDITOR_FRAME_BOUNDS, new Rectangle(30, 30, 800, 800));
        add(frameBounds);
        
        RectangleSetting moreFrameBounds = new RectangleSetting(REPORT_EDITOR_FRAME_BOUNDS, new Rectangle(30, 30, 800, 800));
        add(moreFrameBounds);
        
        DirSetting hierarchiesDir = new DirSetting(null,HIERARCHIES_DIRECTORY, new File(System.getProperty("user.home") + File.separator + "viroDM_user" + File.separator + "hierarchies"));
        add(hierarchiesDir);
        DirSetting tmpDataDir = new DirSetting(null,TMPDATA_DIRECTORY, new File(System.getProperty("user.home") + File.separator + "viroDM_user" + File.separator + "tmp"));
        add(tmpDataDir);
        DirSetting dataDir = new DirSetting(null,MINING_DATA_DIRECTORY, new File(System.getProperty("user.home") + File.separator + "viroDM_user" + File.separator + "data"));
        add(dataDir);
        DirSetting htmlDir = new DirSetting(null,HTML_DIRECTORY, new File(System.getProperty("user.home") + File.separator + "viroDM_user" + File.separator + "html"));
        add(htmlDir);
        DirSetting cubesDir = new DirSetting(null,CUBES_DIRECTORY, new File(System.getProperty("user.home") + File.separator + "viroDM_user" + File.separator + "cubes"));
        add(cubesDir);
        BooleanSetting cleanTMP = new BooleanSetting(null, CLEAN_TEMPORARY_MINING_DATA, Boolean.TRUE);
        add(cleanTMP);
        IntegerSetting cacheSize =  new IntegerSetting(this, EXAMPLE_CACHE_SIZE, new Integer(200000)); // in kilobytes
        add(cacheSize);
        RecentDocumentsSetting recentQueries = new RecentDocumentsSetting(RECENT_QUERIES, 5);
        add(recentQueries);
//        SpectraMaxSetting plateMeasurements = new SpectraMaxSetting();
//        add(plateMeasurements);
    }
    
    public static RegaSettings getInstance() {
        if (instance == null) {
            instance = new RegaSettings();
        }
        return instance;
    }
    
    protected String getHome() {
        return "ViroDM";
    }
    
    protected String getLocation() {
        return getDataDir() + File.separator + ".regarc";
    }
    
//    public PrologSetting getPrologSetting() {
//        return (PrologSetting)getChild(PrologSetting.DEFAULT_NAME);
//    }
    
    public FixedComposedSetting getPatientVisualizationOptions() {
        return (FixedComposedSetting)getChild("PatientVisualizationOptions");
    }
    
    public boolean getAbsoluteCD4Count() {
        return ((Boolean)(((BooleanSetting)getPatientVisualizationOptions().getChild(CD4_CNT_ABS))).getValue()).booleanValue();
    }
    
    public void setAbsoluteCD4Count(boolean b) {
        (((BooleanSetting)getPatientVisualizationOptions().getChild("CD4_Count_Absolute"))).setValue(b);
    }
    
    public boolean getLogViralLoad() {
        return ((Boolean)(((BooleanSetting)getPatientVisualizationOptions().getChild(VIRAL_LOAD_LOG))).getValue()).booleanValue();
    }
    
    public void setLogViralLoad(boolean b) {
        (((BooleanSetting)getPatientVisualizationOptions().getChild(VIRAL_LOAD_LOG))).setValue(b);
    }
    
    public boolean getRelativeGraphicsScale() {
        return ((Boolean)(((BooleanSetting)getPatientVisualizationOptions().getChild(AUTO_VAL_SCALE_PATIENT))).getValue()).booleanValue();
    }
    
    public void setRelativeGraphicsScale(boolean b) {
        (((BooleanSetting)getPatientVisualizationOptions().getChild(AUTO_VAL_SCALE_PATIENT))).setValue(b);
    }
    
    public boolean getRelativeTimeScale() {
        return ((Boolean)(((BooleanSetting)getPatientVisualizationOptions().getChild(AUTO_TIME_SCALE_PATIENT))).getValue()).booleanValue();
    }
    
    public void setRelativeTimeScale(boolean b) {
        (((BooleanSetting)getPatientVisualizationOptions().getChild(AUTO_TIME_SCALE_PATIENT))).setValue(b);
    }
    
    public boolean getHIVMedicationOnly() {
        return ((Boolean)(((BooleanSetting)getPatientVisualizationOptions().getChild(HIV_MED_ONLY))).getValue()).booleanValue();
    }
    
    public void setHIVMedicationOnly(boolean b) {
        (((BooleanSetting)getPatientVisualizationOptions().getChild(HIV_MED_ONLY))).setValue(b);
    }
    
    public double getMinViralLoad() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_VIRAL_LOAD))).getValue()).doubleValue();
    }
    
    public void setMinViralLoad(double min) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_VIRAL_LOAD))).setValue(new Double(min));
    }
    
    public double getMaxViralLoad() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_VIRAL_LOAD))).getValue()).doubleValue();
    }
    
    public void setMaxViralLoad(double max) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_VIRAL_LOAD))).setValue(new Double(max));
    }
    
    public double getMinCD4() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_CD4))).getValue()).doubleValue();
    }
    
    public void setMinCD4(double min) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_CD4))).setValue(new Double(min));
    }
    
    public double getMaxCD4() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_CD4))).getValue()).doubleValue();
    }
    
    public void setMaxCD4(double max) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_CD4))).setValue(new Double(max));
    }
    
    public double getMinCD4Rel() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_CD4_PERC))).getValue()).doubleValue();
    }
    
    public void setMinCD4Rel(double min) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MIN_CD4_PERC))).setValue(new Double(min));
    }
    
    public double getMaxCD4Rel() {
        return ((Double)(((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_CD4_PERC))).getValue()).doubleValue();
    }
    
    public void setMaxCD4Rel(double max) {
        (((DoubleSetting)getPatientVisualizationOptions().getChild(MAX_CD4_PERC))).setValue(new Double(max));
    }
    
    public Date getMinDate() {
        return ((Date)(((DateSetting)getPatientVisualizationOptions().getChild(MIN_DATE))).getValue());
    }
    
    public Date getMaxDate() {
        return ((Date)(((DateSetting)getPatientVisualizationOptions().getChild(MAX_DATE))).getValue());
    }
    
    
    public ComposedSetting getLastDBLogin() {
        return (ComposedSetting)getChild(LAST_DB_LOGIN);
    }
    
    public Rectangle getQueryEditorFrameBounds() {
        RectangleSetting frameBounds = (RectangleSetting)getChild(QUERY_EDITOR_FRAME_BOUNDS);
        return frameBounds.getRectangle();
    }
    
    public void setQueryEditorFrameBounds(Rectangle rect) {
        RectangleSetting frameBounds = (RectangleSetting)getChild(QUERY_EDITOR_FRAME_BOUNDS);
        frameBounds.setRectangle(rect);
    }
    
    public Rectangle getReportBuilderFrameBounds() {
        RectangleSetting frameBounds = (RectangleSetting)getChild(REPORT_EDITOR_FRAME_BOUNDS);
        return frameBounds.getRectangle();
    }
    
    public void setReportBuilderFrameBounds(Rectangle rect) {
        RectangleSetting frameBounds = (RectangleSetting)getChild(REPORT_EDITOR_FRAME_BOUNDS);
        frameBounds.setRectangle(rect);
    }
    
    public File getHierarchiesDirectory() {
        return ((DirSetting)getChild(HIERARCHIES_DIRECTORY)).dirValue();
    }
    
    public File getTemporaryDataDirectory() {
        return ((DirSetting)getChild(TMPDATA_DIRECTORY)).dirValue();
    }
    
    public File getDataDirectory() {
        return ((DirSetting)getChild(MINING_DATA_DIRECTORY)).dirValue();
    }
    
    public File getHTMLDirectory() {
        return ((DirSetting)getChild(HTML_DIRECTORY)).dirValue();
    }
    
    public File getCubesDirectory() {
        return ((DirSetting)getChild(CUBES_DIRECTORY)).dirValue();
    }
    
    public boolean getCleanTemporaryMiningData() {
        return ((BooleanSetting)getChild(CLEAN_TEMPORARY_MINING_DATA)).booleanValue();
    }
    
    public int getExampleCacheSize() {  // in kilobytes
        return ((IntegerSetting)getChild(EXAMPLE_CACHE_SIZE)).intValue();
    }
    
    public RecentDocumentsSetting getRecentQueriesSetting() {
        return (RecentDocumentsSetting)getChild(RECENT_QUERIES);
    }
    
//    public SpectraMaxSetting getPlateMeasurementsSetting() {
//        return (SpectraMaxSetting)getChild(SPECTRAMAX);
//    }
}
