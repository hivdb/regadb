package net.sf.wts.services.util;

import java.util.Comparator;


public class ChallengeComparator implements Comparator<Challenge>
{
    public int compare(Challenge o1, Challenge o2) 
    {
        return o1.challenge_.compareTo(o2.challenge_);
    }
}
