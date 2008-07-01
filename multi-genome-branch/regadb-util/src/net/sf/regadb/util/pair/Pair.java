package net.sf.regadb.util.pair;

public class Pair<KeyType, ValueType>
{
    private KeyType key_;
    private ValueType value_;
    
    public Pair(KeyType key, ValueType value)
    {
        key_ = key;
        value_ = value;
    }

    public KeyType getKey() 
    {
        return key_;
    }

    public void setKey(KeyType key_) 
    {
        this.key_ = key_;
    }

    public ValueType getValue() 
    {
        return value_;
    }

    public void setValue(ValueType value_) 
    {
        this.value_ = value_;
    }
}
