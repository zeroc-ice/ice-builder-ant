// **********************************************************************
//
// Copyright (c) 2003-2017 ZeroC, Inc. All rights reserved.
//
// **********************************************************************

//package Ice.Ant;

public class SliceDefine
{
    public void
    setName(String name)
    {
        _name = name;
    }

    public String
    getName()
    {
        return _name;
    }

    public void
    setValue(String value)
    {
        _value = value;
    }

    public String
    getValue()
    {
        return _value;
    }

    private String _name;
    private String _value;
}
