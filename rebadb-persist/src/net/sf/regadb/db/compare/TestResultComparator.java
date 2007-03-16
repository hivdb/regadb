package net.sf.regadb.db.compare;

import java.util.Comparator;

import net.sf.regadb.db.TestResult;

public class TestResultComparator implements Comparator<TestResult>
{
    public int compare(TestResult tr1, TestResult tr2)
    {
        return tr1.getTestDate().compareTo(tr2.getTestDate());
    }
}
