package net.sf.regadb.analysis.functions;

public class FastaRead 
{
    public FastaReadStatus status_;
    public String xna_;
    public String invalidChars_;
    
    public FastaRead(String xna)
    {
        xna_ = xna;
        status_ = FastaReadStatus.Valid;
    }
    
    public FastaRead(FastaReadStatus status)
    {
        status_ = status;
    }
}
