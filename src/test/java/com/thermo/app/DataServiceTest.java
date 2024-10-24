package com.thermo.app;


import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;


public class DataServiceTest 
{
    private DataService dataService = new DataService();

    @Test
    public void isCorrectValue()
    {
        String[] testValue = {"dazadazd","-10","41","17","10.0"};

        assertFalse(dataService.isNumber(testValue[0]));
        assertFalse(dataService.isNumber(testValue[1]));
        assertFalse(dataService.isNumber(testValue[2]));
        assertTrue(dataService.isNumber(testValue[3]));
        assertFalse(dataService.isNumber(testValue[4]));
    }

}
