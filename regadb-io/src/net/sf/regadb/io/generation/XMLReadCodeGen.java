/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.generation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientImpl;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;

public class XMLReadCodeGen {
    private static class ObjectField {
        ObjectInfo parent;
        String name;
        Class javaClass;

        enum Type { Primitive, ObjectSet, ObjectKey, ObjectPointer, ObjectPointerSet };
        Type type;
        boolean composite;
        
        ObjectInfo resolved;
        
        ObjectField(String name, Class javaClass, Type type, boolean composite) {
            this.parent = null;
            this.name = name;
            this.javaClass = javaClass;
            this.type = type;
            this.composite = composite;
            this.resolved = null;
        }

        public String memberName() {
            return "field" + parent.javaClass.getSimpleName()+"_"+name;
        }

        public String typeName(boolean isMethod) {
            if (isSet())
                return "Set<" + javaClass.getSimpleName() + ">";
            else if(javaClass.getName().endsWith("[B") && isMethod)
                return "byteArray";
            else
                return javaClass.getSimpleName();
         }

        public void writeCreate(int tabs) throws IOException {
            if (javaClass == TestResult.class) {
                write(tabs, resolved.varName() + " = patient.createTestResult(fieldTestResult_test);\n");
                write(tabs, memberName() + ".add(" + resolved.varName() + ");\n");
            } else if (javaClass == Therapy.class) {
                write(tabs, resolved.varName() + " = patient.createTherapy(fieldTherapy_startDate);\n");
            } else if (javaClass == PatientAttributeValue.class) {
                write(tabs, resolved.varName() + " = patient.createPatientAttributeValue(fieldPatientAttributeValue_attribute);\n");
            } else if (javaClass == Dataset.class) {
                write(tabs, resolved.varName() + " = null; // FIXME\n");                
            } else if (parent.javaClass == Patient.class) {
                write(tabs, resolved.varName() + " = patient.create" + javaClass.getSimpleName() + "();\n");
            } else {
                if (resolved.referenced) {
                    write(tabs, "if (reference" + resolved.javaClass.getSimpleName() + " != null) { \n");
                    write(tabs + 1, resolved.varName() + " = ref" + resolved.javaClass.getSimpleName() + "Map.get(reference" + resolved.javaClass.getSimpleName() + ");\n");
                    write(tabs + 1, "referenceResolved = " + resolved.varName() + " != null;\n");
                    write(tabs, "}\n");

                    write(tabs, "if (!referenceResolved) {\n");
                    ++tabs;
                }

                write(tabs, resolved.varName() + " = new " + javaClass.getSimpleName() + "();\n");
                if (resolved.hasCompositeId)
                    write(tabs, resolved.varName() + ".setId(new " + javaClass.getSimpleName() + "Id());\n");
                
                if (resolved.referenced) {
                    write(tabs, "if (reference" + resolved.javaClass.getSimpleName() + "!= null)\n");
                    write(tabs + 1, "ref" + resolved.javaClass.getSimpleName() + "Map.put(reference" + resolved.javaClass.getSimpleName() + ", " + resolved.varName() + ");\n");
                    --tabs;
                    write(tabs, "}\n");
                }

                if (isSet())
                    write(tabs, memberName() + ".add(" + resolved.varName() + ");\n");
                else
                    write(tabs, memberName() + " = " + resolved.varName() + ";\n");
            }
        }

        private boolean isSet() {
            return (type == Type.ObjectSet || type == Type.ObjectPointerSet);
        }

        public String setterName() {
            return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }

        public String getterName() {
            if (name.equals("patientDatasets"))
                return "getDatasets";
            else
                return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }

        public boolean isPointer() {
            return (type == Type.ObjectPointer || type == Type.ObjectPointerSet);
        }
    }
    
    private static class ObjectInfo {
        String id;
        Class javaClass;
        boolean hasCompositeId;
        
        ArrayList<ObjectField> fields;
        public boolean referenced;
        
        ObjectInfo(String id, Class javaClass) {
            this.id = id;
            this.javaClass = (javaClass == PatientImpl.class ? Patient.class : javaClass);
            this.fields = new ArrayList<ObjectField>();
            this.hasCompositeId = false;
            this.referenced = false;
        }
        
        void addField(ObjectField f) {
            fields.add(f);
            f.parent = this;
            if (f.composite)
                hasCompositeId = true;
        }

        public String parseState() {
            return "state" + javaClass.getSimpleName();
        }

        public String matchesXMLElement() {
            String n = Character.toLowerCase(javaClass.getSimpleName().charAt(0))
                + javaClass.getSimpleName().substring(1);

            String result = "\"" + n + "\".equals(qName) || \"" + n + "s-el\".equals(qName)";
            
            for (ObjectField f: getRefererringFields())
                if (f.isSet())
                    result += "|| \"" + f.name + "-el\".equals(qName)";
                else
                    result += "|| \"" + f.name + "\".equals(qName)";

            if (javaClass == Patient.class)
                result += "|| \"patients-el\".equals(qName)";
            
            return result;
        }

        public ArrayList<ObjectField> getRefererringFields() {
            ArrayList<ObjectField> result = new ArrayList<ObjectField>();

            for (String i : objectIdMap.keySet()) {
                ObjectInfo o = objectIdMap.get(i);
                
                for (ObjectField f : o.fields) {
                    if (f.javaClass == this.javaClass)
                        result.add(f);
                }
            }
            
            return result;
        }

        public String varName() {
            return "el" + javaClass.getSimpleName();
        }
    }
    
    private static Map<String, ObjectInfo> objectIdMap = new TreeMap<String, ObjectInfo>();
    private static Writer output;

    public static void addObject(Class c, String id) {
        objectIdMap.put(id, new ObjectInfo(id, c));
    }

    public static void addSet(String id, String name, Class bareClass) {
        objectIdMap.get(id).addField(new ObjectField(name, bareClass, ObjectField.Type.ObjectSet, false));
    }

    public static void addRepresentedValue(String id, String name, Class bareClass, boolean isComposite) {
        objectIdMap.get(id).addField(new ObjectField(name, bareClass, ObjectField.Type.ObjectKey, isComposite));
    }

    public static void addPointer(String id, String name, Class bareClass, boolean isComposite) {
        objectIdMap.get(id).addField(new ObjectField(name, bareClass, ObjectField.Type.ObjectPointer, isComposite));        
    }

    public static void addPointerSet(String id, String name, Class bareClass) {
        objectIdMap.get(id).addField(new ObjectField(name, bareClass, ObjectField.Type.ObjectPointerSet, false));
    }

    public static void addPrimitive(String id, String name, Class bareClass, boolean isComposite) {
        objectIdMap.get(id).addField(new ObjectField(name, bareClass, ObjectField.Type.Primitive, isComposite));
    }
    
    public static void generate(Writer writer) throws IOException {
        output = writer;

        resolveReferredObjects();
        
        write(0, "package net.sf.regadb.io.importXML;\n"
               + "import java.util.*;\n"
               + "import net.sf.regadb.db.*;\n"
               + "import net.sf.regadb.db.meta.*;\n"
               + "import org.xml.sax.*;\n"
               + "import org.xml.sax.helpers.XMLReaderFactory;\n"
               + "import java.io.IOException;\n"
               + "\n"
               + "public class ImportFromXML extends ImportFromXMLBase {\n");

        /*
         * List a state corresponding to every object type
         */
        write(1, "enum ParseState { TopLevel");
        
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);
            write(0, ", " + o.parseState());
        }
        
        write(0, " };\n\n");

        write(1, "public ImportFromXML() {\n");
        write(2, "parseStateStack.add(ParseState.TopLevel);\n");
        write(1, "}\n\n");

        write(1, "private ArrayList<ParseState> parseStateStack = new ArrayList<ParseState>();\n\n");
        
        write(1, "void pushState(ParseState state) {\n");
        //write(2, "System.err.println(\"+ \" + state.name());\n");
        write(2, "parseStateStack.add(state);\n");
        write(1, "}\n\n");
        
        write(1, "void popState() {\n");
        //write(2, "System.err.println(\"- \" + parseStateStack.get(parseStateStack.size() - 1).name());\n");
        write(2, "parseStateStack.remove(parseStateStack.size() - 1);\n");
        write(1, "}\n\n");
        
        write(1, "ParseState currentState() {\n");
        write(2, "return parseStateStack.get(parseStateStack.size() - 1);\n");
        write(1, "}\n\n");

        write(1, "List topLevelObjects = new ArrayList();\n");
        write(1, "ImportHandler importHandler = null;\n");
        write(1, "Class topLevelClass = null;\n\n");

        /*
         * Handling references: create a map for each referenced type.
         */
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            if (o.referenced) {
                write(1, "private Map<String, " + o.javaClass.getSimpleName()+"> ref" + o.javaClass.getSimpleName() + "Map " +
                        "= new HashMap<String, " + o.javaClass.getSimpleName()+">();\n");
                write(1, "private String reference" + o.javaClass.getSimpleName() + " = null;\n");
            }
        }
        
        /*
         * Declare all object_field combinations as members
         */
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            for (ObjectField f : o.fields) {
                write(1, "private " + f.typeName(false) + " " + f.memberName() + ";\n");
            }
        }

        write(0, "\n");
        
        write(1, "public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {\n");

        write(2, "value = null;\n");
        write(2, "if (false) {\n");
        
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            write(2, "} else if (\"" + o.javaClass.getSimpleName() + "\".equals(qName)" + ") {\n");
            write(2, "} else if ("+ o.matchesXMLElement() + ") {\n");
            
            write(3, "pushState(ParseState." + o.parseState() + ");\n");

            if (o.referenced)
                write(3, "reference" + o.javaClass.getSimpleName() + " = null;\n");
            
            if (o.javaClass == Patient.class)
                write(3, "patient = new Patient();\n");
            
            for (ObjectField f : o.fields) {
                if (f.isSet())
                    write(3, f.memberName() + " = new Hash" + f.typeName(true) + "();\n");
                else
                    if (f.type == ObjectField.Type.Primitive)
                        write(3, f.memberName() + " = nullValue" + f.typeName(true) + "();\n");
                    else
                        write(3, f.memberName() + " = null;\n");
            }
        }
        
        write(2, "}\n");
        write(1, "}\n\n");
        
        write(1, "@SuppressWarnings(\"unchecked\")\n");
        write(1, "public void endElement(String uri, String localName, String qName) throws SAXException {\n");
       
        write(2, "if (false) {\n");
        
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            /*
             * -- End element of the object itself.
             */
            write(2, "} else if (\"" + o.javaClass.getSimpleName() + "\".equals(qName)" + ") {\n");
            write(2, "} else if (currentState() == ParseState." + o.parseState() + ") {\n");
            write(3, "if ("+ o.matchesXMLElement() + ") {\n");
            write(4, "popState();\n");
            write(4, o.javaClass.getSimpleName() + " " + o.varName() + " = null;\n");

            if (o.referenced)
                write(4, "boolean referenceResolved = false;\n");

            write(4, "if (currentState() == ParseState.TopLevel) {\n");
            write(5, "if (topLevelClass == " + o.javaClass.getSimpleName() + ".class) {\n");

            if (o.javaClass != Patient.class) {
                write(6, o.varName() + " = new " + o.javaClass.getSimpleName() + "();\n");
                if (o.hasCompositeId)
                    write(6, o.varName() + ".setId(new " + o.javaClass.getSimpleName() + "Id());\n");
            } else {
                write(6, "elPatient = patient;\n");
            }

            write(5, "} else {\n");
            write(6, "throw new SAXException(new ImportException(\"Unexpected top level object: \" + qName));\n");
            write(5, "}\n");

            /*
             * Find fields which contain this object, create object and add object
             */
            for (ObjectField f : o.getRefererringFields()) {
                write(4, "} else if (currentState() == ParseState." + f.parent.parseState() + ") {\n");
                f.writeCreate(5);
            }
            
            write(4, "} else {\n");
            write(5, "throw new SAXException(new ImportException(\"Nested object problem: \" + qName));\n");
            write(4, "}\n");

            /*
             * Set all members
             */
            for (ObjectField f : o.fields) {
                /*
                 * Enforce no fields to be set when the reference was resolved.
                 */
                if (o.referenced) {
                    if (f.isSet()) {
                        write(4, "if (referenceResolved && !" + f.memberName() + ".isEmpty())\n");
                    } else {
                    String nullValue
                        = (f.type == ObjectField.Type.Primitive ? "nullValue" + f.typeName(true) + "()" : "null");
                        write(4, "if (referenceResolved && " + f.memberName() + " != " + nullValue + ")\n");
                    }
                    write(5, "throw new SAXException(new ImportException(\"Cannot modify resolved reference\"));\n");
                    write(4, "if (!referenceResolved) {\n");
                } else
                    write(4, "{\n");

                if (f.composite)
                    write(5, o.varName() + ".getId()." + f.setterName() + "(" + f.memberName() + ");\n");
                else
                    if (!(f.isSet() && o.javaClass == Patient.class))
                        write(5, o.varName() + "." + f.setterName() + "(" + f.memberName() + ");\n");
                if (f.isSet() && o.javaClass != Patient.class) {
                    write(5, "for (" + f.javaClass.getSimpleName() + " o : " + f.memberName() + ")\n");
                    if (f.resolved != null && f.resolved.hasCompositeId)
                        write (6, "o.getId().");
                    else
                        write(6, "o.");
                    write(0, "set" + o.javaClass.getSimpleName() + "(" + o.varName() + ");\n");
                }
                
                write(4, "}\n");
            }

            write(4, "if (currentState() == ParseState.TopLevel) {\n");
            write(5, "if (importHandler != null)\n");
            write(6, "importHandler.importObject(" + o.varName() + ");\n");
            write(5, "else\n");
            write(6, "topLevelObjects.add(" + o.varName() + ");\n");
            write(4, "}\n");

            /*
             * -- End elements of fields.
             */
            for (ObjectField f : o.fields) {
                write(3, "} else if (\"" + f.name + "\".equals(qName)) {\n");
                if (f.type == ObjectField.Type.Primitive) {
                    write(4, f.memberName() + " = parse" + f.typeName(true) + "(value);\n");
                } else if (f.type == ObjectField.Type.ObjectKey) {
                    write(4, f.memberName() + " = resolve" + f.javaClass.getSimpleName() + "(value);\n");
                }
            }
            
            if (o.referenced) {                
                write(3, "} else if (\"reference\".equals(qName)) {\n");
                write(4, "reference" + o.javaClass.getSimpleName() + " = value;\n");
            }
            
            write(3, "} else {\n");
            write(4, "//throw new SAXException(new ImportException(\"Unrecognized element: \" + qName));\n");
            write(4, "System.err.println(\"Unrecognized element: \" + qName);\n");
            write(3, "}\n");
        }

        write(2, "}\n");
        
        write(1, "}\n\n");

        /*
         * -- Parser entry points: one for each object type
         */

        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            write(1, "@SuppressWarnings(\"unchecked\")\n");
            write(1, "public List<" + o.javaClass.getSimpleName() + "> read" + o.javaClass.getSimpleName()
                    + "s(InputSource source, ImportHandler<" + o.javaClass.getSimpleName() + "> handler) throws SAXException, IOException {\n");

            write(2, "topLevelClass = " + o.javaClass.getSimpleName() + ".class;\n");
            write(2, "importHandler = handler;\n");
            write(2, "parse(source);\n");
            write(2, "return topLevelObjects;\n");
            write(1, "}\n\n");
        }

        write(1, "private void parse(InputSource source)  throws SAXException, IOException {\n");
        write(2, "XMLReader xmlReader = XMLReaderFactory.createXMLReader();\n");
        write(2, "xmlReader.setContentHandler(this);\n");
        write(2, "xmlReader.setErrorHandler(this);\n");        
        write(2, "xmlReader.parse(source);\n");
        write(1, "}\n\n");

        /*
         * -- Synchronize methods: one for each object type
         */
        
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);
            
            write(1, "private void sync(Transaction t, " + o.javaClass.getSimpleName() + " o, " + o.javaClass.getSimpleName() + " dbo, boolean simulate) {\n");

            /*
             * TODO: must decide: what if a referenced object is already in the database, do we also 'update' it ?
             *       currently we don't.
             */
            
            /*
             * Set all members
             */
            for (ObjectField f : o.fields) {
                String comp = "";
                if (f.composite)
                    comp = "getId().";

                if (!f.isSet())
                    if (f.resolved != null) {
                        write(2, "if (dbo == null) {\n");
                        if (!f.resolved.referenced)
                            write(3, "sync(t, o."  + comp + f.getterName() + "(), (" + f.resolved.javaClass.getSimpleName() + ")null, simulate);\n");
                        else {
                            // find the thing in the database
                            write(3, f.resolved.javaClass.getSimpleName() + " d = Retrieve.retrieve(t, o."  + comp + f.getterName() + "());\n");
                            write(3, "if (d == null) {\n");
                            write(4, "log.append(\"Adding: \" + Describe.describe(o."  + comp + f.getterName() + "()) + \"\\n\");\n");
                            write(4, "sync(t, o."  + comp + f.getterName() + "(), (" + f.resolved.javaClass.getSimpleName() + ")null, simulate);\n");
                            write(3, "} else\n");
                            write(4, "if (!simulate)\n"); // <- sync deep here for alternative strategy
                            write(5, "o." + comp + f.setterName() + "(d);\n");
                        }
                        write(2, "} else {\n");
                        write(3, "if (Equals.isSame" + f.resolved.javaClass.getSimpleName() + "(o." + comp + f.getterName() + "(), dbo." + comp + f.getterName() + "()))\n");
                        write(4, "sync(t, o." + comp + f.getterName() + "(), dbo." + comp + f.getterName() + "(), simulate);\n");
                        write(3, "else {\n");
                        if (f.resolved.referenced) {
                            // find the thing in the database
                            write(4, f.resolved.javaClass.getSimpleName() + " d = Retrieve.retrieve(t, o."  + comp + f.getterName() + "());\n");
                            write(4, "if (d == null) {\n");
                            write(5, "log.append(\"Adding: \" + Describe.describe(o."  + comp + f.getterName() + "()) + \"\\n\");\n");
                            write(5, "sync(t, o."  + comp + f.getterName() + "(), (" + f.resolved.javaClass.getSimpleName() + ")null, simulate);\n");
                            write(5, "if (!simulate)\n");
                            write(6, "dbo." + comp + f.setterName() + "(o." + comp + f.getterName() + "());\n");
                            write(4, "} else {\n");
                            write(5, "if (!simulate)\n");  // <- sync deep here for alternative strategy
                            write(6, "dbo." + comp + f.setterName() + "(d);\n");
                            write(4, "}\n");
                        } else {
                            write(4, "if (!simulate)\n");
                            write(5, "dbo." + comp + f.setterName() + "(o." + comp + f.getterName() + "());\n");
                        }

                        write(4, "log.append(Describe.describe(o) + \": updating " + f.name + "\\n\");\n");
                        write(3, "}\n");
                        write(2, "}\n");
                    } else {
                        write(2, "if (dbo != null) {\n");
                        write(3, "if (!equals(dbo." + comp + f.getterName() + "(), o." + comp + f.getterName() + "())) {\n");
                        write(4, "if (!simulate)\n");
                        write(5, "dbo." + comp + f.setterName() + "(o." + comp + f.getterName() + "());\n");
                        write(4, "log.append(Describe.describe(o) + \": updating " + f.name + "\\n\");\n");
                        write(3, "}\n");
                        write(2, "}\n");
                    }
                else {
                    /*
                     * Synchronize the set
                     */
                    write(2, "for(" + f.resolved.javaClass.getSimpleName() + " e : o." + f.getterName() + "()) {\n");
                    write(3, f.resolved.javaClass.getSimpleName() + " dbe = null;\n");
                    write(3, "for(" + f.resolved.javaClass.getSimpleName() + " f : dbo." + f.getterName() + "()) {\n");
                    write(4, "if (Equals.isSame" + f.resolved.javaClass.getSimpleName() + "(e, f)) {\n");
                    write(5, "dbe = f; break;\n");
                    write(4, "}\n");
                    write(3, "}\n");
                    
                    write(3, "if (dbe == null) {\n");
                    write(4, "log.append(Describe.describe(dbo) + \": adding \" + Describe.describe(e) + \"\\n\");\n");
                    write(4, "if (!simulate) {\n");
                    if (o.javaClass == Patient.class) {
                        if (f.resolved.javaClass != Dataset.class)
                            write(5, "o.add" + f.resolved.javaClass.getSimpleName() + "(e);\n");
                        else
                            write(5, ";// TODO\n");
                    } else {
                        write(5, "dbo." + f.getterName() + "().add(e);\n");
                        if (f.resolved.hasCompositeId)
                            write (5, "e.getId().");
                        else
                            write(5, "e.");
                        write(0, "set" + o.javaClass.getSimpleName() + "(dbo);\n");
                    }
                    write(4, "}\n");
                    write(3, "} else\n");
                    write(4, "sync(t, e, dbe, simulate);\n");
                    write(2, "}\n");

                    write(2, "for(" + f.resolved.javaClass.getSimpleName() + " dbe : dbo." + f.getterName() + "()) {\n");
                    write(3, f.resolved.javaClass.getSimpleName() + " e = null;\n");
                    write(3, "for(" + f.resolved.javaClass.getSimpleName() + " f : o." + f.getterName() + "()) {\n");
                    write(4, "if (Equals.isSame" + f.resolved.javaClass.getSimpleName() + "(e, f)) {\n");
                    write(5, "e = f; break;\n");
                    write(4, "}\n");
                    write(3, "}\n");

                    write(3, "if (e == null) {\n");
                    write(4, "log.append(Describe.describe(dbo) + \": removing: \" + Describe.describe(dbe) + \"\\n\");\n");
                    write(4, "if (!simulate)\n");
                    write(5, "dbo." + f.getterName() + "().remove(dbe);\n");
                    write(3, "}\n");
                    write(2, "}\n");
                }
            }
            
            write(1, "}\n\n");
        }

        /*
         * -- Synchronize methods: one for each unscoped object type
         */
        Class unscopedClasses[] = { Patient.class, Attribute.class, Test.class, TestType.class };
        
        //write(1, "public enum SyncMode { Clean, Update };\n");
        //write(1, "StringBuffer log = new StringBuffer();\n");
        
        for (Class c : unscopedClasses) {
            write(1, "public " + c.getSimpleName() + " sync(Transaction t, " + c.getSimpleName() + " o, SyncMode mode, boolean simulate) throws ImportException {\n");
            write(2, c.getSimpleName() + " dbo = dbFind" + c.getSimpleName() + "(t, o);\n");
            write(2, "if (dbo != null) {\n");
            write(3, "if (mode == SyncMode.Clean)\n");
            write(4, "throw new ImportException(Describe.describe(o) + \" already exists\");\n");
            write(3, "sync(t, o, dbo, simulate);\n");
            write(3, "if (!simulate)\n");
            write(4, "t.update(dbo);\n");
            write(3, "return dbo;\n");
            write(2, "} else {\n");
            write(3, "log.append(\"Adding: \" + Describe.describe(o) + \"\\n\");\n");
            write(3, "sync(t, o, (" + c.getSimpleName() + ")null, simulate);\n");
            write(3, "if (!simulate)\n");
            write(4, "t.save(o);\n");
            write(3, "return o;\n");
            write(2, "}\n");
            write(1, "}\n\n");
        }
        
        write(0, "}\n");
    }

    private static void resolveReferredObjects() {
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);
            for (ObjectField f : o.getRefererringFields()) {
                f.resolved = o;
                if (f.isPointer())
                    o.referenced = true;
            }
        }
    }

    private static void write(int tabs, String s) throws IOException {
        for (int i = 0; i < tabs; ++i) {
            output.write("    ");
        }
        output.write(s);
    }
    }
