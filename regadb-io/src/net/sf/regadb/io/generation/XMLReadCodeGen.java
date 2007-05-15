/*
 * Created on May 10, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.generation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientImpl;
import net.sf.regadb.db.TestResult;
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

        public String typeName() {
            if (isSet())
                return "Set<" + javaClass.getSimpleName() + ">";
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

            String result = "\"" + n + "\".equals(qName)";
            
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
        write(2, "System.err.println(\"+ \" + state.name());\n");
        write(2, "parseStateStack.add(state);\n");
        write(1, "}\n\n");
        
        write(1, "void popState() {\n");
        write(2, "System.err.println(\"- \" + parseStateStack.get(parseStateStack.size() - 1).name());\n");
        write(2, "parseStateStack.remove(parseStateStack.size() - 1);\n");
        write(1, "}\n\n");
        
        write(1, "ParseState currentState() {\n");
        write(2, "return parseStateStack.get(parseStateStack.size() - 1);\n");
        write(1, "}\n\n");

        write(1, "List topLevelObjects = new ArrayList();\n");
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
                write(1, "private " + f.typeName() + " " + f.memberName() + ";\n");
            }
        }

        write(0, "\n");
        
        write(1, "public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {\n");

        write(2, "value = null;\n");
        write(2, "if (false) {\n");
        
        for (String id : objectIdMap.keySet()) {
            ObjectInfo o = objectIdMap.get(id);

            write(2, "} else if ("+ o.matchesXMLElement() + ") {\n");
            
            write(3, "pushState(ParseState." + o.parseState() + ");\n");

            if (o.referenced)
                write(3, "reference" + o.javaClass.getSimpleName() + " = null;\n");
            
            if (o.javaClass == Patient.class)
                write(3, "patient = new Patient();\n");
            
            for (ObjectField f : o.fields) {
                if (f.isSet())
                    write(3, f.memberName() + " = new Hash" + f.typeName() + "();\n");
                else
                    if (f.type == ObjectField.Type.Primitive)
                        write(3, f.memberName() + " = nullValue" + f.typeName() + "();\n");
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

            write(6, "topLevelObjects.add(" + o.varName() + ");\n");
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
                        = (f.type == ObjectField.Type.Primitive ? "nullValue" + f.typeName() + "()" : "null");
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

            if (o.javaClass == Patient.class) {
                write(4, "importPatient(patient);\n");
            }
            
            /*
             * -- End elements of fields.
             */
            for (ObjectField f : o.fields) {
                write(3, "} else if (\"" + f.name + "\".equals(qName)) {\n");
                if (f.type == ObjectField.Type.Primitive) {
                    write(4, f.memberName() + " = parse" + f.javaClass.getSimpleName() + "(value);\n");
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
                    + "s(InputSource source) throws SAXException, IOException {\n");

            write(2, "topLevelClass = " + o.javaClass.getSimpleName() + ".class;\n");
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
